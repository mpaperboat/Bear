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
    public String sendMsg(String ip,String msg) {

        String res = null;
        Socket socket = null;

        try {
            socket = new Socket(ip, SERVERPORT);
            //向服务器发送消息
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            os.println(msg);
            os.flush();// 刷新输出流，使Server马上收到该字符串

            //从服务器获取返回消息
            DataInputStream input = new DataInputStream(socket.getInputStream());
            res = input.readUTF();
            System.out.println("server 返回信息：" + res);
            Message.obtain(handler, 222, res).sendToTarget();//发送服务器返回消息

        } catch (Exception unknownHost) {
            System.out.println("You are trying to connect to an unknown host!");
        } finally {
            // 4: Closing connection
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return res;
    }

    public void scan(){

        locAddress = getLocAddrIndex();//获取本地ip前缀

        if(locAddress.equals("")){
            //Toast.makeText(ctx, "扫描失败，请检查wifi网络", Toast.LENGTH_LONG).show();
            return ;
        }
      //  final Semaphore semp = new Semaphore(256);
        for ( int i = 0; i < 256; i++) {//创建256个线程分别去ping

             j = i ;
            String current_ip2 = locAddress+ KeyMode.this.j;
            if(current_ip2==mp)
                continue;
            Thread tmp=new Thread(new Runnable() {

                public void run() {
                 //   try {
                 //       semp.acquire();
                //    }catch (Exception e){

                  //  }
                    String p = KeyMode.this.ping + locAddress + KeyMode.this.j ;

                    String current_ip = locAddress+ KeyMode.this.j;
                    if(current_ip==mp)
                        return;

                    try {
                        proc = run.exec(p);
                        if(current_ip==mp)
                            return;
                        int result = proc.waitFor();
                        if (result == 0) {
                            if(current_ip!=mp) {
                                System.out.println("连接成功" + current_ip);
                                server=current_ip;
                                //setdbg(server);
                            }
                            // 向服务器发送验证信息
                        } else {

                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    } finally {
                        proc.destroy();
                    }
                 //   semp.release();
                }
            });
            tmp.start();

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
            //List<int[]> tmp=parameters.getSupportedPreviewFpsRange();
            //System.out.println(parameters.getSupportedPreviewFpsRange().size());
           // for(int i=1;i==1;);
            parameters.setPreviewFpsRange(15000, 15000);
          //  parameters.setPreviewFrameRate(2);
            camera.setParameters(parameters);
            camera.setPreviewCallback(new Camera.PreviewCallback(){

                public void onPreviewFrame(byte[] data, Camera camera) {
                    Size size = camera.getParameters().getPreviewSize();
                    try {
                        // 调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        if (image != null) {
                            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outstream);
                            outstream.flush();
                            // 启用线程将图像数据发送出去
                            Thread th = new MyThread(outstream, brainip);
                            th.start();
                        }
                    } catch (Exception ex) {
                        Log.e("Sys", "Error:" + ex.getMessage());
                    }
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

    public MyThread(ByteArrayOutputStream myoutputstream, String ipname) {
        this.myoutputstream = myoutputstream;
        this.ipname = ipname;
        try {
            myoutputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // 将图像数据通过Socket发送出去
            Socket tempSocket = new Socket(ipname, 6000);
            outsocket = tempSocket.getOutputStream();
            ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
            int amount;
            while ((amount = inputstream.read(byteBuffer)) != -1) {
                outsocket.write(byteBuffer, 0, amount);
            }
            myoutputstream.flush();
            myoutputstream.close();
            tempSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}