package com.example.babara.coupon_customer_part.pojo;

import java.io.Serializable;

/**
 * Created by babara on 2017/3/12.
 */

public class CouponItem implements Serializable{
    private String couponId;
    private String amount;
    private String obtainTime;
    private String validityFrom;
    private String validityTo;
    private String merchantName;
    private int expense;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getExpense() {
        return expense;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public CouponItem(String couponId,String amount, String obtainTime, String validityFrom, String validityTo) {
        this.couponId = couponId;
        this.amount = amount;
        this.obtainTime = obtainTime;
        this.validityFrom = validityFrom;
        this.validityTo = validityTo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getObtainTime() {
        return obtainTime;
    }

    public void setObtainTime(String obtainTime) {
        this.obtainTime = obtainTime;
    }

    public String getValidityFrom() {
        return validityFrom;
    }

    public void setValidityFrom(String validityFrom) {
        this.validityFrom = validityFrom;
    }

    public String getValidityTo() {
        return validityTo;
    }

    public void setValidityTo(String validityTo) {
        this.validityTo = validityTo;
    }
}
