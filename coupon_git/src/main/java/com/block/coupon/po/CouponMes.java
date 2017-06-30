package com.block.coupon.po;

/**
 * Created by wxw on 2017/4/17.
 */
public class CouponMes {
    private String id;
    private Integer couponValue;
    private String name;
    private String consumptionDate;
    private String status;
    private String ownerId;
    private String ownerAccount;
    private String consumeMerchant;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(Integer couponValue) {
        this.couponValue = couponValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(String consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerAccount() {
        return ownerAccount;
    }

    public void setOwnerAccount(String ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    public String getConsumeMerchant() {
        return consumeMerchant;
    }

    public void setConsumeMerchant(String consumeMerchant) {
        this.consumeMerchant = consumeMerchant;
    }

}
