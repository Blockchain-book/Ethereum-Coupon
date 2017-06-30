package com.example.babara.coupon_customer_part.utils;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTaskTool extends AsyncTask<Void, Void, String> {
    private String conditionString;
    private String urlString;

    public AsyncResponse asyncResponse;
    //外部类使用该方法即可获取交互结果
    public void setOnAsyncResponse(AsyncResponse asyncResponse)
    {
        this.asyncResponse = asyncResponse;
    }

    public HttpTaskTool(String conditionString, String urlString){
        this.conditionString = conditionString;
        this.urlString = urlString;
    }
    //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
    //在这里使用AsyncResponse接口来实现获取doInBackground的执行结果
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        asyncResponse.onDataReceivedSuccess(result);

    }
    //doInBackground方法内部执行后台任务,不可在此方法内修改UI
    @Override
    protected String doInBackground(Void... params) {
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            String data = conditionString;
            byte[] bytes = data.getBytes();
            conn.getOutputStream().write(bytes); // 写出数据
            //读取服务器的返回结果
            InputStream is = null;
            StringBuffer sb = new StringBuffer();
            String str = "";
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while((str = reader.readLine())!=null){
                    sb.append(str);
                }
            }
            String result = sb.toString();
            return result;
        }catch(Exception e){
        e.printStackTrace();
        }
        return null;
    }
}