package com.block.coupon.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	static public String getCurrentDate(){
		Date date=new Date();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(date);
	}
	
	static public String getCurrentTime() {
		Date date=new Date();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(date);
	}
	
//	将字符串转化为日期
	 static public Date getString2Date(String date) throws ParseException{
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		 return sdf.parse(date);
	 }

}
