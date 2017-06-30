package com.example.babara.coupon_customer_part.utils;

/**
 * Created by babara on 2017/3/13.
 */

public class XChartCalc {

    //Position位置
    private float posX = 0.0f;
    private float posY = 0.0f;

    public XChartCalc()
    {

    }


    //依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
    public void CalcArcEndPointXY(float cirX, float cirY, float radius, float cirAngle){

        //将角度转换为弧度
        float arcAngle = (float) (Math.PI * cirAngle / 180.0);
        if (cirAngle < 90)
        {
            posX = cirX + (float)(Math.cos(arcAngle)) * radius;
            posY = cirY + (float)(Math.sin(arcAngle)) * radius;
        }
        else if (cirAngle == 90)
        {
            posX = cirX;
            posY = cirY + radius;
        }
        else if (cirAngle > 90 && cirAngle < 180)
        {
            arcAngle = (float) (Math.PI * (180 - cirAngle) / 180.0);
            posX = cirX - (float)(Math.cos(arcAngle)) * radius;
            posY = cirY + (float)(Math.sin(arcAngle)) * radius;
        }
        else if (cirAngle == 180)
        {
            posX = cirX - radius;
            posY = cirY;
        }
        else if (cirAngle > 180 && cirAngle < 270)
        {
            arcAngle = (float) (Math.PI * (cirAngle - 180) / 180.0);
            posX = cirX - (float)(Math.cos(arcAngle)) * radius;
            posY = cirY - (float)(Math.sin(arcAngle)) * radius;
        }
        else if (cirAngle == 270)
        {
            posX = cirX;
            posY = cirY - radius;
        }
        else
        {
            arcAngle = (float) (Math.PI * (360 - cirAngle) / 180.0);
            posX = cirX + (float)(Math.cos(arcAngle)) * radius;
            posY = cirY - (float)(Math.sin(arcAngle)) * radius;
        }

    }


    //////////////
    public float getPosX() {
        return posX;
    }


    public float getPosY() {
        return posY;
    }
    //////////////
}
