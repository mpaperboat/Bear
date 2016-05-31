package com.example.mpape.bear;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MyApplication extends Application{

    private String mylabel ;
    public BluetoothSocket mmSocket;
    public OutputStream mmOutputStream;
    public InputStream mmInputStream;
    public Bitmap mImage, mLastFrame;
    public SocketServer server;
    public LinkedList<Bitmap> mQueue = new LinkedList<Bitmap>();
    public static final int MAX_BUFFER = 15;
    public String getLabel(){
        return mylabel;
    }
    public void setLabel(String s){
        this.mylabel = s;
    }
    private BluetoothDevice mmDevice;
    private BluetoothAdapter mBTAdapter;
    private UUID uuid;
    public LinkedList<Bitmap> phts;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        phts=new LinkedList<Bitmap>();
        super.onCreate();
        System.out.println("welcome");
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
        if(pairedDevices.size()!=0)
            mmDevice = pairedDevices.iterator().next();
        uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        }
        catch (Exception e){

        }
        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(100);
                        //if(activepohoto<2)
                        //  continue;
                        if(sendData("-"))
                            continue;
                        mmSocket.connect();
                        mmOutputStream = mmSocket.getOutputStream();
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
       server = new SocketServer();
       server.start();
    }
    boolean sendData(String m){
        try{
            String msg = m;
            mmOutputStream.write(msg.getBytes());
            System.out.println("Data Sent");
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
