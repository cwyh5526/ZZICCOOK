package com.example.user.zziccook.OpenCV;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Button;

/**
 * Created by user on 2016-10-03.
 */
public class MeasureHeight {

    private float mPhoneHeight;

    private double mX,mY,mZ,mDistance,mHeight,mDistanceAngle,mHeightAngle;


    public MeasureHeight(float phoneHeight){
        mPhoneHeight=phoneHeight;
    }
    public void setXYZ(double x, double y, double z)
    {
        double norm = Math.sqrt(x * x + y * y + z * z);

        this.mX = x / norm;
        this.mY = y / norm;
        this.mZ = z / norm;
    }

    public double getDistance()
    {
        double inclination = Math.toDegrees(Math.acos(mZ));
        double angle;
        angle = Math.round((inclination) * 10.0) / 10.0;    // 지면과 스마트폰 사이의 각도
        mDistanceAngle = angle;

        double cosAngle =Math.cos(Math.toRadians(angle));
        mDistance=Math.round(mPhoneHeight/cosAngle);

        return   mDistance; // 스마트폰과 물체 사이의 거리
    }


    public float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }

    public double getAngle(){
        double inclination = Math.toDegrees(Math.acos(mZ));
        double angle;
        angle = Math.round((inclination) * 10.0) / 10.0;    // 지면과 스마트폰 사이의 각도
        return angle;
    }

    public double getHeight()
    {
        mHeightAngle = Math.toDegrees(Math.acos(mZ));

        if(mHeightAngle<90){   //예각일 경우
//            Log.d("if","distanceAngle"+mDistanceAngle+"angle"+mHeightAngle);
            double deltaL = mPhoneHeight/Math.cos(Math.toRadians(mHeightAngle))
                    - mPhoneHeight/Math.cos(Math.toRadians(mDistanceAngle));
            double tanAngle = Math.tan(Math.toRadians(mHeightAngle));

            mHeight=Math.round(deltaL/tanAngle);
            return mHeight;  //물체의 높이
        }else{  //둔각일 경우
//            Log.d("ELSE","distanceAngle"+mDistanceAngle+"angle"+mHeightAngle);
            double tanAngle = Math.tan(Math.toRadians(mHeightAngle-90));

            double x= mPhoneHeight/Math.sin(Math.toRadians(mHeightAngle-90));
            this.mHeightAngle =mHeightAngle-mDistanceAngle;
            mHeight= Math.round(tanAngle*(x+mDistance));

            return mHeight; //물체의 높이
        }


    }

}
