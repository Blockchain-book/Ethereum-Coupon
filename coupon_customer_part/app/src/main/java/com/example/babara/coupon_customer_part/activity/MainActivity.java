package com.example.babara.coupon_customer_part.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.example.babara.coupon_customer_part.Fragment.FirstPageFragment;
import com.example.babara.coupon_customer_part.Fragment.MyFragment;
import com.example.babara.coupon_customer_part.Fragment.WalletFragment;
import com.example.babara.coupon_customer_part.R;

public class MainActivity extends Activity implements View.OnClickListener{


    private TextView tabFirstPage;         //底部导航栏“首页”选项
    private TextView tabWallet;            //底部导航栏“钱包”选项
    private TextView tabMy;                //底部导航栏“我的”选项

    private FirstPageFragment f1;          //中间部分“首页”fragment
    private WalletFragment f2;             //中间部分“钱包”fragment
    private MyFragment f3;                 //中间部分“我的”fragment

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplication());
        SharedPreferences read = getSharedPreferences("data", MODE_PRIVATE);
        String mobileNo = read.getString("mobileNo", "");
        boolean hasLogined = read.getBoolean("hasLogined", false);
        if (!hasLogined){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        bindView();
    }
    //UI组件初始化与事件绑定
    private void bindView() {
        tabFirstPage = (TextView) findViewById(R.id.tab_first_page);
        tabWallet = (TextView) findViewById(R.id.tab_circulate);
        tabMy = (TextView) findViewById(R.id.tab_my);

        tabFirstPage.setOnClickListener(this);
        tabWallet.setOnClickListener(this);
        tabMy.setOnClickListener(this);
        //接收LoginActivity传来的Activity，未登录自动点击首页，登录后返回“我的”页面
        int id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            tabFirstPage.performClick();
        } else {
            tabMy.performClick();
        }
    }

    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        //分别点击导航栏三个按钮时，分别加载三个fragment
        switch (v.getId()) {
            case R.id.tab_circulate:
                selected();
                tabWallet.setSelected(true);
                if (f2 == null) {
                    f2 = new WalletFragment();
                    transaction.add(R.id.fragment_container, f2);
                } else {
                    transaction.show(f2);
                }
                break;

            case R.id.tab_first_page:
                selected();
                tabFirstPage.setSelected(true);
                if (f1 == null) {
                    f1 = new FirstPageFragment();
                    transaction.add(R.id.fragment_container, f1);
                } else {
                    transaction.show(f1);
                }
                break;

            case R.id.tab_my:
                selected();
                tabMy.setSelected(true);
                if (f3 == null) {
                    f3 = new MyFragment();
                    transaction.add(R.id.fragment_container, f3);
                } else {
                    transaction.show(f3);
                }
                break;
        }
        transaction.commit();
    }
    //加载每个fragment前，先隐藏所有的fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (f1 != null) {
            transaction.hide(f1);
        }
        if (f2 != null) {
            transaction.hide(f2);
        }
        if (f3 != null) {
            transaction.hide(f3);
        }
    }
    //重置所有文本的选中状态
    public void selected() {
        tabFirstPage.setSelected(false);
        tabWallet.setSelected(false);
        tabMy.setSelected(false);
    }
}
