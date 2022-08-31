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
import android.widget.TextView;

import java.lang.reflect.Field;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

public class TpisWindow implements View.OnTouchListener {
    public Context mContext;
    public WindowManager.LayoutParams mWindowParams;
    public WindowManager mWindowManager;
    public static TextView textView;
    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;
    public static boolean isUpdateXY = false, isOut = false;
    public static Observable<Boolean> observable;

    public TpisWindow(Context context, String value) {
        this.mContext = context;
        initFloatWindow();
        showFloatWindow();
        setText(value);
        observable = Observable.create(
                (ObservableEmitter<Boolean> emitter) -> {
                    while (true) {
                        emitter.onNext(true);
                        Thread.sleep(1000 * 10);
                    }
                }
        );
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (Boolean s) -> updateView(s),
                        Functions.<Throwable>emptyConsumer(),
                        () -> System.out.println("线程完成")
                );
    }

    public static void updateView(Boolean b) {
        textView.setVisibility(View.GONE);
        isOut = true;
    }

    public void setText(String value) {
        textView.setText(value);
        textView.setVisibility(View.VISIBLE);
    }

    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        textView = new TextView(mContext);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.mipmap.tpis_bg);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(15);
        textView.setOnTouchListener(this);

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
                mWindowManager.updateViewLayout(textView, mWindowParams);
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
        if (null != textView && textView.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.x = MainActivity.x / 2 - 250;
            mWindowParams.y = 0;
            mWindowParams.width = 500;
            mWindowParams.height = 120;
            mWindowManager.addView(textView, mWindowParams);
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CommandExecution.execCmdNotRoot("input tap 2222 50 \n");
                }
            });
        }
    }

    public void deleteWindow() {
        mWindowManager.removeView(textView);
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