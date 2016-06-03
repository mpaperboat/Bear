package com.example.mpape.bear;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.LinkedList;
import java.util.ListIterator;
public class GestureMode extends Activity implements SurfaceHolder.Callback,GestureDetector.OnGestureListener,DataListener {
    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private ListIterator<Bitmap> it;
    private GestureDetector mGestureDetector;
    LinkedList<Integer>damn;
    Handler myHandler;
    protected void onCreate(Bundle savedInstanceState) {
        damn=new LinkedList<Integer>();
        super.onCreate(savedInstanceState);
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                int t=msg.what;
                if(t==13){
                    Toast.makeText(getApplicationContext(), "Found A Human",
                            Toast.LENGTH_SHORT).show();
                }
                if(t==1){
                    Toast.makeText(getApplicationContext(), "Found Something",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gesture_mode);
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
        final Button gray_radio = ( Button) findViewById(R.id.button2);
        gray_radio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cops Are On The Way",
                        Toast.LENGTH_SHORT).show();
                ((MyApplication)getApplication()).server.add(1);
            }
        });
    }
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }
    public void surfaceCreated(SurfaceHolder holder) {
        if(!((MyApplication)getApplication()).phts.isEmpty()){
            paint(((MyApplication)getApplication()).phts.getFirst());
            it=((MyApplication)getApplication()).phts.listIterator();
            it.next();
        }else{
            it=null;
        }
    }
    protected void onPause() {
        super.onPause();
        surfaceview.setVisibility(View.GONE);
        sendData("Q");
    }
    protected void onResume() {
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);
        ((MyApplication)getApplication()).server.setOnDataListener(this);
    }
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }
    public void paint(Bitmap m){
        Canvas c=surfaceholder.lockCanvas();
        if(c!=null){
            synchronized (surfaceholder) {
                Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                c.drawBitmap(m,null,tmp,new Paint());
            }
            surfaceholder.unlockCanvasAndPost(c);
        }
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
    public boolean onSingleTapUp(MotionEvent e) {
        sendData("Q");
        return false;
    }
    public void onLongPress(MotionEvent e) {
    }
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("zkw"+(e1.getX()-e2.getX()));
        if(e1.getX()-e2.getX() > 200 && Math.abs(velocityX) > 200){
            sendData("A");
        }else if(e2.getX() - e1.getX() > 200 && Math.abs(velocityX) > 200){
            sendData("D");
        }else if(e1.getY()-e2.getY() > 200 && Math.abs(velocityY) > 200){
            sendData("W");
        }else if(e2.getY()-e1.getY() > 200 && Math.abs(velocityY) > 200){
            sendData("S");
        }else{
            sendData("Q");
        }
        return false;
    }
    public void onShowPress(MotionEvent e) {
    }
    public boolean onDown(MotionEvent e) {
        return false;
    }
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }
    public void onDirty(Bitmap bufferedImage) {
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
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
        }
        else if (((MyApplication)getApplication()).mImage != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    c.drawBitmap(((MyApplication)getApplication()).mImage,null,tmp,new Paint());
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
        }
        if(damn.size()!=0){
            int t=damn.poll();
            if(t==13){
                Toast.makeText(getApplicationContext(), "Found A Human",
                        Toast.LENGTH_SHORT).show();
            }
            if(t==1){
                Toast.makeText(getApplicationContext(), "Found Something",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    boolean sendData(String m){
        try{
            String msg = m;
            ((MyApplication)getApplication()).mmOutputStream.write(msg.getBytes());
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public void conv(int t){
        Message msg=new Message();
        msg.what=t;
        myHandler.sendMessage(msg);
    }
}