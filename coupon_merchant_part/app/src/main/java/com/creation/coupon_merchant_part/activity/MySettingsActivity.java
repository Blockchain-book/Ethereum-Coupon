package com.creation.coupon_merchant_part.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.support.multidex.MultiDex;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.fragment.AccountSettingFragment;
import com.creation.coupon_merchant_part.fragment.InfoSettingFragment;
import com.creation.coupon_merchant_part.fragment.PasswordSettingFragment;
/**
 * Description:该类主要实现我的设置功能。
 */

public class MySettingsActivity extends Activity implements View.OnClickListener {
    private ImageView backImageView;           //返回按钮
    private TextView infoSetTextView;          //“用户信息”按钮
    private TextView passworSetTextView;       //“密码设置”按钮
    private TextView accountSetTextView;       //“银行账户”按钮
    private FragmentManager fragmentManager;
    private InfoSettingFragment f1;           //“商户信息”fragment
    private PasswordSettingFragment f2;       //“密码设置”fragment
    private AccountSettingFragment f3;        //“银行账户”fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_settings);
        bindView();
    }
    //UI组件初始化和监听功能
    public void bindView() {
        backImageView = (ImageView) findViewById(R.id.my_settings_back);
        infoSetTextView = (TextView) findViewById(R.id.info_set_text);
        passworSetTextView = (TextView) findViewById(R.id.password_set_text);
        accountSetTextView = (TextView) findViewById(R.id.account_set_text);

        backImageView.setOnClickListener(this);
        infoSetTextView.setOnClickListener(this);
        passworSetTextView.setOnClickListener(this);
        accountSetTextView.setOnClickListener(this);
        //商户信息自动点击
        infoSetTextView.performClick();
    }

    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch (v.getId()) {
            case R.id.my_settings_back:
                //点击返回按钮，退出此页
                this.finish();
                break;
            case R.id.info_set_text:
                //点击商户信息按钮，加载商户信息的fragment
                selected();
                infoSetTextView.setSelected(true);
                if (f1 == null) {
                    f1 = new InfoSettingFragment();
                    transaction.add(R.id.fragment_setting_container, f1);
                } else {
                    transaction.show(f1);
                }
                break;
            case R.id.password_set_text:
                //点击密码按钮，加载密码的fragment
                selected();
                passworSetTextView.setSelected(true);
                if (f2 == null) {
                    f2 = new PasswordSettingFragment();
                    transaction.add(R.id.fragment_setting_container, f2);
                } else {
                    transaction.show(f2);
                }
                break;
            case R.id.account_set_text:
                //点击银行账户按钮，加载银行账户的fragment
                selected();
                accountSetTextView.setSelected(true);
                if (f3 == null) {
                    f3 = new AccountSettingFragment();
                    transaction.add(R.id.fragment_setting_container, f3);
                } else {
                    transaction.show(f3);
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
        if (f3 != null) {
            transaction.hide(f3);
        }

    }

    //重置所有文本的选中状态
    public void selected() {
        infoSetTextView.setSelected(false);
        passworSetTextView.setSelected(false);
        accountSetTextView.setSelected(false);
    }
}


