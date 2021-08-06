package com.qy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

public class MaxMapInfoWindow implements View.OnTouchListener {

    public Context mContext;
    public static WindowManager.LayoutParams mWindowParams;
    public static WindowManager mWindowManager;
    private int windowX = 340, windowY = 70;
    public View maxMapInfoView;
    private static ImageView[] heroView = new ImageView[5];
    private static ProgressBar[] heroHp = new ProgressBar[5];
    private static TextView[] heroMaxCd = new TextView[5];
    private static TextView[] heroCd = new TextView[5];
    private static LinearLayout[] linear_layout_hero_max_cd = new LinearLayout[5];
    private static LinearLayout[] linear_layout_hero_cd = new LinearLayout[5];
    private List<Hero> hl;

    public MaxMapInfoWindow(Context context) {
        this.mContext = context;
        initFloatWindow();
    }


    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        maxMapInfoView = (View) inflater.inflate(R.layout.max_map_info, null);
        maxMapInfoView.setOnTouchListener(this);

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
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        heroView[0] = maxMapInfoView.findViewById(R.id.hero_info1);
        heroView[1] = maxMapInfoView.findViewById(R.id.hero_info2);
        heroView[2] = maxMapInfoView.findViewById(R.id.hero_info3);
        heroView[3] = maxMapInfoView.findViewById(R.id.hero_info4);
        heroView[4] = maxMapInfoView.findViewById(R.id.hero_info5);
        heroHp[0] = maxMapInfoView.findViewById(R.id.hero_hp1);
        heroHp[1] = maxMapInfoView.findViewById(R.id.hero_hp2);
        heroHp[2] = maxMapInfoView.findViewById(R.id.hero_hp3);
        heroHp[3] = maxMapInfoView.findViewById(R.id.hero_hp4);
        heroHp[4] = maxMapInfoView.findViewById(R.id.hero_hp5);
        heroMaxCd[0] = maxMapInfoView.findViewById(R.id.hero_max_cd1);
        heroMaxCd[1] = maxMapInfoView.findViewById(R.id.hero_max_cd2);
        heroMaxCd[2] = maxMapInfoView.findViewById(R.id.hero_max_cd3);
        heroMaxCd[3] = maxMapInfoView.findViewById(R.id.hero_max_cd4);
        heroMaxCd[4] = maxMapInfoView.findViewById(R.id.hero_max_cd5);
        heroCd[0] = maxMapInfoView.findViewById(R.id.hero_cd1);
        heroCd[1] = maxMapInfoView.findViewById(R.id.hero_cd2);
        heroCd[2] = maxMapInfoView.findViewById(R.id.hero_cd3);
        heroCd[3] = maxMapInfoView.findViewById(R.id.hero_cd4);
        heroCd[4] = maxMapInfoView.findViewById(R.id.hero_cd5);
        linear_layout_hero_max_cd[0] = maxMapInfoView.findViewById(R.id.linear_layout_hero_max_cd1);
        linear_layout_hero_max_cd[1] = maxMapInfoView.findViewById(R.id.linear_layout_hero_max_cd2);
        linear_layout_hero_max_cd[2] = maxMapInfoView.findViewById(R.id.linear_layout_hero_max_cd3);
        linear_layout_hero_max_cd[3] = maxMapInfoView.findViewById(R.id.linear_layout_hero_max_cd4);
        linear_layout_hero_max_cd[4] = maxMapInfoView.findViewById(R.id.linear_layout_hero_max_cd5);
        linear_layout_hero_cd[0] = maxMapInfoView.findViewById(R.id.linear_layout_hero_cd1);
        linear_layout_hero_cd[1] = maxMapInfoView.findViewById(R.id.linear_layout_hero_cd2);
        linear_layout_hero_cd[2] = maxMapInfoView.findViewById(R.id.linear_layout_hero_cd3);
        linear_layout_hero_cd[3] = maxMapInfoView.findViewById(R.id.linear_layout_hero_cd4);
        linear_layout_hero_cd[4] = maxMapInfoView.findViewById(R.id.linear_layout_hero_cd5);
        for (int i = 0; i < heroView.length; i++) {
            heroView[i].setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < heroHp.length; i++) {
            heroHp[i].setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < heroMaxCd.length; i++) {
            heroMaxCd[i].setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < heroCd.length; i++) {
            heroCd[i].setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        return false;
    }

    public void showFloatWindow() {
        if (null != maxMapInfoView && maxMapInfoView.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.width = 350;
            mWindowParams.height = 175;
            mWindowParams.x = windowX;
            mWindowParams.y = windowY;
            mWindowParams.alpha = 0.9f;
            mWindowManager.addView(maxMapInfoView, mWindowParams);
        }
    }

    public void setMap(ThisData td) {
        if (null != td) {
            hl = td.getHl();
            for (int i = 0; i < hl.size(); i++) {
                setMap(heroView[i], heroHp[i], heroMaxCd[i], heroCd[i], linear_layout_hero_max_cd[i], linear_layout_hero_cd[i], hl.get(i));
            }
        }
    }

    @SuppressLint("WrongConstant")
    public static void setMap(ImageView maxMapHeroInfo, ProgressBar hp, TextView maxCd, TextView cd, LinearLayout linear_layout_hero_max_cd, LinearLayout linear_layout_hero_cd, Hero hero) {
        int isShowMaxCdView = 4, isShowCdView = 4, isShowHpView = 4;
        setLayoutSrc(maxMapHeroInfo, hero.getHeroId());
        maxMapHeroInfo.setVisibility(View.VISIBLE);
        int hpInt = (int) Math.floor(hero.getHpPercentage());
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0 < hero.getHpPercentage() ? 1 : 0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        maxMapHeroInfo.setColorFilter(filter);
        if (0 < hero.getBigMoveCd()) {
            maxCd.setText(hero.getBigMoveCd() + "");
            isShowMaxCdView = 0;
        }
        linear_layout_hero_max_cd.setVisibility(isShowMaxCdView);
        maxCd.setVisibility(isShowMaxCdView);
        if (0 < hero.getSummonercdSkill()) {
            cd.setText(hero.getSummonercdSkill() + "");
            isShowCdView = 0;
        }
        linear_layout_hero_cd.setVisibility(isShowCdView);
        cd.setVisibility(isShowCdView);
        if (0 < hpInt) {
            hp.setSecondaryProgress(hpInt);
            isShowHpView = 0;
        }
        hp.setVisibility(isShowHpView);
    }

    public static void setLayoutXY(ImageView view, int x, int y) {
        view.setX(x);
        view.setY(y);
    }

    public static void setLayoutSrc(ImageView view, int heroId) {
        try {
            Field field = (Field) R.mipmap.class.getDeclaredField("_" + heroId);
            view.setImageResource(field.getInt(R.mipmap.class));
        } catch (Exception ex) {

        }
    }

    public View getMiniMapView() {
        return maxMapInfoView;
    }

    public int getWindowX() {
        return windowX;
    }

    public void setWindowX(int windowX) {
        this.windowX = windowX;
    }

    public int getWindowY() {
        return windowY;
    }

    public void setWindowY(int windowY) {
        this.windowY = windowY;
    }
}