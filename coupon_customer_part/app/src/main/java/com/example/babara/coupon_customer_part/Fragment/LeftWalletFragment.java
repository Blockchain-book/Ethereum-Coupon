package com.example.babara.coupon_customer_part.Fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.activity.ConfirmPayActivity;
import com.example.babara.coupon_customer_part.activity.PayCouponActivity;
import com.example.babara.coupon_customer_part.pojo.NearbyMerchantItem;
import com.example.babara.coupon_customer_part.utils.AsyncResponse;
import com.example.babara.coupon_customer_part.utils.CyclicGraphView;
import com.example.babara.coupon_customer_part.utils.HttpTaskForJsonTool;
import com.example.babara.coupon_customer_part.utils.HttpTaskTool;
import com.example.babara.coupon_customer_part.utils.NearbyMerchantAdapter;
import com.example.babara.coupon_customer_part.utils.PanelDountChart;
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

public class LeftWalletFragment extends Fragment{
    public LeftWalletFragment() {
    }

    private TextView merchantTextView1;
    private TextView merchantTextView2;
    private TextView merchantTextView3;
    private LinearLayout panelDountChartLayout;
    private String latitude;
    private String longitude;
    private LocationClient mLocationClient = null;
    private BDLocationListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_left_wallet, container, false);
        merchantTextView1 = (TextView)v.findViewById(R.id.merchant_text_1);
        merchantTextView2 = (TextView)v.findViewById(R.id.merchant_text_2);
        merchantTextView3 = (TextView)v.findViewById(R.id.merchant_text_3);
        panelDountChartLayout = (LinearLayout)v.findViewById(R.id.panelDountChart);

        //获取优惠券状态信息
        queryCouponStatus();

        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        initLocation();
        mListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                int locType = bdLocation.getLocType();
                switch (locType){
                    case BDLocation.TypeServerError:
                        Toast.makeText(getActivity().getApplicationContext(),"服务端网络定位失败",Toast.LENGTH_SHORT).show();
                        break;
                    case BDLocation.TypeNetWorkException:
                        Toast.makeText(getActivity().getApplicationContext(),"请检查网络是否畅通",Toast.LENGTH_SHORT).show();
                        break;
                    case BDLocation.TypeCriteriaException:
                        Toast.makeText(getActivity().getApplicationContext(),"请检查是否处于飞行模式或重启手机",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        latitude = bdLocation.getLatitude()+"";
                        longitude = bdLocation.getLongitude()+"";
                        nearByMerchant();
                        break;
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        mLocationClient.registerLocationListener(mListener);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationClient.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationClient.stop();
    }

    private void initLocation() {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClientOption.setCoorType("gcj02");
        locationClientOption.setScanSpan(10000);
        locationClientOption.setIsNeedAddress(false);
        locationClientOption.setOpenGps(true);
        locationClientOption.setIsNeedLocationDescribe(false); //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationClientOption.setIgnoreKillProcess(false); //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationClientOption.SetIgnoreCacheException(false);
        locationClientOption.setEnableSimulateGps(true);
        mLocationClient.setLocOption(locationClientOption);
    }

    private void queryCouponStatus() {
        String url = new UrlManager().createUrlString("/consumer/queryCouponStatus.action");
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        String consumerId = sp.getString("consumerId","");
        String request = "consumerId="+consumerId;
        HttpTaskTool couponStatusHttpTask = new HttpTaskTool(request,url);
        couponStatusHttpTask.execute();
        couponStatusHttpTask.setOnAsyncResponse(new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"".equals(data)){
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        double outDateRate = jsonObject.getDouble("outDate");
                        double almostOutDateRate = jsonObject.getDouble("almostOutDate");
                        double unusedRate = jsonObject.getDouble("unused");
                        int totalValue = jsonObject.getInt("totalValue");
                        float[] generalValue = {(float) unusedRate,(float) almostOutDateRate,(float) outDateRate};
                        panelDountChartLayout.addView(new PanelDountChart(getActivity().getApplicationContext(), generalValue, totalValue));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //获取附近商户
    private void nearByMerchant() {
        String url = new UrlManager().createUrlString("/merchant/fetchNearby.action");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude",latitude);
            jsonObject.put("longitude",longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String request = jsonObject.toString();
        HttpTaskForJsonTool nearByMerchantHttpTask = new HttpTaskForJsonTool(request, url);
        nearByMerchantHttpTask.execute();
        nearByMerchantHttpTask.setOnAsyncResponse(new AsyncResponse() {
            @Override
            public void onDataReceivedSuccess(String data) {
                if (data!=null && !"[]".equals(data)){
                    try{
                        final NearbyMerchantItem[] merchant = new NearbyMerchantItem[3];
                        JSONArray jsonArray = new JSONArray(data);
                        int length = jsonArray.length();
                        for(int i = 0; i < length; i++){
                            JSONObject tempJsonObject = jsonArray.getJSONObject(i);
                            String merchantId = tempJsonObject.getString("merchantId");
                            String distance = tempJsonObject.getString("distance");
                            String merchantName = tempJsonObject.getString("merchantName");
                            String merchantInfo = tempJsonObject.getString("merchantInfo");
                            merchant[i] = new NearbyMerchantItem(merchantId,distance, merchantName, merchantInfo);
                        }

                        if (merchant[0]!=null) {
                            merchantTextView1.setText(merchant[0].getMerchantName());
                        }
                        if (merchant[1]!=null){
                            merchantTextView2.setText(merchant[1].getMerchantName());
                        }
                        if (merchant[2]!=null){
                            merchantTextView3.setText(merchant[2].getMerchantName());
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
