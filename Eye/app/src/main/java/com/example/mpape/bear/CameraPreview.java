package com.example.mpape.bear;
import java.io.IOException;
import java.util.LinkedList;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Size mPreviewSize;
    private LinkedList<byte[]> mQueue = new LinkedList<byte[]>();
    private static final int MAX_BUFFER = 15;
    private byte[] mLastFrame = null;
    private int mFrameLength;
    private SoundPool sp;
    private SoundPool sp2,sp3;
    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Parameters params = mCamera.getParameters();
        params.setPreviewFormat(ImageFormat.NV21);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setPictureSize(176,144);
        params.setPreviewSize(720,480);
        params.setPreviewFpsRange(15000, 15000);
        mCamera.setParameters(params);
        mPreviewSize = mCamera.getParameters().getPreviewSize();
        int format = mCamera.getParameters().getPreviewFormat();
        mFrameLength = mPreviewSize.width * mPreviewSize.height * ImageFormat.getBitsPerPixel(format) / 8;
        sp=new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        sp.load(context,R.raw.aa,1);
        sp2=new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        sp2.load(context,R.raw.eee,1);
        sp3=new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        sp3.load(context,R.raw.asdasd,1);
    }
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }
    public void liton(){
        sp.play(1,1, 1, 0, 0, 1);
    }
    public void litoff(){
        sp2.play(1,1, 1, 0, 0, 1);
    }
    public void ren(){
        sp3.play(1,1, 1, 0, 0, 1);
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
            resetBuff();
        } catch (Exception e){
        }
        try {
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
        }
    }
    public byte[] getImageBuffer() {
        synchronized (mQueue) {
			if (mQueue.size() > 0) {
				mLastFrame = mQueue.poll();
			}
    	}
        return mLastFrame;
    }
    private void resetBuff() {
        synchronized (mQueue) {
        	mQueue.clear();
        	mLastFrame = null;
    	}
    }
    public int getPreviewLength() {
        return mFrameLength;
    }
    public int getPreviewWidth() {
        return mPreviewSize.width;
    }
    public int getPreviewHeight() {
    	return mPreviewSize.height;
    }
    private PreviewCallback mPreviewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        	synchronized (mQueue) {
    			if (mQueue.size() == MAX_BUFFER) {
    				mQueue.poll();
    			}
    			mQueue.add(data);
        	}
        }
    };
}
