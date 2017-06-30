package com.example.babara.coupon_customer_part.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by babara on 2017/3/12.
 */

public class CyclicGraphView {
    private Context context;
    private ArrayList<double[]> values;
    private ArrayList<String[]> descriptions;
    private int[] colors = new int[] { 0xffa9d86e, 0xff50b8d8, 0xfff9a415,
            0xfffa9c9c, Color.GREEN };

    public CyclicGraphView(Context context, ArrayList<double[]> values,
                           ArrayList<String[]> descriptions) {
        this.context = context;
        this.values = values;
        this.descriptions = descriptions;
    }

    public View excute() {
        MultipleCategorySeries dataset = buildMultipleCategorySeries("");
        DefaultRenderer renderer = buildCategoryRenderer();
        return ChartFactory.getDoughnutChartView(context, dataset, renderer);
    }

    protected MultipleCategorySeries buildMultipleCategorySeries(String title) {
        MultipleCategorySeries multipleCategorySeries=new MultipleCategorySeries(title);
        Integer total = 0;
        int count=0;
        ArrayList<String []> des=new ArrayList<String[]>();
        ArrayList<double []> val=new ArrayList<double[]>();
        String []precent=new String[values.get(0).length];
        for (int i = 0; i < values.get(0).length; i++) {
            total += (int)values.get(0)[i];
        }
        NumberFormat nft=NumberFormat.getPercentInstance();
        nft.setMaximumFractionDigits(0);
        for (int i = 0; i < values.get(0).length; i++) {
            precent[i]=nft.format(values.get(0)[i]/total)+"";
        }
        String []newprecent=new String[values.get(0).length];
        double []newval=new double[values.get(0).length];
        for(int i=0;i<values.get(0).length;i++){
            if(!precent[i].equals("0%")){
                newprecent[count]=precent[i];
                newval[count]=values.get(0)[i];
                count=count+1;
            }
        }
        String []desresult=new String[count];
        double []valresult=new double[count];
        System.arraycopy(newprecent, 0, desresult, 0, count);
        System.arraycopy(newval, 0, valresult, 0, count);
        des.add(desresult);
        val.add(valresult);
        multipleCategorySeries.add(des.get(0), val.get(0));

        return multipleCategorySeries;
    }
    protected DefaultRenderer buildCategoryRenderer() {
        DefaultRenderer renderer = new DefaultRenderer();
        // 显示标签
        renderer.setShowLabels(true);
        // 不显示底部说明
        renderer.setShowLegend(false);
        // 设置标签字体大小
        renderer.setLabelsTextSize(24);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setZoomEnabled(false);
        renderer.setPanEnabled(false);
        renderer.setDisplayValues(true);
        for (int i = 0; i < values.get(0).length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            if(values.get(0)[i]!=0){
                r.setColor(colors[i]);
                r.setChartValuesFormat(NumberFormat.getPercentInstance());
                renderer.addSeriesRenderer(r);}
        }
        return renderer;
    }
}
