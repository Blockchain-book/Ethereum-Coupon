package com.block.coupon.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import com.alibaba.fastjson.JSONObject;
/**
 * Created by thomas on 2017/2/28.
 */

public class JSONUtils {
    public static<T> T jsonToObj(String json_str, Class<T> obj){
        T t = null;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            t = objectMapper.readValue(json_str,obj);
        }catch(Exception e){
            e.printStackTrace();
        }
        return t;
    }

    public static<T> String objTojson(T obj) throws JSONException, IOException{
		/*String json_str = "";
		ObjectMapper objectMapper = new ObjectMapper();
		json_str = objectMapper.writeValueAsString(obj);
		return json_str;*/
        return JSONObject.toJSONString(obj);
    }

    public static<T> List<T> jArraytoList(String json_str, Class<T> obj) throws JSONException{
        List<T> result_list = new ArrayList<T>();
        JSONArray array = new JSONArray(json_str);
        for(int i=0;i<array.length();i++){
            org.codehaus.jettison.json.JSONObject temp = array.getJSONObject(i);
            T t = jsonToObj(temp.toString(), obj);
            result_list.add(t);
        }
        if(result_list.size()!=0){
            return result_list;

        }else{
            return null;
        }
    }
}
