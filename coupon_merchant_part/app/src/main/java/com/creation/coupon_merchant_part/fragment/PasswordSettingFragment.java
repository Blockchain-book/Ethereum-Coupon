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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.activity.IssueActivity;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
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
 * Description:该类实现“我的->我的设置->密码设置”的功能
 */

public class PasswordSettingFragment extends Fragment implements View.OnClickListener{
    public PasswordSettingFragment(){}
    private Button passwordModifyButton;  //确认修改按钮
    private String passwordModifyUrl;
    private String requestState;
    private boolean isNetworkAvailable;
    private String urlString3;
    private String account;
    private String password;
    private EditText verify_password;
    private EditText new_password;
    private EditText old_password;
    //@Nullable表示定义的字段可以为空
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_setting,container,false);
        passwordModifyButton = (Button)view.findViewById(R.id.password_modify_button);
        passwordModifyButton.setEnabled(true);
        passwordModifyButton.setOnClickListener(this);

        new_password = (EditText) view.findViewById(R.id.new_password);
        verify_password=(EditText) view.findViewById(R.id.verify_password);
        old_password = (EditText) view.findViewById(R.id.prime_password);

        SharedPreferences sp = getActivity().getSharedPreferences("currentSession",Context.MODE_PRIVATE);
        account = sp.getString("account","111");
        networkListener();
        return view;
    }
    @Override
    public void onClick(View v){
        String newPassword = new_password.getText().toString();
        String verifyPassword = verify_password.getText().toString();
        password = old_password.getText().toString();
        switch (v.getId()){
            case R.id.password_modify_button:
                if (newPassword==null || verifyPassword==null){
                    Toast.makeText(getActivity(),"密码输入不正确",Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(newPassword) || "".equals(verifyPassword) || "".equals(password)){
                    Toast.makeText(getActivity(),"密码输入不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPassword.equals(verifyPassword)) {
                    if (isNetworkAvailable) {
                        //处理页面生成时加载数据
                        JSONObject conditionJson = new JSONObject();
                        try {
                            conditionJson.put("account", account);
                            conditionJson.put("oldPassword",old_password.getText().toString());
                            conditionJson.put("newPassword", newPassword);
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                        requestState = conditionJson.toString();
                        urlString3 = UrlManager.createUrlString("/merchant/modifyPassword.action");
                        new HttpTask(requestState, urlString3).execute();
                        passwordModifyButton.setEnabled(false);
                    } else {
                        Toast.makeText(getActivity(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
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
                    if ("1".equals(res)){
                        Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), "旧密码不正确", Toast.LENGTH_SHORT).show();
                    }
                    passwordModifyButton.setEnabled(true);
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

}