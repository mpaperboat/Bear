package com.example.mpape.bear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;
//import java.util.logging.Handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;


public class KeyMode extends Activity implements SurfaceHolder.Callback {
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
    private int activepohoto;
    private Semaphore ims;
    @Override
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
                } catch (Exception e) {
                    setdbg("Bluetooth Error");
                }
            }
        });
        Button button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    new Thread(new Runnable(){
                        public void run() {
                           lettherebewifi();
                        }

                    }).start();


            }
        });
        final ImageButton ibutton = (ImageButton) findViewById(R.id.button6);
        ibutton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                checkblue();
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
        final ImageButton ibutton3 = (ImageButton) findViewById(R.id.button7);
        ibutton3.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                checkblue();
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
                checkblue();
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
        context = this;
        surfaceview = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // 更新UI
                switch (msg.what) {
                    case 1:
                        setdbg2("WiFi On");
                        break;
                    case 0:
                        setdbg2("WiFi Error");
                        break;
                }

            }};
        new Thread(new Runnable(){
            public void run() {
                lettherebewifi();
            }

        }).start();
        //activepohoto=1;
        ims=new Semaphore(1);
        try {
            ss.close();
        }catch (Exception e){

        }
        try {
            ss = new ServerSocket(6000);
        }catch (Exception e){

        }
        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    if(activepohoto==1){
                        System.out.println("begin\n");
                        try {
                            pdate();
                            Socket s = ss.accept();
                            pdate();
                            System.out.println("连接成功!");
                            ins = s.getInputStream();
                            pdate();
                            //ims.acquire();

                            image=InputStream2Bitmap(ins);
                            System.out.println(image.getByteCount());
                            pdate();
                           // System.out.print("haha"+image.getBounds().toString());
                            //ims.release();

                            //ins.close();
                            //s.close();
                            //ss.close();
                            pdate();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        System.out.println("end\n");
                    }
                }
            }

        }).start();
    }
    int pht=0;
    private Handler mHandler;
    private int hahap=0;
        private ServerSocket ss;
       private Bitmap image;
       private InputStream ins;
    private Handler dh2=null;

    public Drawable bitmap2Drawable(Bitmap bitmap) {
                BitmapDrawable bd = new BitmapDrawable(bitmap);
               Drawable d = (Drawable) bd;
                return d;
            }

    public Bitmap InputStream2Bitmap(InputStream is) {
                return BitmapFactory.decodeStream(is);
            }

    public Drawable InputStream2Drawable(InputStream is) {
                Bitmap bitmap = this.InputStream2Bitmap(is);
        return this.bitmap2Drawable(bitmap);
         }
    void pdate(){
        Date dt= new Date();
        Long time= dt.getTime();
        System.out.println(time);
    }
    public void lettherebewifi(){
        Message msg = new Message();
        //给message对象赋值

        //发送message值给Handler接收


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

        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> address = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip instanceof Inet4Address) {
                        ipaddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("", "获取本地ip地址失败");
            e.printStackTrace();
        }

        //System.out.println("本机IP:" + ipaddress);

        return ipaddress;

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
    void setdbg2(String s){
        TextView dbg=(TextView)findViewById(R.id.textView3);
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
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        System.out.println("surfacechanged");
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("surfacecreated");

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
        }catch (Exception e){
            setdbg("Bluetooth Error");
        }
        activepohoto=1;
        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try{wait(100);}catch (Exception e){}
                    if(activepohoto==1&&image!=null){
                        Canvas c=surfaceholder.lockCanvas();
                        if(c!=null){
                            synchronized (surfaceholder) {
                               // try{ims.acquire();}catch (Exception e){}
                                Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                                c.drawBitmap(image,null,tmp,new Paint());
                                //c.drawBitmap(image, 0, 0, new Paint());
                                //ims.release();
                            }

                            surfaceholder.unlockCanvasAndPost(c);
                        }
                    }
                }
            }

        }).start();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        System.out.println("surfaceDestroyed");

        try {
            mmSocket.close();
        }catch (Exception e){

        }
    }
}
