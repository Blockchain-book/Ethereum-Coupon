package com.creation.coupon_merchant_part.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.creation.coupon_merchant_part.R;

public class NoCouponRulerActivity extends AppCompatActivity {
    private TextView issureCouponTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_coupon_ruler);
        issureCouponTextView = (TextView)findViewById(R.id.issureRulerText);
        issureCouponTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoCouponRulerActivity.this,IssueActivity.class);
                startActivity(intent);
                NoCouponRulerActivity.this.finish();
            }
        });
    }

}
