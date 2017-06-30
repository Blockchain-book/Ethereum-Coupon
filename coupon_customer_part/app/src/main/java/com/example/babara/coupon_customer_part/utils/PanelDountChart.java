package com.example.babara.coupon_customer_part.utils;

/**
 * Created by babara on 2017/3/13.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;


public class PanelDountChart extends View{

    private int ScrWidth,ScrHeight;
    private float arrPer[];
    private int totalValue;

    //演示用的百分比例,实际使用中，即为外部传入的比例参数
    //private final float arrPer[] = new float[]{20f,30f,10f,40f};
    //RGB颜色数组
    private final int arrColorRgb[][] = { {30,144,255},
            {255,105,180}, {169,169,169}} ;


    public PanelDountChart(Context context, float arrPer[], int totalValue) {
        super(context);

        //屏幕信息
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ScrHeight = dm.heightPixels;
        ScrWidth = dm.widthPixels;
        this.arrPer = arrPer;
        this.totalValue = totalValue;
    }


    public void onDraw(Canvas canvas){
        //画布背景
        canvas.drawColor(Color.WHITE);

        float cirX = ScrWidth / 2;
        float cirY = ScrHeight / 4 ;
        float radius = ScrHeight / 5 ;//150;

        float arcLeft = cirX - radius;
        float arcTop  = cirY - radius ;
        float arcRight = cirX + radius ;
        float arcBottom = cirY + radius ;
        RectF arcRF0 = new RectF(arcLeft ,arcTop,arcRight,arcBottom);

        //画笔初始化
        Paint PaintArc = new Paint();
        Paint PaintLabel = new Paint();
        PaintLabel.setTextSize(25);
        PaintLabel.setColor(Color.WHITE);

        //位置计算类
        XChartCalc xcalc = new XChartCalc();

        float Percentage = 0.0f;
        float CurrPer = 0.0f;
        int i= 0;
        for(i=0; i<arrPer.length; i++)
        {
            //将百分比转换为饼图显示角度
            Percentage = 360 * (arrPer[i]/ 100);
            Percentage = (float)(Math.round(Percentage *100))/100;
            //分配颜色
            PaintArc.setARGB(255,arrColorRgb[i][0], arrColorRgb[i][1], arrColorRgb[i][2]);

            //在饼图中显示所占比例
            canvas.drawArc(arcRF0, CurrPer, Percentage, true, PaintArc);
            //计算百分比标签
            xcalc.CalcArcEndPointXY(cirX, cirY, radius - radius/2/2, CurrPer + Percentage/2);
            //标识
            if (arrPer[i] != 0) {
                canvas.drawText(Float.toString(arrPer[i]) + "%", 3*xcalc.getPosX()/4, xcalc.getPosY(), PaintLabel);
            }
            //下次的起始角度
            CurrPer += Percentage;
        }

        //画圆心
        PaintArc.setColor(Color.WHITE);
        canvas.drawCircle(cirX,cirY,radius/2,PaintArc);

        PaintLabel.setColor(Color.BLUE);
        String totalValueString = "总价值约为"+String.valueOf(totalValue)+"元";
        float length = PaintLabel.measureText(totalValueString);
        canvas.drawText(totalValueString, ScrWidth/2-length/2, cirY, PaintLabel);

    }
}
