package com.block.coupon.po;

public class CouponPayApplicationCustom extends CouponPayApplication{

	private String account;  //消费者账号
	private int couponValue;  //优惠券面值
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public int getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(int couponValue) {
		this.couponValue = couponValue;
	}
	
	
	
}
