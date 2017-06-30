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
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.UrlManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description:该类主要实现发行规则的展示和终止发行的功能。
 */
public class IssueManageActivity extends Activity implements View.OnClickListener{
    private ImageView backImageView;
    private Button stopIssueButton;
    private TextView upToText;
    private TextView returnText;
    private TextView addUpText;
    private TextView mostText;
    private TextView totalText;
    private TextView fromText;
    private TextView toText;
    private TextView totalSendText;
    private TextView notSendText;
    private TextView notUseText;
    private TextView callBackText;
    private ScrollView scrollView;
    private String issueManageStr;
    private String requestState;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private String merchantId;
    private String couponRulerId;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_issue_manage);
        linearLayout = (LinearLayout) findViewById(R.id.activity_issue_manage);
        linearLayout.setVisibility(View.GONE);
        initializeAllWidget();
        backImageView.setOnClickListener(this);
        stopIssueButton.setOnClickListener(this);
        networkListener();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.issue_manage_back:
                //点击返回按钮，返回到主页
                this.finish();
                break;
            case R.id.stop_issue:
                //点击终止发行按钮，将终止信息发送到服务器
                String urlString = new UrlManager().createUrlString("/merchant/terminateIssue.action");
                String requestRemain1= "couponRulerId="+couponRulerId;
                new HttpTask(requestRemain1, urlString, "stopIssue").execute();
                stopIssueButton.setEnabled(false);
                break;
        }
    }

    public void initializeAllWidget(){
        backImageView = (ImageView)findViewById(R.id.issue_manage_back);
        stopIssueButton = (Button)findViewById(R.id.stop_issue);
        stopIssueButton.setEnabled(true);
        upToText = (TextView)findViewById(R.id.up_to_text);
        returnText = (TextView)findViewById(R.id.return_text);
        addUpText = (TextView)findViewById(R.id.add_up_text);
        mostText = (TextView)findViewById(R.id.most_text);
        totalText = (TextView)findViewById(R.id.total_text);
        fromText = (TextView)findViewById(R.id.from_text);
        toText = (TextView)findViewById(R.id.to_text);
        totalSendText = (TextView)findViewById(R.id.total_send_text);
        notSendText = (TextView)findViewById(R.id.not_send_text);
        notUseText = (TextView)findViewById(R.id.not_use_text);
        callBackText = (TextView)findViewById(R.id.call_back_text);
        scrollView = (ScrollView)findViewById(R.id.issue_manage_in_net);
//        linearLayout = (LinearLayout)findViewById(R.id.issue_manage_no_net);

        //SharedPreference取商户Id
        SharedPreferences sp = getSharedPreferences("currentSession",Context.MODE_PRIVATE);
//        merchantId = sp.getString("merchantId",null);
        merchantId = sp.getString("merchantId","111");
        couponRulerId= sp.getString("couponRulerId","66dfb70e-6ebf-42c1-a957-30b8b2cd6b1d");
    }

    public class HttpTask extends AsyncTask<Void, Void, String> {
        private String conditionString;
        private String urlString;
        private String type;

        public HttpTask(String conditionString,String urlString, String type){
            this.conditionString = conditionString;
            this.urlString = urlString;
            this.type = type;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        //在这里使用AsyncResponse接口来实现获取doInBackground的执行结果
        @Override
        protected void onPostExecute(String result) {
            if(result != null && !"".equals(result)){
                linearLayout.setVisibility(View.VISIBLE);
                super.onPostExecute(result);
                try {
                    if (type.equals("issueManage")) {
                        JSONObject jsonObject = new JSONObject(result);

                        upToText.setText(jsonObject.getString("upTo"));
                        returnText.setText(jsonObject.getString("returnValue"));
                        addUpText.setText(transferAddup(jsonObject.getString("addUp")));
                        mostText.setText(jsonObject.getString("most"));
                        totalText.setText(jsonObject.getString("total"));
                        fromText.setText(jsonObject.getString("from"));
                        toText.setText(jsonObject.getString("to"));
                        totalSendText.setText(jsonObject.getString("totalSend"));
                        notSendText.setText(jsonObject.getString("notSend"));
                        notUseText.setText(jsonObject.getString("notUse"));
                        callBackText.setText(jsonObject.getString("callBack"));
                    } else if (type.equals("stopIssue")) {
                        JSONObject jsonObject = new JSONObject(result);
                        switch(jsonObject.getString("resultCode")){
                            case "1":
                                SharedPreferences sp1 = getSharedPreferences("currentSession", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp1.edit();
                                editor.remove("couponRulerId").commit();
                                editor.remove("curRulerReach").commit();
                                Toast.makeText(IssueManageActivity.this, result, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(IssueManageActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case "-1":
                                Toast.makeText(IssueManageActivity.this, "终止失败", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        stopIssueButton.setEnabled(true);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else{
                Intent intent = new Intent(IssueManageActivity.this,NoCouponRulerActivity.class);
                startActivity(intent);
                IssueManageActivity.this.finish();
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

    private String transferAddup(String addUp) {
        return addUp.equals("true")?"可":"不可";
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                scrollView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                issueManageStr = new UrlManager().createUrlString("/merchant/terminateIssueEncapsulate.action");
                requestState = "merchantId="+merchantId;
                new HttpTask(requestState, issueManageStr, "issueManage").execute();
            }
            else{
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
                scrollView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

                //跳转到无网络页面
                Intent intentNoNet = new Intent(IssueManageActivity.this , NoNetActivity.class);
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
}
