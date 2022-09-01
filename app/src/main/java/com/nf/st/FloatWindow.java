package com.nf.st;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;

public class FloatWindow implements View.OnTouchListener {
    public MenuWindow menuWindow;

    public Context mContext;
    public static WindowManager.LayoutParams mWindowParams;
    public static WindowManager mWindowManager;
    public static View fwView;
    private int windowX = 340, windowY = 70;

    public FloatWindow(Context context) {
        this.mContext = context;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onTouch(View view, MotionEvent event) {
//        floatLayoutTouch(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            fwView.setVisibility(View.GONE);
            if (null != menuWindow && !menuWindow.isOpen) {
                menuWindow.getMenuView().setVisibility(View.VISIBLE);
                return false;
            }
            if (null == menuWindow) {
                menuWindow = new MenuWindow(mContext);
            }
        }
        return false;
    }

    public void showFloatWindow() {
        if (null != fwView && fwView.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            System.out.println("===" + MainActivity.x);
            if (2160 >= MainActivity.x) {
                mWindowParams.x = 600;
            } else if (2160 < MainActivity.x && 2320 >= MainActivity.x) {
                mWindowParams.x = 640;
            }else{
                mWindowParams.x = 600;
            }
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