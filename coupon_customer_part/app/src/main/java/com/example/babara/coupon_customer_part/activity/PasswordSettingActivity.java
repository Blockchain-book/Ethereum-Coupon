package com.example.babara.coupon_customer_part.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.utils.AsyncResponse;
import com.example.babara.coupon_customer_part.utils.HttpTaskForJsonTool;
import com.example.babara.coupon_customer_part.utils.UrlManager;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class PasswordSettingActivity extends Activity implements View.OnClickListener{

    private ImageView backImageView;
    private EditText oldPasswordEdit;
    private EditText newPasswordEdit;
    private EditText reNewpasswordEdit;
    private Button button ;
    private EditText registerCodeEditText;  //验证码
    private Button verifyCodeButton;        //获取验证码按钮
    private String mPhoneNums;
    private int i = 30;
    private boolean isCodeRight;
    private EditText registerNoEditText;    //手机号
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_password_setting);

        backImageView = (ImageView)findViewById(R.id.password_back);
        oldPasswordEdit = (EditText)findViewById(R.id.old_password);
        newPasswordEdit = (EditText)findViewById(R.id.new_password);
        reNewpasswordEdit = (EditText)findViewById(R.id.verify_password);
        button = (Button)findViewById(R.id.password_modify_button);
        registerCodeEditText = (EditText) findViewById(R.id.register_verify_code);
        verifyCodeButton = (Button) findViewById(R.id.obtain_verify_code_button);
        registerNoEditText = (EditText) findViewById(R.id.register_mobile_no);
        SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        String mobileNo = sp.getString("mobileNo","");
        registerNoEditText.setText(mobileNo);
        registerNoEditText.setEnabled(false);

        backImageView.setOnClickListener(this);
        button.setOnClickListener(this);
        verifyCodeButton.setOnClickListener(this);


        //启动短信验证
        SMSSDK.initSDK(PasswordSettingActivity.this, "1a2a53325c010", "948025918fac4f8907e97ddd85dad3bd");
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler); // 注册回调监听接口
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.password_back:
                this.finish();
                break;
            case R.id.password_modify_button:
                //提交用户输入的验证码，在后台进行验证，并在监听中返回
                SMSSDK.submitVerificationCode("86", mPhoneNums, registerCodeEditText.getText().toString());
                if (isCodeRight) {
                    tempModifyPassword();
                }
                break;
            case R.id.obtain_verify_code_button:
                mPhoneNums = registerNoEditText.getText().toString().trim();
                if (!isMobileNoValid(mPhoneNums)) {// 判断输入号码是否正确
                    Toast.makeText(PasswordSettingActivity.this,"手机格式不正确",Toast.LENGTH_SHORT).show();
                    return;
                }

                SMSSDK.getVerificationCode("86", mPhoneNums); // 调用sdk发送短信验证
                verifyCodeButton.setClickable(false);// 设置按钮不可点击 显示倒计时
                verifyCodeButton.setText("重新发送(" + i + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (i = 30; i > 0; i--) {
                            handler.sendEmptyMessage(-9);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);// 线程休眠实现读秒功能
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);// 在30秒后重新显示为获取验证码
                    }
                }).start();
                break;
        }
    }

    public void tempModifyPassword(){
        String oldPassword = oldPasswordEdit.getText().toString();
        String newPassword = newPasswordEdit.getText().toString();
        String reNewPassword = reNewpasswordEdit.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(oldPassword) || !isPasswordValid(oldPassword)) {
            oldPasswordEdit.setError("请输入四位以上原密码");
            focusView = oldPasswordEdit;
            cancel = true;
        }

        // Check for a valid email address.
        else if (TextUtils.isEmpty(newPassword) || !isPasswordValid(newPassword)) {
            newPasswordEdit.setError("请输入四位以上新密码");
            focusView = newPasswordEdit;
            cancel = true;
        }

        else if (TextUtils.isEmpty(reNewPassword)) {
            reNewpasswordEdit.setError("确认密码不能为空");
            focusView = reNewpasswordEdit;
            cancel = true;
        }

        else if(!newPassword.equals(reNewPassword)) {
            reNewpasswordEdit.setError("两次输入新密码不一致，请重新输入");
            focusView = reNewpasswordEdit;
            cancel = true;
        }

        else if(TextUtils.isEmpty(registerNoEditText.getText().toString())) {
            reNewpasswordEdit.setError("手机号不能为空");
            focusView = reNewpasswordEdit;
            cancel = true;
        }

        else if(TextUtils.isEmpty(registerCodeEditText.getText().toString())) {
            reNewpasswordEdit.setError("验证码不能为空");
            focusView = reNewpasswordEdit;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            String mobileNo = registerNoEditText.getText().toString();
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("account",mobileNo);
                jsonObject.put("oldPassword",oldPassword);
                jsonObject.put("newPassword",newPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String requestState = jsonObject.toString();

           // String requestString = "oldPassword="+oldPassword+"&newPassword="+newPassword;
            String url = UrlManager.createUrlString("/consumer/modifyPassword.action");
            HttpTaskForJsonTool task = new HttpTaskForJsonTool(requestState,url);
            task.execute();
            button.setEnabled(false);
            task.setOnAsyncResponse(new AsyncResponse() {
                @Override
                public void onDataReceivedSuccess(String data) {
                    if (data!=null && !"".equals(data)){
                        try {
                            JSONObject result = new JSONObject(data);
                            String res = result.getString("resultCode");
                            if ("0".equals(res)){
                                Toast.makeText(PasswordSettingActivity.this,"旧密码不正确",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(PasswordSettingActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    button.setEnabled(true);
                }
            });
        }

        mPhoneNums = registerNoEditText.getText().toString().trim();
        if (!isMobileNoValid(mPhoneNums)) {// 判断输入号码是否正确
            Toast.makeText(PasswordSettingActivity.this,"手机格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isMobileNoValid(String mobileNo) {
        //验证手机格式
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNo)) {
            Toast.makeText(PasswordSettingActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return mobileNo.matches(telRegex);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                verifyCodeButton.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                verifyCodeButton.setText("获取验证码");
                verifyCodeButton.setClickable(true); // 设置可点击
                i = 30;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
//                        Toast.makeText(getApplicationContext(), "提交验证码成功",
//                                Toast.LENGTH_SHORT).show();
                        // 验证成功后跳转主界面
                        //此处可注释掉
                        isCodeRight = true;
                        tempModifyPassword();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };

}
