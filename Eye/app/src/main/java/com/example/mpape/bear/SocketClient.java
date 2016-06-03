package com.example.mpape.bear;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
public class SocketClient extends Thread {
    private Socket mSocket;
    private ServerSocket mServer;
    private CameraPreview mCameraPreview;
    public SocketClient(CameraPreview preview) {
        mCameraPreview = preview;
        start();
    }
    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
    public void run() {
        super.run();
        try {
            mServer = new ServerSocket(8888);
        }catch (Exception e){
        }
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
                mSocket=mServer.accept();
                if (mSocket == null)
                    continue;
                BufferedOutputStream outputStream = new BufferedOutputStream(mSocket.getOutputStream());
                final BufferedInputStream inputStream = new BufferedInputStream(mSocket.getInputStream());
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("type", "data");
                jsonObj.addProperty("length", mCameraPreview.getPreviewLength());
                jsonObj.addProperty("width", mCameraPreview.getPreviewWidth());
                jsonObj.addProperty("height", mCameraPreview.getPreviewHeight());
                byte[] buff = new byte[256];
                int len = 0;
                String msg = null;
                outputStream.write(jsonObj.toString().getBytes());
                outputStream.flush();
                while (!Thread.currentThread().isInterrupted()&&(len = inputStream.read(buff)) != -1) {
                    msg = new String(buff, 0, len);
                    JsonParser parser = new JsonParser();
                    boolean isJSON = true;
                    JsonElement element = null;
                    try {
                        element = parser.parse(msg);
                    } catch (JsonParseException e) {
                        isJSON = false;
                    }
                    if (isJSON && element != null) {
                        JsonObject obj = element.getAsJsonObject();
                        element = obj.get("state");
                        if (element != null && element.getAsString().equals("ok")) {
                            new Thread(new Runnable(){
                                public void run() {
                                    byte[] buff = new byte[256];
                                    try {
                                        while (inputStream.read(buff, 0, 1) == 1) {
                                            System.out.println("ydf:" + buff[0]);
                                            if(buff[0]==1)
                                                mCameraPreview.liton();
                                            if(buff[0]==0)
                                                mCameraPreview.litoff();
                                            if(buff[0]==8)
                                                mCameraPreview.ren();
                                        }
                                    }catch (Exception e){
                                    }
                                }
                            }).start();
                            while (true) {
                                YuvImage image = new YuvImage(mCameraPreview.getImageBuffer(), ImageFormat.NV21, mCameraPreview.getPreviewWidth(), mCameraPreview.getPreviewHeight(), null);
                                ByteArrayOutputStream myoutputstream = new ByteArrayOutputStream();
                                image.compressToJpeg(new Rect(0, 0, mCameraPreview.getPreviewWidth(), mCameraPreview.getPreviewHeight()), 60, myoutputstream);
                                myoutputstream.flush();
                                myoutputstream.close();
                                byte tmp[]=myoutputstream.toByteArray();
                                outputStream.write(intToBytes2(tmp.length));
                                outputStream.write(tmp);
                                System.out.println("cxy: send"+tmp.length+":"+tmp[0]+tmp[500]+tmp[5000]+tmp[tmp.length-5000]);
                                outputStream.flush();
                                if (Thread.currentThread().isInterrupted()) {
                                    break;
                                }
                            }
                            break;
                        }
                    } else
                        break;
                }
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
            } finally {
                try {
                    mSocket.close();
                    mSocket = null;
                } catch (IOException e) {
                }
            }
        }
    }
}
