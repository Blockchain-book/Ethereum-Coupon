package com.creation.coupon_merchant_part.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/15.
 * Description:该类是优惠券发放请求类。首页->状态管理中下部分的发行状态列表
 * 中的每个条目都是该类的一个对象
 */

//实现Serializable接口是为了intent.putExtra可以传递该类的对象
public class CouponStateItem implements Parcelable {
    private String user;                //用户
    private String usingDate;           //取得优惠券的时间7
    private String returnAmount;        //返还优惠券的金额数
    private String usingState;          //使用状态（使用or未使用）

    protected CouponStateItem(Parcel in) {
        user = in.readString();
        usingDate = in.readString();
        returnAmount = in.readString();
        usingState = in.readString();
    }

    public static final Creator<CouponStateItem> CREATOR = new Creator<CouponStateItem>() {
        @Override
        public CouponStateItem createFromParcel(Parcel in) {
            return new CouponStateItem(in);
        }

        @Override
        public CouponStateItem[] newArray(int size) {
            return new CouponStateItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user);
        parcel.writeString(usingDate);
        parcel.writeString(returnAmount);
        parcel.writeString(usingDate);
    }

    public CouponStateItem(String user, String usingDate, String returnAmount, String usingState) {
        this.user = user;
        this.usingDate = usingDate;
        this.returnAmount = returnAmount;
        this.usingState = usingState;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUsingDate() {
        return usingDate;
    }

    public void setUsingDate(String usingDate) {
        this.usingDate = usingDate;
    }

    public String getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(String returnAmount) {
        this.returnAmount = returnAmount;
    }

    public String getUsingState() {
        return usingState;
    }

    public void setUsingState(String usingState) {
        this.usingState = usingState;
    }

}
