package com.example.mpape.bear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
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

public class GravityMode extends Activity implements SensorEventListener,SurfaceHolder.Callback,DataListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final String TAG="TestTag";
    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private Camera camera = null;
    private UUID uuid;
    private int bconnected;
    private int statu;
    private int on=0;
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
                intent.setClass(GravityMode.this,GestureMode.class);
                startActivity(intent);
            }
        });
        context = this;
        surfaceview = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
        final ImageButton ibutton = (ImageButton) findViewById(R.id.button6);
        ibutton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
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
        final ImageButton ibutton5 = (ImageButton) findViewById(R.id.button5);
        ibutton5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MyApplication)getApplication()).phts.addFirst(((MyApplication)getApplication()).mLastFrame);
                Toast.makeText(getApplicationContext(), "Photo Taken",
                        Toast.LENGTH_SHORT).show();
            }
        });
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
            ((MyApplication)getApplication()).mmOutputStream.write(msg.getBytes());
           // System.out.println("Data Sent");
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onSensorChanged(SensorEvent event) {
        if(on==0)
            return;
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
    @Override
    protected void onPause(){
        super.onPause();
        surfaceview.setVisibility(View.GONE);
        on=0;
        //surfaceview.d
        sendData("Q");
        // camera.release();
    }
    @Override
    protected void onResume(){
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);
        ((MyApplication)getApplication()).server.setOnDataListener(this);
        on=1;
        // camera.release();
    }
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        System.out.println("surfacechanged");
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("surfacecreated");
        //获取camera对

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }
    public void onDirty(Bitmap bufferedImage) {
        // TODO Auto-generated method stub
        updateUI(bufferedImage);
    }
    private void updateUI(Bitmap bufferedImage) {

        synchronized (((MyApplication)getApplication()).mQueue) {
            if (((MyApplication)getApplication()).mQueue.size() ==  ((MyApplication)getApplication()).MAX_BUFFER) {
                ((MyApplication)getApplication()).mLastFrame = ((MyApplication)getApplication()).mQueue.poll();
            }
            ((MyApplication)getApplication()).mQueue.add(bufferedImage);
        }

        repaint();
    }
    public void repaint() {
        synchronized (((MyApplication)getApplication()).mQueue) {
            if (((MyApplication)getApplication()).mQueue.size() > 0) {
                ((MyApplication)getApplication()).mLastFrame = ((MyApplication)getApplication()).mQueue.poll();
            }
        }
        if (((MyApplication)getApplication()).mLastFrame != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    c.drawBitmap(((MyApplication)getApplication()).mLastFrame,null,tmp,new Paint());
                    // System.out.println("draw one img!");
                    // pdate();
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
            //g.drawImage(mLastFrame, 0, 0, null);
        }
        else if (((MyApplication)getApplication()).mImage != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    c.drawBitmap(((MyApplication)getApplication()).mImage,null,tmp,new Paint());
                    // System.out.println("draw one img!");
                    // pdate();
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
            //g.drawImage(mImage, 0, 0, null);
        }
    }
}
