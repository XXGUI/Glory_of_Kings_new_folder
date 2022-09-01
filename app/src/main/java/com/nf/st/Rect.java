package com.nf.st;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

//绘制正方型
public class Rect extends Contanier {
    private float x = 0, y = 0;
    private List<Paint> pl = new ArrayList<>();

    public Rect() {
        for (int i = 0; i < 5; i++) {
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#FFFFEB3B"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(7);
            paint.setFilterBitmap(true);
            pl.add(paint);
        }
    }

    @Override
    public void childrenDraw(Canvas canvas) {
        super.childrenDraw(canvas);
        for (Paint paint : pl) {
            canvas.drawRect(0, 0, 150, 270, paint);
            this.setX(x);
            this.setY(y);
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setXY(float x,float y){
        this.x = x;this.y = y;
    }

}