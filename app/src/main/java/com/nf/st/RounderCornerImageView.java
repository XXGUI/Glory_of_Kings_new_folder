package com.nf.st;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RounderCornerImageView extends View {
    private Paint mBitmapPaint;//paint
    private List<RectF> mBrounds = new ArrayList<>();//rect
    private float mRadius = 20.0f;//round
    public static int height = MainActivity.y, width = MainActivity.x;
    public static int heightPaint = 270, widthPaint = 150;
    Random rd = new Random();

    public RounderCornerImageView(Context context) {
        this(context, null);
    }

    public RounderCornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RounderCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setColor(Color.parseColor("#FFFFEB3B"));
        mBitmapPaint.setStyle(Paint.Style.STROKE);//空心
        mBitmapPaint.setStrokeWidth(7);//空心圆的边框
        for (int i = 0; i < 5; i++) {
            mBrounds.add(new RectF());
        }
//        mBrounds = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MainActivity.x, MainActivity.y);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            for (RectF mBround : mBrounds) {
                mBround.set(rd.nextInt(100)+widthPaint, rd.nextInt(100)+heightPaint, widthPaint, heightPaint);
            }
            if (null != mBitmapPaint.getShader()) {
                Matrix m = new Matrix();
                m.setTranslate(0, 0);
                mBitmapPaint.getShader().setLocalMatrix(m);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (null != mBitmapPaint) {
//                canvas.drawRect(rd.nextInt(100), rd.nextInt(100), width, height, paint);
            for (RectF mBround : mBrounds) {
                canvas.drawRoundRect(mBround, mRadius, mRadius, mBitmapPaint);
//                canvas.drawRect(rd.nextInt(100), rd.nextInt(100), widthPaint, heightPaint, mBitmapPaint);
            }
        }
    }
}

