package com.creation.coupon_merchant_part.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thomas on 2017/3/3.
 */

public class SettlementRecord implements Parcelable {
    private String merchantId;
    private String operationDate;
    private String operationType;
    private Integer operationAmount;
    private String recheckStatus;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Integer getOperationAmount() {
        return operationAmount;
    }

    public void setOperationAmount(Integer operationAmount) {
        this.operationAmount = operationAmount;
    }

    public String getRecheckStatus() {
        return recheckStatus;
    }

    public void setRecheckStatus(String recheckStatus) {
        this.recheckStatus = recheckStatus;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(merchantId);
        arg0.writeString(operationDate);
        arg0.writeString(operationType);
        arg0.writeInt(operationAmount);
        arg0.writeString(recheckStatus);
    }
    @Override
    public int describeContents(){
        return 0;
    }
    public static final Creator<SettlementRecord> CREATOR = new Creator(){
        @Override
        public SettlementRecord createFromParcel(Parcel source) {
            SettlementRecord s = new SettlementRecord();
            s.setMerchantId(source.readString());
            s.setOperationDate(source.readString());
            s.setOperationType(source.readString());
            s.setOperationAmount(source.readInt());
            s.setRecheckStatus(source.readString());
            return s;
        }

        @Override
        public SettlementRecord[] newArray(int size) {
            return new SettlementRecord[size];
        }
    };
}
