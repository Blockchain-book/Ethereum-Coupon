package com.block.coupon.po;

public class CouponApplication {
    private String id;

    private String merchantId;

    private String consumerId;

    private String consumptionTime;

    private String consumptionValue;

    private Integer couponValue;

    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId == null ? null : merchantId.trim();
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId == null ? null : consumerId.trim();
    }

    public String getConsumptionTime() {
        return consumptionTime;
    }

    public void setConsumptionTime(String consumptionTime) {
        this.consumptionTime = consumptionTime == null ? null : consumptionTime.trim();
    }

    public String getConsumptionValue() {
        return consumptionValue;
    }

    public void setConsumptionValue(String consumptionValue) {
        this.consumptionValue = consumptionValue == null ? null : consumptionValue.trim();
    }

    public Integer getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(Integer couponValue) {
        this.couponValue = couponValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    @Override
    public String toString() {
        return "CouponApplication{" +
                "id='" + id + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", consumerId='" + consumerId + '\'' +
                ", consumptionTime='" + consumptionTime + '\'' +
                ", consumptionValue='" + consumptionValue + '\'' +
                ", couponValue=" + couponValue +
                ", status='" + status + '\'' +
                '}';
    }
}