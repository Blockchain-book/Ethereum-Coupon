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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.SettlementRecord;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.DataUtils;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;
import com.creation.coupon_merchant_part.utils.SettlementRecordAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:该类用户商家向银行申请结算券
 */
public class TicketApplyActivity extends Activity implements View.OnClickListener{
    private ImageView backImageView;
    private TextView accountBalance;
    private TextView SettlementBalance;
    private Button applySettlement;
    private EditText settlementAppAmount;
    private String urlString;
    private String conditionString;
    private NetworkChangeReceiver networkChangeReceiver;
    private String urlFetchSettlementBalance;
    private String urlFetchAccountBalance;
    private String merchantId;
    private EditText startDate;
    private EditText endDate;
    private Button searchBill;
    private String urlSearchBillRecord;
    private ListView recordListView;
    private SettlementRecordAdapter settRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ticket_apply);
        backImageView = (ImageView)findViewById(R.id.ticket_apply_back);
        backImageView.setOnClickListener(this);
        init();
        scanNetworkChange();

    }

    //初始化所有的View组件
    public void init(){
        accountBalance = (TextView) findViewById(R.id.account_balance);
        SettlementBalance = (TextView)findViewById(R.id.settlement_balance);
        applySettlement = (Button) findViewById(R.id.apply_settlement);
        applySettlement.setEnabled(true);
        applySettlement.setOnClickListener(this);
        settlementAppAmount = (EditText)findViewById(R.id.settlement_application_amount);
        startDate = (EditText)findViewById(R.id.start_date_input);
        endDate = (EditText)findViewById(R.id.end_date_input);
        searchBill = (Button)findViewById(R.id.search_bill_button);
        searchBill.setOnClickListener(this);
        recordListView = (ListView)findViewById(R.id.apply_settlement_list);
        //SharedPreference取商户Id
        SharedPreferences sp = getSharedPreferences("currentSession",Context.MODE_PRIVATE);
//        merchantId = sp.getString("merchantId",null);
        merchantId = sp.getString("merchantId","111");

    }
    //传入起止日期，查询申请记录
    public void searchBillRecord() throws JSONException {
        urlSearchBillRecord = UrlManager.createUrlString("/merchant/querySettlementRecord.action");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startDate",startDate.getText());
        jsonObject.put("endDate",endDate.getText());
        jsonObject.put("merchantId",merchantId);
        conditionString = jsonObject.toString();
        ApplySettlementHttpTask applySettlementHttpTask = new ApplySettlementHttpTask(conditionString,urlSearchBillRecord);
        applySettlementHttpTask.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                List<SettlementRecord> recordList = new ArrayList<SettlementRecord>();
                try {
                    JSONArray array = new JSONArray(data);
                    for(int i=0;i<array.length();i++){
                        SettlementRecord record = new SettlementRecord();
                        JSONObject obj = array.getJSONObject(i);
                        record.setMerchantId(obj.getString("merchantId"));
                        record.setOperationDate(obj.getString("operationDate"));
                        record.setOperationType(obj.getString("operationType"));
                        record.setOperationAmount(obj.getInt("operationAmount"));
                        record.setRecheckStatus(transferType(obj.getString("recheckStatus")));
                        recordList.add(record);
                    }
                    settRecAdapter = new SettlementRecordAdapter(TicketApplyActivity.this, R.layout.apply_recording_item, recordList);
                    recordListView.setAdapter(settRecAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        applySettlementHttpTask.execute();
    }
    //将数据库中的数字表示的操作类型转化为字符串
    private String transferType(String type){
        String r = "";
        switch(type){
            case "0":
                r = "审核失败";
                break;
            case "1":
                r = "审核中";
                break;
            case "2":
                r = "审核通过";
                break;
        }
        return r;
    }

    //从后台获取结算券余额
    public void fetchSettlementBalance() throws JSONException {
        urlFetchSettlementBalance = UrlManager.createUrlString("/merchant/querySettlementBalance.action");
        conditionString = "id="+merchantId;
        HttpTaskTool applySettlementHttpTask = new HttpTaskTool(conditionString,urlFetchSettlementBalance);
        applySettlementHttpTask.execute();
        applySettlementHttpTask.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"".equals(data)){
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String accountBalanceFetched = jsonObject.get("settlementBalance").toString();
                        SettlementBalance.setText(accountBalanceFetched);
                    }catch(JSONException je){
                        je.printStackTrace();
                    }
                }
            }
        };
    }

    //点击申请结算券按钮时调用
    public void applySettlement() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId",merchantId);
        jsonObject.put("operationAmount",Integer.parseInt(settlementAppAmount.getText().toString()));
        jsonObject.put("operationType","1");
        urlString = new UrlManager().createUrlString("/merchant/applySettlementCoupon.action");
        conditionString = jsonObject.toString();
        ApplySettlementHttpTask applySettlementHttpTask = new ApplySettlementHttpTask(conditionString, urlString);

        applySettlementHttpTask.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"".equals(data)){
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String resultCode = jsonObject.getString("resultCode");
                        if(resultCode.equals("1")){
                            Toast.makeText(getBaseContext(), "请求发送成功！",Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(getBaseContext(), "请求未能成功发送",Toast.LENGTH_SHORT).show();
                        }
                        applySettlement.setEnabled(true);
                    }catch(JSONException je){
                        je.printStackTrace();
                    }
                }
            }
        };
        applySettlementHttpTask.execute();
    }

    public void scanNetworkChange(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    class NetworkChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connMananger = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connMananger.getActiveNetworkInfo();
            if(networkInfo == null || !networkInfo.isAvailable()){
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();

                //跳转到无网络页面
                Intent intentNoNet = new Intent(TicketApplyActivity.this , NoNetActivity.class);
                startActivity(intentNoNet);
            }else{
                try {
                    fetchSettlementBalance();
                }catch(JSONException je){
                    je.printStackTrace();
                }
            }
        }
    }

    class ApplySettlementHttpTask extends AsyncTask<Void, Void, String> {
        private String urlString;
        private String conditionString;
        public AsyncResponse asyncResponse;
        public ApplySettlementHttpTask(String conditionString,String urlString){
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
            try {
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
                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    while ((str = reader.readLine()) != null) {
                        sb.append(str);
                    }
                }
                String result = sb.toString();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ticket_apply_back:
                //点击返回按钮，退出该页
                this.finish();
                break;
            case R.id.apply_settlement:
                if ("".equals(settlementAppAmount.getText().toString())){
                    Toast.makeText(this,"申请金额不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    applySettlement();
                    applySettlement.setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.search_bill_button:
                try {
                    if (!DataUtils.validData(startDate.getText().toString()) || !DataUtils.validData(endDate.getText().toString())){
                        Toast.makeText(this,"日期格式不正确",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (startDate.getText().toString().compareTo(endDate.getText().toString()) > 0){
                        Toast.makeText(this,"开始日期不能早于截止日期",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    searchBillRecord();
                }catch(JSONException je){
                    je.printStackTrace();
                }
                break;
        }
    }
}
