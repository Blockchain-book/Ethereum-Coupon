package com.block.coupon.po;

/**
 * 查询商户使用和未使用优惠券总额
 * @author linbo
 *
 */
public class CouponCountInfo {
	
	private int totalUsedCount;
	
	private int totalUnusedCount;

	public int getTotalUsedCount() {
		return totalUsedCount;
	}

	public void setTotalUsedCount(int totalUsedCount) {
		this.totalUsedCount = totalUsedCount;
	}

	public int getTotalUnusedCount() {
		return totalUnusedCount;
	}

	public void setTotalUnusedCount(int totalUnusedCount) {
		this.totalUnusedCount = totalUnusedCount;
	}
	
	
}
