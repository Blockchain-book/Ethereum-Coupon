package com.example.babara.coupon_customer_part.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.activity.PayCouponActivity;
import com.example.babara.coupon_customer_part.activity.PresentActivity;
import com.example.babara.coupon_customer_part.zxing.activity.CaptureActivity;

/**
 * Created by Babara Liang on 2016/12/26.
 * Description:该类主要展现首页上的四个功能按钮，并且实现点击时的跳转。
 */

public class FirstPageFragment extends Fragment implements View.OnClickListener{
    private TextView payCouponText ;            //首页优惠券支付（可点击）
    private TextView scanCodeText;              //首页扫一扫拿优惠（可点击）
    private TextView presentCouponText;         //首页优惠券转赠（可点击）

    public FirstPageFragment(){
    }

    @Nullable //@Nullable表示定义的字段可以为空
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firstpage,container, false);
        //UI组件初始化
        payCouponText = (TextView)view.findViewById(R.id.pay_coupon_text);
        scanCodeText = (TextView)view.findViewById(R.id.scan_code_text);
        presentCouponText = (TextView)view.findViewById(R.id.present_coupon_text);

        //按钮监听
        presentCouponText.setOnClickListener(this);
        scanCodeText.setOnClickListener(this);
        payCouponText.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.pay_coupon_text:
                Intent intent1 = new Intent(getActivity(), PayCouponActivity.class);
                startActivity(intent1);
                break;
            case R.id.present_coupon_text:
                Intent intent2 = new Intent(getActivity(), PresentActivity.class);
                startActivity(intent2);
                break;
            case R.id.scan_code_text:
                Intent intent3 = new Intent(getActivity(), CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type","getCoupon");
                intent3.putExtras(bundle);
                startActivity(intent3);
        }
    }

}