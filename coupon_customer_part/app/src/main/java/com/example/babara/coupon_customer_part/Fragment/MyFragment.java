package com.example.babara.coupon_customer_part.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.creation.coupon_merchant_part.R;
//import com.creation.coupon_merchant_part.activity.CapitalAccountActivity;
//import com.creation.coupon_merchant_part.activity.HelpActivity;
//import com.creation.coupon_merchant_part.activity.LoginActivity;
//import com.creation.coupon_merchant_part.activity.MySettingsActivity;
//import com.creation.coupon_merchant_part.activity.QRCodeActivity;
import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.activity.HelpActivity;
import com.example.babara.coupon_customer_part.activity.LoginActivity;
import com.example.babara.coupon_customer_part.activity.MyAccountActivity;
import com.example.babara.coupon_customer_part.activity.PasswordSettingActivity;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Babara Liang on 2016/12/26.
 * Description:该类主要实现我的页面中四个功能的展示，以及四个功能的点击事件。
 */

public class MyFragment extends Fragment implements View.OnClickListener{
    private TextView loginText;             //页面最上方的“登录”按钮
    private TextView myAcountText;     //“资金账户”按钮
    private TextView passwordSettingsText;        //“我的设置”按钮
    private TextView helpText;              //“帮助”按钮
    boolean hasLogined;


    public MyFragment(){
    }
    //@Nullable表示定义的字段可以为空
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,container,false);
        loginText = (TextView) view.findViewById(R.id.login_text);
        passwordSettingsText = (TextView)view.findViewById(R.id.password_settings_text);
        helpText = (TextView)view.findViewById(R.id.help_text);
        myAcountText = (TextView)view.findViewById(R.id.my_account_text);


        passwordSettingsText.setOnClickListener(this);
        helpText.setOnClickListener(this);
        myAcountText.setOnClickListener(this);
        //从SharedPreferences取出登录状态，如果登录成功，则显示号码。
        SharedPreferences read = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        String mobileNo = read.getString("mobileNo", "");
        hasLogined = read.getBoolean("hasLogined", false);
        if (hasLogined){
            Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_LONG).show();
            loginText.setText(mobileNo);
        }
        else{
            loginText.setOnClickListener(this);
        }
        return view;
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_text:
                //点击登录按钮,实现页面跳转
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.my_account_text:
                if(hasLogined){
                    Intent intent2 = new Intent(getActivity(), MyAccountActivity.class);
                    startActivity(intent2);
                }
                else {
                    Toast.makeText(getActivity(), "您还未登录， 请先登录", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.password_settings_text:
                if(hasLogined){
                    Intent intent2 = new Intent(getActivity(), PasswordSettingActivity.class);
                    startActivity(intent2);
                }
                else {
                    Toast.makeText(getActivity(), "您还未登录， 请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.help_text:
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putBoolean("hasLogined", false);
                editor.commit();
                Toast.makeText(getActivity(), "您已退出登录", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(getActivity(), LoginActivity.class);
                intent3.putExtra("id", 1);
                startActivity(intent3);
                break;
        }
    }
}