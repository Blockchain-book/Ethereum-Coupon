package com.creation.coupon_merchant_part.activity;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.view.Window;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.HttpTaskForJsonTool;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description:该类主要实现注册功能。
 * 用户将所注册的商户信息提交给服务器，以给银行方面审核。
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    private ImageView backImageView;            //返回按钮
    private EditText mRegisterMobileNo;         //手机号输入框
    private EditText mRegisterVerifyCode;       //验证码输入框
    private Button mObtainVerifyCodeButton;     //获取验证码按钮
    private Button mRegisterButton;             //注册按钮,没有用上
    private CheckBox mServiceCheckBox;          //同意条款的单选框
    private EditText mPassword;                 //密码输入框
    private EditText mRepeatPassword;           //密码再次输入框
    private EditText mName;                     //商户名称
    private EditText mBussinessSalesNum;        //商户营业直销号
    private EditText mLegalPersonName;          //商户法人姓名
    private EditText mBussinessScope;           //商户经营范围
    private EditText mBussinessAddress;         //商户地址
    private String isCodeRight;
    private Handler uiHandler;

    private String registerMobileNo;
    private String password;
    private String repeatPassword;
    private String name;
    private String businessSalesNum;
    private String legalPersonName;
    private String businessScope;
    private String businessAddress;

    //设置重新发送验证码的时间为30s
    int i = 30;
    private String mPhoneNums;//用于记录用户输入的手机号码
    private String mInputCode;//用于记录用户输入的验证码


    private String latitude;
    private String longitude;
    private LocationClient mLocationClient;
    private BDLocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        backImageView = (ImageView)findViewById(R.id.register_back);
        mRegisterMobileNo = (EditText) findViewById(R.id.register_mobile_no);
        mRegisterVerifyCode = (EditText)findViewById(R.id.register_verify_code);
        mObtainVerifyCodeButton = (Button) findViewById(R.id.obtain_verify_code_button);
        mRegisterButton = (Button)findViewById(R.id.register_button);
        mPassword = (EditText)findViewById(R.id.set_password);
        mRepeatPassword = (EditText)findViewById(R.id.set_password_again);
        mName = (EditText)findViewById(R.id.input_name);
        mBussinessSalesNum = (EditText)findViewById(R.id.input_license_no);
        mLegalPersonName = (EditText)findViewById(R.id.input_legal_person_name);
        mBussinessScope = (EditText)findViewById(R.id.input_business_scope);
        mBussinessAddress = (EditText)findViewById(R.id.input_business_address);

        mServiceCheckBox = (CheckBox)findViewById(R.id.service_checkbox);
        mServiceCheckBox.setOnClickListener(listener);//设置监听事件
        backImageView.setOnClickListener(this);
        mRegisterButton.setEnabled(false);//注册按钮默认为不可点击

        //启动短信验证
        SMSSDK.initSDK(RegisterActivity.this, "1a2a53325c010", "948025918fac4f8907e97ddd85dad3bd");

        mObtainVerifyCodeButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);

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

        initLocationListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationClient.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationClient.stop();
    }

    private void initLocationListener() {
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        uiHandler = new Handler();
        mLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                int locType = bdLocation.getLocType();
                switch(locType){
                    case BDLocation.TypeServerError:
                        Toast.makeText(getApplicationContext(),"服务端网络定位失败",Toast.LENGTH_SHORT).show();
                        break;
                    case BDLocation.TypeNetWorkException:
                        Toast.makeText(getApplicationContext(),"请检查网络是否畅通",Toast.LENGTH_SHORT).show();
                        break;
                    case BDLocation.TypeCriteriaException:
                        Toast.makeText(getApplicationContext(),"请检查是否处于飞行模式或重启手机",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        latitude = bdLocation.getLatitude()+"";
                        longitude = bdLocation.getLongitude()+"";
                        businessAddress = bdLocation.getAddrStr();
                        new Thread(){
                            public void run(){
                                uiHandler.post(runnableUi);
                            }
                        }.start();

                        break;
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        mLocationClient.registerLocationListener(mLocationListener);
    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            mBussinessAddress.setText(businessAddress);
            mBussinessAddress.setEnabled(false);
        }

    };

    private void initLocation() {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClientOption.setCoorType("gcj02");
        locationClientOption.setScanSpan(0);
        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setOpenGps(true);
        locationClientOption.setIsNeedLocationDescribe(false); //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationClientOption.setIgnoreKillProcess(true); //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationClientOption.SetIgnoreCacheException(false);
        locationClientOption.setEnableSimulateGps(true);
        mLocationClient.setLocOption(locationClientOption);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_back:
                this.finish();
                break;
            case R.id.obtain_verify_code_button:
                mPhoneNums = mRegisterMobileNo.getText().toString().trim();
                if (!judgePhoneNums(mPhoneNums)) {// 判断输入号码是否正确
                    return;
                }

                SMSSDK.getVerificationCode("86", mPhoneNums); // 调用sdk发送短信验证
                mObtainVerifyCodeButton.setClickable(false);// 设置按钮不可点击 显示倒计时
                mObtainVerifyCodeButton.setText("重新发送(" + i + ")");
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

            case R.id.register_button:
                registerMobileNo = mRegisterMobileNo.getText().toString();
                password = mPassword.getText().toString();
                repeatPassword = mRepeatPassword.getText().toString();
                mInputCode = mRegisterVerifyCode.getText().toString().trim();
                name = mName.getText().toString();
                businessSalesNum = mBussinessSalesNum.getText().toString();
                legalPersonName = mLegalPersonName.getText().toString();
                businessScope = mBussinessScope.getText().toString();
                businessAddress = mBussinessAddress.getText().toString();
                if ("".equals(registerMobileNo) || "".equals(password) || "".equals(repeatPassword) || "".equals(mInputCode)
                        || "".equals(name) || "".equals(businessSalesNum) || "".equals(legalPersonName) || "".equals(businessScope)
                        || "".equals(businessAddress)){
                    Toast.makeText(RegisterActivity.this, "请补全注册信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(repeatPassword)){
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (latitude == null || longitude == null){
                    Toast.makeText(RegisterActivity.this, "请等待定位成功", Toast.LENGTH_SHORT).show();
                    return;
                }
                //提交用户输入的验证码，在后台进行验证，并在监听中返回
                SMSSDK.submitVerificationCode("86", mPhoneNums, mInputCode);
                mRegisterButton.setEnabled(false);
               // createProgressBar();
                break;

        }
    }
    //此函数用于判断用户输入的手机号码的合法性
    public boolean judgePhoneNums(String numsString){
        //验证手机格式
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(numsString))
            return false;
        else
            return numsString.matches(telRegex);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                mObtainVerifyCodeButton.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                mObtainVerifyCodeButton.setText("获取验证码");
                mObtainVerifyCodeButton.setClickable(true); // 设置可点击
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
                        insertInfoToDatabase();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "ok ",Toast.LENGTH_SHORT).show();
                }
                mRegisterButton.setEnabled(true);
            }
        }
    };

    private void insertInfoToDatabase() {
        final String registerUrl = UrlManager.createUrlString("/merchant/register.action");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("account",registerMobileNo);
            jsonObject.put("password",password);
            jsonObject.put("name",name);
            jsonObject.put("licence",businessSalesNum);
            jsonObject.put("legalEntityName",legalPersonName);
            jsonObject.put("businessScope",businessScope);
            jsonObject.put("address",businessAddress);
            jsonObject.put("latitude",latitude);
            jsonObject.put("longitude",longitude);
            String requestState = jsonObject.toString();
            HttpTaskForJsonTool httpTask = new HttpTaskForJsonTool(requestState,registerUrl);
            httpTask.execute();
            httpTask.setOnAsyncResponse(new AsyncResponse() {
                @Override
                public void onDataReceivedSuccess(String data) {
                    if (data!=null && !"".equals(data)){
                        try {
                            JSONObject resultJson = new JSONObject(data);
                            String res = resultJson.getString("resultCode");
                            if ("0".equals(res)){
                                Toast.makeText(getApplicationContext(),"注册失败，请重新注册",Toast.LENGTH_SHORT).show();
                            }else if ("1".equals(res)){
                                Toast.makeText(getApplicationContext(),"注册成功，请等待审核通过",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("flag", "newuser");
                                startActivity(intent);
                                Log.e("注册", "成功！");
                                finish();// 成功跳转之后销毁当前页面
                            }else if ("2".equals(res)){
                                Toast.makeText(getApplicationContext(),"该用户正在审核中，请等待",Toast.LENGTH_SHORT).show();
                            }else if ("3".equals(res)){
                                Toast.makeText(getApplicationContext(),"该账户已存在",Toast.LENGTH_SHORT).show();
                            }
                            mRegisterButton.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //监听事件，如果服务条款被选中的话，将注册按钮释放，否则注册按钮不可点击
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mServiceCheckBox.isChecked()){
                mRegisterButton.setEnabled(true);
            }
            else {
                mRegisterButton.setEnabled(false);
            }
        }
    };
}
