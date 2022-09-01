package com.nf.st;

import android.content.Context;
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

import com.nf.st.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxMapInfoWindow implements View.OnTouchListener {

    public static Context mContext;
    public static WindowManager.LayoutParams mWindowParams;
    public static WindowManager mWindowManager;
    private int windowX = 340, windowY = 70;
    public View maxMapInfoView;
    private static ImageView[] heroView = new ImageView[5];
    private static ImageView[] heroTopCd = new ImageView[5];
    private static ImageView[] heroMaxCdImg = new ImageView[5];
    private static ImageView[] heroCdImg = new ImageView[5];
    private static ProgressBar[] heroHp = new ProgressBar[5];
    private static TextView[] heroMaxCd = new TextView[5];
    private static TextView[] heroCd = new TextView[5];
    private static LinearLayout[] linear_layout_hero_max_cd = new LinearLayout[5];
    private static LinearLayout[] linear_layout_hero_cd = new LinearLayout[5];
    private List<Hero> hl;
    public static boolean openTpis = false;
    public static HeroName heroName = new HeroName();
    public static Map<String, TpisWindow> tpisWindowMap = new HashMap<>();

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
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_SECURE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

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
        heroTopCd[0] = maxMapInfoView.findViewById(R.id.hero_top_cd1);
        heroTopCd[1] = maxMapInfoView.findViewById(R.id.hero_top_cd2);
        heroTopCd[2] = maxMapInfoView.findViewById(R.id.hero_top_cd3);
        heroTopCd[3] = maxMapInfoView.findViewById(R.id.hero_top_cd4);
        heroTopCd[4] = maxMapInfoView.findViewById(R.id.hero_top_cd5);
        heroCd[0] = maxMapInfoView.findViewById(R.id.hero_cd1);
        heroCd[1] = maxMapInfoView.findViewById(R.id.hero_cd2);
        heroCd[2] = maxMapInfoView.findViewById(R.id.hero_cd3);
        heroCd[3] = maxMapInfoView.findViewById(R.id.hero_cd4);
        heroCd[4] = maxMapInfoView.findViewById(R.id.hero_cd5);
        heroMaxCdImg[0] = maxMapInfoView.findViewById(R.id.hero_max_cd_img1);
        heroMaxCdImg[1] = maxMapInfoView.findViewById(R.id.hero_max_cd_img2);
        heroMaxCdImg[2] = maxMapInfoView.findViewById(R.id.hero_max_cd_img3);
        heroMaxCdImg[3] = maxMapInfoView.findViewById(R.id.hero_max_cd_img4);
        heroMaxCdImg[4] = maxMapInfoView.findViewById(R.id.hero_max_cd_img5);
        heroCdImg[0] = maxMapInfoView.findViewById(R.id.hero_cd_img1);
        heroCdImg[1] = maxMapInfoView.findViewById(R.id.hero_cd_img2);
        heroCdImg[2] = maxMapInfoView.findViewById(R.id.hero_cd_img3);
        heroCdImg[3] = maxMapInfoView.findViewById(R.id.hero_cd_img4);
        heroCdImg[4] = maxMapInfoView.findViewById(R.id.hero_cd_img5);
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
//        for (int i = 0; i < linear_layout_hero_max_cd.length; i++) {
//            linear_layout_hero_max_cd[i].setVisibility(View.INVISIBLE);
//        }
//        for (int i = 0; i < linear_layout_hero_cd.length; i++) {
//            linear_layout_hero_cd[i].setVisibility(View.INVISIBLE);
//        }
        for (int i = 0; i < heroView.length; i++) {
            heroView[i].setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < heroHp.length; i++) {
            heroHp[i].setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < heroTopCd.length; i++) {
            heroTopCd[i].setVisibility(View.VISIBLE);
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
            mWindowParams.height = 210;
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
                setMap(heroView[i], heroMaxCdImg[i], heroCdImg[i], heroTopCd[i], heroHp[i], heroMaxCd[i],
                        heroCd[i], hl.get(i));
            }
        }
    }

    public static void setMap(ImageView maxMapHeroInfo, ImageView heroMaxCdImg, ImageView heroCdImg,
                              ImageView heroTopCd, ProgressBar hp, TextView maxCd, TextView cd,Hero hero) {
        int isShowMaxCdView = 4, isShowCdView = 4, isShowHpView = 4;
        setImgSrc(maxMapHeroInfo, hero.getHeroId(), "_");
        setImgSrc(heroCdImg, hero.getSummonercdSkillId(), "_");
        setImgSrc(heroMaxCdImg, hero.getHeroId(), "cd_");
        maxMapHeroInfo.setVisibility(View.VISIBLE);
        heroMaxCdImg.setVisibility(View.VISIBLE);
        int hpInt = (int) Math.floor(hero.getHpPercentage());
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0 < hpInt ? 1 : 0);
        ColorMatrix matrix2 = new ColorMatrix();
        matrix2.setSaturation(0 < hero.getBigMoveCd() ? 0 : 1);
        ColorMatrix matrix3 = new ColorMatrix();
        matrix3.setSaturation(0 < hero.getSummonercdSkill() ? 0 : 1);
        maxMapHeroInfo.setColorFilter(new ColorMatrixColorFilter(matrix));
        heroMaxCdImg.setColorFilter(new ColorMatrixColorFilter(matrix2));
        heroCdImg.setColorFilter(new ColorMatrixColorFilter(matrix3));
        System.out.println(hero.getHeroId()+"=="+hero.getSummonercdSkillId());
        if (0 < hero.getBigMoveCd()) {
            maxCd.setText(hero.getBigMoveCd() + "");
            isShowMaxCdView = 0;
        }
        if (0 < hero.getSummonercdSkill()) {
            cd.setText(hero.getSummonercdSkill() + "");
            isShowCdView = 0;
        }
        if (0 < hpInt) {
            hp.setSecondaryProgress(hpInt);
            isShowHpView = 0;
        }

        setTopCdSrc(heroTopCd, 0 == isShowMaxCdView ? 0 : 1);
//        linear_layout_hero_max_cd.setVisibility(isShowMaxCdView);
        maxCd.setVisibility(isShowMaxCdView);
//        linear_layout_hero_cd.setVisibility(isShowCdView);
        cd.setVisibility(isShowCdView);
        hp.setVisibility(isShowHpView);
    }

    public static void setLayoutXY(ImageView view, int x, int y) {
        view.setX(x);
        view.setY(y);
    }

    public static void setImgSrc(ImageView view, int imgId, String topz) {
        try {
            if (0 != imgId) {
                Field field = MainActivity.lruCacheUtils.getPicFromMemory(topz + imgId);
                view.setImageResource(field.getInt(R.mipmap.class));
            }
        } catch (Exception e) {
        }
    }

    public static void setTopCdSrc(ImageView view, int imgId) {
        try {
            Field field = MainActivity.lruCacheUtils.getPicFromMemory("bigcd" + imgId);
            view.setImageResource(field.getInt(R.mipmap.class));
        } catch (Exception e) {
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