package com.example.babara.coupon_customer_part.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.activity.ConfirmPayActivity;
import com.example.babara.coupon_customer_part.activity.PayCouponActivity;
import com.example.babara.coupon_customer_part.pojo.CouponItem;
import com.example.babara.coupon_customer_part.pojo.NearbyMerchantItem;
import com.example.babara.coupon_customer_part.utils.CouponAdapter;
import com.example.babara.coupon_customer_part.utils.NearbyMerchantAdapter;
import com.example.babara.coupon_customer_part.utils.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RightWalletFragment extends Fragment {


    public RightWalletFragment() {
        // Required empty public constructor
    }

    private ArrayList<CouponItem> list;
    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right_wallet, container, false);
        listView = (ListView) view.findViewById(R.id.all_coupon_list);


        String url = new UrlManager().createUrlString("/consumer/queryAllCouponDetails.action");
        SharedPreferences sp = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        String request = "consumerId="+sp.getString("consumerId","111");
        new HttpTask(request, url).execute();

        return view;
    }



    public class HttpTask extends AsyncTask<Void, Void, String> {
        private String conditionString;
        private String urlString;

        public HttpTask(String conditionString,String urlString){
            this.conditionString = conditionString;
            this.urlString = urlString;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        //在这里使用AsyncResponse接口来实现获取doInBackground的执行结果
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                list = new ArrayList<CouponItem>();
                JSONObject jo = new JSONObject(result);
                JSONArray outDate = jo.getJSONArray("outDate");
                JSONArray almostOutDate = jo.getJSONArray("almostOutDate");
                JSONArray unused = jo.getJSONArray("unused");

                for(int i = 0; i < outDate.length(); i++){
                    JSONObject tempJsonObject = outDate.getJSONObject(i);
                    String couponId = tempJsonObject.getString("couponId");
                    String amount = tempJsonObject.getInt("value")+"";
                    String obtainTime = tempJsonObject.getString("consumptionDate");
                    String validityFrom = tempJsonObject.getString("startDate");
                    String validityTo = tempJsonObject.getString("endDate");
                    String merchantName = tempJsonObject.getString("merchantName");
                    Integer obtainValue = tempJsonObject.getInt("consumptionValue");
                    CouponItem ciTemp = new CouponItem(couponId,amount, obtainTime, validityFrom, validityTo);
                    ciTemp.setExpense(obtainValue);
                    ciTemp.setMerchantName(merchantName);
                    ciTemp.setState("1");
                    list.add(ciTemp);
                }
                for(int i = 0; i < almostOutDate.length(); i++){
                    JSONObject tempJsonObject = almostOutDate.getJSONObject(i);
                    String couponId = tempJsonObject.getString("couponId");
                    String amount = tempJsonObject.getInt("value")+"";
                    String obtainTime = tempJsonObject.getString("consumptionDate");
                    String validityFrom = tempJsonObject.getString("startDate");
                    String validityTo = tempJsonObject.getString("endDate");
                    String merchantName = tempJsonObject.getString("merchantName");
                    Integer obtainValue = tempJsonObject.getInt("consumptionValue");
                    CouponItem ciTemp = new CouponItem(couponId,amount, obtainTime, validityFrom, validityTo);
                    ciTemp.setExpense(obtainValue);
                    ciTemp.setMerchantName(merchantName);
                    ciTemp.setState("2");
                    list.add(ciTemp);
                }
                for(int i = 0; i < unused.length(); i++){
                    JSONObject tempJsonObject = unused.getJSONObject(i);
                    String couponId = tempJsonObject.getString("couponId");
                    String amount = tempJsonObject.getInt("value")+"";
                    String obtainTime = tempJsonObject.getString("consumptionDate");
                    String validityFrom = tempJsonObject.getString("startDate");
                    String validityTo = tempJsonObject.getString("endDate");
                    String merchantName = tempJsonObject.getString("merchantName");
                    Integer obtainValue = tempJsonObject.getInt("consumptionValue");
                    CouponItem ciTemp = new CouponItem(couponId,amount, obtainTime, validityFrom, validityTo);
                    ciTemp.setExpense(obtainValue);
                    ciTemp.setMerchantName(merchantName);
                    ciTemp.setState("3");
                    list.add(ciTemp);
                }


                CouponAdapter arrayAdpter = new CouponAdapter(getActivity(), R.layout.coupon_item, list);

                listView.setAdapter(arrayAdpter);
            }catch (JSONException e){
                e.printStackTrace();
            }

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


}
