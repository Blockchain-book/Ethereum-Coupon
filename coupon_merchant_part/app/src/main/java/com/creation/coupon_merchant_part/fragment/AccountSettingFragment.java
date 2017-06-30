package com.creation.coupon_merchant_part.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.activity.CouponStateActivity;
import com.creation.coupon_merchant_part.activity.NoNetActivity;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Babara Liang on 2016/12/26.
 * Description:该类实现“我的->我的设置->银行账户”的设置功能。
 * 系统从服务器中获取银行账户的相关信息，并加以展示
 */

public class AccountSettingFragment extends Fragment {
    private TextView merchantNumber;
    private TextView merchantName;
    private TextView merchantBankNum;
    private TextView merchantBankName;
    private String merchantId;
    public AccountSettingFragment(){
    }
    //@Nullable表示定义的字段可以为空
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_setting,container,false);
        merchantNumber = (TextView)view.findViewById(R.id.merchant_number);
        merchantName = (TextView)view.findViewById(R.id.merchant_name);
        merchantBankNum = (TextView)view.findViewById(R.id.merchant_bank_number);
        merchantBankName = (TextView)view.findViewById(R.id.merchant_bank_name);

        return view;

    }
    public void showInfo(){
        SharedPreferences sp = getActivity().getSharedPreferences("currentSession",Context.MODE_PRIVATE);
//        merchantId = sp.getString("merchantId",null);
        merchantId = sp.getString("merchantId","111");
        String url = UrlManager.createUrlString("/merchant/queryMerchantInfo.action");
        HttpTaskTool htt = new HttpTaskTool("merchantId="+merchantId, url);
        htt.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                try {
                    JSONObject jo1 = new JSONObject(data);
                    merchantNumber.setText(jo1.get("id").toString());
                    merchantName.setText(jo1.get("name").toString());
                    merchantBankName.setText(jo1.get("bankName").toString());
                    merchantBankNum.setText(jo1.get("bankNumber").toString());
                }catch(JSONException je){
                    je.printStackTrace();
                }

            }
        };
        htt.execute();
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectivityManager = (ConnectivityManager)(getActivity().getSystemService(Context.CONNECTIVITY_SERVICE));
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                showInfo();
            }
            else{
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void networkListener(){
        //每当网络状态发生变化时，系统会发出"android.net.conn.CONNECTIVITY_CHANGE"的广播
        //要给intentFilter添加这样一个action，所以想要监听什么样的广播，就添加相应的action
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        getActivity().registerReceiver(networkChangeReceiver, intentFilter);
    }

}