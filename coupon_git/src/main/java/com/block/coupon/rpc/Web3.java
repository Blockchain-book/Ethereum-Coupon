package com.block.coupon.rpc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import net.sf.json.JSONArray;
import java.util.ArrayList;
import net.sf.json.JSONObject;

import com.block.coupon.controller.AbiParse;
import com.block.coupon.sha3.Sha3;
import com.block.coupon.util.ReadAccount;
import com.sun.jersey.api.client.WebResource;


public class Web3 {
	
	private static WebResource r = WebResourceObj.getWebResource();
	private static Map<String,Map<String,String>> map = new HashMap<String,Map<String,String>>();
	
	
	public static void init(){
		InputStream in = Web3.class.getClassLoader().getResourceAsStream("abi-ours/MerchantABI.txt");
		addABIFile(in, "Merchant");
		in=Web3.class.getClassLoader().getResourceAsStream("abi-ours/CouponABI.txt");
		addABIFile(in, "Coupon");
		in=Web3.class.getClassLoader().getResourceAsStream("abi-ours/BankABI.txt");
	    addABIFile(in, "Bank");
		in=Web3.class.getClassLoader().getResourceAsStream("abi-ours/ConsumerABI.txt");
	    addABIFile(in, "Consumer");
	}
	
	public static String sendTransactionMap(String abFunctionName,String[] content,String from,String to){
		String[] contractAndFunction = abFunctionName.split("\\.");
		if(map.isEmpty()) {
			System.out.println("map is null");
			return "";
		}
		System.out.println(contractAndFunction[0]);
		String contractName = contractAndFunction[0];
		if(!map.containsKey(contractName)){
			System.out.println("contract is not exist");
			return "";
		}
		Map<String,String> funs = map.get(contractName);
		String functionName = contractAndFunction[1];
		if(!funs.containsKey(functionName)) {
			System.out.println("contract \""+contractName+"\" does not have \""+functionName+"\"  function");
			return "";
		}
		String[] types = funs.get(functionName).split(",");
		if(types.length==0)
			types = null;
		return sendTransaction(functionName, types, content, from, to);
	}
	
	public static String getValueMap(String abFunctionName,String[] content,String from,String to){
		String[] contractAndFunction = abFunctionName.split("\\.");
		if(map.isEmpty()) {
			System.out.println("map is null");
			return "";
		}
		System.out.println(contractAndFunction[0]);
		String contractName = contractAndFunction[0];
		if(!map.containsKey(contractName)){
			System.out.println("contract is not exist");
			return "";
		}
		Map<String,String> funs = map.get(contractName);
		String functionName = contractAndFunction[1];
		if(!funs.containsKey(functionName)) {
			System.out.println("contract \""+contractName+"\" does not have \""+functionName+"\"  function");
			return "";
		}
		String[] types = funs.get(functionName).split(",");
		if(types.length==0)
			types = null;
		return getValue(functionName, types, content, from, to);
	}
	
//	发送交易执行这个方法
	public static String sendTransaction(String functionName,String[] type,String[] content,String from,String to){
		String data = getData(functionName,type,content);
		System.out.println("data:" + data);

//		String gas = getGas(from, to, data);
//		System.out.println(gas);
		String gas = "0x9faf080";//"0x9faf080";0x470000
		String rpc = "{\"jsonrpc\":\"2.0\",\"method\": \"eth_sendTransaction\", \"params\": [{\"from\": \""+from+"\", \"to\": \""+to+"\",\"gas\":\""+gas+"\" ,\"data\": \""+data+"\"}], \"id\": 8}";
		System.out.println(rpc);
		String response = r.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_XML)
                .entity(rpc, MediaType.APPLICATION_JSON)
                .post(String.class);
		JSONObject json=JSONObject.fromObject(response);
		System.out.println(json.toString(4));
	    System.out.println(json.getString("result"));
//	    返回交易哈希
	    return json.getString("result");
	}
	
	
	
	public static String getValue(String functionName,String[] type,String[] content,String from,String to){
		for(int i = 0; i < content.length; ++i) {
			System.out.println("getValue.content[" + i + "]: " + content[i]);
		}

		String data = getData(functionName,type,content);
		System.out.println("getValue.data:" + data);
		String rpc = "{\"jsonrpc\":\"2.0\",\"method\": \"eth_call\", \"params\": [{\"from\": \""+from+"\", \"to\": \""+to+"\",\"data\": \""+data+"\"},\"latest\"], \"id\": 8}";
		System.out.println("getValue.rpc: " + rpc);
		String response = r.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_XML)
                .entity(rpc, MediaType.APPLICATION_JSON)
                .post(String.class);
		JSONObject json=JSONObject.fromObject(response);
		System.out.println("getValue.json:");
		System.out.println(json.toString(4));
//		System.out.println(json.getString("result"));
		return json.getString("result");
	}
	
	
//	对方法的返回值进行解码,这个方法只能解码返回值类型是静态类型的返回值
	public static List<String> decodeReturnValue(String returnType, String data) {
		List<String > list=new ArrayList<String>();
		System.out.println("returnType: " + returnType);
		System.out.println("data: " + data);
		if(returnType.contains("uint")) {
			String value=data.substring(2, data.length());
			list.add(Integer.parseInt(value,16)+"");
			return list;
		} else if (returnType.contains("bool")) {
			list.add( data.substring(data.lastIndexOf("0")+1,data.length()).equals("")?"0":"1");
			return list;
		} else if(returnType.contains("bytes")) {
			int count=Integer.parseInt(returnType.substring(5));
//			除去首部的0
			 String value=data.substring(2, count*2+2);
//			转换成字符串
			 StringBuffer sb=new StringBuffer();
			 for(int i=0; i<value.length(); i=i+2){
				 int num=Integer.parseInt(value.substring(i, i+2), 16);
				 sb.append((char)num);
			 }
			 list.add(sb.toString());
			 return list;
			 
		} else if (returnType.contains("address") && !returnType.contains("[")){
			list.add("0x"+data.substring(26, data.length()));
			return list;
		} else if (returnType.contains("address[]")){ 
			int i=0;
			data=data.substring(130,data.length());
			for(int count=0;count<data.length();){
				list.add("0x"+data.substring(24+64*i, 64+64*i));
				count= 64*(i+1);
				i++;
			}
			return list;
		}else{
			return list;
		}
	}
	
//	封装了系统的一些方法
	public static String universalCall(String functionName,String objStr,String[] content,Boolean flag){
		System.out.println("objStr: " + objStr);
		for(int i = 0; i < content.length; ++i) {
			System.out.println("content[" + i + "]: " + content[i]);
		}
		System.out.println("flag: " + flag);
		String m = "";
		if(content!=null&&content.length>0){
			StringBuffer con = new StringBuffer();
			for(int i=0;i<content.length;i++){
				con.append("\""+content[i]+"\",");
			}
			m = con.substring(0, con.length()-1);
		}
		if(objStr==null){
			objStr = "";
		}
		if(!objStr.isEmpty()&&!m.isEmpty()){
			objStr += ",";
		}
		String flagStr = "";
		
		if((!objStr.isEmpty()||!m.isEmpty())&&flag!=null){
			flagStr = ","+flag+"";
		}
		if(objStr.isEmpty()&&m.isEmpty()&&flagStr!=null)
			flagStr = ""+flag+"";
		String rpc = "{\"jsonrpc\":\"2.0\",\"method\":\""+functionName+"\",\"params\":["+objStr+m+flagStr+"],\"id\":\"8\"}";
		System.out.println(rpc);
		String response = r.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_XML)
                .entity(rpc, MediaType.APPLICATION_JSON)
                .post(String.class);
		JSONObject json=JSONObject.fromObject(response);
		System.out.println(json.toString(4));
//		System.out.println(json.getString("result"));
		return json.getString("result");
	}
	
	public static void addABIFile(InputStream in,String contractName){
		BufferedReader br = null;
		try {
			br=new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String str = null;
			while((str=br.readLine())!=null){
				sb.append(str);
			}
			JSONArray json = JSONArray.fromObject(sb.toString());
			List<Map<String,Object>> lms = (List)json;
			Map<String,String> inner = new HashMap<String,String>();
			for(int i=0;i<lms.size();i++){//变脸每一个方法
				Map<String,Object> m = lms.get(i);
				Object methodName = m.get("name");
				if(methodName==null){//constructor
					methodName = contractName;
				}
				JSONArray tp = JSONArray.fromObject(m.get("inputs"));
				List<Map<String,Object>> lm = (List)tp;
				String types = "";
				for(int j=0;j<lm.size();j++){//遍历某个方法的每个参数
					types += lm.get(j).get("type")+",";
				}
				if(types.contains(",")){
					types = types.substring(0,types.length()-1);
				}
				inner.put(methodName.toString(), types);
			}
			map.put(contractName,inner);
			System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

//  调用平台方法，估测一笔交易需要消耗多少gas
	private static String getGas(String from,String to,String data){

		String rpc = "{\"jsonrpc\":\"2.0\",\"method\": \"eth_estimateGas\", \"params\": [{\"from\": \"" + from + "\", \"gasPrice\": \"0x4a817c800\", \"data\": \"" + data + "\"}], \"id\": 5}";
		System.out.println("getGas: " + rpc);
		String response = r.accept(MediaType.APPLICATION_JSON, MediaType.TEXT_XML)
				.entity(rpc, MediaType.APPLICATION_JSON)
				.post(String.class);
		JSONObject json=JSONObject.fromObject(response);
		System.out.println(json.toString(4));
		System.out.println(json.getString("result"));
		return json.getString("result");
	}
	
//	对传递进入的参数进行封装
	public static String getData(String functionName,String[] type,String[] content){
		if(type==null){
			String functionId = "0x"+Sha3.sha3(functionName+"()").substring(0, 8);
			return functionId;
		}
		String functionHead = functionName+"(";
		StringBuffer dataTail = new StringBuffer();
		String curType = "";
		int curOffset = 0;
		int curIndex = 0;
		
		String dyTypes = "";
		for(int i=0;i<type.length;i++){
		///////basic type
			///type uint transfer to uint256
			if(type[i].equals("uint")) {
				type[i] = "uint256";
//				dataTail.append(convert32Bytes(Integer.toHexString(Integer.parseInt(content[i]))));
			}
			///type uint32,uint64 and so on
			if(!type[i].equals("uint")&&type[i].contains("uint")&&!type[i].contains("[")){
				dataTail.append(convert32Bytes(Integer.toHexString(Integer.parseInt(content[i]))));
			}
			if(type[i].equals("address")){
				dataTail.append(convert32Bytes(content[i].substring(2)));
			}
			///type bytes1 to bytes32
			if(!type[i].equals("bytes")&&type[i].contains("bytes")&&!type[i].contains("[")){
				dataTail.append(convertASC(content[i]));
			}
			if(type[i].equals("bool")){
				if(content[i].equals("true"))
					dataTail.append(convert32Bytes(1+""));
				if(content[i].equals("false"))
					dataTail.append(convert32Bytes(0+""));
			}
		///////dynamic type	
			if(type[i].equals("bytes")){
				if(curType.isEmpty()){
					curOffset = 32*content.length;
				}else{
					if(curType.equals("bytes")){
						curOffset += 32*2;
					}
					if(curType.startsWith("uint")&&curType.contains("[")){
						String[] str = content[curIndex].substring(1, content[curIndex].length()-1).split(",");
						curOffset += (str.length+1)*32;
					}
					if(curType.equals("address[]")){
						String[] str = content[curIndex].substring(1, content[curIndex].length()-1).split(",");
						curOffset += (str.length+1)*32;
					}
				}
				dataTail.append(convert32Bytes(Integer.toHexString(curOffset)));
				curType = "bytes";
				curIndex = i;
				dyTypes += "bytes-"+i+",";
			}
			if(type[i].contains("[")){
				String subType = type[i].substring(0, type[i].indexOf('['));
				if(subType.contains("uint")){
					if(subType.equals("uint")){
						type[i] = "uint256[]";
					}
					if(curType.isEmpty()){
						curOffset = 32*content.length;
					}else{
						if(curType.equals("bytes")){
							curOffset += 32*2;
						}
						if(curType.startsWith("uint")&&curType.contains("[")){
							String[] str = content[curIndex].substring(1, content[curIndex].length()-1).split(",");
							curOffset += (str.length+1)*32;
						}
						if(curType.equals("address[]")){
							String[] str = content[curIndex].substring(1, content[curIndex].length()-1).split(",");
							curOffset += (str.length+1)*32;
						}
					}
					dataTail.append(convert32Bytes(Integer.toHexString(curOffset)));
					curType = "uint[]";
					curIndex = i;
					dyTypes += "uint[]-"+i+",";
				}
				if(subType.contains("address")){
					if(curType.isEmpty()){
						curOffset = 32*content.length;
					}else{
						if(curType.equals("bytes")){
							curOffset += 32*2;
						}
						if(curType.startsWith("uint")&&curType.contains("[")){
							String[] str = content[curIndex].substring(1, content[curIndex].length()-1).split(",");
							curOffset += (str.length+1)*32;
						}
						if(curType.equals("address[]")){
							String[] str = content[curIndex].substring(1, content[curIndex].length()-1).split(",");
							curOffset += (str.length+1)*32;
						}
					}
					dataTail.append(convert32Bytes(Integer.toHexString(curOffset)));
					curType = "address[]";
					curIndex = i;
					dyTypes += "address[]-"+i+",";
				}
			}
			functionHead += type[i]+",";
		}
		functionHead = functionHead.substring(0, functionHead.length()-1)+")";
		System.out.println(functionHead);
		String functionId = "0x"+Sha3.sha3(functionHead).substring(0, 8);
		if(!dyTypes.isEmpty()){
			String[] dy = dyTypes.substring(0, dyTypes.length()-1).split(",");
			for(int i=0;i<dy.length;i++){
				if(dy[i].contains("bytes-")){
					String[] s = dy[i].split("-");
					int index = Integer.parseInt(s[1]);
					dataTail.append(convert32Bytes(Integer.toHexString(content[index].length())));
					dataTail.append(convertASC(content[index]));
				}
				if(dy[i].contains("[")){
					if(dy[i].contains("uint")){
						String[] s = dy[i].split("-");
						int index = Integer.parseInt(s[1]);
						String[] arr = content[index].substring(1, content[index].length()-1).split(",");
						dataTail.append(convert32Bytes(Integer.toHexString(arr.length)));
						for(int j=0;j<arr.length;j++){
							dataTail.append(convert32Bytes(Integer.toHexString(Integer.parseInt(arr[j]))));
						}
					}
					if(dy[i].contains("address")){
						String[] s = dy[i].split("-");
						int index = Integer.parseInt(s[1]);
						String[] arr = content[index].substring(1, content[index].length()-1).split(",");
						dataTail.append(convert32Bytes(Integer.toHexString(arr.length)));
						for(int j=0;j<arr.length;j++){
							dataTail.append(convert32Bytes(arr[j].substring(2)));
						}
					}
				}
			}
		}
		return functionId+dataTail;
	}
	

	private static String convertASC(String string) {
		String s = "";
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<string.length();i++){
			sb.append(Integer.toHexString(string.charAt(i)-'0'+'0'));
		}
		
		int len = sb.length();
		for(int i=0;i<64-len;i++){
			sb.append('0');
		}
		return sb.toString();
	}

	private static String convert32Bytes(String string) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<64-string.length();i++){
			sb.append('0');
		}
		return sb.toString()+string;
	}
	
}
