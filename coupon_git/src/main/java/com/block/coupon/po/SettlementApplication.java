package com.block.coupon.po;


/**
 * 用于映射查询到的结算券申请信息
 * @author linbo
 *
 */
public class SettlementApplication {
	
	private String id;
	
	private String merchantId;
	
	private String merchantName;
	
	private String operationAmount;
	
	private String operationDate;
	
	private String firstEncryptStr;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getOperationAmount() {
		return operationAmount;
	}

	public void setOperationAmount(String operationAmount) {
		this.operationAmount = operationAmount;
	}

	public String getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(String operationDate) {
		this.operationDate = operationDate;
	}

	public String getFirstEncryptStr() {
		return firstEncryptStr;
	}

	public void setFirstEncryptStr(String firstEncryptStr) {
		this.firstEncryptStr = firstEncryptStr;
	}
	
	

}
