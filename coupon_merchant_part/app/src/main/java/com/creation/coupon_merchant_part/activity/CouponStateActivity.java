package com.creation.coupon_merchant_part.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.CouponStateItem;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.CouponStateAdapter;
import com.creation.coupon_merchant_part.utils.DataUtils;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Description:该类主要实现优惠券发行状态的展示。
 */
public class CouponStateActivity extends Activity implements View.OnClickListener{
    private ArrayList<CouponStateItem> list;  //发行状态列表
    private ListView listView;                  //列表放置的ListView
    private ImageView backImageView;            //返回按钮
    private TextView totalText;                 //顶端展示的发放总量
    private TextView alreadyUseText;            //顶端展示的已使用优惠券的数量
    private TextView notUseText;                //顶端展示的未使用优惠券的数量
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private LinearLayout inNetLinearLayout;
    private LinearLayout noNetLinearLayout;
    private String urlString3;
    private String requestState3;
    private String merchantId;
    private EditText startDate;
    private EditText endDate;
    private Button searchCouponState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coupon_state);
        initializeAllWidgets();
        listen();
        networkListener();

    }
    //此方法主要实现监听事件
    public void listen(){
        backImageView.setOnClickListener(this);
        searchCouponState.setOnClickListener(this);

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.coupon_state_back:
                //点击返回按钮，退出该页面
                this.finish();
                break;
            case R.id.search_coupon_state:
                if (!DataUtils.validData(startDate.getText().toString()) || !DataUtils.validData(endDate.getText().toString())){
                    Toast.makeText(this,"日期格式不正确",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startDate.getText().toString().compareTo(endDate.getText().toString()) > 0){
                    Toast.makeText(this,"开始日期不能早于截止日期",Toast.LENGTH_SHORT).show();
                    return;
                }
                searchCouponState();
                break;
        }
    }
    //UI控件的初始化
    public void initializeAllWidgets(){
        backImageView = (ImageView)findViewById(R.id.coupon_state_back);
        totalText = (TextView)findViewById(R.id.total_text);
        alreadyUseText = (TextView)findViewById(R.id.already_use_text);
        notUseText = (TextView)findViewById(R.id.not_use_text);
        inNetLinearLayout = (LinearLayout)findViewById(R.id.state_in_net);
        noNetLinearLayout = (LinearLayout)findViewById(R.id.state_no_net);
        SharedPreferences sp = getSharedPreferences("currentSession",Context.MODE_PRIVATE);
        merchantId = sp.getString("merchantId","111");
        startDate = (EditText)findViewById(R.id.start_date);
        endDate = (EditText)findViewById(R.id.end_date);
        searchCouponState = (Button)findViewById(R.id.search_coupon_state);
    }


    public class HttpTask extends AsyncTask<Void, Void, String> {
        private String conditionString;
        private String urlString;
        public AsyncResponse asyncResponse;

        public HttpTask(String conditionString,String urlString){
            this.conditionString = conditionString;
            this.urlString = urlString;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        //在这里使用AsyncResponse接口来实现获取doInBackground的执行结果
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            asyncResponse.onDataReceivedSuccess(result);
            if (result!=null && !"[]".equals(result)){
                try {
                    list = new ArrayList<CouponStateItem>();
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject tempJsonObject = jsonArray.getJSONObject(i);
                        String usernameStr = tempJsonObject.getString("account");
                        String dateStr = tempJsonObject.getString("consumptionDate");
                        String returnStr = tempJsonObject.getString("couponValue");
                        String stateStr = transferToStatus(tempJsonObject.getString("status"));
                        CouponStateItem item = new CouponStateItem(usernameStr, dateStr, returnStr, stateStr);
                        list.add(item);
                    }
                    CouponStateAdapter arrayAdpter = new CouponStateAdapter(CouponStateActivity.this, R.layout.coupon_item, list);
                    listView = (ListView) findViewById(R.id.testListView);
                    listView.setAdapter(arrayAdpter);
                }catch(JSONException je){
                    je.printStackTrace();
                }
            }
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(Void... params) {
            try{
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
                return null;
            }
        }
    }

    //0：未发放
    //1：已使用
    //2：未使用
    //3：申请使用中
    private String transferToStatus(String status) {
        switch (status){
            case "0":
                return "未发放";
            case "1":
                return "已使用";
            case "2":
                return "未使用" ;
            case  "3":
                return "申请使用中";
        }
        return "";
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                //处理页面生成时加载数据
                requestState3 = "merchantId="+merchantId;
                urlString3 = UrlManager.createUrlString("/merchant/couponStateInfoEncapsulate.action");
                HttpTaskTool htFetchInfo = new HttpTaskTool(requestState3, urlString3);
                htFetchInfo.asyncResponse = new AsyncResponse() {
                    @Override
                    public void onDataReceivedSuccess(String data) {
                        if (data!=null && !"".equals(data)){
                            try {
                                JSONObject jo1 = new JSONObject(data);
                                totalText.setText(jo1.getString("total"));
                                alreadyUseText.setText(jo1.getString("alreadyUse"));
                                notUseText.setText(jo1.getString("notUse"));
                            }catch(JSONException je){
                                je.printStackTrace();
                            }
                        }
                    }
                };
                htFetchInfo.execute();
                inNetLinearLayout.setVisibility(View.VISIBLE);
                noNetLinearLayout.setVisibility(View.GONE);
            }
            else{
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
                noNetLinearLayout.setVisibility(View.VISIBLE);
                inNetLinearLayout.setVisibility(View.GONE);

                //跳转到无网络页面
                Intent intentNoNet = new Intent(CouponStateActivity.this , NoNetActivity.class);
                startActivity(intentNoNet);
            }
        }
    }
    public void networkListener(){
        //每当网络状态发生变化时，系统会发出"android.net.conn.CONNECTIVITY_CHANGE"的广播
        //要给intentFilter添加这样一个action，所以想要监听什么样的广播，就添加相应的action
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        //调用 registerReceiver()进行注册，NetworkChangeReceiver()会收到
        // 所有值为"android.net.conn.CONNECTIVITY_CHANGE"的广播
        registerReceiver(networkChangeReceiver, intentFilter);
    }
    public void searchCouponState(){
        urlString3 = new UrlManager().createUrlString("/merchant/queryCouponsStatus.action");
        JSONObject jo2 = new JSONObject();
        try {
            jo2.put("id", merchantId);
            jo2.put("startDate", startDate.getText().toString());
            jo2.put("endDate", endDate.getText().toString());
            requestState3 = jo2.toString();
        }catch(JSONException je){
            je.printStackTrace();
        }
        HttpTask ht2 = new HttpTask(requestState3, urlString3);
//        ht2.asyncResponse = new AsyncResponse() {
//            @Override
//            public void onDataReceivedSuccess(String data) {
//                if (data!=null && !"[]".equals(data)){
//                    try {
//                        JSONArray jsonArray = new JSONArray(data);
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject tempJsonObject = jsonArray.getJSONObject(i);
//                            String usernameStr = tempJsonObject.getString("account");
//                            String dateStr = tempJsonObject.getString("consumptionDate");
//                            String returnStr = tempJsonObject.getString("couponValue");
//                            String stateStr = tempJsonObject.getString("status");
//                            CouponStateItem item = new CouponStateItem(usernameStr, dateStr, returnStr, stateStr);
//                            list.add(item);
//                        }
//                        CouponStateAdapter arrayAdpter = new CouponStateAdapter(CouponStateActivity.this, R.layout.coupon_item, list);
//                        listView = (ListView) findViewById(R.id.testListView);
//                        listView.setAdapter(arrayAdpter);
//                    }catch(JSONException je){
//                        je.printStackTrace();
//                    }
//                }
//            }
//        };
        ht2.execute();
    }

}
