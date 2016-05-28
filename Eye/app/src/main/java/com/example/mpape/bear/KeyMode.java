package com.example.mpape.bear;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.*;
import java.util.concurrent.Semaphore;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera.Size;

public class KeyMode extends Activity implements SurfaceHolder.Callback {
    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private Camera camera = null;
    private static final String TAG="TestTag";
    private int j;//
    private ServerSocket sever;
    private Semaphore semabrainip;
    private String brainip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        phttt=new Semaphore(10);
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
    }
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
    Semaphore phttt;
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
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setPictureSize(176,144);
            List<Size>haha=parameters.getSupportedPreviewSizes();
            parameters.setPreviewSize(480,320);
            parameters.setPreviewFpsRange(15000, 15000);
            camera.setParameters(parameters);
            camera.setPreviewCallback(new Camera.PreviewCallback(){
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Size size = camera.getParameters().getPreviewSize();
                    try {
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        if (image != null) {
                            Thread th = new MyThread( brainip,image,size,phttt);
                            th.start();
                            th.join();
                        }
                    } catch (Exception ex) {
                    }
                }

            });
            camera.startPreview();
            camera.autoFocus(null);
        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
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
    Semaphore phtt;
    public MyThread(String ipname,YuvImage ima,Size siz,Semaphore pht) {
        this.ipname = ipname;
        image=ima;
        size=siz;
        phtt=pht;
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
        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 70, myoutputstream);
        pdate();
        try {
            myoutputstream.flush();
            myoutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("ha1");
            Socket tempSocket  = new Socket();
            System.out.println("ha5");
            SocketAddress socAddress = new InetSocketAddress(ipname,6000);
            tempSocket.connect(socAddress);
            System.out.println("ha6");
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