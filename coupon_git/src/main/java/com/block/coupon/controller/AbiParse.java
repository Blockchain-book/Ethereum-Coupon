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
//			String path="/Users/linbo/Desktop/czbank/coupon/src/main/resources/abi/";
////			String path=getPath();
//			// TODO Auto-generated method stub
//			Web3.addABIFile(path+"MainABI.txt","Main");
//		    Web3.addABIFile(path+"MerchantABI.txt","Merchant");
//		    Web3.addABIFile(path+"CouponABI.txt","Coupon");
//		    Web3.addABIFile(path+"BankABI.txt","Bank");
//	        Web3.addABIFile(path+"ConsumerABI.txt","Consumer");  
////	       out.println("==================");
//	        flag=false;
			// InputStream in = Web3.class.getClassLoader().getResourceAsStream("abi-ours/MainABI.txt");
			// Web3.addABIFile(in, "Main");
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
