package com.example.mpape.bear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.content.Intent;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyImageView extends ImageView {
    public double offset;
    private Bitmap bit;
    public void init(){
        bit= BitmapFactory.decodeResource(getResources(),R.drawable.ht2);
        offset=0;
    }
    public MyImageView(Context context){
        super(context);
        init();
    }
    public MyImageView(Context c,AttributeSet a){
        super(c,a);
        init();
    }
    public MyImageView(Context c,AttributeSet a,int d){
        super(c,a,d);
        init();
    }
    void setLoc(int x,int y){
//        int a=1;
//        while(a!=2){
//            a=3;
//        }
//        this.setFrame(this.getLeft()+x,this.getTop()+y,this.getRight()+x,this.getBottom()+y);
    }
    @Override
    protected void onDraw(Canvas can){
        Rect src=new Rect(0,0,bit.getWidth(),bit.getHeight());
        float d=(float)(offset/100.0*can.getWidth()*0.4);
        RectF dst=new RectF(d,0,(float)(can.getWidth()+d),can.getHeight());
        //can.drawBitmap();
        can.drawBitmap(bit,src,dst,null);
        // can.drawBitmap(bit,can.getWidth()/3,0,null);
    }
}
