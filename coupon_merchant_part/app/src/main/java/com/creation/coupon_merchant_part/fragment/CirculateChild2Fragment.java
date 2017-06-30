package com.creation.coupon_merchant_part.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.activity.MainActivity;
import com.creation.coupon_merchant_part.pojo.CouponRequestItem;
import com.creation.coupon_merchant_part.pojo.CouponVerifyItem;
import com.creation.coupon_merchant_part.pojo.UrlManager;
import com.creation.coupon_merchant_part.utils.AsyncResponse;
import com.creation.coupon_merchant_part.utils.CouponRequestAdapter;
import com.creation.coupon_merchant_part.utils.CouponVerifyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
/**
 * Created by Babara Liang on 2016/12/26.
 *  Description:这是流通Fragment的子Fragment，实现优惠券验证的功能
 */

public class CirculateChild2Fragment extends Fragment implements View.OnClickListener{
    private ListView listView;                      //放置优惠券使用确认请求列表的ListView
    private ArrayList<CouponVerifyItem> list;     //请求确认的数据列表
    private TextView userText;                      //当点击每个条目时，在上方展示用户名
    private TextView expenseTimeText;               //当点击每个条目时，在上方展示使用优惠券的时间
    private TextView expenseAmountText;             //当点击每个条目时，在上方展示使用优惠券的金额
    private String urlString;
    private String requestState;
    private String merchantId;
    private Button confirmPay;
    private CouponVerifyItem curTempItem;
    private Handler handlerRequest;
    private int currentPosition;
    private CouponVerifyAdapter arrayAdpter;
    private Runnable runnable;

    public CirculateChild2Fragment(){
    }
    @Override
    public void onClick(View view) {
        if (curTempItem == null){
            Toast.makeText(getActivity(),"当前无申请用户",Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jo = new JSONObject();
        try {
            jo.put("applicationCode", curTempItem.getApplicationCode());
            jo.put("id",merchantId);
        }catch(JSONException je){
            je.printStackTrace();
        }
        String conditionString = jo.toString();
        String url = UrlManager.createUrlString("/merchant/updateCouponPayApp.action");
        HttpTaskPost htConfPay =  new HttpTaskPost(conditionString, url);
        confirmPay.setEnabled(false);
        htConfPay.asyncResponse = new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                System.out.println(data);
                try {
                    JSONObject jo2 = new JSONObject(data);
                    if(jo2.get("resultCode").equals("1")){

                        list.remove(currentPosition);
                        arrayAdpter = new CouponVerifyAdapter(getActivity(), R.layout.coupon_verify_item, list);
                        listView.setAdapter(arrayAdpter);
                        Toast.makeText(getActivity(),"请求处理成功！",Toast.LENGTH_SHORT).show();
                        if(list!=null && list.size()!=0){
                            userText.setText(list.get(0).getUser());
                            expenseTimeText.setText(list.get(0).getTime());
                            expenseAmountText.setText(list.get(0).getCouponValue()+"元");
                            curTempItem = list.get(0);
                            currentPosition = 0;
                        }else{
                            userText.setText("XXX");
                            expenseTimeText.setText("XXX");
                            expenseAmountText.setText("XXX" + " 元");
                            curTempItem = null;
                        }
                    }else{
                        Toast.makeText(getActivity(),"请求处理失败！",Toast.LENGTH_SHORT).show();
                    }
                    confirmPay.setEnabled(true);
                }catch(JSONException je){
                    je.printStackTrace();
                }

            }
        };
        htConfPay.execute();
    }
    //@Nullable表示定义的字段可以为空
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circulate_child2,container,false);
        listView = (ListView)view.findViewById(R.id.coupon_verify_list);
        userText = (TextView)view.findViewById(R.id.circulate_user2_text);
        expenseTimeText = (TextView)view.findViewById(R.id.circulate_time2_text);
        expenseAmountText = (TextView)view.findViewById(R.id.circulate_amount2_text);
        confirmPay = (Button)view.findViewById(R.id.confirm_pay);
        confirmPay.setEnabled(true);
        SharedPreferences sp = getActivity().getSharedPreferences("currentSession",Context.MODE_PRIVATE);
        merchantId = sp.getString("merchantId","111");
        //获取MainActivity中获取的确认请求列表
        //如果列表不为空，则展示列表的第一个（类似于实现第一个Item的自动点击功能）
        urlString = new UrlManager().createUrlString("/merchant/queryCouponPayApp.action");
        requestState = "id="+merchantId;

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerRequest.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handlerRequest=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                new HttpTask(requestState, urlString).execute();
                handlerRequest.postDelayed(this, 30000);
            }
        };
        handlerRequest.postDelayed(runnable, 2000);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        confirmPay.setOnClickListener((View.OnClickListener)this);
    }

    class HttpTask extends AsyncTask<Void, Void, String> {
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
            if(result!=null && !result.equals("[]")) {
                super.onPostExecute(result);
                try {
                    list = new ArrayList<CouponVerifyItem>();
                    JSONArray jsonArray = new JSONArray(result);
                    int length = jsonArray.length();
                    CouponVerifyItem[] items = new CouponVerifyItem[length];
                    for (int i = 0; i < length; i++) {
                        JSONObject tempJsonObject = jsonArray.getJSONObject(i);
                        String applicationCode = tempJsonObject.getString("applicationCode");
                        String usernameStr = tempJsonObject.getString("account");
                        String timeStr = tempJsonObject.getString("applicationTime");
                        Integer couponValue = tempJsonObject.getInt("couponValue");
                        items[i] = new CouponVerifyItem(applicationCode, usernameStr, timeStr, couponValue);
                        list.add(items[i]);
                    }
                    if (list != null) {
                        userText.setText(list.get(0).getUser());
                        expenseTimeText.setText(list.get(0).getTime());
                        expenseAmountText.setText(list.get(0).getCouponValue() + "元");
                        curTempItem = list.get(0);
                        currentPosition = 0;
                    }
                    arrayAdpter = new CouponVerifyAdapter(getActivity(), R.layout.coupon_verify_item, list);
                    listView.setAdapter(arrayAdpter);
                    //点击列表中的每一条，会将该条目的信息展示在上部分，以共商家查看详细信息，确定发放或者取消
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CouponVerifyItem c = list.get(position);
                            userText.setText(c.getUser());
                            expenseTimeText.setText(c.getTime());
                            expenseAmountText.setText(c.getCouponValue() + "元");
                            curTempItem = c;
                            currentPosition = position;
                            confirmPay.setEnabled(true);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(Void... params) {
            try{
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
                return null;
            }
        }
    }

    class HttpTaskPost extends AsyncTask<Void, Void, String> {
        private String conditionString;
        private String urlString;
        public AsyncResponse asyncResponse;

        public HttpTaskPost(String conditionString,String urlString){
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
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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