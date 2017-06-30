package com.block.coupon.po;

/**
 * Created by thomas on 2017/3/29.
 */
public class AvailableQuery {
    private String consumerId;
    private Integer comsuptionAmount;


    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public Integer getComsuptionAmount() {
        return comsuptionAmount;
    }

    public void setComsuptionAmount(Integer comsuptionAmount) {
        this.comsuptionAmount = comsuptionAmount;
    }
}
