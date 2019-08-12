package com.creation.coupon_merchant_part.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.creation.coupon_merchant_part.utils.AsyncResponse;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Babara Liang on 2016/12/26.
 * Description:该类实现“我的->我的设置->商户信息”的设置功能
 */

public class InfoSettingFragment extends Fragment implements View.OnClickListener{
    private boolean isNetworkAvailable;
    private String urlString3;
    private String merchantId;
    private String conditionString;
    private String name;
    private String description;
    private EditText merchant_name;
    private EditText merchant_description;
    private String account;

    public InfoSettingFragment(){
    }
    private Button infoSubmitButton;     //提交按钮
    //@Nullable表示定义的字段可以为空
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        SharedPreferences sp = getActivity().getSharedPreferences("currentSession",Context.MODE_PRIVATE);
        merchantId = sp.getString("merchantId","111");
        account = sp.getString("account","");
        View view = inflater.inflate(R.layout.fragment_info_setting,container,false);
        infoSubmitButton = (Button)view.findViewById(R.id.info_submit_button);
        infoSubmitButton.setEnabled(true);
        merchant_name = (EditText) view.findViewById(R.id.merchant_name);
        merchant_description=(EditText) view.findViewById(R.id.merchant_description);
        infoSubmitButton.setOnClickListener(this);
        networkListener();
        try {
            queryMerchantInfoByAccount();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.info_submit_button:
                if(isNetworkAvailable){
                    ////点击提交按钮，系统向服务器提交商家输入的商户信息设置的数据
                    String urlString = new UrlManager().createUrlString("/merchant/submitMerchantIntro.action");
                    JSONObject requestRemain=new JSONObject();
                    try {
                        name = merchant_name.getText().toString();
                        description = merchant_description.getText().toString();
                        requestRemain.put("id",merchantId);
                        requestRemain.put("name",name);
                        requestRemain.put("description",description);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                        String request=requestRemain.toString();
                    new HttpTask(request,urlString).execute();
                    infoSubmitButton.setEnabled(false);
                }
                else{
                 Toast.makeText(getActivity(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class HttpTask extends AsyncTask<Void, Void, String> {
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
            if (result!=null && !"".equals(result)){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.getString("resultCode");
                    if ("0".equals(res)){
                        Toast.makeText(getActivity(), "提交失败", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                    }
                    infoSubmitButton.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
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
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

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

    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                isNetworkAvailable = true;
            }
            else{
                isNetworkAvailable = false;
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
        getActivity().registerReceiver(networkChangeReceiver, intentFilter);
    }

    public void queryMerchantInfoByAccount() throws JSONException {
        urlString3 = UrlManager.createUrlString("/merchant/queryMerchantInfoByAccount.action");
        String conditionString="id="+account;
        HttpTaskTool mySettingHttpTask = new HttpTaskTool(conditionString,urlString3);
        mySettingHttpTask.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String name = jsonObject.get("name").toString();
                    merchant_name.setText(name);
                    String description = jsonObject.get("description").toString();
                    merchant_description.setText(description );
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        };
        mySettingHttpTask.execute();
    }
}