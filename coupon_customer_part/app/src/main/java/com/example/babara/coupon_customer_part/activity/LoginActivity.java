package com.example.babara.coupon_customer_part.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.utils.UrlManager;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Description:该类主要实现用户登录功能。
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private boolean hasLogined = false;
    private static final int REQUEST_READ_CONTACTS = 0;
    private AutoCompleteTextView mMobieNoView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mLoginButton;
    private Button mRegisterButton;
    private final int GETVALUE = 0,POSTVALUE = 1;
    private String id;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        mMobieNoView = (AutoCompleteTextView) findViewById(R.id.mobile_no);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginButton = (Button)findViewById(R.id.login_button);
        mRegisterButton = (Button)findViewById(R.id.register_button);

        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_button:
                //设置登录按钮点击事件
                attemptLogin();
                break;

            case R.id.register_button:
                //设置点击注册按钮，跳转到RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    //设置处理信息的handler
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETVALUE:
                    try {
                        msg.obj = URLDecoder.decode(msg.obj + "", "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    //请求用户以获得读取账户的权限
//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mMobieNoView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }




    //初步判断用户名密码的合法性，并且给出错误的提示
    private void attemptLogin() {
        // Reset errors.
        mMobieNoView.setError(null);
        mPasswordView.setError(null);

        String mobileNo = mMobieNoView.getText().toString();
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
            String requestString = "account="+mobileNo+"&password="+password;
            String urlString = new UrlManager().createUrlString("/consumer/login.action");

            new HttpTask(requestString, urlString).execute();
            mLoginButton.setEnabled(false);
        }
    }

    public void recordLoginedState(){
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("mobileNo", mMobieNoView.getText().toString());
        editor.putBoolean("hasLogined", true);
        editor.putString("consumerId",id);
        editor.commit();
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
     * @param msg
     */
    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setCancelable(false).setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
            if(result!=null && !"".equals(result)){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.getString("resultCode");
                    if("1".equals(res)){
                        id = jsonObject.getString("id");
                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("id", 1);
                        startActivity(intent);
                        recordLoginedState();
                    }else{
                        Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mLoginButton.setEnabled(true);
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

