package com.creation.coupon_merchant_part.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.CouponStateItem;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Babara Liang on 2017/1/15.
 * Description:自定义一个优惠券使用状态的适配器，使使用状态的list列表显示在ListView中
 */

public class CouponStateAdapter extends ArrayAdapter<CouponStateItem>{
    private int textViewResource;
    public CouponStateAdapter(Context context, int textViewResource, List<CouponStateItem> objects){
        super(context, textViewResource, objects);
        this.textViewResource = textViewResource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CouponStateItem couponStateItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(textViewResource, null);
        TextView userTextView = (TextView)view.findViewById(R.id.user_text);
        TextView usingDateTextView = (TextView)view.findViewById(R.id.using_date_text);
        TextView returnAmountTextView = (TextView)view.findViewById(R.id.return_amount_text);
        TextView isUsedTextView = (TextView)view.findViewById(R.id.is_used_text);

        userTextView.setText(couponStateItem.getUser());
        usingDateTextView.setText(couponStateItem.getUsingDate());
        returnAmountTextView.setText(couponStateItem.getReturnAmount());
        isUsedTextView.setText(couponStateItem.getUsingState());

        return view;
    }
}
