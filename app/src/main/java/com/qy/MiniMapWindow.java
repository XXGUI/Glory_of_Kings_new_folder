package com.qy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiniMapWindow implements View.OnTouchListener {

    public Context mContext;
    public static WindowManager.LayoutParams mWindowParams;
    public static WindowManager mWindowManager;
    public View miniMapView;
    private static LinearLayout heroTx;
//    private static ImageView hero1View, hero2View, hero3View, hero4View, hero5View;
    private static ImageView[] heroView=new ImageView[5];
    private static TextView buff1, buff2, buff3, buff4;
    private List<Hero> hl;
    private static int screenOffset = 150;

    public MiniMapWindow(Context context) {
        this.mContext = context;
        initFloatWindow();
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
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        heroTx = miniMapView.findViewById(R.id.hero_tx);
        heroView[0] = miniMapView.findViewById(R.id.hero1);
        heroView[1] = miniMapView.findViewById(R.id.hero2);
        heroView[2] = miniMapView.findViewById(R.id.hero3);
        heroView[3] = miniMapView.findViewById(R.id.hero4);
        heroView[4] = miniMapView.findViewById(R.id.hero5);
        buff1 = miniMapView.findViewById(R.id.buff1);
        buff2 = miniMapView.findViewById(R.id.buff2);
        buff3 = miniMapView.findViewById(R.id.buff3);
        buff4 = miniMapView.findViewById(R.id.buff4);
        buff1.setX(170);
        buff1.setY(260);
        buff2.setX(85);
        buff2.setY(155);
        buff3.setX(160);
        buff3.setY(50);
        buff4.setX(245);
        buff4.setY(160);
        for (int i = 0; i < heroView.length; i++) {
            heroView[i].setVisibility(View.INVISIBLE);
        }
    }

    public static void updateBuffView(boolean isShow) {
        if (isShow) {
            buff1.setVisibility(View.VISIBLE);
            buff2.setVisibility(View.VISIBLE);
            buff3.setVisibility(View.VISIBLE);
            buff4.setVisibility(View.VISIBLE);
        } else {
            buff1.setVisibility(View.GONE);
            buff2.setVisibility(View.GONE);
            buff3.setVisibility(View.GONE);
            buff4.setVisibility(View.GONE);
        }
    }

    public static void updateHeroView(boolean isShow) {
        if (isShow) {
            heroTx.setVisibility(View.VISIBLE);
        } else {
            heroTx.setVisibility(View.GONE);
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
            if (null != td && null != td.getHl()) {
                hl = td.getHl();
                int buffCd = 100;
                for (int i = 0; i <hl.size() ; i++) {
                    setMap(heroView[i], hl.get(i));
                }

                if (buffCd == td.getWeRedBuffcd()) {
                    buff1.setText("●");
                } else {
                    buff1.setText(td.getWeRedBuffcd() + "");
                }
                if (buffCd == td.getWeBlueBuffcd()) {
                    buff2.setText("●");
                } else {
                    buff2.setText(td.getWeBlueBuffcd() + "");
                }
                if (buffCd == td.getEnemyRedBuffcd()) {
                    buff3.setText("●");
                } else {
                    buff3.setText(td.getEnemyRedBuffcd() + "");
                }
                if (buffCd == td.getEnemyBlueBuffcd()) {
                    buff4.setText("●");
                } else {
                    buff4.setText(td.getEnemyBlueBuffcd() + "");
                }
            }
        } catch (Exception e) {
            System.out.println("MiniMap:160====程序异常");
        }
    }

    public static void setMap(ImageView view, Hero hero) {
        if (0 < hero.getHpPercentage()) {
            view.setVisibility(View.VISIBLE);
            String[] xyArr = hero.getMapXY().split(",");
            setLayoutSrc(view, hero.getHeroId());
            setLayoutXY(view, Double.valueOf(xyArr[1]).intValue(), Double.valueOf(xyArr[0]).intValue());
        }else{
            view.setVisibility(View.GONE);
        }
    }

    public static void setLayoutXY(ImageView view, int x, int y) {
        float w=2160f/2;
        view.setX((float) (x*0.000003148*w)+screenOffset+5);
        view.setY((float) (y*0.000003148*w)+screenOffset-2);
    }

    public static void setLayoutSrc(ImageView view, int heroId) {
        try {
            Field field = (Field) R.mipmap.class.getDeclaredField("_" + heroId);
            view.setImageResource(field.getInt(R.mipmap.class));
        } catch (Exception ex) {
            System.out.println("MiniMap===212：异常");
        }
    }

    public View getMiniMapView() {
        return miniMapView;
    }

}