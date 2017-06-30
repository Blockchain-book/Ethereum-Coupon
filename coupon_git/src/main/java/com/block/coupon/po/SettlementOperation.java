package com.block.coupon.po;

public class SettlementOperation {
    private String id;

    private String merchantId;

    private Integer operationAmount;

    private String operationDate;

    private String operationType;

    private String operatorId;

    private String operationTime;

    private String operatorOpinion;

    private String checkStatus;

    private String firstEncryptStr;

    private String recheckId;

    private String recheckTime;

    private String recheckOpinion;

    private String recheckStatus;

    private String secondEncryptStr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId == null ? null : merchantId.trim();
    }

    public Integer getOperationAmount() {
        return operationAmount;
    }

    public void setOperationAmount(Integer operationAmount) {
        this.operationAmount = operationAmount;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate == null ? null : operationDate.trim();
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType == null ? null : operationType.trim();
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId == null ? null : operatorId.trim();
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime == null ? null : operationTime.trim();
    }

    public String getOperatorOpinion() {
        return operatorOpinion;
    }

    public void setOperatorOpinion(String operatorOpinion) {
        this.operatorOpinion = operatorOpinion == null ? null : operatorOpinion.trim();
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus == null ? null : checkStatus.trim();
    }

    public String getFirstEncryptStr() {
        return firstEncryptStr;
    }

    public void setFirstEncryptStr(String firstEncryptStr) {
        this.firstEncryptStr = firstEncryptStr == null ? null : firstEncryptStr.trim();
    }

    public String getRecheckId() {
        return recheckId;
    }

    public void setRecheckId(String recheckId) {
        this.recheckId = recheckId == null ? null : recheckId.trim();
    }

    public String getRecheckTime() {
        return recheckTime;
    }

    public void setRecheckTime(String recheckTime) {
        this.recheckTime = recheckTime == null ? null : recheckTime.trim();
    }

    public String getRecheckOpinion() {
        return recheckOpinion;
    }

    public void setRecheckOpinion(String recheckOpinion) {
        this.recheckOpinion = recheckOpinion == null ? null : recheckOpinion.trim();
    }

    public String getRecheckStatus() {
        return recheckStatus;
    }

    public void setRecheckStatus(String recheckStatus) {
        this.recheckStatus = recheckStatus == null ? null : recheckStatus.trim();
    }

    public String getSecondEncryptStr() {
        return secondEncryptStr;
    }

    public void setSecondEncryptStr(String secondEncryptStr) {
        this.secondEncryptStr = secondEncryptStr == null ? null : secondEncryptStr.trim();
    }


    @Override
    public String toString() {
        return "SettlementOperation{" +
                "id='" + id + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", operationAmount=" + operationAmount +
                ", operationDate='" + operationDate + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", operationTime='" + operationTime + '\'' +
                ", operatorOpinion='" + operatorOpinion + '\'' +
                ", checkStatus='" + checkStatus + '\'' +
                ", firstEncryptStr='" + firstEncryptStr + '\'' +
                ", recheckId='" + recheckId + '\'' +
                ", recheckTime='" + recheckTime + '\'' +
                ", recheckOpinion='" + recheckOpinion + '\'' +
                ", recheckStatus='" + recheckStatus + '\'' +
                ", secondEncryptStr='" + secondEncryptStr + '\'' +
                '}';
    }
}