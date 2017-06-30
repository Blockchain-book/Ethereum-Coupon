package com.creation.coupon_merchant_part.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.CouponRequestItem;
import com.creation.coupon_merchant_part.pojo.CouponVerifyItem;

import java.util.List;

/**
 * Created by Babara Liang on 2017/1/15.
 * Description:自定义一个优惠券确认的适配器，使的li确认请求的list列表显示在ListView中
 */

public class CouponVerifyAdapter extends ArrayAdapter<CouponVerifyItem>{
    private int textViewResource;
    public CouponVerifyAdapter(Context context, int textViewResource, List<CouponVerifyItem> objects){
        super(context, textViewResource, objects);
        this.textViewResource = textViewResource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CouponVerifyItem couponVerifyItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(textViewResource, null);
        TextView requestUserTextView = (TextView)view.findViewById(R.id.coupon_verify_user);
        TextView requestTimeTextView = (TextView)view.findViewById(R.id.coupon_verify_time);
        TextView requestAmountTextView = (TextView)view.findViewById(R.id.coupon_verify_amount);

        requestUserTextView.setText(couponVerifyItem.getUser());
        requestTimeTextView.setText(couponVerifyItem.getTime());
        requestAmountTextView.setText(String.valueOf(couponVerifyItem.getCouponValue()));
        return view;
    }
}
