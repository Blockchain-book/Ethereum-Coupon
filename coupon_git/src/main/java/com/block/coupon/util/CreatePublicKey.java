package com.block.coupon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * 创建商户公钥
 * @author linbo
 *
 */
public class CreatePublicKey {
	public static String create() {
		try {
			String commands="geth --password ./passwd account new";
			Process process=Runtime.getRuntime().exec(commands);
			InputStreamReader ir=new InputStreamReader(process.getInputStream());
			BufferedReader br=new BufferedReader(ir);
			String line;
			if((line=br.readLine())!=null){
				return line.substring(line.indexOf("{")+1, line.lastIndexOf("}"));
			}else{
				return null;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("IOException"+e.getMessage());
			return null;
		}
	}
}
