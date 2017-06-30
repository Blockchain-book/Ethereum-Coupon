package com.block.coupon.po;

/**
 * Created by ziqiji on 17/4/8.
 */
public class MerchantStatus {
    private String id;
    private String status;

    public MerchantStatus(){}
    public MerchantStatus(String id, String status){
        this.id = id;
        this.status = status;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
