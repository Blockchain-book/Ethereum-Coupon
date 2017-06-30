package com.creation.coupon_merchant_part.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.fragment.CirculateFragment;
import com.creation.coupon_merchant_part.fragment.FirstFragment;
import com.creation.coupon_merchant_part.fragment.MyFragment;
import com.creation.coupon_merchant_part.pojo.CouponRequestItem;
import com.creation.coupon_merchant_part.pojo.CouponVerifyItem;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.HttpTaskTool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
/**
 * Description:该方法主要实现主页上底部导航栏，及他们的点击事件。
 * 并且从服务器获取优惠券请求列表和使用确认列表，以在“流通”fragment中使用。
*/
public class MainActivity extends Activity implements View.OnClickListener {
    private int logined;                    //用来记录用户是否已经登录

    private TextView tabFirstPage;         //底部导航栏“首页”选项
    private TextView tabCirculate;         //底部导航栏“流通”选项
    private TextView tabMy;                //底部导航栏“我的”选项

    private FirstFragment f1;              //中间部分“首页”fragment
    private CirculateFragment f2;          //中间部分“流通”fragment
    private MyFragment f3;                  //中间部分“我的”fragment

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(!getSharedPreferences("currentSession",MODE_PRIVATE).getBoolean("hasLogined",false)){
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        bindView();
    }
    //UI组件初始化与事件绑定
    private void bindView() {
        tabFirstPage = (TextView) findViewById(R.id.tab_first_page);
        tabCirculate = (TextView) findViewById(R.id.tab_circulate);
        tabMy = (TextView) findViewById(R.id.tab_my);
//
// = getCouponVerifyList();

        tabFirstPage.setOnClickListener(this);
        tabCirculate.setOnClickListener(this);
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
                tabCirculate.setSelected(true);
                if (f2 == null) {
                    f2 = new CirculateFragment();
                    transaction.add(R.id.fragment_container, f2);
                } else {
                    transaction.show(f2);
                }
                break;

            case R.id.tab_first_page:
                selected();
                tabFirstPage.setSelected(true);
                if (f1 == null) {
                    f1 = new FirstFragment();
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
        tabCirculate.setSelected(false);
        tabMy.setSelected(false);
    }

}
