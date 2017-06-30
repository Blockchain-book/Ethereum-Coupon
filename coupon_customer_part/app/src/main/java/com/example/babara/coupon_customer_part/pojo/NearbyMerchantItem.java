package com.example.babara.coupon_customer_part.pojo;

import java.io.Serializable;

/**
 * Created by babara on 2017/3/12.
 */

public class NearbyMerchantItem implements Serializable{
    private String merchantId;
    private String distance;
    private String merchantName;
    private String merchantInfo;

    public NearbyMerchantItem(String merchantId,String merchantName){
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }

    public NearbyMerchantItem(String merchantId, String distance, String merchantName, String merchantInfo) {
        this.merchantId = merchantId;
        this.distance = distance;
        this.merchantName = merchantName;
        this.merchantInfo = merchantInfo;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantInfo() {
        return merchantInfo;
    }

    public void setMerchantInfo(String merchantInfo) {
        this.merchantInfo = merchantInfo;
    }
}
