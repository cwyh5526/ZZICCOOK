package com.example.user.zziccook;


public class CalculateMeasure  {
    int userValue; // 사용자로부터 입력받은 계량값
    //double bottom;
    //double top;
    double maxVol;
    double realHeight;
    public double userHeight;

    /*
    public  void  setBottom(double bottom){
        this.bottom=bottom;
    }
    public void  setTop(double top){
        this.top=top;
    }
    */
    public void  setMaxVol(double maxVol){
        this.maxVol=maxVol;
    }
    public  void  setRealHeight(double realHeight){
        this.realHeight=realHeight;
     }
    public  void  setUserValue(int userValue){
        this.userValue=userValue;
    }

    public void calculateUserHeight(){
        userHeight = userValue*realHeight/maxVol;
    }




}

