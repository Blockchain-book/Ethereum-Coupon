package com.creation.coupon_merchant_part.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.creation.coupon_merchant_part.R;
import com.creation.coupon_merchant_part.pojo.CouponStateItem;
import com.creation.coupon_merchant_part.pojo.SettlementRecord;

import java.util.List;

/**
 * Created by thomas on 2017/3/3.
 */

public class SettlementRecordAdapter extends ArrayAdapter<SettlementRecord> {
    private int viewResource;
    public SettlementRecordAdapter(Context context, int viewResource, List<SettlementRecord> records){
        super(context,viewResource,  records);
        this.viewResource = viewResource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        SettlementRecord settlementRecord = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(viewResource, null);
        TextView operationDateTextView = (TextView)view.findViewById(R.id.operation_date);
        TextView operationAmountTextView = (TextView)view.findViewById(R.id.operation_amount);
        TextView operationStatusTextView = (TextView)view.findViewById(R.id.operation_status);

        operationDateTextView.setText(settlementRecord.getOperationDate());
        operationAmountTextView.setText(String.valueOf(settlementRecord.getOperationAmount()));
        operationStatusTextView.setText(settlementRecord.getRecheckStatus());

        return view;
    }
}
