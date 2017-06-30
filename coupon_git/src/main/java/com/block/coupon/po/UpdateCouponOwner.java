package com.block.coupon.po;

/**
 * Created by ziqiji on 17/3/28.
 */
public class UpdateCouponOwner {
    private String targetId;
    private String couponsId;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getCouponsId() {
        return couponsId;
    }

    public void setCouponsId(String couponsId) {
        this.couponsId = couponsId;
    }
}
