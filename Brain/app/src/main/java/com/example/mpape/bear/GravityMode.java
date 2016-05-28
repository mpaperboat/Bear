package com.example.mpape.bear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.*;
import java.util.Random;
import android.util.Log;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.widget.Toast;

public class GravityMode extends Activity implements SensorEventListener,SurfaceHolder.Callback {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final String TAG="TestTag";
    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private Camera camera = null;
    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private UUID uuid;
    private int bconnected;
    private int statu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);//全屏

        setContentView(R.layout.activity_gravity_mode);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_GAME);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(GravityMode.this,PhotoMode.class);
                startActivity(intent);
            }
        });
        context = this;
        surfaceview = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    checkblue();
                    if(getdbg()=="Bluetooth On")
                        return;
                    mBTAdapter = BluetoothAdapter.getDefaultAdapter();
                    Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
                    if (pairedDevices.size() != 1) {
                        System.out.print(pairedDevices.size());
                        throw new Exception("haha");
                    }

                    mmDevice = pairedDevices.iterator().next();
                    uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                    mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                    mmSocket.connect();
                    mmOutputStream = mmSocket.getOutputStream();
                    mmInputStream = mmSocket.getInputStream();
                    setdbg("Bluetooth On");
                    statu=0;
                } catch (Exception e) {
                    setdbg("Bluetooth Error");
                }
            }
        });
        final ImageButton ibutton = (ImageButton) findViewById(R.id.button6);
        ibutton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                checkblue();
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(discheck())
                        sendData("W");
                        System.out.print("w pressed");
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("Q");
                        return true;
                }
                return false;
            }
        });
        final ImageButton ibutton2 = (ImageButton) findViewById(R.id.button3);
        ibutton2.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                checkblue();
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendData("S");
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("Q");
                        return true;
                }
                return false;
            }
        });
    }
    void  checkblue(){
        if(!sendData("-")){
            setdbg("Bluetooth Error");
        }
    }
    void setdbg(String s){
        TextView dbg=(TextView)findViewById(R.id.textView);
        dbg.setText(s);
    }
    String getdbg(){
        TextView dbg=(TextView)findViewById(R.id.textView);
        return String.valueOf(dbg.getText());
    }
    boolean sendData(String m){
        try{
            String msg = m;
            mmOutputStream.write(msg.getBytes());
           // System.out.println("Data Sent");
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            if(y>5)y=5;
            if(y<-5)y=-5;
            MyImageView ps = (MyImageView) findViewById(R.id.imageView2);
            int sign=1;
            if(y<0)sign=-1;
            y=y*sign;
            double t=y;
            t/=5;
            t=Math.pow(t,1.0/1);
            t=t*sign*100;
            //Log.i(TAG,String.valueOf(t));
            ps.offset=t;
            if(t>=40){
                if(statu!=1)
                sendData("D");
                statu=1;
            }else if(t<=-40){
                if(statu!=-1)
                sendData("A");
                statu=-1;
            }else{
               // sendData("A");
                if(statu!=0) {
                    sendData("Q");
                    statu = 0;
                }
            }
            // Log.i(TAG,ps.offset.t));
            ps.invalidate();
        }
    }
    boolean discheck(){
        try{
        sendData("C");
        byte[] buff=new byte[10];
            buff[0]='*';
        for(int i=1;i<=100;++i){
            if(mmInputStream.read(buff)!=0)
                break;
        }
        if(buff[0]=='*'){
            return false;
        }
         int t=0;
            for(int i=0;buff[i]!=13&&buff[i]!=10;++i){
                t=t*10+(buff[i]-'0');
            }
            Log.i(TAG,String.valueOf(t));
            if(t<15)
                return false;
            return true;
        }
        catch (Exception e){

        }
        return true;
    }
    @Override
    protected void onPause(){
        super.onPause();
        surfaceview.setVisibility(View.GONE);
        //surfaceview.d
        // camera.release();
    }
    @Override
    protected void onResume(){
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);
        // camera.release();
    }
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        System.out.println("surfacechanged");
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("surfacecreated");
        //获取camera对象
        camera = Camera.open();
        try {
            //设置预览监听
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();

            if (this.getResources().getConfiguration().orientation
                    != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            camera.setParameters(parameters);
            //启动摄像头预览
            camera.startPreview();
            System.out.println("camera.startpreview");

        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
            System.out.println("camera.release");
        }
        try {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
            if(pairedDevices.size()!=1)
                throw new Exception("haha");
            mmDevice=pairedDevices.iterator().next();
            uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            setdbg("Bluetooth On");
            statu=0;
        }catch (Exception e){
            setdbg("Bluetooth Error");
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        System.out.println("surfaceDestroyed");
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        try {
            mmSocket.close();
        }catch (Exception e){

        }
    }
}
