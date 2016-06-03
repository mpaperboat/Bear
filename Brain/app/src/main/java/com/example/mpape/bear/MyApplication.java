package com.example.mpape.bear;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.Buffer;
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
    private SoundPool sp;
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
    private BufferedReader pht;
    public LinkedList<Bitmap> phts;
    public void onCreate() {
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
                        if(sendData("-"))
                            continue;
                        mmSocket.connect();
                        mmOutputStream = mmSocket.getOutputStream();
                        mmInputStream = mmSocket.getInputStream();
                        pht=new BufferedReader(new InputStreamReader(mmInputStream));
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
        server = new SocketServer();
        server.start();
        sp=new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(100);
                        sendData("C");
                        if(Integer.valueOf(pht.readLine())<20) {
                            server.add(0);
                            server.mDataListener.conv(1);
                            Thread.sleep(5000);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
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
