package com.block.coupon.po;

/**
 * Created by thomas on 2017/4/12.
 */
public class CouponDetail {
    private String couponId;
    private Integer value;
    private String merchantName;
    private Integer consumptionValue;
    private String consumptionDate;
    private String startDate;
    private String endDate;

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Integer getConsumptionValue() {
        return consumptionValue;
    }

    public void setConsumptionValue(Integer consumptionValue) {
        this.consumptionValue = consumptionValue;
    }

    public String getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(String consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
