package com.example.babara.coupon_customer_part.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.pojo.NearbyMerchantItem;

import java.util.List;
public class NearbyMerchantAdapter extends ArrayAdapter<NearbyMerchantItem> {
    private int textViewResource;
    public NearbyMerchantAdapter(Context context, int textViewResource, List<NearbyMerchantItem> objects){
        super(context, textViewResource, objects);
        this.textViewResource = textViewResource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        NearbyMerchantItem nearbyMerchantItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(textViewResource, null);
        TextView distance = (TextView)view.findViewById(R.id.merchant_distance_text);
        TextView merchantName = (TextView)view.findViewById(R.id.merchant_name_text);
        TextView merchantInfo = (TextView)view.findViewById(R.id.merchant_info_text);

        distance.setText(nearbyMerchantItem.getDistance());
        merchantName.setText(nearbyMerchantItem.getMerchantName());
        merchantInfo.setText(nearbyMerchantItem.getMerchantInfo());

        return view;
    }
}

