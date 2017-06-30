package com.block.coupon.rpc;

import com.block.coupon.po.MerchantCustom;
import com.block.coupon.util.ReadAccount;
import com.sun.jersey.api.client.WebResource;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class TestMain {
	static String contractAddress;
	public static void main(String[] args) throws IOException{

		String code = "contract A{uint a;bool locked;function A(){a=10;locked=false;}function getA() constant returns(uint){while(locked){}return a;}function setA(uint x){lock();a=x;unlock();}function lock(){locked=true;}function unlock(){locked=false;}}";
		String funcName1 = "eth_compileSolidity()";
		String txHash = Web3.universalCall(funcName1,"{from:"+ReadAccount.getAccount("bankAccount")+",\"data\":"+code+",\"gas\":"+"0x470000}",null,null);
		String finish = "null";
		String[] content = {txHash};
		while(finish.equals("null")){
		    finish = Web3.universalCall("eth_getTransactionReceipt",null, content,null);
        }
        JSONObject jo = JSONObject.fromObject(finish);
		contractAddress = jo.getString("contractAddress");


	}
}
