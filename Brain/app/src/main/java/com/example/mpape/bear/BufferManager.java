package com.example.mpape.bear;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.provider.Settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import com.example.mpape.bear.Utils;

public class BufferManager extends Thread {
    private ImageBuffer[] mBufferQueue;
    private int mFillCount = 0;
    private final int mFrameLength;
    private int mRemained = 0;
    private static final int MAX_BUFFER_COUNT = 2;
    private int mWidth, mHeight;
    public LinkedList<byte[]> mYUVQueue = new LinkedList<byte[]>();
    private DataListener mListener;
    
    public BufferManager(int frameLength, int width, int height) {
        // TODO Auto-generated constructor stub
    	mWidth = width;
    	mHeight = height;
        mFrameLength = frameLength;
        mBufferQueue = new ImageBuffer[MAX_BUFFER_COUNT];
        for (int i = 0; i < MAX_BUFFER_COUNT; ++i) {
            mBufferQueue[i] = new ImageBuffer(mFrameLength, width, height);
        }
    }
	public static int bytesToInt2(byte[] src, int offset) {
		    int value;
		  value = (int) ( ((src[offset] & 0xFF)<<24)
		           |((src[offset+1] & 0xFF)<<16)
		          |((src[offset+2] & 0xFF)<<8)
		          |(src[offset+3] & 0xFF));
		    return value;
	}

	public void fillBuffer(byte[] data, int len) {
		mFillCount = mFillCount % MAX_BUFFER_COUNT;
		if (mRemained != 0) {
			if (mRemained < len) {
				mBufferQueue[mFillCount].fillBuffer(data, 0, mRemained, mYUVQueue);
				++mFillCount;
				if (mFillCount == MAX_BUFFER_COUNT)
					mFillCount = 0;
				mBufferQueue[mFillCount].fillBuffer(data, mRemained, len - mRemained, mYUVQueue);
				mRemained = mFrameLength - len + mRemained;
			} else if (mRemained == len) {
				mBufferQueue[mFillCount].fillBuffer(data, 0, mRemained, mYUVQueue);
				mRemained = 0;
				++mFillCount;
				if (mFillCount == MAX_BUFFER_COUNT)
                    mFillCount = 0;
			} else {
				mBufferQueue[mFillCount].fillBuffer(data, 0, len, mYUVQueue);
				mRemained = mRemained - len;
			}
		} else {
			int len2=bytesToInt2(data,0);
			mBufferQueue[mFillCount].mFrameLength=len2;
			mBufferQueue[mFillCount].fillBuffer(data, 4, len, mYUVQueue);

			if (len < mFrameLength) {
				mRemained = mFrameLength - len;
				System.out.print("hell2\n");
			} else {
				System.out.print("hell\n");
				++mFillCount;
				if (mFillCount == MAX_BUFFER_COUNT)
				    mFillCount = 0;
			}
		}
	}
    
    public void setOnDataListener(DataListener listener) {
    	mListener = listener;
    	start();
    }
    
    public void close() {
    	interrupt();
    	try {
			join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public void run() {
    	// TODO Auto-generated method stub
    	super.run();
    	
    	while (!Thread.currentThread().isInterrupted()) {
    		byte[] data = null;
    		synchronized (mYUVQueue) {
    			data = mYUVQueue.poll();
    			
    			if (data != null) {
    				Bitmap bufferedImage = null;

					byte tmp[]=data;
					System.out.println("cxy:push"+tmp.length+":"+tmp[0]+":"+tmp[500]+":"+tmp[5000]+":"+tmp[tmp.length-5000]);
					bufferedImage= BitmapFactory.decodeByteArray(tmp,0,tmp.length);

                    mListener.onDirty(bufferedImage);
                    //System.out.println("time cost = " + (System.currentTimeMillis() - t));
    			}
    			
    		}
    	}
    }
}
