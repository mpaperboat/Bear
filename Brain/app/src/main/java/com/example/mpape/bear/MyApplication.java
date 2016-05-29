package com.example.mpape.bear;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;

import java.io.InputStream;
import java.io.OutputStream;

public class MyApplication extends Application{

    private String mylabel ;
    public BluetoothSocket mmSocket;
    public OutputStream mmOutputStream;
    public InputStream mmInputStream;
    public Bitmap mImage, mLastFrame;
    public String getLabel(){
        return mylabel;
    }
    public void setLabel(String s){
        this.mylabel = s;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        setLabel("Welcome!"); //初始化全局变量
    }
}
