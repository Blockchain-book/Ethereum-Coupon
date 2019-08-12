package com.block.coupon.rpc;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class WebResourceObj {
	
//	private final static String ROUTE="http://192.168.137.176:8545";

//	private final static String ROUTE="http://10.82.59.76:8545";

    private final static String ROUTE="http://127.0.0.1:8545";

	public static WebResource getWebResource(){
//    	客户端配置对象
        ClientConfig clientConfig = new DefaultClientConfig();
//        进行相关配置
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE );
//        使用客户端配置对象创建客户端
        Client client = Client.create(clientConfig);
        
        WebResource r = client.resource(ROUTE);
      
        return r;
	}
}
