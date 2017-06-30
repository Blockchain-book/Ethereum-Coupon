package com.creation.coupon_merchant_part.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.BillItem;
import com.creation.coupon_merchant_part.pojo.CouponStateItem;

import java.util.List;

/**
 * Created by Babara Liang on 2017/1/15.
 * Description:自定义一个账单的适配器，使账单的list列表显示在ListView中
 */

public class BillAdapter extends ArrayAdapter<BillItem>{
    private int textViewResource;
    public BillAdapter(Context context, int textViewResource, List<BillItem> objects){
        super(context, textViewResource, objects);
        this.textViewResource = textViewResource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        BillItem billItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(textViewResource, null);
        TextView timeTextView = (TextView)view.findViewById(R.id.bill_time_text);
        TextView typeTextView = (TextView)view.findViewById(R.id.bill_type_text);
        TextView remainTextView = (TextView)view.findViewById(R.id.bill_remain_text);

        timeTextView.setText(billItem.getTime());
        typeTextView.setText(billItem.getType());
        remainTextView.setText(billItem.getAccountRemain());
        return view;
    }
}
