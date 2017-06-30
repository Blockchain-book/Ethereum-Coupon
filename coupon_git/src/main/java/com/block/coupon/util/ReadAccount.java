package com.block.coupon.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadAccount {

	
	
	public static String getAccount(String key) throws IOException{
		Properties p=new Properties();
		InputStream in= ReadAccount.class.getClassLoader().getResourceAsStream("account.properties");
		p.load(in);
		return p.getProperty(key);
	}
	
}
