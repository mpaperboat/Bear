package com.example.mpape.bear;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ListIterator;
public class GestureMode extends Activity implements SurfaceHolder.Callback,GestureDetector.OnGestureListener,DataListener {
    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private ListIterator<Bitmap> it;
    private GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);//全屏
        setContentView(R.layout.activity_gesture_mode);
        //mGestureDetector=new GestureDetector(this.surfaceview,this);
        mGestureDetector = new GestureDetector(this, this);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(GestureMode.this, PhotoMode.class);
                startActivity(intent);
            }
        });
        context = this;
        surfaceview = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
        final ImageButton ibutton5 = (ImageButton) findViewById(R.id.button5);
        ibutton5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MyApplication)getApplication()).phts.addFirst(((MyApplication)getApplication()).mLastFrame);
                Toast.makeText(getApplicationContext(), "Photo Taken",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!((MyApplication)getApplication()).phts.isEmpty()){
            paint(((MyApplication)getApplication()).phts.getFirst());
            it=((MyApplication)getApplication()).phts.listIterator();
            it.next();
        }else{
            it=null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        surfaceview.setVisibility(View.GONE);
        //surfaceview.d
        sendData("Q");
    }
    @Override
    protected void onResume() {
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);
        ((MyApplication)getApplication()).server.setOnDataListener(this);

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }
    public void paint(Bitmap m){
        Canvas c=surfaceholder.lockCanvas();
        if(c!=null){
            synchronized (surfaceholder) {
                Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                c.drawBitmap(m,null,tmp,new Paint());
                // System.out.println("draw one img!");
                // pdate();
            }
            surfaceholder.unlockCanvasAndPost(c);
        }
        //surfaceview
        System.out.println("wfk");
    }
    private String getActionName(int action) {
        String name = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                name = "ACTION_DOWN";
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                name = "ACTION_MOVE";
                break;
            }
            case MotionEvent.ACTION_UP: {
                name = "ACTION_UP";
                break;
            }
            default:
                break;
        }
        return name;
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(getClass().getName(), "onSingleTapUp-----" + getActionName(e.getAction()));
        sendData("Q");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(getClass().getName(), "onLongPress-----" + getActionName(e.getAction()));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i(getClass().getName(),
                "onScroll-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                        + e2.getX() + "," + e2.getY() + ")");
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("zkw"+(e1.getX()-e2.getX()));
        //大于设定的最小滑动距离并且在水平/竖直方向速度绝对值大于设定的最小速度，则执行相应方法
        if(e1.getX()-e2.getX() > 200 && Math.abs(velocityX) > 200){
           sendData("A");

        }else if(e2.getX() - e1.getX() > 200 && Math.abs(velocityX) > 200){
            //Toast.makeText(this, "turn right", Toast.LENGTH_SHORT).show();
            sendData("D");

        }else if(e1.getY()-e2.getY() > 200 && Math.abs(velocityY) > 200){
           // Toast.makeText(MainActivity.this, "turn up", Toast.LENGTH_SHORT).show();
            sendData("W");

        }else if(e2.getY()-e1.getY() > 200 && Math.abs(velocityY) > 200){
            sendData("S");
        }else{
            sendData("Q");
        }

        return false;
    }


    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(getClass().getName(), "onShowPress-----" + getActionName(e.getAction()));
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(getClass().getName(), "onDown-----" + getActionName(e.getAction()));
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "+ onTouchEvent(event:" + event + ")");
        mGestureDetector.onTouchEvent(event);
       // Log.d(TAG, "- onTouchEvent()");
        return true;
    }
    public void onDirty(Bitmap bufferedImage) {
        // TODO Auto-generated method stub
        updateUI(bufferedImage);
    }
    private void updateUI(Bitmap bufferedImage) {

        synchronized (((MyApplication)getApplication()).mQueue) {
            if (((MyApplication)getApplication()).mQueue.size() ==  ((MyApplication)getApplication()).MAX_BUFFER) {
                ((MyApplication)getApplication()).mLastFrame = ((MyApplication)getApplication()).mQueue.poll();
            }
            ((MyApplication)getApplication()).mQueue.add(bufferedImage);
        }

        repaint();
    }
    public void repaint() {
        synchronized (((MyApplication)getApplication()).mQueue) {
            if (((MyApplication)getApplication()).mQueue.size() > 0) {
                ((MyApplication)getApplication()).mLastFrame = ((MyApplication)getApplication()).mQueue.poll();
            }
        }
        if (((MyApplication)getApplication()).mLastFrame != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    c.drawBitmap(((MyApplication)getApplication()).mLastFrame,null,tmp,new Paint());
                    // System.out.println("draw one img!");
                    // pdate();
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
            //g.drawImage(mLastFrame, 0, 0, null);
        }
        else if (((MyApplication)getApplication()).mImage != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    c.drawBitmap(((MyApplication)getApplication()).mImage,null,tmp,new Paint());
                    // System.out.println("draw one img!");
                    // pdate();
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
            //g.drawImage(mImage, 0, 0, null);
        }
    }
    boolean sendData(String m){
        try{
            String msg = m;
            ((MyApplication)getApplication()).mmOutputStream.write(msg.getBytes());
            // System.out.println("Data Sent");
            return true;
        }catch (Exception e){
            return false;
        }
    }
}