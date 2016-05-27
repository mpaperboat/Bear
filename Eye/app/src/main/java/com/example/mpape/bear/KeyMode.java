package com.example.mpape.bear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import java.net.*;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import java.util.concurrent.Semaphore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class KeyMode extends Activity implements SurfaceHolder.Callback {
    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private Camera camera = null;
    private String ping = "ping -c 1 -w 0.5 " ,mp;//
    private static final String TAG="TestTag";
    private int j;//
    private ServerSocket sever;
    private Semaphore semabrainip;
    private String brainip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_key_mode);
        context = this;
        surfaceview = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
        try{
            sever=new ServerSocket(8888);
        }catch (IOException e){
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        System.out.println("wifi"+"waiting!");
                        Socket socket = sever.accept();
                        BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        String str=in.readLine();
                        //str=in.readLine();
                        System.out.println("wifi"+str);
                        System.out.println("wifi"+str.length());
                        semabrainip.acquire();
                        brainip=str;
                        semabrainip.release();
                        socket.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        semabrainip=new Semaphore(1);
        //brainip="";
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
    public String getLocAddrIndex(){

        String str = getLocAddress();

        if(!str.equals("")){
            return str.substring(0,str.lastIndexOf(".")+1);
        }

        return null;
    }
    private String locAddress;//
    private Process proc = null;
    private Runtime run = Runtime.getRuntime();//
    private int SERVERPORT = 8888;
    private String server="";
    private Handler handler = new Handler(){

        public void dispatchMessage(Message msg) {
            switch (msg.what) {

                case 222:// 服务器消息
                    break;

                case 333:// 扫描完毕消息
                    //Toast.makeText(ctx, "扫描到主机："+((String)msg.obj).substring(6), Toast.LENGTH_LONG).show();
                    Log.i(TAG,"扫描到主机："+((String)msg.obj).substring(6));
                    break;
                case 444://扫描失败
                    //Toast.makeText(ctx, (String)msg.obj, Toast.LENGTH_LONG).show();
                    Log.i(TAG,(String)msg.obj);
                    break;
            }
        }

    };
    void pdate(){
        Date dt= new Date();
        Long time= dt.getTime();
        System.out.println(time);
    }
    @Override
    protected void onPause(){
        super.onPause();
        surfaceview.setVisibility(View.GONE);
    }
    @Override
    protected void onResume(){
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);
    }
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        System.out.println("surfacechanged");
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("surfacecreated");
        try {
            camera = Camera.open();
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
            //List<Size>tmp=parameters.getSupportedPictureSizes();
            //List<int[]> tmp=parameters.getSupportedPreviewFpsRange();
            //System.out.println(parameters.getSupportedPreviewFpsRange().size());
           // for(int i=1;i==1;);
            //parameters.setPictureSize(176,144);
            parameters.setPreviewFpsRange(15000, 15000);

          //  parameters.setPreviewFrameRate(2);
            camera.setParameters(parameters);

            camera.setPreviewCallback(new Camera.PreviewCallback(){
                public void onPreviewFrame(byte[] data, Camera camera) {

                   // pdate();
                    Size size = camera.getParameters().getPreviewSize();
                    try {
                        // 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                     //   pdate();
                        if (image != null) {

                            // 启用线程将图像数据发送出去
                            Thread th = new MyThread( brainip,image,size);

                           // pdate();
                            th.start();
                            th.join();
                        }
                    } catch (Exception ex) {
                        Log.e("Sys", "Error:" + ex.getMessage());
                    }
                 //   pdate();
                 //   System.out.println("hehe");
                }

            });
            camera.startPreview();
            camera.autoFocus(null);
            System.out.println("camera.startpreview");

        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
            System.out.println("camera.release");
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        System.out.println("surfaceDestroyed");
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }
}

class MyThread extends Thread {
    private byte byteBuffer[] = new byte[1024];
    private OutputStream outsocket;
    private ByteArrayOutputStream myoutputstream;
    private String ipname;
    YuvImage image;
    Size size;
    public MyThread(String ipname,YuvImage ima,Size siz) {

        this.ipname = ipname;
        image=ima;
        size=siz;

    }
    void pdate(){
        Date dt= new Date();
        Long time= dt.getTime();
        System.out.println(time);
        }
    public void run() {
        System.out.println("habg");
        pdate();
        this.myoutputstream = new ByteArrayOutputStream();;
        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 15, myoutputstream);
        pdate();
        try {
            myoutputstream.flush();
            myoutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 将图像数据通过Socket发送出去
            System.out.println("ha1");
            Socket tempSocket  = new Socket();
            SocketAddress socAddress = new InetSocketAddress(ipname,6000);
            tempSocket.connect(socAddress);
            //Socket tempSocket = new Socket(ipname,6000);
            outsocket = tempSocket.getOutputStream();
            ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
            int amount;
            while ((amount = inputstream.read(byteBuffer)) != -1) {
                outsocket.write(byteBuffer, 0, amount);
            }
            System.out.println("ha2");
            myoutputstream.flush();
            myoutputstream.close();
            tempSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pdate();
        System.out.println("haed");

    }

}