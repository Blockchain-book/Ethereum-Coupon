package com.block.coupon.po;

/**
 * Created by thomas on 2017/4/16.
 */
public class ConsumerInfo {
    private String consumerId;
    private String account;
    private String mark;//0异常，1正常
    private String isFrozen;//0冻结，1正常
    private Integer totalOwn;
    private Integer totalConsume;
    private Integer totalSendOut;

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(String isFrozen) {
        this.isFrozen = isFrozen;
    }

    public Integer getTotalOwn() {
        return totalOwn;
    }

    public void setTotalOwn(Integer totalOwn) {
        this.totalOwn = totalOwn;
    }

    public Integer getTotalConsume() {
        return totalConsume;
    }

    public void setTotalConsume(Integer totalConsume) {
        this.totalConsume = totalConsume;
    }

    public Integer getTotalSendOut() {
        return totalSendOut;
    }

    public void setTotalSendOut(Integer totalSendOut) {
        this.totalSendOut = totalSendOut;
    }
}
