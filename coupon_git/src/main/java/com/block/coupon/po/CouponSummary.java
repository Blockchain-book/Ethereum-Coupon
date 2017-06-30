package com.block.coupon.po;

/**
 * Created by thomas on 2017/4/11.
 */
public class CouponSummary {
    Double outDate;
    Double almostOutDate;
    Double unused;
    Integer totalValue;

    public CouponSummary(Double outDate,Double almostOutDate,Double unused,Integer totalValue){
        this.outDate = outDate;
        this.almostOutDate = almostOutDate;
        this.unused = unused;
        this.totalValue = totalValue;
    }

    public Double getOutDate() {
        return outDate;
    }

    public void setOutDate(Double outDate) {
        this.outDate = outDate;
    }

    public Double getAlmostOutDate() {
        return almostOutDate;
    }

    public void setAlmostOutDate(Double almostOutDate) {
        this.almostOutDate = almostOutDate;
    }

    public Double getUnused() {
        return unused;
    }

    public void setUnused(Double unused) {
        this.unused = unused;
    }

    public Integer getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Integer totalValue) {
        this.totalValue = totalValue;
    }
}
