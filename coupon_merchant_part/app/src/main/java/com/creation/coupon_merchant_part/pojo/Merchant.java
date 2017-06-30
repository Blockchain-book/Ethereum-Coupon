package com.creation.coupon_merchant_part.pojo;

/**
 * Created by Babara Liang on 2017/1/11.
 */

public class Merchant {
    private String name;
    private String password;
    private String description;
    private Integer accountBalance;

    public Merchant(Integer accountBalance, String name, String password, String description) {
        this.accountBalance = accountBalance;
        this.name = name;
        this.password = password;
        this.description = description;
    }

    public Integer getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Integer accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Merchant(String name, String password){
        this.name = name;
        this.password = password;
        this.description=description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
