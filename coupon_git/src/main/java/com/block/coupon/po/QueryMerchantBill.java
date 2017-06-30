package com.block.coupon.po;

/**
 * Created by wxw on 2017/3/8.
 */
public class QueryMerchantBill {
    private String id; //商户id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    private String startDate;

    private String endDate;
}
