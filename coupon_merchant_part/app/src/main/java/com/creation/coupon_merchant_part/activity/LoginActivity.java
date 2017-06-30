package com.creation.coupon_merchant_part.activity;

import android.annotation.TargetApi;
import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.creation.coupon_merchant_part.R;

import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.HttpTaskForJsonTool;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;


import static android.Manifest.permission.READ_CONTACTS;
import static android.R.id.content;
import static android.R.id.switch_widget;

/**
 * Description:该类主要实现用户登录功能。
 */
public class LoginActivity extends Activity implements OnClickListener {

    private boolean hasLogined = false;
    private static final int REQUEST_READ_CONTACTS = 0;
    private AutoCompleteTextView mMobieNoView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mLoginButton;
    private Button mRegisterButton;
    private final int GETVALUE = 0, POSTVALUE = 1;
    private String account;
    private LinearLayout inNetLinearLayout;
    private LinearLayout noNetLinearLayout;
    private IntentFilter intentFilter;
    private ImageView backImage;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //启动定位服务
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        mMobieNoView = (AutoCompleteTextView) findViewById(R.id.mobile_no);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setEnabled(true);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        inNetLinearLayout = (LinearLayout) findViewById(R.id.state_in_net);
        noNetLinearLayout = (LinearLayout) findViewById(R.id.state_no_net);
        backImage = (ImageView)findViewById(R.id.login_back_image);

        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        backImage.setOnClickListener(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    networkListener();
                    return true;
                }
                return false;
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                //设置登录按钮点击事件
                mLoginButton.setEnabled(true);
                attemptLogin();
                break;

            case R.id.register_button:
                //设置点击注册按钮，跳转到RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.login_back_image:
                this.finish();
                break;
        }
    }


    //请求用户以获得读取账户的权限
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mMobieNoView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    //初步判断用户名密码的合法性，并且给出错误的提示
    private void attemptLogin() {
        // Reset errors.
        mMobieNoView.setError(null);
        mPasswordView.setError(null);

        String mobileNo = mMobieNoView.getText().toString();
        account = mMobieNoView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mobileNo)) {
            mMobieNoView.setError(getString(R.string.error_field_required));
            focusView = mMobieNoView;
            cancel = true;
        } else if (!isMobileNoValid(mobileNo)) {
            mMobieNoView.setError(getString(R.string.error_invalid_phone));
            focusView = mMobieNoView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            JSONObject jo = new JSONObject();
            try {
                jo.put("account", mobileNo);
                jo.put("password", password);
            } catch (JSONException je) {
                je.printStackTrace();
            }
            String requestString = jo.toString();
            String urlString = new UrlManager().createUrlString("/merchant/login.action");
            LoginHttpTask loginHttpTask = new LoginHttpTask(requestString, urlString);
            loginHttpTask.execute();

//            HttpTaskForJsonTool httpTaskTool = new HttpTaskForJsonTool(requestString, urlString);
//            httpTaskTool.setOnAsyncResponse(new AsyncResponse() {
//                //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
//                @Override
//                public void onDataReceivedSuccess(String data) {
//                    if (!data.equals("") && data != null) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(data);
//                            if (jsonObject.getString("resultCode").equals("1")) {
////                                Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
//                                SharedPreferences.Editor editor = getSharedPreferences("currentSession", MODE_PRIVATE).edit();
//                                editor.putString("merchantId", jsonObject.getString("id"));
//                                editor.putString("account", account);
//                                if (jsonObject.has("reach") && jsonObject.has("give")) {
//                                    editor.putInt("curRulerReach", Integer.parseInt(jsonObject.getString("reach")));
//                                    editor.putInt("curRulerSend", Integer.parseInt(jsonObject.getString("give")));
//                                }
//                                editor.putBoolean("hasLogined", true);
//                                editor.commit();
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            } else if (jsonObject.getString("resultCode").equals("0")) {
//                                showDialog("0");
//                            } else if (jsonObject.getString("resultCode").equals("-1")) {
//                                showDialog("-1");
//                            } else if (jsonObject.getString("resultCode").equals("-2")) {
//                                showDialog("-2");
//                            } else if (jsonObject.getString("resultCode").equals("-3")) {
//                                showDialog("-3");
//                            }
//                        } catch (JSONException je) {
//                            je.printStackTrace();
//                        }
//                    }
//                    mLoginButton.setEnabled(true);
//                }
//            });
//            httpTaskTool.execute();
        }
    }

    private boolean isMobileNoValid(String mobileNo) {
        //验证手机格式
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNo))
            return false;
        else
            return mobileNo.matches(telRegex);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * 自定义一个消息提示窗口
     *
     * @param mark
     */
    private void showDialog(String mark) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        switch (mark) {
            case "0":
                builder.setMessage("用户名不存在，请先注册").setCancelable(false).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                break;
            case "-1":
                builder.setMessage("系统错误,请稍后重试").setCancelable(false).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                break;
            case "-2":
                builder.setMessage("密码错误，请确认后重试").setCancelable(false).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                break;
            default:
                builder.setMessage("审核中，请稍后重试").setCancelable(false).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                break;
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void networkListener(){
        //每当网络状态发生变化时，系统会发出"android.net.conn.CONNECTIVITY_CHANGE"的广播
        //要给intentFilter添加这样一个action，所以想要监听什么样的广播，就添加相应的action
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangeReceiver networkChangeReceiver = new LoginActivity.NetworkChangeReceiver();
        //调用 registerReceiver()进行注册，NetworkChangeReceiver()会收到
        // 所有值为"android.net.conn.CONNECTIVITY_CHANGE"的广播
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                //处理页面生成时加载数据
                attemptLogin();
                inNetLinearLayout.setVisibility(View.VISIBLE);
                noNetLinearLayout.setVisibility(View.GONE);
            } else {
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
                noNetLinearLayout.setVisibility(View.VISIBLE);
                inNetLinearLayout.setVisibility(View.GONE);

                //跳转到无网络页面
                Intent intentNoNet = new Intent(LoginActivity.this, NoNetActivity.class);
                startActivity(intentNoNet);
            }
        }
    }


    class LoginHttpTask extends AsyncTask<Void, Void, String> {
        private String urlString;
        private String conditionString;
        public AsyncResponse asyncResponse;
        public LoginHttpTask(String conditionString,String urlString){
            this.conditionString = conditionString;
            this.urlString = urlString;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        //在这里使用AsyncResponse接口来实现获取doInBackground的执行结果
        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            if (!data.equals("") && data != null) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.getString("resultCode").equals("1")) {
//                                Toast.makeText(LoginActivity.this, data, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences("currentSession", MODE_PRIVATE).edit();
                        editor.putString("merchantId", jsonObject.getString("id"));
                        editor.putString("account", account);
                        if (jsonObject.has("reach") && jsonObject.has("give")) {
                            editor.putInt("curRulerReach", Integer.parseInt(jsonObject.getString("reach")));
                            editor.putInt("curRulerSend", Integer.parseInt(jsonObject.getString("give")));
                        }
                        editor.putBoolean("hasLogined", true);
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if (jsonObject.getString("resultCode").equals("0")) {
                        showDialog("0");
                    } else if (jsonObject.getString("resultCode").equals("-1")) {
                        showDialog("-1");
                    } else if (jsonObject.getString("resultCode").equals("-2")) {
                        showDialog("-2");
                    } else if (jsonObject.getString("resultCode").equals("-3")) {
                        showDialog("-3");
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
            mLoginButton.setEnabled(true);

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
}

