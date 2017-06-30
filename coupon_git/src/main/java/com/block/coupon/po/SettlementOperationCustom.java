package com.block.coupon.po;

public class SettlementOperationCustom extends SettlementOperation{
	private String merchantName;

	private String bankStaffId;

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getBankStaffId() {
		return bankStaffId;
	}

	public void setBankStaffId(String bankStaffId) {
		this.bankStaffId = bankStaffId;
	}


	@Override
	public String toString() {
		return "SettlementOperationCustom{" +
				"SettlementOperation{"+
				"SettlementOperation{" +
				"id='" + getId() + '\'' +
				", merchantId='" + getMerchantId() + '\'' +
				", operationAmount=" + getOperationAmount() +
				", operationDate='" + getOperationDate() + '\'' +
				", operationType='" + getOperationType() + '\'' +
				", operatorId='" + getOperatorId() + '\'' +
				", operationTime='" + getOperationTime() + '\'' +
				", operatorOpinion='" + getOperatorOpinion() + '\'' +
				", checkStatus='" + getCheckStatus() + '\'' +
				", firstEncryptStr='" + getFirstEncryptStr() + '\'' +
				", recheckId='" + getRecheckId() + '\'' +
				", recheckTime='" + getRecheckTime() + '\'' +
				", recheckOpinion='" + getRecheckOpinion() + '\'' +
				", recheckStatus='" + getRecheckStatus() + '\'' +
				", secondEncryptStr='" + getSecondEncryptStr() + '\'' +
				'}' +
				"merchantName='" + merchantName + '\'' +
				", bankStaffId='" + bankStaffId + '\'' +
				'}';
	}
}
