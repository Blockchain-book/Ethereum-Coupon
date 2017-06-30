package com.example.babara.coupon_customer_part.utils;

/**
 * Created by Babara Liang on 2017/1/14.
 * Description:使用该接口是为了在onPostExecute之外获取到服务器传送的数据，
 * 配合HttpTackTool的使用
 */

public interface AsyncResponse {
    void onDataReceivedSuccess(String data);
    //void onDataReceivedFailed();
}
