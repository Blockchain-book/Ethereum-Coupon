package com.example.babara.coupon_customer_part.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BaseTransientBottomBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.pojo.CouponItem;
import com.example.babara.coupon_customer_part.pojo.NearbyMerchantItem;
import com.example.babara.coupon_customer_part.utils.AsyncResponse;
import com.example.babara.coupon_customer_part.utils.CouponAdapter;
import com.example.babara.coupon_customer_part.utils.CouponConfirmAdapter;
import com.example.babara.coupon_customer_part.utils.HttpTaskForJsonTool;
import com.example.babara.coupon_customer_part.utils.HttpTaskTool;
import com.example.babara.coupon_customer_part.utils.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfirmPayActivity extends Activity implements View.OnClickListener{
    private TextView merchantName;
    private NearbyMerchantItem merchant;
    private ArrayList<CouponItem> list;
    private ListView listView;
    private Button payButton;
    private ImageView backImage;
    private EditText consumeValueEditText;
    private Handler handler = new Handler();
    private CouponItem selectedCoupon;
    private Integer consumptionValue = 0;
    private AlertDialog alert = null;
    private Intent intent;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirm_pay);
        bindView();

    }

    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            String payAmount = consumeValueEditText.getText().toString();
            if (payAmount != null && !"".equals(payAmount)){
                Integer consumeAmount = Integer.parseInt(payAmount);
                String getCouponUrl = UrlManager.createUrlString("/consumer/queryAvalibleCoupons.action");
                try{
                    JSONObject jo = new JSONObject();
                    jo.put("consumerId",getSharedPreferences("data",MODE_PRIVATE).getString("consumerId","111"));
                    jo.put("comsuptionAmount",consumeAmount);
                    HttpTaskForJsonTool htfj1 = new HttpTaskForJsonTool(jo.toString(),getCouponUrl);
                    htfj1.execute();
                    htfj1.asyncResponse = new AsyncResponse() {
                        @Override
                        public void onDataReceivedSuccess(String data) {
                            list = new ArrayList<>();
                            if (data!=null && !"[]".equals(data)){
                                try {
                                    JSONArray array = new JSONArray(data) ;
                                    for(int i=0;i<array.length();i++){
                                        JSONObject tempJsonObject = array.getJSONObject(i);
                                        String couponId = tempJsonObject.getString("couponId");
                                        String amount = tempJsonObject.getInt("value")+"";
                                        String obtainTime = tempJsonObject.getString("consumptionDate");
                                        String validityFrom = tempJsonObject.getString("startDate");
                                        String validityTo = tempJsonObject.getString("endDate");
                                        String merchantName = tempJsonObject.getString("merchantName");
                                        Integer obtainValue = tempJsonObject.getInt("consumptionValue");
                                        CouponItem ciTemp = new CouponItem(couponId,amount, obtainTime, validityFrom, validityTo);
                                        ciTemp.setExpense(obtainValue);
                                        ciTemp.setMerchantName(merchantName);
                                        list.add(ciTemp);
                                    }
                                }catch(JSONException je){
                                    je.printStackTrace();
                                }
                                CouponConfirmAdapter arrayAdpter = new CouponConfirmAdapter(ConfirmPayActivity.this, R.layout.coupon_item, list);
                                listView = (ListView) findViewById(R.id.my_coupon_list);
                                listView.setAdapter(arrayAdpter);
                                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        点击列表中的每一条，会将该条目的信息展示在上部分，以共商家查看详细信息，确定发放或者取消
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        selectedCoupon = list.get(position);
                                        index = position;
                                    }
                                });
                            }else{
                                list.clear();
                                CouponConfirmAdapter arrayAdpter = new CouponConfirmAdapter(ConfirmPayActivity.this, R.layout.coupon_item, list);
                                listView = (ListView) findViewById(R.id.my_coupon_list);
                                listView.setAdapter(arrayAdpter);
                            }
                        }
                    };
                }catch(JSONException je){
                    je.printStackTrace();
                }
            }
        }
    };

    public void bindView(){

        merchantName = (TextView)findViewById(R.id.comfirm_merchant_name);
        payButton = (Button)findViewById(R.id.confirm_pay_button);
        backImage = (ImageView)findViewById(R.id.confirm_back);
        consumeValueEditText = (EditText)findViewById(R.id.consumeAmount);
        payButton.setOnClickListener(this);
        backImage.setOnClickListener(this);
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.containsKey("merchantInfo")){
            merchant = (NearbyMerchantItem) bundle.getSerializable("merchantInfo");
            merchantName.setText(merchant.getMerchantName());
        }

        //支付金额EditText焦点监听
        consumeValueEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (consumeValueEditText.getText().toString() != null && !"".equals(consumeValueEditText.getText().toString())){
                    consumptionValue = Integer.parseInt(consumeValueEditText.getText().toString());
                }
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 1000);
            }
        });
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.confirm_back:
                this.finish();
                break;
            case R.id.confirm_pay_button:
                if(selectedCoupon == null){
                    Toast.makeText(ConfirmPayActivity.this,"请先选择优惠券", Toast.LENGTH_SHORT);
                }else if(consumptionValue<=0){
                    Toast.makeText(ConfirmPayActivity.this,"请先填入合法的消费金额", Toast.LENGTH_SHORT);
                }else if(intent.getSerializableExtra("merchantInfo")==null){
                    Toast.makeText(ConfirmPayActivity.this,"请先选择当前商家", Toast.LENGTH_SHORT);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmPayActivity.this);
                    alert = builder.setTitle("亲，注意了哦！")
                            .setMessage("您确定使用这张优惠券支付吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alert.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    intent = getIntent();
                                    merchant = (NearbyMerchantItem) intent.getSerializableExtra("merchantInfo");
                                    String merchantId = merchant.getMerchantId();
                                    String consumerId = getSharedPreferences("data", MODE_PRIVATE).getString("consumerId", "");
                                    String couponId = selectedCoupon.getCouponId();
                                    String consumeValue = consumptionValue+"";
                                    String params = "merchantId="+merchantId+"&couponIds="+couponId+"&consumerId="
                                            +consumerId+"&consumeValue="+consumeValue;
                                    String url1 = UrlManager.createUrlString("/consumer/applyUseCoupon.action");
                                    HttpTaskTool htt1 = new HttpTaskTool(params, url1);
                                    htt1.execute();
                                    htt1.asyncResponse = new AsyncResponse() {
                                        @Override
                                        public void onDataReceivedSuccess(String data) {
                                            payButton.setEnabled(true);
                                            Toast.makeText(ConfirmPayActivity.this,"请求发送成功！",Toast.LENGTH_SHORT);
                                        }
                                    };
                                    payButton.setEnabled(false);
                                    list.remove(index);
                                    CouponConfirmAdapter arrayAdpter = new CouponConfirmAdapter(ConfirmPayActivity.this, R.layout.coupon_item, list);
                                    listView = (ListView) findViewById(R.id.my_coupon_list);
                                    listView.setAdapter(arrayAdpter);
                                }
                            }).create();
                    alert.show();
                }
                break;
        }
    }
}
