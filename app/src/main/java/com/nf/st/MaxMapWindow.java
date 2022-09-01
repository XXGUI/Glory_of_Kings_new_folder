package com.nf.st;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nf.st.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MaxMapWindow extends AndroidViewModel implements View.OnTouchListener {

    public static Context mContext;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    public View maxMapView;
    private static LinearLayout heroTx;
    private static ImageView heroView[] = new ImageView[5];
    private List<Hero> hl;
    private static int X = 0, Y = 0, entityWidth = 150, entityHeight = 270;
    public static LinearLayout[] llArr = new LinearLayout[5];
    public static VerticalProgressBar[] hpArr = new VerticalProgressBar[5];
    private MutableLiveData<String> data;
    public static boolean isOnemaxEvent = true;
    public static boolean isOpen = false;
    public static CanvasMaxMap rect;
    List<String> xyList = new ArrayList<>();

    public MaxMapWindow(@NonNull Application application) {
        super(application);
        initFloatWindow();
    }

    public MaxMapWindow(@NonNull Application application, Context context) {
        super(application);
        this.mContext = context;
        initFloatWindow();
    }

    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        maxMapView = (View) inflater.inflate(R.layout.max_map, null);
        maxMapView.setOnTouchListener(this);

        mWindowParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SECURE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        heroTx = maxMapView.findViewById(R.id.hero_tx);
        llArr[0] = maxMapView.findViewById(R.id.linear_layout_hero1);
        llArr[1] = maxMapView.findViewById(R.id.linear_layout_hero2);
        llArr[2] = maxMapView.findViewById(R.id.linear_layout_hero3);
        llArr[3] = maxMapView.findViewById(R.id.linear_layout_hero4);
        llArr[4] = maxMapView.findViewById(R.id.linear_layout_hero5);
        heroView[0] = maxMapView.findViewById(R.id.hero1);
        heroView[1] = maxMapView.findViewById(R.id.hero2);
        heroView[2] = maxMapView.findViewById(R.id.hero3);
        heroView[3] = maxMapView.findViewById(R.id.hero4);
        heroView[4] = maxMapView.findViewById(R.id.hero5);
        hpArr[0] = maxMapView.findViewById(R.id.hero_hp1);
        hpArr[1] = maxMapView.findViewById(R.id.hero_hp2);
        hpArr[2] = maxMapView.findViewById(R.id.hero_hp3);
        hpArr[3] = maxMapView.findViewById(R.id.hero_hp4);
        hpArr[4] = maxMapView.findViewById(R.id.hero_hp5);
        for (int i = 0; i < hpArr.length; i++) {
            initProgressBar(hpArr[i]);
        }
        for (int i = 0; i < llArr.length; i++) {
            llArr[i].setVisibility(View.GONE);
        }
        for (int i = 0; i < heroView.length; i++) {
            heroView[i].getBackground().setAlpha(80);
        }
        entityWidth = llArr[0].getLayoutParams().width;
        entityHeight = llArr[0].getLayoutParams().height;
//        hero1View.performClick();
        isOpen = true;

    }

    //设置值动画 progressbar动起来
    private void initProgressBar(VerticalProgressBar ivProgressBar) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(10, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ivProgressBar.setProgress((Integer) valueAnimator.getAnimatedValue());
            }
        });

        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(1);
        valueAnimator.start();

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ivProgressBar.setProgress(100);
            }
        });
    }

    public static void updateHeroView(boolean isShow) {
        if (isShow) {
            heroTx.setVisibility(View.VISIBLE);
        } else {
            heroTx.setVisibility(View.GONE);
        }
    }

    public void showFloatWindow() {
        if (null != maxMapView && maxMapView.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.width = metrics.widthPixels <= 1080 ? 2160 : metrics.widthPixels;
            mWindowParams.height = metrics.heightPixels;
//            mWindowManager.addView(maxMapView, mWindowParams);
//            RounderCornerImageView view=new RounderCornerImageView(mContext);
            rect = new CanvasMaxMap(mContext,5);
            mWindowManager.addView(rect, mWindowParams);
        }
    }

    public void setMap(ThisData td) {
        try {
            if (null != td && null != td.getHl()) {
                hl = td.getHl();
                for (int i = 0; i < hl.size(); i++) {
//                    setMap(hpArr[i], llArr[i], hl.get(i));
                    xyList.add(hl.get(i).getEntityXY());
                }
                rect.setXY(xyList);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rect.notifyView();
            xyList.clear();
        }
    }

    public static void setMap(ProgressBar hp, LinearLayout linear_layout_hero_max, Hero hero) {
        hp.setSecondaryProgress((int) Math.floor(hero.getHpPercentage()));
        if (0 < hero.getHpPercentage()) {
            linear_layout_hero_max.setVisibility(View.VISIBLE);
            String[] xyArr = hero.getEntityXY().split(",");
            setLayoutXY(linear_layout_hero_max, Float.parseFloat(xyArr[1]), Float.parseFloat(xyArr[0]));
        } else {
            linear_layout_hero_max.setVisibility(View.GONE);
        }
    }

    public static void setLayoutXY(LinearLayout view, float x, float y) {
        view.setX(y - 50 + X);
        view.setY(x - 210 + Y);
    }

    public static void setLayoutSrc(ImageView view, int heroId) {
        try {
            Field field = (Field) R.mipmap.class.getDeclaredField("_" + heroId);
            view.setImageResource(field.getInt(R.mipmap.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return false;
    }


    public static void setChuMoXy() {
        isOnemaxEvent = true;
    }

    public View getMaxMapView() {
        return maxMapView;
    }

    public void setEntitySize(int entitySize) {
        for (int i = 0; i < llArr.length; i++) {
            llArr[i].setLayoutParams(new LinearLayout.LayoutParams(entitySize - 100, entitySize + 20));
        }
    }

    public void setX(int x) {
        X = x;
    }

    public void setY(int y) {
        Y = y;
    }

    public MutableLiveData<String> getData() {
        return data;
    }
}