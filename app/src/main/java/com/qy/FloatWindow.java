package com.qy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.util.List;

public class FloatWindow implements View.OnTouchListener {
    public MenuWindow menuWindow;

    public Context mContext;
    public static WindowManager.LayoutParams mWindowParams;
    public static WindowManager mWindowManager;
    public static View fwView;
    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;
    private Process p;
    private int windowX=340,windowY=70;

    public FloatWindow(Context context,Process p) {
        this.mContext = context;
        this.p=p;
        initFloatWindow();
    }

    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        fwView = (View) inflater.inflate(R.layout.icon_float, null);
        fwView.setOnTouchListener(this);

        mWindowParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
//        floatLayoutTouch(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            fwView.setVisibility(View.GONE);
            if (null != menuWindow&&!menuWindow.isOpen) {
                Animation topAnim = AnimationUtils.loadAnimation( mContext, R.anim.top_enter);
                menuWindow.getMenuView().setAnimation(topAnim);
                menuWindow.getMenuView().setVisibility(View.VISIBLE);
                return false;
            }
            if(null==menuWindow){
                menuWindow = new MenuWindow(mContext);
            }
        }
        return false;
    }

    /**
     * 拖动图标
     *
     * @param motionEvent
     * @return
     */
    private boolean floatLayoutTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取相对View的坐标，即以此View左上角为原点
                mInViewX = motionEvent.getX();
                mInViewY = motionEvent.getY();
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                mDownInScreenX = motionEvent.getRawX();
                mDownInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                break;
            case MotionEvent.ACTION_MOVE:
                // 更新浮动窗口位置参数
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                mWindowParams.x = (int) (mInScreenX - mInViewX);
                mWindowParams.y = (int) (mInScreenY - mInViewY);
                // 手指移动的时候更新小悬浮窗的位置
                mWindowManager.updateViewLayout(fwView, mWindowParams);
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (mDownInScreenX == mInScreenX && mDownInScreenY == mInScreenY) {

                }
                break;
        }
        return true;
    }

    public void showFloatWindow() {
        if (null != fwView && fwView.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.x = 600;
            mWindowParams.width = 257;
            mWindowParams.height = 58;
            fwView.setAlpha(0.89F);
            mWindowManager.addView(fwView, mWindowParams);
        }
    }

    public void hideFloatWindow() {
        if (fwView.getParent() != null)
            mWindowManager.removeView(fwView);
    }


    // 获取系统状态栏高度
    public static int getSysBarHeight(Context contex) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = contex.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public View getFwView() {
        return fwView;
    }
}