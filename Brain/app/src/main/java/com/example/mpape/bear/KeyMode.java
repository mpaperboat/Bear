package com.example.mpape.bear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;

public class KeyMode extends Activity implements SurfaceHolder.Callback,DataListener{
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    Bitmap mImage, mLastFrame;
    private UUID uuid;
    private int activepohoto;
    private LinkedList<Bitmap> mQueue = new LinkedList<Bitmap>();
    private static final int MAX_BUFFER = 15;
    protected void onCreate(Bundle savedInstanceState) {
        activepohoto=0;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_key_mode);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KeyMode.this,GravityMode.class);
                startActivity(intent);
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                        if(sendData("-"))
                            continue;
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
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
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
        final ImageButton ibutton3 = (ImageButton) findViewById(R.id.button7);
        ibutton3.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){

                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendData("A");
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("Q");
                        return true;
                }
                return false;
            }
        });
        final ImageButton ibutton4 = (ImageButton) findViewById(R.id.button4);
        ibutton4.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){

                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendData("D");
                        System.out.print("d pressed");
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("Q");
                        return true;
                }
                return false;
            }
        });
        surfaceview = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // 更新UI
                switch (msg.what) {
                    case 1:

                        break;
                    case 0:

                        break;
                }

            }};
        SocketServer server = new SocketServer();
        server.setOnDataListener(this);
        server.start();
    }

    private Handler mHandler;
    private ServerSocket ss;
    private Bitmap image;
    private InputStream ins;

    public Bitmap InputStream2Bitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    void pdate(){
        Date dt= new Date();
        Long time= dt.getTime();
        System.out.println(time);
    }
    public void lettherebewifi(){
        Message msg = new Message();
        try {
            Socket socket = new Socket("192.168.43.1", 8888);
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.write(getLocAddress() + "\n");
            pw.flush();
            //pw.close();
            socket.close();
            msg.what = 1;
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            msg.what = 0;
            mHandler.sendMessage(msg);
        }
    }
    public String getLocAddress(){
        String ipaddress = "";
        try{
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                Enumeration<InetAddress> address = networks.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip instanceof Inet4Address) {
                        ipaddress = ip.getHostAddress();
                    }
                }
            }
        }catch (SocketException e) {
            e.printStackTrace();
        }
        return ipaddress;
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
    @Override
    protected void onPause(){
        super.onPause();
        activepohoto=0;
        surfaceview.setVisibility(View.GONE);
        try {
            ss.close();
        }catch (Exception e){

        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);
        activepohoto=1;
        try {
            ss = new ServerSocket(6000);
        }catch (Exception e){

        }
    }
    public void surfaceCreated(SurfaceHolder holder) {
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

        }catch (Exception e){

        }
        activepohoto=1;
    }
    public void surfaceChanged(SurfaceHolder holder,int a,int b,int c){
    }
    public void surfaceDestroyed(SurfaceHolder arg0) {
        try {
            mmSocket.close();
        }catch (Exception e){

        }
    }
    @Override
    public void onDirty(Bitmap bufferedImage) {
        // TODO Auto-generated method stub
        updateUI(bufferedImage);
    }
    private void updateUI(Bitmap bufferedImage) {

        synchronized (mQueue) {
            if (mQueue.size() ==  MAX_BUFFER) {
                mLastFrame = mQueue.poll();
            }
            mQueue.add(bufferedImage);
        }

        repaint();
    }
    public void repaint() {
        synchronized (mQueue) {
            if (mQueue.size() > 0) {
                mLastFrame = mQueue.poll();
            }
        }
        if (mLastFrame != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    c.drawBitmap(mLastFrame,null,tmp,new Paint());
                    // System.out.println("draw one img!");
                    // pdate();
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
            //g.drawImage(mLastFrame, 0, 0, null);
        }
        else if (mImage != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    c.drawBitmap(mImage,null,tmp,new Paint());
                    // System.out.println("draw one img!");
                    // pdate();
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
            //g.drawImage(mImage, 0, 0, null);
        }
    }
}
