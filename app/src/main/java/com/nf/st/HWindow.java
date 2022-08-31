package com.nf.st;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jni.JniDemo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HWindow implements View.OnTouchListener {
    public Context mContext;
    public WindowManager.LayoutParams mWindowParams;
    public WindowManager mWindowManager;
    public Button button;
    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;
    public static boolean isUpdateXY = false, isOpen = false;
    public H h;
    public String[] startCmd;


    public HWindow(Context context, H h) {
        this.mContext = context;
        this.h = h;
        startCmd = h.getData().split(";");
        initFloatWindow();
        showFloatWindow();
    }

    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        button = (Button) new Button(mContext);
        button.setText(h.getName());
        button.setTextColor(Color.WHITE);
        button.setBackgroundResource(R.drawable.button_shape);
        button.setTextSize(12);
        button.setOnTouchListener(this);

        mWindowParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = 130;
        mWindowParams.height = 130;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (isUpdateXY) {
            floatLayoutTouch(event);
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
                mWindowManager.updateViewLayout(button, mWindowParams);
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
        if (null != button && button.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.x = 2000;
            mWindowParams.y = 500;
            mWindowParams.width = 130;
            mWindowParams.height = 130;
            mWindowManager.addView(button, mWindowParams);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CommandExecution.execCommand(startCmd, true);
                }
            });
        }
    }

    public void deleteWindow() {
        mWindowManager.removeView(button);
    }

    public void setIsUpdateXY(boolean isUpdateXY) {
        this.isUpdateXY = isUpdateXY;
    }

    // 获取系统状态栏高度
    public int getSysBarHeight(Context contex) {
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


}