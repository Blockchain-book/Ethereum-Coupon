package com.block.coupon.po;

/**
 * 商户查询优惠券状态，
 * 传入参数：商户id，筛选开始日期，筛选截止日期
 * @author linbo
 *
 */
public class QueryCouponStatus {
	
	private String id; //商户id
	
	private String conCurrentCouponRulerId;
	
	private String startDate;
	
	private String endDate;

	public String getId() {
		return id;
	}

	public String getConCurrentCouponRulerId() {
		return conCurrentCouponRulerId;
	}

	public void setConCurrentCouponRulerId(String conCurrentCouponRulerId) {
		this.conCurrentCouponRulerId = conCurrentCouponRulerId;
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
	
}
