package com.example.mpape.bear;
import android.content.Context;
import android.hardware.Camera;
public class CameraManager {
	private Camera mCamera;
	private Context mContext;
	public CameraManager(Context context) {
		mContext = context;
        mCamera = getCameraInstance();
	}
	public Camera getCamera() {
		return mCamera;
	}
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	private static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open();
	    }
	    catch (Exception e){
	    }
	    return c;
	}
}
