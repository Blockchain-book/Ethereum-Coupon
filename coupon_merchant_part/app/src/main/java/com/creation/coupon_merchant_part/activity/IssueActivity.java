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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.DataUtils;
import com.creation.coupon_merchant_part.utils.HttpTaskForJsonTool;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description:该类主要实现优惠券的发行功能。
 * 商户可以输入优惠券的发行规则，点击发行按钮，将数据传送到数据库中。
 */
public class IssueActivity extends Activity implements View.OnClickListener{
    private TextView remainNumTextView;     //从服务器上获取可用的余额
    private EditText upToEditText;          //商家输入满a元
    private EditText reductionEditText;     //商家输入赠送b元
    private EditText mostEditText;          //商家优惠封顶c元
    private CheckBox addUpCheckBox;         //商家输入是否可以累加
    private EditText totalEditText;         //商家输入发行总量d元
    private EditText fromDateEditText;      //商家输入发行开始日期
    private EditText toDateEditText;        //商家输入发行截止日期
    private Button issueButton;             //发行按钮
    private ImageView backImageView;        //返回按钮
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private String urlString1;
    private String requestRemain1;
    private String merchantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_issue);
        bindView();
    }
    //实现UI组件的初始化
    public void bindView(){
        upToEditText = (EditText)findViewById(R.id.up_to_edit);
        reductionEditText = (EditText)findViewById(R.id.reduction_edit);
        mostEditText = (EditText)findViewById(R.id.most_edit);
        addUpCheckBox = (CheckBox)findViewById(R.id.add_up_checkbox);
        remainNumTextView = (TextView)findViewById(R.id.remain_num);
        totalEditText = (EditText)findViewById(R.id.total_edit);
        fromDateEditText = (EditText)findViewById(R.id.from_date_edit);
        toDateEditText = (EditText)findViewById(R.id.to_date_edit);
        issueButton = (Button)findViewById(R.id.issue_button);
        issueButton.setEnabled(true);
        backImageView = (ImageView)findViewById(R.id.issue_back);
        linearLayout = (LinearLayout)findViewById(R.id.no_net);
        scrollView = (ScrollView)findViewById(R.id.in_net);
        SharedPreferences sp = getSharedPreferences("currentSession", Context.MODE_PRIVATE);
        merchantId = sp.getString("merchantId","111");
        urlString1 = new UrlManager().createUrlString("/merchant/querySettlementBalance.action");
        requestRemain1= "id="+merchantId;
        networkListener();
        issueButton.setOnClickListener(this);
        backImageView.setOnClickListener(this);

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.issue_button:
                attmptIssue();
                issueButton.setEnabled(false);
                break;
            case R.id.issue_back:
                this.finish();
                break;
        }

    }
    //将发行规则传送到服务器中
    public void attmptIssue(){
        //获取用户输入的发行规则
        String upToString = upToEditText.getText().toString();
        String reductionString = reductionEditText.getText().toString();
        String mostString = mostEditText.getText().toString();
        String isAddUp = trasferIsAddUp(addUpCheckBox.isChecked());
        String totalString = totalEditText.getText().toString();
        String fromDateString = fromDateEditText.getText().toString();
        String toDateString = toDateEditText.getText().toString();
        if (!DataUtils.validData(fromDateString) || !DataUtils.validData(toDateString)){
            Toast.makeText(this,"日期格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }
        if (fromDateString.compareTo(toDateString) > 0){
            Toast.makeText(this,"开始日期不能早于截止日期",Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(upToString) || "".equals(reductionString) || "".equals(mostString) || "".equals(totalString)
                || "".equals(fromDateString) || "".equals(toDateString)){
            Toast.makeText(this,"数据不能为空，请填写数据",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("reach", upToString);
            jsonObject.put("give", reductionString);
            jsonObject.put("capping", mostString);
            jsonObject.put("isAccumulation", isAddUp);
            jsonObject.put("totalAmount", totalString);
            jsonObject.put("validStartDate", fromDateString);
            jsonObject.put("validEndDate", toDateString);
            jsonObject.put("merchantId",merchantId);
            //将规则发送到服务器中
            String issueCondition = jsonObject.toString();
            String urlString = new UrlManager().createUrlString("/merchant/issueCoupons.action");
            //实现向服务器发送发行的条件，发行成功后给出提示，并且跳转到主页面
            HttpTaskForJsonTool httpTaskTool = new HttpTaskForJsonTool(issueCondition, urlString);
            httpTaskTool.execute();
            httpTaskTool.setOnAsyncResponse(new AsyncResponse() {
                //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
                @Override
                public void onDataReceivedSuccess(String data) {
                    try {
                        if (data != null && !"".equals(data)) {
                            JSONObject jo = new JSONObject(data);
                            switch (jo.getString("resultCode")){
                                case "0":
                                    Toast.makeText(IssueActivity.this, "发行失败", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    SharedPreferences.Editor editor = getSharedPreferences("currentSession", MODE_PRIVATE).edit();
                                    editor.putString("couponRulerId", jo.getString("resultCode"));
                                    editor.putInt("reach", Integer.parseInt(upToEditText.getText().toString()));
                                    editor.putInt("give", Integer.parseInt(reductionEditText.getText().toString()));
                                    editor.commit();
                                    Toast.makeText(IssueActivity.this, data, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(IssueActivity.this, MainActivity.class);
                                    startActivity(intent);
                            }
                        }
                        issueButton.setEnabled(true);
                    }catch(JSONException je){
                        je.printStackTrace();
                    }
                }
            });
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String trasferIsAddUp(boolean checked) {
        if (checked){
            return "1";
        }else{
            return "0";
        }
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
                HttpTaskTool httpTaskTool = new HttpTaskTool(requestRemain1, urlString1);
                httpTaskTool.execute();
                httpTaskTool.setOnAsyncResponse(new AsyncResponse() {
                    @Override
                    public void onDataReceivedSuccess(String data) {
                        if (data != null && !"".equals(data)){
                            try{
                                JSONObject jsonObject = new JSONObject(data);
                                String remain = jsonObject.getString("settlementBalance");
                                remainNumTextView.setText(remain);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            else{
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
                scrollView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

                //跳转到无网络页面
                Intent intentNoNet = new Intent(IssueActivity.this , NoNetActivity.class);
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
    public boolean isNetworkAvailable(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo info = connectivity.getActiveNetworkInfo();
        //return info.isAvailable();
        NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(info != null){
            return info.isConnectedOrConnecting();
        }
        else{
            return false;
        }
    }
}


