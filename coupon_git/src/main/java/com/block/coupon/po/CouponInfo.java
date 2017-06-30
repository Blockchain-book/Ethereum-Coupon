package com.block.coupon.po;


/**
 * 商户查询优惠券状态
 * 存储优惠券信息
 * @author linbo
 *
 */
public class CouponInfo {
	
	private String account;
	
	private String consumptionDate;
	
	private int couponValue;
	
	private String status;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getConsumptionDate() {
		return consumptionDate;
	}

	public void setConsumptionDate(String consumptionDate) {
		this.consumptionDate = consumptionDate;
	}

	public int getCouponValue() {
		return couponValue;
	}

	public void setCouponValue(int couponValue) {
		this.couponValue = couponValue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
