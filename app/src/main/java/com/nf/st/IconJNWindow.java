package com.nf.st;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.lang.reflect.Field;

public class IconJNWindow implements View.OnTouchListener {
    public Context mContext;
    public static WindowManager.LayoutParams mWindowParams;
    public static WindowManager mWindowManager;
    public static View iconJNView;
    public static ImageView iconImg;
    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;
    private int windowX = 1000, windowY = 200;
    public static boolean isUpdateXY = false, isOpen = false;

    public IconJNWindow(Context context) {
        this.mContext = context;
        initFloatWindow();
    }

    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        iconJNView = (View) inflater.inflate(R.layout.icon_jineng, null);
        iconJNView.setOnTouchListener(this);

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
        iconImg = iconJNView.findViewById(R.id.icon_jineng);
        isOpen = true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        floatLayoutTouch(event);
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
                mWindowManager.updateViewLayout(iconJNView, mWindowParams);
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (mDownInScreenX == mInScreenX && mDownInScreenY == mInScreenY) {

                }
                break;
        }
        return true;
    }

    public void showFloatWindow(Field field) {
        if (null != iconJNView && iconJNView.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.x = MainActivity.x / 2 - 250;
            mWindowParams.y = MainActivity.y / 2 - 190;
            mWindowParams.width = 100;
            mWindowParams.height = 100;
            mWindowManager.addView(iconJNView, mWindowParams);
        }
        try {
            if (null != field) {
                iconImg.setImageResource(field.getInt(R.mipmap.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideFloatWindow() {
        if (iconJNView.getParent() != null)
            mWindowManager.removeView(iconJNView);
    }

    /**
     * 设置图标大小
     *
     * @param size
     */
    public void setIconSize(int size) {
        if (null == iconImg) {
            System.err.println("iconLinearLayout为空");
            return;
        }
        ViewGroup.LayoutParams lp = iconImg.getLayoutParams();
        try {
            mWindowParams.width = size;
            mWindowParams.height = size;
            lp.width = size;
            lp.height = size;
            mWindowManager.updateViewLayout(iconJNView, mWindowParams);
            iconImg.setLayoutParams(lp);
        } catch (Exception e) {
            System.out.println("设置异常");
        }
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


    public static boolean isIsUpdateXY() {
        return isUpdateXY;
    }

    public static void setIsUpdateXY(boolean isUpdateXY) {
        IconJNWindow.isUpdateXY = isUpdateXY;
    }

    public static View getIconJNView() {
        return iconJNView;
    }

    public static void setIconJNView(View iconJNView) {
        IconJNWindow.iconJNView = iconJNView;
    }
}