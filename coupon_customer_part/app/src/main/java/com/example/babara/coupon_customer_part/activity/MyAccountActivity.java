package com.example.babara.coupon_customer_part.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.babara.coupon_customer_part.R;
import com.example.babara.coupon_customer_part.zxing.encoding.EncodingUtils;

public class MyAccountActivity extends Activity implements View.OnClickListener{

    private ImageView backImageView;
    private Button logOutButton;
    private int QR_WIDTH = 200, QR_HEIGHT = 200;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_account);

        backImageView = (ImageView)findViewById(R.id.my_account_back);
        logOutButton = (Button)findViewById(R.id.log_out_button);
        imageView = (ImageView)findViewById(R.id.create_myAccount_QR);

        backImageView.setOnClickListener(this);
        logOutButton.setOnClickListener(this);

        createCustomerCode();
    }

    public void createCustomerCode(){
        SharedPreferences read = getSharedPreferences("data", MODE_PRIVATE);
        String mobileNo = read.getString("mobileNo", "");
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.mainbg4);
        Bitmap bitmap= EncodingUtils.createQRCode(mobileNo, 500, 500, logoBitmap);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.my_account_back:
                this.finish();
                break;
            case R.id.log_out_button:
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putBoolean("hasLogined", false);
                editor.commit();
                Toast.makeText(MyAccountActivity.this, "您已退出登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                intent.putExtra("id", 1);
                startActivity(intent);

        }
    }
}
