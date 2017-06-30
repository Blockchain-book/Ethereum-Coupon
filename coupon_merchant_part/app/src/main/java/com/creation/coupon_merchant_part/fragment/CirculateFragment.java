package com.creation.coupon_merchant_part.fragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.CouponRequestItem;
import com.creation.coupon_merchant_part.pojo.UrlManager;

import java.util.ArrayList;
/**
 * Created by Babara Liang on 2016/12/26.
 * Description:该方法主要实现流通页面上顶部导航栏，及他们的点击事件。
 */

public class CirculateFragment extends Fragment implements View.OnClickListener{
    public  CirculateFragment(){
    }
    private TextView couponSendText;                //“优惠券发放”按钮
    private TextView couponVerifyText;              //“优惠券验证”按钮
    private FragmentManager fragmentManager;
    private CirculateChild1Fragment f1;           //按发放按钮，加载的fragment
    private CirculateChild2Fragment f2;           //按验证按钮，加载的fragment
    private LinearLayout inNet1;
    private FrameLayout inNet2;
    private LinearLayout noNet3;
    //@Nullable表示定义的字段可以为空
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circulate,container,false);
        couponSendText = (TextView)view.findViewById(R.id.coupon_send_text);
        couponVerifyText = (TextView)view.findViewById(R.id.coupon_verify_text);
        inNet1 = (LinearLayout)view.findViewById(R.id.circulate_in_net1);
        inNet2 = (FrameLayout) view.findViewById(R.id.fragment_circulate_container);
        noNet3 = (LinearLayout)view.findViewById(R.id.state_no_net);
        networkListener();
        couponSendText.setOnClickListener(this);
        couponVerifyText.setOnClickListener(this);
        if(isNetworkAvailable()){
            couponSendText.performClick();
        }
        return view;
    }
    @Override
    public void onClick(View v){
        fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch (v.getId()){
            case R.id.coupon_send_text:
                selected();
                couponSendText.setSelected(true);
                if(f1==null){
                    f1 = new CirculateChild1Fragment();
                    transaction.add(R.id.fragment_circulate_container,f1);
                }else{
                    transaction.show(f1);
                }
                break;
            case R.id.coupon_verify_text:
                selected();
                couponVerifyText.setSelected(true);
                if(f2==null){
                    f2 = new CirculateChild2Fragment();
                    transaction.add(R.id.fragment_circulate_container,f2);
                }else{
                    transaction.show(f2);
                }
                break;
        }
        transaction.commit();
    }

    //隐藏所有的fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (f1 != null) {
            transaction.hide(f1);
        }
        if (f2 != null) {
            transaction.hide(f2);
        }
    }
    //重置所有文本的选中状态
    public void selected(){
        couponSendText.setSelected(false);
        couponVerifyText.setSelected(false);
    }
    class NetworkChangeReceiver extends BroadcastReceiver {
        //每当网络状态发生变化时，onReceive方法会得到执行
        //执行后实时更新网路状态isNetworkAvailable
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                inNet1.setVisibility(View.VISIBLE);
                inNet2.setVisibility(View.VISIBLE);
                noNet3.setVisibility(View.GONE);

            }
            else{
                Toast.makeText(context, "网络连接断开", Toast.LENGTH_SHORT).show();
                inNet1.setVisibility(View.GONE);
                inNet2.setVisibility(View.GONE);
                noNet3.setVisibility(View.VISIBLE);
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
    public boolean isNetworkAvailable(){
        ConnectivityManager connectivity = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo info = connectivity.getActiveNetworkInfo();
        //return info.isAvailable();
        NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(info != null){
            return info.isConnectedOrConnecting();
        }
        else{
            return false;
        }
    }
}