package com.block.coupon.po;

public class CouponApplicationCustom extends CouponApplication{

	private String account;
	
	/**
	 * 发放优惠券时，一次可能发放多张优惠券
	 */
	private int couponNum;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getCouponNum() {
		return couponNum;
	}

	public void setCouponNum(int couponNum) {
		this.couponNum = couponNum;
	}

	
}
