package com.creation.coupon_merchant_part.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.coupon_merchant_part.activity.CapitalAccountActivity;
import com.creation.coupon_merchant_part.activity.CouponStateActivity;
import com.creation.coupon_merchant_part.activity.HelpActivity;
import com.creation.coupon_merchant_part.activity.LoginActivity;
import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.activity.MySettingsActivity;
import com.creation.coupon_merchant_part.activity.QRCodeActivity;
import com.creation.coupon_merchant_part.pojo.BillItem;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Babara Liang on 2016/12/26.
 * Description:该类主要实现我的页面中四个功能的展示，以及四个功能的点击事件。
 */

public class MyFragment extends Fragment implements View.OnClickListener{
    private TextView loginText;             //页面最上方的“登录”按钮
    private TextView capitalAcountText;     //“资金账户”按钮
    private TextView QRCodeText;            //“商户二维码”按钮
    private TextView mySettingsText;        //“我的设置”按钮
    private TextView helpText;              //“帮助”按钮


    public  MyFragment(){
    }
    //@Nullable表示定义的字段可以为空
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,container,false);
        loginText = (TextView) view.findViewById(R.id.login_text);
        mySettingsText = (TextView)view.findViewById(R.id.my_settings_text);
        QRCodeText = (TextView)view.findViewById(R.id.qr_code_text);
        helpText = (TextView)view.findViewById(R.id.help_text);
        capitalAcountText = (TextView)view.findViewById(R.id.capital_account_text);


        mySettingsText.setOnClickListener(this);
        QRCodeText.setOnClickListener(this);
        helpText.setOnClickListener(this);
        capitalAcountText.setOnClickListener(this);
        //从SharedPreferences取出登录状态，如果登录成功，则显示号码。
        SharedPreferences read = getActivity().getSharedPreferences("currentSession", MODE_PRIVATE);
        String mobileNo = read.getString("account", "");
        boolean hasLogined = read.getBoolean("hasLogined", false);
        String s = String.valueOf(hasLogined);

        if(!hasLogined) {
            loginText.setOnClickListener(this);
        } else {
            loginText.setText(mobileNo);
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
            case R.id.capital_account_text:
                Intent intent2 = new Intent(getActivity(), CapitalAccountActivity.class);
                startActivity(intent2);
                break;
            case R.id.qr_code_text:
                //点击商户二维码按钮，实现页面跳转
                Intent toQRCode = new Intent(getActivity(), QRCodeActivity.class);
                startActivity(toQRCode);
                break;
            case R.id.my_settings_text:
                //点击我的设置按钮，实现页面跳转
                Intent toMySettings = new Intent(getActivity(), MySettingsActivity.class);
                startActivity(toMySettings);
                break;
            case R.id.help_text:
                //点击帮助按钮，实现页面跳转
                SharedPreferences sp = getActivity().getSharedPreferences("currentSession", Context.MODE_PRIVATE);
                sp.edit().clear().commit();
                if ("".equals(sp.getString("merchantId",""))){
                    Intent toHelp = new Intent(getActivity(), LoginActivity.class);
                    startActivity(toHelp);
                }
                break;
        }
    }
}