package com.example.babara.coupon_customer_part.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.babara.coupon_customer_part.R;

public class NearbyMerchantFragment extends Fragment implements View.OnClickListener{
    public NearbyMerchantFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_merchant,container,false);
        return view;
    }
    @Override
    public void onClick(View v){


    }
}
