package com.example.babara.coupon_customer_part.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.pojo.CouponItem;
import com.example.babara.coupon_customer_part.pojo.NearbyMerchantItem;
import com.example.babara.coupon_customer_part.utils.AsyncResponse;
import com.example.babara.coupon_customer_part.utils.CouponAdapter;
import com.example.babara.coupon_customer_part.utils.HttpTaskForJsonTool;
import com.example.babara.coupon_customer_part.utils.HttpTaskTool;
import com.example.babara.coupon_customer_part.utils.NearbyMerchantAdapter;
import com.example.babara.coupon_customer_part.utils.PresentCouponAdapter;
import com.example.babara.coupon_customer_part.utils.UrlManager;
import com.example.babara.coupon_customer_part.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

public class PresentActivity extends Activity implements View.OnClickListener{
    private Button submitButton;
    private ArrayList<CouponItem> list;
    private ListView listView;
    private String couponUrl;
    private String couponRequest;
    private Button scanPresentButton;
    private ImageView backImage;
    private EditText validityToNoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_present);

        submitButton = (Button)findViewById(R.id.present_button);
        listView = (ListView)findViewById(R.id.present_coupon_list);
        scanPresentButton = (Button)findViewById(R.id.scan_code_present_button);
        backImage = (ImageView)findViewById(R.id.present_back);
        validityToNoEditText = (EditText)findViewById(R.id.validity_to_no);

        submitButton.setOnClickListener(this);
        scanPresentButton.setOnClickListener(this);
        backImage.setOnClickListener(this);

        list = new ArrayList<>();

        couponUrl = new UrlManager().createUrlString("/consumer/queryUnusedCoupons.action");
        SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        String consumerId = sp.getString("consumerId","");
        couponRequest = "id="+consumerId;

        new HttpTask(couponRequest, couponUrl).execute();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null && bundle.containsKey("result")){
            String presentNo = bundle.getString("result");
            if (presentNo != null && !"".equals(presentNo)){
                validityToNoEditText.setText(presentNo);
            }
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.present_button:
                presentCoupons();
                break;
            case R.id.scan_code_present_button:
                Intent intent = new Intent(PresentActivity.this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type","present");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.present_back:
                this.finish();
                break;

        }
    }

    private void presentCoupons() {
        String validityToNo = validityToNoEditText.getText().toString();
        if ("".equals(validityToNo)){
            Toast.makeText(PresentActivity.this,"请输入对方帐号",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isMobileNoValid(validityToNo)){
            Toast.makeText(PresentActivity.this,"对方帐号格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }
        final long[] checkedItemIds = listView.getCheckedItemIds();
        if (checkedItemIds.length == 0){
            Toast.makeText(PresentActivity.this,"请选择要转赠的优惠券",Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        String consumerId = sp.getString("consumerId","");
        String url = UrlManager.createUrlString("/consumer/presentCoupon.action");

        String couponsId = "";
        final int length = checkedItemIds.length;
        for (int i = 0 ; i < length-1 ; i++){
            couponsId += list.get((int)checkedItemIds[i]).getCouponId()+",";
        }
        couponsId += list.get((int)checkedItemIds[length-1]).getCouponId();

        String requestString = "from="+consumerId+"&to="+validityToNo+"&couponsId="+couponsId;

        HttpTaskTool task = new HttpTaskTool(requestString,url);
        task.execute();
        submitButton.setEnabled(false);
        task.setOnAsyncResponse(new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"".equals(data)){
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String res  = jsonObject.getString("resultCode");
                        if ("1".equals(res)){
                            Toast.makeText(PresentActivity.this,"优惠券赠送成功",Toast.LENGTH_SHORT).show();
                            for (int i = 0 ; i < length ; i++){
                                list.remove((int)checkedItemIds[i]-i);
                            }
                            PresentCouponAdapter arrayAdpter = new PresentCouponAdapter(PresentActivity.this, R.layout.coupon_item, list);
                            listView.setAdapter(arrayAdpter);
                            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        }else {
                            Toast.makeText(PresentActivity.this,"对方用户不存在",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                submitButton.setEnabled(true);
            }
        });
    }

    private boolean isMobileNoValid(String mobileNo) {
        //验证手机格式
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNo))
            return false;
        else
            return mobileNo.matches(telRegex);
    }

    public class HttpTask extends AsyncTask<Void, Void, String> {
        private String conditionString;
        private String urlString;

        public HttpTask(String conditionString,String urlString){
            this.conditionString = conditionString;
            this.urlString = urlString;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        //在这里使用AsyncResponse接口来实现获取doInBackground的执行结果
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                JSONArray jsonArray = new JSONArray(result);

                int length = jsonArray.length();

                CouponItem[] items = new CouponItem[length];
                for(int i = 0; i < length; i++){
                    JSONObject tempJsonObject = jsonArray.getJSONObject(i);
                    String couponId = tempJsonObject.getString("couponId");
                    String amount = tempJsonObject.getString("couponValue");
                    String obtainTime = tempJsonObject.getString("consumptionDate");
                    String validityFrom = tempJsonObject.getString("validStartDate");
                    String validityTo = tempJsonObject.getString("validEndDate");
                    String merchantName = tempJsonObject.getString("merchantName");
                    String obtainValue = tempJsonObject.getString("obtainValue");
                    items[i] = new CouponItem(couponId, amount, obtainTime, validityFrom, validityTo);
                    items[i].setMerchantName(merchantName);
                    items[i].setExpense(Integer.parseInt(obtainValue));
                    list.add(items[i]);
                }
                PresentCouponAdapter arrayAdpter = new PresentCouponAdapter(PresentActivity.this, R.layout.coupon_item, list);
                listView.setAdapter(arrayAdpter);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(Void... params) {
            try{
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                String data = conditionString;
                byte[] bytes = data.getBytes();
                conn.getOutputStream().write(bytes); // 写出数据
                //读取服务器的返回结果
                InputStream is = null;
                StringBuffer sb = new StringBuffer();
                String str = "";
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    while((str = reader.readLine())!=null){
                        sb.append(str);
                    }
                }
                String result = sb.toString();
                return result;
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
