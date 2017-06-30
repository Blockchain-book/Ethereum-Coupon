package com.creation.coupon_merchant_part.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.zxing.encoding.EncodingUtils;

/**
 * Description:该类主要展示从服务器获取的二维码。
 */
public class QRCodeActivity extends Activity implements View.OnClickListener{
    private ImageView backImageView;
    private ImageView merchantQRImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_qrcode);
        backImageView = (ImageView)findViewById(R.id.qrcode_back);
        backImageView.setOnClickListener(this);
        merchantQRImage = (ImageView)findViewById(R.id.merchant_qr_image);

        createMerchantCode();
    }

    private void createMerchantCode() {
        SharedPreferences sp = getSharedPreferences("currentSession",MODE_PRIVATE);
        String merchantId = sp.getString("merchantId","");
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.mainbg4);
        Bitmap bitmap= EncodingUtils.createQRCode(merchantId, 500, 500, logoBitmap);
        merchantQRImage.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.qrcode_back:
                //点击返回按钮，退出此页
                this.finish();
                break;
        }
    }
}
