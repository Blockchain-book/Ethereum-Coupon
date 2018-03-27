package com.block.coupon.controller;


import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.block.coupon.rpc.Web3;
import com.block.coupon.rpc.WebResourceObj;
import com.sun.jersey.api.client.WebResource;
import net.sf.json.JSONObject;

public class AbiParse extends HttpServlet {

	/**
	 * 
	 */
	private Boolean flag=true;
	private static final long serialVersionUID = 1L;

//	项目启动时，解析ABI文件
	public void init(ServletConfig arg0) throws ServletException {
		if(flag) {
			InputStream in = Web3.class.getClassLoader().getResourceAsStream("abi-ours/MerchantABI.txt");
			Web3.addABIFile(in, "Merchant");
			in = Web3.class.getClassLoader().getResourceAsStream("abi-ours/CouponABI.txt");
			Web3.addABIFile(in, "Coupon");
			in = Web3.class.getClassLoader().getResourceAsStream("abi-ours/BankABI.txt");
			Web3.addABIFile(in, "Bank");
			in = Web3.class.getClassLoader().getResourceAsStream("abi-ours/ConsumerABI.txt");
			Web3.addABIFile(in, "Consumer");
			flag = false;
		}
	}

}
