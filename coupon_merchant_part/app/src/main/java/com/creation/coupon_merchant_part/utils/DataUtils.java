package com.creation.coupon_merchant_part.utils;

/**
 * Created by Babara Liang on 2017/1/11.
 */

public class DataUtils {
    public static boolean validData(String data){
        //判断是否满足日期格式
        String dataRegex = "\\d{4}-\\d{2}-\\d{2}";
        if(data.matches(dataRegex)){
            String[] dataDate = data.split("-");
            if (dataDate[0].compareTo("2000")<0 || dataDate[0].compareTo("2100")>0) return false;
            //判断2月
            if ("02".equals(dataDate[1])){
                int year = Integer.parseInt(dataDate[0]);
                if ((year%4==0 && year%100!=0) || year%400==0){
                    if (dataDate[2].compareTo("29")>0){
                        return false;
                    }
                }else {
                    if (dataDate[2].compareTo("28")>0){
                        return false;
                    }
                }
            }
            //判断月份
            if (dataDate[1].compareTo("01")<0 || dataDate[1].compareTo("12")>0){
                return false;
            }
            //判断日
            switch (dataDate[1]){
                case "01":
                case "03":
                case "05":
                case "07":
                case "08":
                case "10":
                case "12":
                    if (dataDate[2].compareTo("01")<0 || dataDate[2].compareTo("31")>0) return false;
                    break;
                case "04":
                case "06":
                case "09":
                case "11":
                    if (dataDate[2].compareTo("01")<0 || dataDate[2].compareTo("30")>0) return false;
                    break;
            }
        }else {
            return false;
        }
        return true;
    }
}
