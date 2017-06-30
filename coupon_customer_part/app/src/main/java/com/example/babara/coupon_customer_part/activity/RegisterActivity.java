package com.example.babara.coupon_customer_part.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.utils.AsyncResponse;
import com.example.babara.coupon_customer_part.utils.HttpTaskForJsonTool;
import com.example.babara.coupon_customer_part.utils.UrlManager;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private ImageView backImageView;        //返回按钮
    private EditText registerNoEditText;    //手机号
    private EditText registerCodeEditText;  //验证码
    private EditText passwordEditText;      //密码
    private EditText passwordRepeatEditText;//二次输入密码
    private Button registerButton;          //注册按钮
    private Button verifyCodeButton;        //获取验证码按钮
    private CheckBox serviceCheckBox;       //服务同意框
    private String mPhoneNums;
    private int i = 30;
    private boolean isCodeRight;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        registerNoEditText = (EditText) findViewById(R.id.register_mobile_no);
        registerCodeEditText = (EditText) findViewById(R.id.register_verify_code);
        passwordEditText = (EditText) findViewById(R.id.register_password);
        passwordRepeatEditText = (EditText) findViewById(R.id.register_repeat_password);
        backImageView = (ImageView) findViewById(R.id.register_back);
        registerButton = (Button) findViewById(R.id.register_button);
        verifyCodeButton = (Button) findViewById(R.id.obtain_verify_code_button);
        serviceCheckBox = (CheckBox)findViewById(R.id.service_checkbox);

        backImageView.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        verifyCodeButton.setOnClickListener(this);
        serviceCheckBox.setOnClickListener(listener);

        registerButton.setEnabled(false);

        //启动短信验证
        SMSSDK.initSDK(RegisterActivity.this, "1a2a53325c010", "948025918fac4f8907e97ddd85dad3bd");
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_back:
                this.finish();
                break;
            case R.id.register_button:
                if ("".equals(registerNoEditText.getText().toString()) || "".equals(passwordEditText.getText().toString())
                        || "".equals(passwordRepeatEditText.getText().toString()) || "".equals(registerCodeEditText.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "请补全注册信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passwordEditText.getText().toString().equals(passwordRepeatEditText.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                //提交用户输入的验证码，在后台进行验证，并在监听中返回
                SMSSDK.submitVerificationCode("86", mPhoneNums, registerCodeEditText.getText().toString());
                if (isCodeRight) {
                    register();
                }
                break;
            case R.id.obtain_verify_code_button:
                mPhoneNums = registerNoEditText.getText().toString().trim();
                if (!isMobileNoValid(mPhoneNums)) {// 判断输入号码是否正确
                    Toast.makeText(RegisterActivity.this,"手机格式不正确",Toast.LENGTH_SHORT).show();
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

    private void register() {
        String mobileNo = registerNoEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String url = UrlManager.createUrlString("/consumer/register.action");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account",mobileNo);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String requestState = jsonObject.toString();
        HttpTaskForJsonTool task = new HttpTaskForJsonTool(requestState,url);
        task.execute();
        registerButton.setEnabled(false);
        task.setOnAsyncResponse(new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"".equals(data)){
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String res = jsonObject.getString("resultCode");
                        if ("0".equals(res)){
                            Toast.makeText(RegisterActivity.this,"用户名已存在",Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                registerButton.setEnabled(true);
            }
        });
    }

    private boolean isMobileNoValid(String mobileNo) {
        //验证手机格式
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNo)) {
            Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
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
                        register();
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

    //监听事件，如果服务条款被选中的话，将注册按钮释放，否则注册按钮不可点击
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(serviceCheckBox.isChecked()){
                registerButton.setEnabled(true);
            }
            else {
                registerButton.setEnabled(false);
            }
        }
    };
}
