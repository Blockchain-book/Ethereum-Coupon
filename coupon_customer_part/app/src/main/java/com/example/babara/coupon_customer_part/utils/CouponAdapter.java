package com.example.babara.coupon_customer_part.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.pojo.CouponItem;
import com.example.babara.coupon_customer_part.pojo.NearbyMerchantItem;

import java.util.List;

/**
 * Created by babara on 2017/3/12.
 */

public class CouponAdapter  extends ArrayAdapter<CouponItem> {
private int textViewResource;
public CouponAdapter(Context context, int textViewResource, List<CouponItem> objects){
        super(context, textViewResource, objects);
        this.textViewResource = textViewResource;
        }
@Override
public View getView(int position, View convertView, ViewGroup parent){
        CouponItem couponItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(textViewResource, null);
        TextView amount = (TextView)view.findViewById(R.id.coupon_amount);
        TextView obtainTime = (TextView) view.findViewById(R.id.coupon_obtain_time);
        TextView validityFrom = (TextView)view.findViewById(R.id.coupon_validity_from);
        TextView validityTo = (TextView)view.findViewById(R.id.coupon_validity_to);
        TextView merchantName = (TextView)view.findViewById(R.id.coupon_merchant_name);
        TextView consumptionValue = (TextView)view.findViewById(R.id.coupon_consume_value);
        LinearLayout valuePart = (LinearLayout)view.findViewById(R.id.value_part);

        switch(couponItem.getState()){
        case "1":
                valuePart.setBackgroundColor(view.getContext().getResources().getColor(R.color.out));
                break;
        case "2":
                valuePart.setBackgroundColor(view.getContext().getResources().getColor(R.color.almost));
                break;
        case "3":
                valuePart.setBackgroundColor(view.getContext().getResources().getColor(R.color.light_blue));
                break;
        }

        amount.setText(couponItem.getAmount());
        obtainTime.setText(couponItem.getObtainTime());
        validityFrom.setText(couponItem.getValidityFrom());
        validityTo.setText(couponItem.getValidityTo());
        merchantName.setText(couponItem.getMerchantName());
        consumptionValue.setText(couponItem.getExpense()+"");
        return view;
        }

@Override
public boolean hasStableIds() {
        //getCheckedItemIds()方法要求此处返回为真
        return true;
        }
        }

