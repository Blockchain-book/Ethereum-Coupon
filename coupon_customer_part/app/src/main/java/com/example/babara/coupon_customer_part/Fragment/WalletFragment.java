package com.example.babara.coupon_customer_part.Fragment;

import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.utils.PanelDountChart;

/**
 * Created by Babara Liang on 2016/12/26.
 * Description:该方法主要实现流通页面上顶部导航栏，及他们的点击事件。
 */

public class WalletFragment extends Fragment implements View.OnClickListener{
    public WalletFragment(){
    }

    private TextView walletGeneral;                 //“优惠券总览”按钮
    private TextView walletDetail;                  //“优惠券明细”按钮

    private FragmentManager fragmentManager;
    private LeftWalletFragment f1;           //按发放按钮，加载的fragment
    private RightWalletFragment f2;           //按验证按钮，加载的fragment

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet,container,false);
        walletGeneral = (TextView)view.findViewById(R.id.wallet_general_text);
        walletDetail = (TextView)view.findViewById(R.id.wallet_detail_text);

        walletGeneral.setOnClickListener(this);
        walletDetail.setOnClickListener(this);
        walletGeneral.performClick();
        return view;
    }
    @Override
    public void onClick(View v){
        fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (v.getId()){
            //分别点击导航栏两个按钮时，分别加载两个fragment
            case R.id.wallet_general_text:
                selected();
                walletGeneral.setSelected(true);
                if(f2==null){
                    f1 = new LeftWalletFragment();
                }
                transaction.replace(R.id.fragment_wallet_container,f1);
                break;
            case R.id.wallet_detail_text:
                selected();
                walletDetail.setSelected(true);
                if(f2==null){
                    f2 = new RightWalletFragment();
                }
                transaction.replace(R.id.fragment_wallet_container,f2);
                break;
        }
        transaction.commit();
    }

    //重置所有文本的选中状态
    public void selected(){
        walletGeneral.setSelected(false);
        walletDetail.setSelected(false);
    }
}