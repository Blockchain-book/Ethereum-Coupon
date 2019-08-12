package com.creation.coupon_merchant_part.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.creation.coupon_merchant_part.R;

public class NoNetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_net);
        networkListener();
    }

    //网络监听
    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                NoNetActivity.this.finish();
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
        registerReceiver(networkChangeReceiver, intentFilter);
    }
}
