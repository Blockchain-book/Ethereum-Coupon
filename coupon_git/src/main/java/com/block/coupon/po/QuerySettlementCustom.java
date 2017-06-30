package com.block.coupon.po;

/**
 * Created by admin on 2017/3/1.
 */
public class QuerySettlementCustom {
    private String merchantId;
    private String startDate;
    private String endDate;

    public String getMerchantId(String s) {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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
