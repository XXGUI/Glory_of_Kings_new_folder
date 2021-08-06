package com.qy;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

public class MenuWindow extends Activity implements View.OnTouchListener {

    private String cmd = "./data/local/tmp/stupidtencent";
    private String dataFile = "/data/data/com.qy/kkData";
    public Context mContext;
    public static MiniMapWindow miniMapWindow;
    public static MaxMapInfoWindow maxMapInfoWindow;
    public static MaxMapWindow maxMapWindow;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    public View menuView;
    public TabHost tabHost;
    public static Thread startReadThread;
    public boolean isOpen = true, isThreadRun = false;
    public Button close_btn;
    public CheckBox start_huizhi, start_all, start_touxiang, start_fangkuang,
            start_jineng_jishi, start_yeguai_jishi, start_show_bingxian, end;
    public SeekBar sb1, sb2, sb3, sb4;
    public RadioButton rb1, rb2;
    public static String[] datas = new String[10];
    public static int datasLength = 0;
    public static Observable<String> observable;

    public MenuWindow(Context context) {
        this.mContext = context;
        maxMapWindow = new MaxMapWindow(getApplication(), mContext);
        maxMapInfoWindow = new MaxMapInfoWindow(mContext);
        miniMapWindow = new MiniMapWindow(mContext);

        initFloatWindow();
        startReadThread = new Thread() {
            @Override
            public void run() {
                GameData.execRootCmd(cmd);
            }
        };

        observable = Observable.create(
                (ObservableEmitter<String> emitter) -> {
                    while (isThreadRun) {
                        emitter.onNext(GameData.ReadTxtFile(dataFile));
                        Thread.sleep(2);
                    }
                }
        );
    }

    public static void updateView(String data) {
        try {
            ThisData td = GameData.parseEasyJson(data);
            if (null != td && null != td.getHl()) {
                if (9 == datasLength) {
                    datasLength = 0;
                }
                datas[datasLength] = data;
                maxMapWindow.setMap(td);
                maxMapInfoWindow.setMap(td);
                miniMapWindow.setMap(td);
                datasLength++;
            }
        } catch (Exception e) {
            System.out.println("Menu102异常");
        }
    }

    public void showFloatWindow() {
        if (null != menuView && menuView.getParent() == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.x = metrics.widthPixels / 2 - 350;
            mWindowParams.y = 0;
            mWindowParams.width = 800;
            mWindowParams.height = 500;
            menuView.setAlpha(0.89F);
            mWindowManager.addView(menuView, mWindowParams);
            if (null != tabHost) {
                tabHost.setup(new LocalActivityManager(this, false));
                LayoutInflater inflater = LayoutInflater.from(mContext);
                inflater.inflate(R.layout.tab1, tabHost.getTabContentView());
                inflater.inflate(R.layout.tab2, tabHost.getTabContentView());
                inflater.inflate(R.layout.tab3, tabHost.getTabContentView());
                tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("绘制").setContent(R.id.left));
                tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("触摸").setContent(R.id.right));
                tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("设置").setContent(R.id.set));
                TextAppearanceUtil.setTabWidgetTitle(tabHost.getTabWidget(), 16, "166,175,188");
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation bottomAnim = AnimationUtils.loadAnimation(mContext, R.anim.bottom_enter);
                        menuView.setAnimation(bottomAnim);
                        menuView.setVisibility(View.GONE);
                        FloatWindow.fwView.setVisibility(View.VISIBLE);
                    }
                });
                isOpen = false;
                updateTab(tabHost);
                tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                    @Override
                    public void onTabChanged(String tabId) {
                        updateTab(tabHost);
                    }
                });

                start_huizhi = tabHost.getTabContentView().findViewById(R.id.start_huizhi);
                start_all = tabHost.getTabContentView().findViewById(R.id.start_all);
                start_touxiang = tabHost.getTabContentView().findViewById(R.id.start_touxiang);
                start_fangkuang = tabHost.getTabContentView().findViewById(R.id.start_fangkuang);
                start_jineng_jishi = tabHost.getTabContentView().findViewById(R.id.start_jineng_jishi);
                start_yeguai_jishi = tabHost.getTabContentView().findViewById(R.id.start_yeguai_jishi);
                start_show_bingxian = tabHost.getTabContentView().findViewById(R.id.start_show_bingxian);
                end = tabHost.getTabContentView().findViewById(R.id.end);
                sb1 = tabHost.getTabContentView().findViewById(R.id.seekbar);
                sb2 = tabHost.getTabContentView().findViewById(R.id.seekbar2);
                sb3 = tabHost.getTabContentView().findViewById(R.id.seekbar3);
                sb4 = tabHost.getTabContentView().findViewById(R.id.seekbar4);
                rb1 = tabHost.getTabContentView().findViewById(R.id.rbBtn_0);
                rb2 = tabHost.getTabContentView().findViewById(R.id.rbBtn_1);
                sb3.setVisibility(View.GONE);
                sb4.setVisibility(View.GONE);
                sb1.setMax(300);
                sb2.setMax(300);
                sb3.setMax(300);
                sb4.setMax(300);
                rb1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sb1.setVisibility(View.VISIBLE);
                        sb2.setVisibility(View.VISIBLE);
                        sb3.setVisibility(View.GONE);
                        sb4.setVisibility(View.GONE);
                    }
                });
                rb2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sb1.setVisibility(View.GONE);
                        sb2.setVisibility(View.GONE);
                        sb3.setVisibility(View.VISIBLE);
                        sb4.setVisibility(View.VISIBLE);
                    }
                });
                sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        WindowManager.LayoutParams updataMinWM = miniMapWindow.mWindowParams;
                        WindowManager.LayoutParams updataMaxInfoWM = maxMapInfoWindow.mWindowParams;
                        WindowManager.LayoutParams updataFloatWM = FloatWindow.mWindowParams;
                        updataMinWM.x = seekBar.getProgress();
                        updataFloatWM.x = 600 + seekBar.getProgress();
                        updataMaxInfoWM.x = maxMapInfoWindow.getWindowX() + seekBar.getProgress();
                        try {
                            FloatWindow.mWindowManager.updateViewLayout(FloatWindow.fwView, updataFloatWM);
                            miniMapWindow.mWindowManager.updateViewLayout(miniMapWindow.miniMapView, updataMinWM);
                            maxMapInfoWindow.mWindowManager.updateViewLayout(maxMapInfoWindow.maxMapInfoView, updataMaxInfoWM);
                        } catch (Exception e) {
                            Toast.makeText(mContext, "开启绘制后再设置", Toast.LENGTH_LONG).show();
                            seekBar.setProgress(0);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        WindowManager.LayoutParams updataMinWM = miniMapWindow.mWindowParams;
                        WindowManager.LayoutParams updataMaxInfoWM = maxMapInfoWindow.mWindowParams;
                        updataMinWM.y = seekBar.getProgress();
                        updataMaxInfoWM.y = maxMapInfoWindow.getWindowY() + seekBar.getProgress();
                        try {
                            miniMapWindow.mWindowManager.updateViewLayout(miniMapWindow.miniMapView, updataMinWM);
                            maxMapInfoWindow.mWindowManager.updateViewLayout(maxMapInfoWindow.maxMapInfoView, updataMaxInfoWM);
                        } catch (Exception e) {
                            Toast.makeText(mContext, "开启绘制后再设置", Toast.LENGTH_LONG).show();
                            seekBar.setProgress(0);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        maxMapWindow.setX(seekBar.getProgress());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                sb4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        maxMapWindow.setY(seekBar.getProgress());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                start_huizhi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            start_touxiang.setChecked(true);
                            start_jineng_jishi.setChecked(true);
                            start_yeguai_jishi.setChecked(true);
                            start_fangkuang.setChecked(true);
                            menuView.setVisibility(View.GONE);
                            maxMapInfoWindow.getMiniMapView().setVisibility(View.VISIBLE);
                            FloatWindow.fwView.setVisibility(View.VISIBLE);

                            maxMapInfoWindow.showFloatWindow();
                            maxMapWindow.showFloatWindow();
                            miniMapWindow.showFloatWindow();

                            maxMapInfoWindow.getMiniMapView().setVisibility(View.VISIBLE);
                            maxMapWindow.getMaxMapView().setVisibility(View.VISIBLE);
                            miniMapWindow.getMiniMapView().setVisibility(View.VISIBLE);

                            isThreadRun = true;
                            try {
                                startReadThread.start();
                            } catch (Exception e) {
                                System.out.println("Menu:295=====程序异常");
                            }
                            observable.subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            (String s) -> updateView(s),
                                            Functions.<Throwable>emptyConsumer(),
                                            () -> System.out.println("线程完成")
                                    );
                        } else {
                            isThreadRun = false;
//                            startReadThread.interrupt();
//                            minBackHandler.getLooper().quit();
                            miniMapWindow.getMiniMapView().setVisibility(View.GONE);
                            maxMapInfoWindow.getMiniMapView().setVisibility(View.GONE);
                            maxMapWindow.getMaxMapView().setVisibility(View.GONE);
                            maxMapInfoWindow.getMiniMapView().setVisibility(View.GONE);
                        }
                    }
                });
                start_touxiang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            start_touxiang.setChecked(true);
                        } else {
                            start_touxiang.setChecked(false);
                        }
                        miniMapWindow.updateHeroView(isChecked);
                    }
                });
                start_show_bingxian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        start_show_bingxian.setChecked(false);
                        Toast.makeText(mContext, "没写", Toast.LENGTH_LONG).show();
                    }
                });
                start_jineng_jishi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            start_jineng_jishi.setChecked(true);
                            maxMapInfoWindow.getMiniMapView().setVisibility(View.VISIBLE);
                        } else {
                            start_jineng_jishi.setChecked(false);
                            maxMapInfoWindow.getMiniMapView().setVisibility(View.GONE);
                        }
                    }
                });
                start_fangkuang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            start_fangkuang.setChecked(true);
                        } else {
                            start_fangkuang.setChecked(false);
                        }
                        maxMapWindow.updateHeroView(isChecked);
                    }
                });
                start_yeguai_jishi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            start_yeguai_jishi.setChecked(true);
                        } else {
                            start_yeguai_jishi.setChecked(false);
                        }
                        miniMapWindow.updateBuffView(isChecked);
                    }
                });
                start_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            start_touxiang.setChecked(true);
                            start_fangkuang.setChecked(true);
                            start_jineng_jishi.setChecked(true);
                            start_yeguai_jishi.setChecked(true);
                            start_show_bingxian.setChecked(true);
                        } else {
                            start_touxiang.setChecked(false);
                            start_fangkuang.setChecked(false);
                            start_jineng_jishi.setChecked(false);
                            start_yeguai_jishi.setChecked(false);
                            start_show_bingxian.setChecked(false);
                        }
                    }
                });
                end.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            System.exit(0);
                        }
                    }
                });
            }
        }
    }

    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        menuView = inflater.inflate(R.layout.tab_title, null);
        menuView.setOnTouchListener(this);

        tabHost = menuView.findViewById(android.R.id.tabhost);
        tabHost.setOnTouchListener(this);


        close_btn = menuView.findViewById(R.id.close_btn);

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
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        showFloatWindow();
    }


    private void updateTab(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = tabHost.getTabWidget().getChildAt(i);
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            if (tabHost.getCurrentTab() == i) {//选中
                view.setBackground(mContext.getResources().getDrawable(R.mipmap.radio_group_pressed));
            } else {//不选中
                view.setBackgroundColor(Color.rgb(18, 35, 59));
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return false;
    }

    public TabHost getTabHost() {
        return tabHost;
    }

    public View getMenuView() {
        return menuView;
    }

    public static MiniMapWindow getMiniMapWindow() {
        return miniMapWindow;
    }

    public static MaxMapInfoWindow getMaxMapInfoWindow() {
        return maxMapInfoWindow;
    }

    public static MaxMapWindow getMaxMapWindow() {
        return maxMapWindow;
    }
}