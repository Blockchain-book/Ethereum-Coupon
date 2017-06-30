package com.block.coupon.util;

import java.util.Random;

/**
 * 生成盐，为密码加密
 * @author linbo
 *
 */
public class Encryption {
	
	private static final int SALT_LENGTH=8;
	
	public static String createSalt() {
		String s = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random r = new Random();
		StringBuilder sb =new StringBuilder();
		for (int i =0; i < SALT_LENGTH; i++ ){
			int n = r.nextInt(62);
			sb.append(s.substring(n, n+1));
		}
		return sb.toString();
	}
}
