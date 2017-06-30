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
import android.support.v7.app.AppCompatActivity;
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
import com.creation.coupon_merchant_part.pojo.BillItem;
import com.creation.coupon_merchant_part.pojo.CouponStateItem;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.BillAdapter;
import com.creation.coupon_merchant_part.utils.CouponStateAdapter;
import com.creation.coupon_merchant_part.utils.DataUtils;
import com.creation.coupon_merchant_part.utils.HttpTaskForJsonTool;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:该类主要实现“我的->资金账户”的展示。
 */
public class CapitalAccountActivity extends Activity implements View.OnClickListener{
    private ImageView backImageView;            //返回按钮
    private ArrayList<BillItem> list;          //账单列表
    private ListView listView;                  //放置账单列表的ListView
    private LinearLayout inNetLinearLayout;    //有网络时会加载的布局
    private LinearLayout noNetLinearayout;     //无网络时会加载的布局
    private String urlString2;                  //请求“资金账户”数据的url
    private String urlString3;
    private String urlString4;
    private String urlString5;
    private String requestState;

    private String merchantId;
    private Integer operationAmount;

    private Integer accountBalance; //账户余额
    private TextView accountBalanceText;
    private Integer settlementBalance;  //结算卷余
    private Integer couponBalance;      //优惠卷余
    private TextView settlementBalanceText;
    private TextView couponRemainText;

    private EditText startDate;
    private EditText endDate;
    private Button fundSettle;
    private Button screenBill;

    private Toast toast=null;
    private Integer send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_capital_account);
        backImageView = (ImageView)findViewById(R.id.capital_account_back);
        inNetLinearLayout = (LinearLayout)findViewById(R.id.capital_account_in_net);
        noNetLinearayout = (LinearLayout)findViewById(R.id.capital_account_no_net);
        settlementBalanceText =(TextView) findViewById(R.id.ticket_remain);
        couponRemainText=(TextView)findViewById(R.id.coupon_remain);
        accountBalanceText=(TextView)findViewById(R.id.account_remain_text);
        startDate = (EditText)findViewById(R.id.start_date);
        endDate = (EditText)findViewById(R.id.end_date);
        fundSettle = (Button)findViewById(R.id.fund_settle);
        fundSettle.setEnabled(true);
        screenBill = (Button)findViewById(R.id.screen_bill);
        backImageView.setOnClickListener(this);
        fundSettle.setOnClickListener(this);
        screenBill.setOnClickListener(this);
        SharedPreferences sp = getSharedPreferences("currentSession", Context.MODE_PRIVATE);
        send = sp.getInt("give",10);

        list = new ArrayList<BillItem>();
//        urlString2 = new UrlManager().createUrlString("/merchant/generateBill.action");
        //requestState = "ask=bill";
        networkListener();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.capital_account_back:
                //点击“返回”按钮，退出此页
                this.finish();
                break;
            case  R.id.fund_settle:
                //资金结算
                fundSettle();
                fundSettle.setEnabled(false);
                break;
            case  R.id.screen_bill:
                //筛选账单
                screenBill();
                break;

        }
    }
    private void fundSettle(){
        SharedPreferences sp = getSharedPreferences("currentSession",Context.MODE_PRIVATE);
        merchantId = sp.getString("merchantId","111");
        urlString3 = new UrlManager().createUrlString("/merchant/applyWDApp.action");
        JSONObject jo2 = new JSONObject();
        try {
            jo2.put("merchantId", merchantId);
            jo2.put("operationAmount", settlementBalance);
            requestState = jo2.toString();
        }catch(JSONException je){
            je.printStackTrace();
        }
        HttpTaskForJsonTool ht2 = new HttpTaskForJsonTool(requestState, urlString3);
        ht2.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {

            }
        };
        ht2.execute();
        urlString5 = new UrlManager().createUrlString("/merchant/generateBill.action");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("merchantId", merchantId);
            jsonObject.put("value", settlementBalance);
            jsonObject.put("type","结算卷提现");
            requestState = jsonObject.toString();
        }catch(JSONException je){
            je.printStackTrace();
        }
        HttpTaskForJsonTool ht3 = new HttpTaskForJsonTool(requestState, urlString5);
        ht3.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"".equals(data)){
                    try {
                        JSONObject object = new JSONObject(data);
                        String res = object.getString("resultCode");
                        if ("1".equals(res)){
                            toast.makeText(CapitalAccountActivity.this, "已发送申请",Toast.LENGTH_LONG).show();
                        }else {
                            toast.makeText(CapitalAccountActivity.this, "发送申请失败",Toast.LENGTH_LONG).show();
                        }
                        fundSettle.setEnabled(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        ht3.execute();
    }
    private void screenBill(){
        SharedPreferences sp = getSharedPreferences("currentSession",Context.MODE_PRIVATE);
        merchantId = sp.getString("merchantId","111");
        urlString4 = new UrlManager().createUrlString("/merchant/queryMerchantBill.action");
        if (!DataUtils.validData(startDate.getText().toString()) || !DataUtils.validData(endDate.getText().toString())){
            Toast.makeText(this,"日期格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }
        if (startDate.getText().toString().compareTo(endDate.getText().toString()) > 0){
            Toast.makeText(this,"开始日期不能早于截止日期",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jo2 = new JSONObject();
        try {
            jo2.put("id", merchantId);
            jo2.put("startDate", startDate.getText());
            jo2.put("endDate", endDate.getText());
            requestState = jo2.toString();
        }catch(JSONException je){
            je.printStackTrace();
        }
        //结算卷体现（余额改？）
        //日期还没改
        HttpTaskForJsonTool ht2 = new HttpTaskForJsonTool(requestState, urlString4);
        ht2.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!="" && !"[]".equals(data)){
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tempJsonObject = jsonArray.getJSONObject(i);
                            String time = tempJsonObject.getString("operationDate");
                            String type = tempJsonObject.getString("type");
                            String accountRemain = tempJsonObject.getString("value");
                            BillItem item = new BillItem(time, type, accountRemain);
                            list.add(item);
                        }
                        BillAdapter billAdapter = new BillAdapter(CapitalAccountActivity.this, R.layout.bill_item, list);
                        listView = (ListView) findViewById(R.id.billListView);
                        listView.setAdapter(billAdapter);
                    }catch(JSONException je){
                        je.printStackTrace();
                    }
                }
            }
        };
        ht2.execute();
    }


    private void initBalance() {
        SharedPreferences sp = getSharedPreferences("currentSession",Context.MODE_PRIVATE);
        merchantId = sp.getString("merchantId","111");
        requestState = "id="+merchantId;
        String requestState1 = "merchantId="+merchantId;
        HttpTask httpTask1 = new HttpTask(requestState1,UrlManager.createUrlString("/merchant/queryAccountBalance.action"));
        httpTask1.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"".equals(data)){
                    try{
                        JSONObject jsonObject = new JSONObject(data);
                        accountBalance = Integer.parseInt(jsonObject.getString("accountBalance"));
                        accountBalanceText.setText(String.valueOf(accountBalance));
                    }catch (JSONException j){
                        j.printStackTrace();
                    }
                }
            }
        };
        httpTask1.execute();
        HttpTask httpTask = new HttpTask(requestState,UrlManager.createUrlString("/merchant/queryCouponsUnsed.action"));
        httpTask.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"".equals(data)){
                    try{
                        JSONObject jsonObject1 = new JSONObject(data);
                        couponBalance = Integer.parseInt(jsonObject1.getString("couponBalance"));
                        couponRemainText.setText(String.valueOf(couponBalance*send));
                    }catch (JSONException j){
                        j.printStackTrace();
                    }
                }else{
                    couponRemainText.setText("0");
                }
            }
        };
        httpTask.execute();
        HttpTask httpTask2 = new HttpTask(requestState,UrlManager.createUrlString("/merchant/querySettlementBalance.action"));
        httpTask2.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!="" && !"".equals(data)){
                    try{
                        JSONObject tempJsonObject = new JSONObject(data);
                        settlementBalance = Integer.parseInt(tempJsonObject.getString("settlementBalance"));
                        settlementBalanceText.setText(String.valueOf(settlementBalance));

                    }catch (JSONException j){
                        j.printStackTrace();
                    }
                }
            }
        };
        httpTask2.execute();
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
            asyncResponse.onDataReceivedSuccess(result);
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
    //网络监听
    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                inNetLinearLayout.setVisibility(View.VISIBLE);
                noNetLinearayout.setVisibility(View.GONE);
                initBalance();
                /*HttpTaskForJsonTool ht = new HttpTaskForJsonTool(requestState, urlString4);
                ht.asyncResponse = new AsyncResponse() {
                    @Override
                    public void onDataReceivedSuccess(String result) {
                        if (result!="" && !"[]".equals(result)){
                            try{
                                ArrayList<BillItem> list = new ArrayList<BillItem>();
                                JSONArray jsonArray = new JSONArray(result);
                                int length = jsonArray.length();
                                BillItem[] items = new BillItem[length];
                                for(int i = 0; i < length; i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String time = jsonObject.getString("time");
                                    String type = jsonObject.getString("type");
                                    String remain = jsonObject.getString("remain");
                                    items[i] = new BillItem(time, type, remain);
                                    list.add(items[i]);
                                }
                                BillAdapter arrayAdpter = new BillAdapter(CapitalAccountActivity.this, R.layout.bill_item, list);
                                listView = (ListView)findViewById(R.id.billListView) ;
                                listView.setAdapter(arrayAdpter);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                };
                ht.execute();*/


            }
            else{
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
                inNetLinearLayout.setVisibility(View.GONE);
                noNetLinearayout.setVisibility(View.VISIBLE);

                //跳转到无网络页面
                Intent intentNoNet = new Intent(CapitalAccountActivity.this , NoNetActivity.class);
                startActivity(intentNoNet);
            }
        }
    }
    public void networkListener(){
        //每当网络状态发生变化时，系统会发出"android.net.conn.CONNECTIVITY_CHANGE"的广播
        //要给intentFilter添加这样一个action，所以想要监听什么样的广播，就添加相应的action
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        //调用 registerReceiver()进行注册，NetworkChangeReceiver()会收到
        // 所有值为"android.net.conn.CONNECTIVITY_CHANGE"的广播
        registerReceiver(networkChangeReceiver, intentFilter);
    }

}
