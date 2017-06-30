package com.creation.coupon_merchant_part.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/17.
 * Description:该类是优惠券发放请求类。流通->优惠券发放->优惠券发放请求列表
 * 中的每个条目都是该类的一个对象
 */
//实现序列化接口是为了使用Bundle来传送该类的对象
public class CouponRequestItem implements Parcelable {
    private String user;
    private String time;
    private String id;
    private String applyId;
    public CouponRequestItem(){
        super();
    }
    public CouponRequestItem(String user, String time,String id,String applyId) {
        super();
        this.user = user;
        this.time = time;
        this.id = id;
        this.applyId = applyId;
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
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getApplyId() {return applyId;}
    public void setApplyId(String applyId) {this.applyId = applyId;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        arg0.writeString(user);
        arg0.writeString(time);
        arg0.writeString(id);
        arg0.writeString(applyId);
    }

    // 1.必须实现Parcelable.Creator接口,否则在获取数据的时候，会报错，如下：
    // android.os.BadParcelableException:
    // Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class com.um.demo.Person
    // 2.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
    // 3.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
    // 4.反序列化对象
    public static final Parcelable.Creator<CouponRequestItem> CREATOR = new Creator(){

        @Override
        public CouponRequestItem createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            CouponRequestItem c = new CouponRequestItem();
            c.setUser(source.readString());
            c.setTime(source.readString());
            c.setId(source.readString());
            c.setApplyId(source.readString());
            return c;
        }

        @Override
        public CouponRequestItem[] newArray(int size) {
            return new CouponRequestItem[size];
        }
    };

}