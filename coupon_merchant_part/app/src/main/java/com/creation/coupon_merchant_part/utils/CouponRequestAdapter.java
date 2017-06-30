package com.creation.coupon_merchant_part.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.CouponRequestItem;
import com.creation.coupon_merchant_part.pojo.CouponStateItem;

import java.util.List;

/**
 * Created by Babara Liang on 2017/1/15.
 *  Description:自定义一个优惠券请求的适配器，使请求的list列表显示在ListView中
 */

public class CouponRequestAdapter extends ArrayAdapter<CouponRequestItem>{
    private int textViewResource;
    public CouponRequestAdapter(Context context, int textViewResource, List<CouponRequestItem> objects){
        super(context, textViewResource, objects);
        this.textViewResource = textViewResource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CouponRequestItem couponRequestItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(textViewResource, null);
        TextView requestUserTextView = (TextView)view.findViewById(R.id.coupon_request_user);
        TextView requestTimeTextView = (TextView)view.findViewById(R.id.coupon_request_time);

        requestUserTextView.setText(couponRequestItem.getUser());
        requestTimeTextView.setText(couponRequestItem.getTime());
        return view;
    }
}
