package com.example.mpape.bear;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Array;
import java.util.UUID;

import com.example.mpape.bear.BufferManager;
import com.example.mpape.bear.DataListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class SocketServer extends Thread {
	private BluetoothServerSocket mServer;
	private DataListener mDataListener;
	private BufferManager mBufferManager;
	private BluetoothDevice eyeDevice;
	public SocketServer(BluetoothDevice eyeDevic) {
		eyeDevice=eyeDevic;
	}
	public static int bytesToInt2(byte[] src, int offset) {
		int value;
		value = (int) ( ((src[offset] & 0xFF)<<24)
				|((src[offset+1] & 0xFF)<<16)
				|((src[offset+2] & 0xFF)<<8)
				|(src[offset+3] & 0xFF));
		return value;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		System.out.println("server's waiting");
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		BluetoothSocket socket = null;
		ByteArrayOutputStream byteArray = null;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				if (byteArray != null)
					byteArray.reset();
				else
					byteArray = new ByteArrayOutputStream();

				socket=eyeDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				socket.connect();

				inputStream = new BufferedInputStream(socket.getInputStream());
				outputStream = new BufferedOutputStream(socket.getOutputStream());
				
				byte[] buff = new byte[256];
				byte[] tmp = null;
				int len = 0;
				String msg = null;
				// read msg
				while ((len = inputStream.read(buff)) != -1) {
					System.out.println("ydf:wt 3");
					msg = new String(buff, 0, len);
					// JSON analysis
	                JsonParser parser = new JsonParser();
	                boolean isJSON = true;
	                JsonElement element = null;
	                try {
	                    element =  parser.parse(msg);
	                }
	                catch (JsonParseException e) {
	                    System.out.println("exception: " + e);
	                    isJSON = false;
	                }
	                if (isJSON && element != null) {
	                    JsonObject obj = element.getAsJsonObject();
	                    element = obj.get("type");
	                    if (element != null && element.getAsString().equals("data")) {
	                        element = obj.get("length");
	                        int length = element.getAsInt();
	                        element = obj.get("width");
	                        int width = element.getAsInt();
	                        element = obj.get("height");
	                        int height = element.getAsInt();
	                        
	                        tmp = new byte[length];
                            mBufferManager = new BufferManager(length, width, height);
                            mBufferManager.setOnDataListener(mDataListener);

                            break;
	                    }
	                }
	                else {
	                    byteArray.write(buff, 0, len);
	                    break;
	                }
				}
				
				if (tmp != null) {
				    JsonObject jsonObj = new JsonObject();
		            jsonObj.addProperty("state", "ok");
		            outputStream.write(jsonObj.toString().getBytes());
		            outputStream.flush();

					while(true){
                        tmp=new byte[4];
                        for(int i=0;i<4;++i) {
                            while(inputStream.read(tmp, i, 1)!=1);
                        }
						int le1n=bytesToInt2(tmp,0);
						tmp=new byte[le1n];
						int cur=0;
						while(cur<le1n){
							int t=inputStream.read(tmp,cur,le1n-cur);
							cur=cur+t;
						}
						System.out.println("cxy:reci"+tmp.length+":"+tmp[0]+tmp[500]+tmp[600]+tmp[tmp.length-1]);
						mBufferManager.mYUVQueue.add(tmp);
                        if(mBufferManager.mYUVQueue.size()>1)
                            mBufferManager.mYUVQueue.poll();
					}


		            // read image data
				    //while ((len = inputStream.read(imageBuff)) != -1) {
	                   // mBufferManager.fillBuffer(imageBuff, len);
	               // }
				}
				
				if (mBufferManager != null) {
					mBufferManager.close();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
					outputStream = null;
				}
				
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}

				if (socket != null) {
					socket.close();
	                socket = null;
				}
				
				if (byteArray != null) {
					byteArray.close();
				}
				
			} catch (Exception e) {

			}

		}

	}

	public void setOnDataListener(DataListener listener) {
		mDataListener = listener;
	}
}
