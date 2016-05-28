package com.example.mpape.bear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

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
