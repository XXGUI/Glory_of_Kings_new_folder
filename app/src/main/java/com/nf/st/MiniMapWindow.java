package com.nf.st;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nf.st.R;

import java.lang.reflect.Field;
import java.util.List;

public class MiniMapWindow extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    static SurfaceHolder surfaceHolder;
    public static Context mContext;
    public static WindowManager.LayoutParams mWindowParams;
    public static WindowManager mWindowManager;
    public View miniMapView;
    private static LinearLayout heroTx;
    //    private static ImageView hero1View, hero2View, hero3View, hero4View, hero5View;
    private static ImageView[] heroView = new ImageView[5];
    private ImageView[] goHome = new ImageView[5];
    private static TextView buff1, buff2, buff3, buff4,
            weLizard, weWolf, wePid, weBird, enemyLizard, enemyWolf, enemyPid, enemyBird;

    private List<Hero> hl;
    private static FrameLayout[] linear_layout_min_map_hero = new FrameLayout[5];
    private static ProgressBar[] heroHp = new ProgressBar[5];
    private static LinearLayout soldie;
    private static int screenOffset = 150;
    int buffCd = 99, ygCd = 79;
    public static float w = MainActivity.x / 2;
    //    public static float w = 1080;
    public static boolean openTpis = false, update_yeguai_rd = false;


    public static Paint paint;
    public static Canvas canvas;

    public static Animation rotateAnimation = null;

    public MiniMapWindow(Context context) {
        super(MainActivity.cContext);
        try {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            this.setZOrderOnTop(true);
            surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
            this.mContext = context;
            initFloatWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        miniMapView = (View) inflater.inflate(R.layout.mini_map, null);
        miniMapView.setOnTouchListener(this);

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

        heroTx = miniMapView.findViewById(R.id.hero_tx);
        soldie = miniMapView.findViewById(R.id.soldie);
        linear_layout_min_map_hero[0] = miniMapView.findViewById(R.id.linear_layout_min_map_hero1);
        linear_layout_min_map_hero[1] = miniMapView.findViewById(R.id.linear_layout_min_map_hero2);
        linear_layout_min_map_hero[2] = miniMapView.findViewById(R.id.linear_layout_min_map_hero3);
        linear_layout_min_map_hero[3] = miniMapView.findViewById(R.id.linear_layout_min_map_hero4);
        linear_layout_min_map_hero[4] = miniMapView.findViewById(R.id.linear_layout_min_map_hero5);
        heroView[0] = miniMapView.findViewById(R.id.hero1);
        heroView[1] = miniMapView.findViewById(R.id.hero2);
        heroView[2] = miniMapView.findViewById(R.id.hero3);
        heroView[3] = miniMapView.findViewById(R.id.hero4);
        heroView[4] = miniMapView.findViewById(R.id.hero5);
        goHome[0] = miniMapView.findViewById(R.id.go_home1);
        goHome[1] = miniMapView.findViewById(R.id.go_home2);
        goHome[2] = miniMapView.findViewById(R.id.go_home3);
        goHome[3] = miniMapView.findViewById(R.id.go_home4);
        goHome[4] = miniMapView.findViewById(R.id.go_home5);
        buff1 = miniMapView.findViewById(R.id.buff1);
        buff2 = miniMapView.findViewById(R.id.buff2);
        buff3 = miniMapView.findViewById(R.id.buff3);
        buff4 = miniMapView.findViewById(R.id.buff4);
        weLizard = miniMapView.findViewById(R.id.weLizard);
        weWolf = miniMapView.findViewById(R.id.weWolf);
        wePid = miniMapView.findViewById(R.id.wePid);
        weBird = miniMapView.findViewById(R.id.weBird);
        enemyLizard = miniMapView.findViewById(R.id.enemyLizard);
        enemyWolf = miniMapView.findViewById(R.id.enemyWolf);
        enemyPid = miniMapView.findViewById(R.id.enemyPid);
        enemyBird = miniMapView.findViewById(R.id.enemyBird);
        heroHp[0] = miniMapView.findViewById(R.id.min_map_hero_hp1);
        heroHp[1] = miniMapView.findViewById(R.id.min_map_hero_hp2);
        heroHp[2] = miniMapView.findViewById(R.id.min_map_hero_hp3);
        heroHp[3] = miniMapView.findViewById(R.id.min_map_hero_hp4);
        heroHp[4] = miniMapView.findViewById(R.id.min_map_hero_hp5);
        buff1.setX(170);
        buff1.setY(260);
        buff2.setX(85);
        buff2.setY(155);
        buff3.setX(155);
        buff3.setY(60);
        buff4.setX(245);
        buff4.setY(160);

        weLizard.setX(45);
        weLizard.setY(145);
        weWolf.setX(65);
        weWolf.setY(195);
        wePid.setX(155);
        wePid.setY(225);
        weBird.setX(220);
        weBird.setY(285);

        enemyLizard.setX(290);
        enemyLizard.setY(180);
        enemyWolf.setX(270);
        enemyWolf.setY(130);
        enemyPid.setX(180);
        enemyPid.setY(100);
        enemyBird.setX(110);
        enemyBird.setY(40);

        weLizard.setVisibility(View.GONE);
        weWolf.setVisibility(View.GONE);
        wePid.setVisibility(View.GONE);
        weBird.setVisibility(View.GONE);
        enemyLizard.setVisibility(View.GONE);
        enemyWolf.setVisibility(View.GONE);
        enemyPid.setVisibility(View.GONE);
        enemyBird.setVisibility(View.GONE);
        for (int i = 0; i < linear_layout_min_map_hero.length; i++) {
            linear_layout_min_map_hero[i].setVisibility(View.INVISIBLE);
        }
        rotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_circle_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();
        rotateAnimation.setInterpolator(interpolator);
        for (int i = 0; i < goHome.length; i++) {
            goHome[i].startAnimation(rotateAnimation);
        }
    }

    public static void updateBuffView(boolean isShow) {
        if (isShow) {
            buff1.setVisibility(View.VISIBLE);
            buff2.setVisibility(View.VISIBLE);
            buff3.setVisibility(View.VISIBLE);
            buff4.setVisibility(View.VISIBLE);
//            weLizard.setVisibility(View.VISIBLE);
//            weWolf.setVisibility(View.VISIBLE);
//            wePid.setVisibility(View.VISIBLE);
//            weBird.setVisibility(View.VISIBLE);
//            enemyLizard.setVisibility(View.VISIBLE);
//            enemyWolf.setVisibility(View.VISIBLE);
//            enemyPid.setVisibility(View.VISIBLE);
//            enemyBird.setVisibility(View.VISIBLE);
        } else {
            buff1.setVisibility(View.GONE);
            buff2.setVisibility(View.GONE);
            buff3.setVisibility(View.GONE);
            buff4.setVisibility(View.GONE);
//            weLizard.setVisibility(View.GONE);
//            weWolf.setVisibility(View.GONE);
//            wePid.setVisibility(View.GONE);
//            weBird.setVisibility(View.GONE);
//            enemyLizard.setVisibility(View.GONE);
//            enemyWolf.setVisibility(View.GONE);
//            enemyPid.setVisibility(View.GONE);
//            enemyBird.setVisibility(View.GONE);
        }
    }

    public static void updateHeroView(boolean isShow) {
        if (isShow) {
            heroTx.setVisibility(View.VISIBLE);
        } else {
            heroTx.setVisibility(View.GONE);
        }
    }

    public static void updateSoldieView(boolean isShow) {
        if (isShow) {
            soldie.setVisibility(View.VISIBLE);
        } else {
            soldie.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return false;
    }


    public void showFloatWindow() {
        if (null != miniMapView && miniMapView.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.width = 350;
            mWindowParams.height = 350;
            mWindowManager.addView(miniMapView, mWindowParams);
        }
    }

    public void setMap(ThisData td) {
        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Thread.yield();
//                canvas = surfaceHolder.lockHardwareCanvas();
//            } else {
//                canvas = surfaceHolder.lockCanvas();
//            }
//            if (null != canvas) {
//                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            }
            if (null != td && null != td.getHl()) {
                hl = td.getHl();
                for (int i = 0; i < hl.size(); i++) {
                    setMap(linear_layout_min_map_hero[i], goHome[i], heroHp[i], heroView[i], hl.get(i));
                }
                if (update_yeguai_rd) {
                    setCd(buff1, buffCd, td.getEnemyRedBuffcd());
                    setCd(buff2, buffCd, td.getEnemyBlueBuffcd());
                    setCd(buff3, buffCd, td.getWeRedBuffcd());
                    setCd(buff4, buffCd, td.getWeBlueBuffcd());
                } else {
                    setCd(buff1, buffCd, td.getWeRedBuffcd());
                    setCd(buff2, buffCd, td.getWeBlueBuffcd());
                    setCd(buff3, buffCd, td.getEnemyRedBuffcd());
                    setCd(buff4, buffCd, td.getEnemyBlueBuffcd());
                }

//                setCd(weLizard, ygCd, td.getWeLizard());
//                setCd(weWolf, ygCd, td.getWeWolf());
//                setCd(wePid, ygCd, td.getWePid());
//                setCd(weBird, ygCd, td.getWeBird());
//                setCd(enemyLizard, ygCd, td.getEnemyLizard());
//                setCd(enemyWolf, ygCd, td.getEnemyWolf());
//                setCd(enemyPid, ygCd, td.getEnemyPid());
//                setCd(enemyBird, ygCd, td.getEnemyBird());
//                setBx(td.getSoldier());
            }
        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
//        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    //绘制小地图兵线
    private static void drawMiniBx(float xx, float yy) {
        if (null != paint && null != canvas) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            paint.setTextSize(10);
            canvas.drawText("●", xx, yy, paint);
        } else {
            System.out.println("====参数为NULL");
        }
    }

    public static void setBx(List<String> data) {
//        soldie.removeAllViews();
        for (String s : data) {
//            TextView soldieView = new TextView(mContext);
//            soldieView.setText("●");
//            soldieView.setTextSize(3);
//            soldieView.setTextColor(Color.RED);
            String[] xy = s.split(",");
            float x = Float.valueOf(xy[0]), y = Float.valueOf(xy[1]);
            float SmapSize = 2160 / (60f / 19f);
//            soldieView.setX(x * (SmapSize / 102100f));
//            soldieView.setY(y * (SmapSize / 102100f));
//            soldie.addView(soldieView);
            drawMiniBx(x * (SmapSize / 102100f) + 1, y * (SmapSize / 102100f));
        }
    }

    public static void setCd(TextView view, int cd, int dataCd) {
        if (cd == dataCd || 0 >= dataCd) {
            view.setText("●");
        } else {
            view.setText(dataCd + "");
        }
    }

    public void setMap(FrameLayout llmph, ImageView goHomeView, ProgressBar hp, ImageView heroTxView, Hero hero) {
        int hpInt = (int) Math.floor(hero.getHpPercentage());
        if (0 < hpInt) {
            llmph.setVisibility(View.VISIBLE);
            hp.setSecondaryProgress(hpInt);
            String[] xyArr = hero.getMapXY().split(",");
            setLayoutSrc(heroTxView, hero.getHeroId());
            setLayoutXY(llmph, Float.parseFloat(xyArr[1]), Float.parseFloat(xyArr[0]));

            if (0 < hero.getGoHome()) {
                if (null == goHomeView.getAnimation()) {
                    goHomeView.startAnimation(rotateAnimation);
                }
            } else {
                goHomeView.clearAnimation();
            }
        } else {
            llmph.setVisibility(View.GONE);
        }
    }

    public static void setLayoutXY(FrameLayout view, float x, float y) {
        view.setX((float) (x * 0.000003148 * w) + screenOffset + 5);
        view.setY((float) (y * 0.000003148 * w) + screenOffset - 2);
    }

    public static void setLayoutSrc(ImageView view, int heroId) {
        try {
            Field field = MainActivity.lruCacheUtils.getPicFromMemory("_" + heroId);
            view.setImageResource(field.getInt(R.mipmap.class));
        } catch (Exception e) {
//            throw new RuntimeException(e);
        }
    }

    public View getMiniMapView() {
        return miniMapView;
    }

    public void setX(float x) {
        w = x;
    }

    /**
     * 设置图标大小
     *
     * @param size
     */
    public void setViewSize(int size) {
        ViewGroup.LayoutParams lp = miniMapView.getLayoutParams();
        try {
            mWindowParams.width = size;
            mWindowParams.height = size;
            lp.width = size;
            lp.height = size;
            mWindowManager.updateViewLayout(miniMapView, mWindowParams);
            miniMapView.setLayoutParams(lp);
        } catch (Exception e) {
            System.out.println("设置异常");
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}