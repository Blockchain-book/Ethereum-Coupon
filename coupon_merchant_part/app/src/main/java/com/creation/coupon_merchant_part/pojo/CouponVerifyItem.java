package com.creation.coupon_merchant_part.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/1/17.
 * Description:该类是优惠券发放请求类。流通->优惠券验证->优惠券确认请求列表
 * 中的每个条目都是该类的一个对象
 */
//实现序列化接口是为了使用Bundle来传送该类的对象
public class CouponVerifyItem implements Parcelable {
    private String applicationCode;
    private String user;
    private String time;
    private Integer couponValue;

    public CouponVerifyItem(){
        super();
    }

    public CouponVerifyItem(String applicationCode, String user, String time, Integer couponValue) {
        super();
        this.applicationCode = applicationCode;
        this.user = user;
        this.time = time;
        this.couponValue = couponValue;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(Integer couponValue) {
        this.couponValue = couponValue;
    }

    @Override
    public String toString() {
        return "CouponVerifyItem [applicationCode="+applicationCode+", user=" + user + ", name=" + time + ", couponValue="+couponValue +"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        arg0.writeString(applicationCode);
        arg0.writeString(user);
        arg0.writeString(time);
        arg0.writeInt(couponValue);
    }

    // 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
    // android.os.BadParcelableException:
    // Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class com.um.demo.Person
    // 2.这个接口实现了从Percel容器读取Person数据，并返回Person对象给逻辑层使用
    // 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
    // 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
    // 5.反序列化对象
    public static final Creator<CouponVerifyItem> CREATOR = new Creator(){

        @Override
        public CouponVerifyItem createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            CouponVerifyItem c = new CouponVerifyItem();
            c.setApplicationCode(source.readString());
            c.setUser(source.readString());
            c.setTime(source.readString());
            c.setCouponValue(source.readInt());
            return c;
        }

        @Override
        public CouponVerifyItem[] newArray(int size) {
            return new CouponVerifyItem[size];
        }
    };

}