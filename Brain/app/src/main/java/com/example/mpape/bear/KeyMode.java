package com.example.mpape.bear;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.Toast;
import java.util.LinkedList;
public class KeyMode extends Activity implements SurfaceHolder.Callback,DataListener{
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private int activepohoto;
    LinkedList<Integer>damn;
    Handler myHandler;
    protected void onCreate(Bundle savedInstanceState) {
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
        System.out.print("hell\n");
        damn=new LinkedList<Integer>();
        activepohoto=0;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_key_mode);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(KeyMode.this,GravityMode.class);
                startActivity(intent);
            }
        });
        final ImageButton ibutton = (ImageButton) findViewById(R.id.button6);
        ibutton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendData("W");
                        System.out.print("w pressed");
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("Q");
                        return true;
                }
                return false;
            }
        });
        final ImageButton ibutton2 = (ImageButton) findViewById(R.id.button3);
        ibutton2.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendData("S");
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("Q");
                        return true;
                }
                return false;
            }
        });
        final ImageButton ibutton3 = (ImageButton) findViewById(R.id.button7);
        ibutton3.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendData("A");
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("Q");
                        return true;
                }
                return false;
            }
        });
        final ImageButton ibutton4 = (ImageButton) findViewById(R.id.button4);
        ibutton4.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){

                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendData("D");
                        System.out.print("d pressed");
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendData("Q");
                        return true;
                }
                return false;
            }
        });
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
        surfaceview = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
    }
    boolean sendData(String m){
        try{
            String msg = m;
            ((MyApplication)getApplication()).mmOutputStream.write(msg.getBytes());
            System.out.println("Data Sent");
            return true;
        }catch (Exception e){
            return false;
        }
    }
    protected void onPause(){
        super.onPause();
        activepohoto=0;
        surfaceview.setVisibility(View.GONE);
    }
    protected void onResume(){
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);
        ((MyApplication)getApplication()).server.setOnDataListener(this);
    }
    public void surfaceCreated(SurfaceHolder holder) {
        activepohoto=1;
    }
    public void surfaceChanged(SurfaceHolder holder,int a,int b,int c){
    }
    public void surfaceDestroyed(SurfaceHolder arg0) {
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
    }
    public void conv(int t){
        Message msg=new Message();
        msg.what=t;
        myHandler.sendMessage(msg);
    }
}
