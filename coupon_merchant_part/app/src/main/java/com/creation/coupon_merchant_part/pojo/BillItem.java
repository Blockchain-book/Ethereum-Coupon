package com.creation.coupon_merchant_part.pojo;

import java.io.Serializable;

/**
 * Created by Babara Liang on 2017/1/17.
 * Description:该类是账单类。设置->资金账户->账单中的每个账单条目都是该类的一个对象
 */

public class BillItem implements Serializable {
    private String time;            //账单的发生时间
    private String type;            //账单的类别
    private String accountRemain;   //账户的余额

    public BillItem(String time, String type, String accountRemain) {
        this.time = time;
        this.type = type;
        this.accountRemain = accountRemain;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountRemain() {
        return accountRemain;
    }

    public void setAccountRemain(String accountRemain) {
        this.accountRemain = accountRemain;
    }
}
