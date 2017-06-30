package com.block.coupon.util;

import com.block.coupon.po.NearbyMerchantItem;

import java.util.Comparator;


public class DistanceComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        if(Double.parseDouble(((NearbyMerchantItem)o1).getDistance()) <
                Double.parseDouble(((NearbyMerchantItem)o2).getDistance())){
            return 1;
        }else if(Double.parseDouble(((NearbyMerchantItem)o1).getDistance()) >
                Double.parseDouble(((NearbyMerchantItem)o2).getDistance())){
            return -1;
        }
        return 0;
    }
}
