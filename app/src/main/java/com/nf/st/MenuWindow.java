package com.nf.st;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.ColorBar;
import com.JedisUtil;
import com.client.HttpURLConnectionUtil;
import com.jni.JniDemo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.hutool.json.JSONUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

public class MenuWindow extends Activity implements View.OnTouchListener, ListItemAdapter.InnerItemOnclickListener, AdapterView.OnItemClickListener {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String cmd = "./data/local/tmp/stfile";
    public static String dataFile = "/storage/emulated/0/ST/kkData";
    public static String setDataFile = "/storage/emulated/0/ST/set";
    public static Context mContext;
    public static MiniMapWindow miniMapWindow;
    public static MaxMapInfoWindow maxMapInfoWindow;
    public static MaxMapWindow maxMapWindow;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    public View menuView;
    public TabHost tabHost;
    public static Thread startReadThread, startReadThread2, sharkDataThread;
    public static boolean isOpen = true, isThreadRun = false, isThreadCmdRun = false,
            isThreadSocket = false, isOneSharkThread = true, isOpenChuMo = false;
    public CheckBox start_huizhi, start_all, start_touxiang, start_fangkuang, start_jineng_jishi,
            start_yeguai_jishi, start_show_bingxian, open_tpis, update_yeguai_rd, start_shark;
    public SeekBar sb1, sb1_2, sb2, sb2_2, sb3, sb4, refresh_rate, refresh_rate2, seekbar_icon, update_jishi_x, update_jishi_y;
    public RadioButton rb1, rb2, rb3;
    public LinearLayout setLayout1, setLayout1_2, setLayout2, setLayout2_2, setLayout3, soldie, update_jishi_linearLayout;
    public static TextView ip;
    public static int datasLength = 0, refreshMs = 10, refreshMs2 = 50, iconSize = 0;
    public static Observable<String> observable;
    public static Observable<String> observableTpis;
    public static List<String> dataList = new ArrayList<>();
    public static int hideDataCount = 0;
    public ImageView qr_code;
    private GridView gridView;
    public static ThisData td;
    public Button end, outLink, refresh_home, clearSet, reyx_h, add_h;
    public static boolean isRoot = false, isShark = false;
    public ImageView close_btn;
    public TextView linkName;
    private List<Map<String, Object>> lists = new ArrayList<>();
    public HashMap<String, H> hHashMap = new HashMap<>();
    public HashMap<String, HWindow> hWindowHashMap = new HashMap<>();
    private List<String> lists_h = new ArrayList<>();
    private SimpleAdapter adapter;
    private ListItemAdapter adapter_h;
    private ListView listView, listView_h;
    public static String homeId = null, setData = null;
    public TpisWindow tpisWindow;
    public IconJNWindow iconJNWindow;
    public ColorBar minMapColorBar, maxMapColorBar;
    public Map<String, String> userMap = new HashMap<>();
    public static String djInfo = null, djFileName = MainActivity.uuid + "djInfo", uploadFileName = null;
    public static List<String> uploadServerData = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MenuWindow(Context context) {
        isRoot = MainActivity.is_root();
        this.mContext = context;
        maxMapWindow = new MaxMapWindow(getApplication(), mContext);
        maxMapInfoWindow = new MaxMapInfoWindow(mContext);
        miniMapWindow = new MiniMapWindow(mContext);
        if (null == iconJNWindow) {
            iconJNWindow = new IconJNWindow(mContext);
        }
        tpisWindow = new TpisWindow(mContext, "");
        startReadThread = new Thread() {
            @Override
            public void run() {
                CommandExecution.execCmd(cmd);
            }
        };
        startReadThread2 = new Thread() {
            @Override
            public void run() {
                CommandExecution.CommandResult cr = CommandExecution.execCommand("ps -ef | grep \"stfile\" | grep -v grep | awk '{print $2}'", true);
                CommandExecution.execCommand("renice -n -10 -p " + cr.successMsg, true);
                cr = CommandExecution.execCommand("ps -ef | grep \"com.qy\" | grep -v grep | awk '{print $2}'", true);
                CommandExecution.execCommand("renice -n -10 -p " + cr.successMsg, true);
            }
        };
        sharkDataThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (isShark) {
                            JedisUtil.setDate(MainActivity.uuid, GameData.ReadTxtFile(dataFile));
                        }
                        Thread.sleep(refreshMs);
                    } catch (InterruptedException e) {
                        System.out.println("共享异常");
                    }
                }
            }
        };
        observable = Observable.create(
                (ObservableEmitter<String> emitter) -> {
                    while (isThreadRun) {
                        try {
                            String data = null;
                            if (null != homeId && 5 < JedisUtil.getKeySize(homeId)) {
                                data = JedisUtil.getDate(homeId);
                            } else {
                                data = GameData.ReadTxtFile(dataFile);
                            }
                            emitter.onNext(data);
                            Thread.sleep(refreshMs);
                        } catch (InterruptedException e) {
                            System.out.println("绘制延迟异常");
                        }
                    }

                }
        );
        if (isRoot) {
            observableTpis = Observable.create(
                    (ObservableEmitter<String> emitter) -> {
                        while (true) {
                            try {
                                if (0 < JedisUtil.getKeySize("nf-tpis")) {
                                    String tpis = JedisUtil.getDate("nf-tpis");
                                    if (null != emitter && null != tpis && !"".equals(tpis)) {
                                        emitter.onNext(tpis);
                                    }
                                }
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                //获取redis公告异常
                            }
                        }

                    }
            );
            observableTpis.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            (String s) -> createTpis(s),
                            Functions.<Throwable>emptyConsumer(),
                            () -> System.out.println("线程完成")
                    );
        }
        initFloatWindow();
    }

    public void createTpis(String tpis) {
        try {
            if (null != tpis && !"".equals(tpis)) {
                tpisWindow.setText(tpis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSetData() {
        setData = "{\"miniMapX\":" + sb1.getProgress() + ",\"miniMapY\":" + sb2.getProgress()
                + ",\"maxMapX\":" + sb3.getProgress() + ",\"maxMapY\":" + sb4.getProgress()
                + ",\"maxMapInfoX\":" + update_jishi_x.getProgress() + ",\"maxMapInfoY\":" + update_jishi_y.getProgress()
                + ",\"refreshMs\":" + refresh_rate.getProgress()
                + ",\"refreshMs2\":" + refresh_rate2.getProgress()
                + ",\"entitySize\":" + sb2_2.getProgress()
                + "}";
        MainActivity.saveFile("set", setData);
    }

    public static void updateView(String data) {
        try {
            if (null != data && !"".equals(data) && 100 < data.length()) {
                ThisData td = GameData.parseEasyJson(data);
                if (null != td && null != td.getHl()) {
                    int count = 0;
                    for (String d : dataList) {
                        if (data.equals(d)) {
                            count++;
                        }
                    }
                    if (refreshMs2 - 1 > count) {
                        if (refreshMs2 <= dataList.size()) {
                            dataList.clear();
                        }
                        dataList.add(data);
                        uploadServerData.add(data);
                        if (isRoot) {
                            maxMapWindow.setMap(td);
                        }
                        maxMapInfoWindow.setMap(td);
                        miniMapWindow.setMap(td);
                        showWindow();
                        if (isRoot) {
                            if (null == djInfo) {
                                String title = MainActivity.uuid + simpleDateFormat.format(new Date());
                                String heroInfo = "敌方英雄：";
                                List<Hero> heroes = td.getHl();
                                for (Hero hero : heroes) {
                                    heroInfo += HeroName.getNameById(hero.getHeroId()) + ",";
                                }
                                djInfo = MainActivity.uuid + "\r\n" + title + "\r\n" + heroInfo + "\r\n" + new Date();
                                MainActivity.saveFile(djFileName, djInfo);
                            }
                            if (null == uploadFileName) {
                                uploadFileName = "uploadFile" + simpleDateFormat.format(new Date());
                            }
                            if (6000 <= uploadServerData.size()) {
                                MainActivity.writeFile(uploadFileName, JSONUtil.toJsonStr(uploadServerData));
                                uploadServerData.clear();
                            }
                        }
                    } else {
                        hideWindow();
                    }
                } else {
                    if (100 < hideDataCount) {
                        hideWindow();
                        hideDataCount = 0;
                    }
                    hideDataCount++;
                }
            } else if ("对局加载中".equals(data)) {
                if (100 < hideDataCount) {
                    hideWindow();
                    hideDataCount = 0;
                    if (isRoot) {
                        djInfo = null;
                        uploadFileName = null;
                        uploadServerData.clear();
                        String content = "" + new Date();
                        MainActivity.writeFile(djFileName, content);
                    }
                }
                hideDataCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideWindow() {
        miniMapWindow.getMiniMapView().setVisibility(View.INVISIBLE);
        maxMapInfoWindow.getMiniMapView().setVisibility(View.INVISIBLE);
        maxMapWindow.getMaxMapView().setVisibility(View.INVISIBLE);
    }

    public static void showWindow() {
        miniMapWindow.getMiniMapView().setVisibility(View.VISIBLE);
        maxMapInfoWindow.getMiniMapView().setVisibility(View.VISIBLE);
        if (isRoot) {
            maxMapWindow.getMaxMapView().setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showFloatWindow() {
        if (null != menuView && menuView.getParent() == null) {
            int x = MainActivity.x;
            if (x >= 1600) {
                x = 1600;
            }
            mWindowParams.x = (MainActivity.x - 1600) / 2;
            mWindowParams.y = (MainActivity.y - 750) / 2;
            mWindowParams.width = x;
            mWindowParams.height = 800;
            menuView.setAlpha(0.95F);
            mWindowManager.addView(menuView, mWindowParams);
            if (null != tabHost) {
                tabHost.setup(new LocalActivityManager(this, false));
                LayoutInflater inflater = LayoutInflater.from(mContext);
                inflater.inflate(R.layout.tab1, tabHost.getTabContentView());
                inflater.inflate(R.layout.tab2, tabHost.getTabContentView());
                inflater.inflate(R.layout.tab3, tabHost.getTabContentView());
                inflater.inflate(R.layout.icon_select, tabHost.getTabContentView());
                inflater.inflate(R.layout.tab5, tabHost.getTabContentView());
                inflater.inflate(R.layout.tab6, tabHost.getTabContentView());
                tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("地图绘制").setContent(R.id.left));
                tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("共享房间").setContent(R.id.right));
                tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("绘图设置").setContent(R.id.set));
                tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("悬浮图标").setContent(R.id.icon_select));
                tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator("自定义宏").setContent(R.id.tab5));
                tabHost.addTab(tabHost.newTabSpec("tab6").setIndicator("内存功能").setContent(R.id.tab6));
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
                end.getLayoutParams().width = tabHost.getTabWidget().getChildAt(0).getLayoutParams().width - 35;
                linkName = tabHost.getTabContentView().findViewById(R.id.link_name);
                reyx_h = tabHost.getTabContentView().findViewById(R.id.reyx_h);
                add_h = tabHost.getTabContentView().findViewById(R.id.add_h);
                outLink = tabHost.getTabContentView().findViewById(R.id.out_link);
                refresh_home = tabHost.getTabContentView().findViewById(R.id.refresh_home);
                start_huizhi = tabHost.getTabContentView().findViewById(R.id.start_huizhi);
                start_all = tabHost.getTabContentView().findViewById(R.id.start_all);
                start_touxiang = tabHost.getTabContentView().findViewById(R.id.start_touxiang);
                start_fangkuang = tabHost.getTabContentView().findViewById(R.id.start_fangkuang);
                start_jineng_jishi = tabHost.getTabContentView().findViewById(R.id.start_jineng_jishi);
                start_yeguai_jishi = tabHost.getTabContentView().findViewById(R.id.start_yeguai_jishi);
                start_show_bingxian = tabHost.getTabContentView().findViewById(R.id.start_show_bingxian);
                sb1 = tabHost.getTabContentView().findViewById(R.id.seekbar);
                sb1_2 = tabHost.getTabContentView().findViewById(R.id.seekbar1_2);
                sb2 = tabHost.getTabContentView().findViewById(R.id.seekbar2);
                sb2_2 = tabHost.getTabContentView().findViewById(R.id.seekbar2_2);
                sb3 = tabHost.getTabContentView().findViewById(R.id.seekbar3);
                sb4 = tabHost.getTabContentView().findViewById(R.id.seekbar4);
                update_jishi_x = tabHost.getTabContentView().findViewById(R.id.update_jishi_x);
                update_jishi_y = tabHost.getTabContentView().findViewById(R.id.update_jishi_y);
                refresh_rate = tabHost.getTabContentView().findViewById(R.id.refresh_rate);
                refresh_rate2 = tabHost.getTabContentView().findViewById(R.id.refresh_rate2);
                seekbar_icon = tabHost.getTabContentView().findViewById(R.id.seekbar_icon);
                rb1 = tabHost.getTabContentView().findViewById(R.id.rbBtn_0);
                rb2 = tabHost.getTabContentView().findViewById(R.id.rbBtn_1);
                rb3 = tabHost.getTabContentView().findViewById(R.id.rbBtn_2);
                setLayout1 = tabHost.getTabContentView().findViewById(R.id.set_layout1);
                setLayout1_2 = tabHost.getTabContentView().findViewById(R.id.set_layout1_2);
                setLayout2 = tabHost.getTabContentView().findViewById(R.id.set_layout2);
                setLayout2_2 = tabHost.getTabContentView().findViewById(R.id.set_layout2_2);
                setLayout3 = tabHost.getTabContentView().findViewById(R.id.set_layout3);
                update_jishi_linearLayout = tabHost.getTabContentView().findViewById(R.id.update_jishi_linearLayout);
                clearSet = tabHost.getTabContentView().findViewById(R.id.clearSet);
                soldie = tabHost.getTabContentView().findViewById(R.id.soldie);
                open_tpis = tabHost.getTabContentView().findViewById(R.id.open_tpis);
                update_yeguai_rd = tabHost.getTabContentView().findViewById(R.id.update_yeguai_rd);
                minMapColorBar = tabHost.getTabContentView().findViewById(R.id.minMapColorBar);
                maxMapColorBar = tabHost.getTabContentView().findViewById(R.id.maxMapColorBar);
                start_shark = tabHost.getTabContentView().findViewById(R.id.start_shark);
                sb3.setVisibility(View.GONE);
                sb4.setVisibility(View.GONE);
                sb1.setMax(300);
                sb2.setMax(300);
                sb3.setMax(300);
                sb4.setMax(300);
                refresh_rate.setMin(5);
                refresh_rate2.setMin(5);
                setData = GameData.ReadTxtFile(setDataFile);
                rb1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLayout1.setVisibility(View.VISIBLE);
                        setLayout2.setVisibility(View.VISIBLE);
                        update_jishi_linearLayout.setVisibility(View.VISIBLE);
                        setLayout1_2.setVisibility(View.VISIBLE);
                        sb1.setVisibility(View.VISIBLE);
                        sb1.setVisibility(View.VISIBLE);
                        sb2.setVisibility(View.VISIBLE);
                        sb3.setVisibility(View.GONE);
                        sb4.setVisibility(View.GONE);
                        setLayout3.setVisibility(View.GONE);
                        setLayout2_2.setVisibility(View.GONE);
                        sb1_2.setVisibility(View.VISIBLE);
                        sb2_2.setVisibility(View.GONE);
                    }
                });
                rb2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLayout1.setVisibility(View.VISIBLE);
                        setLayout2.setVisibility(View.VISIBLE);
                        sb1.setVisibility(View.GONE);
                        sb2.setVisibility(View.GONE);
                        sb3.setVisibility(View.VISIBLE);
                        sb4.setVisibility(View.VISIBLE);
                        setLayout3.setVisibility(View.GONE);
                        setLayout1_2.setVisibility(View.GONE);
                        setLayout2_2.setVisibility(View.VISIBLE);
                        sb1_2.setVisibility(View.GONE);
                        sb2_2.setVisibility(View.VISIBLE);
                        update_jishi_linearLayout.setVisibility(View.GONE);
                    }
                });
                rb3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLayout3.setVisibility(View.VISIBLE);
                        setLayout1.setVisibility(View.GONE);
                        setLayout2.setVisibility(View.GONE);
                        setLayout1_2.setVisibility(View.GONE);
                        setLayout2_2.setVisibility(View.GONE);
                        update_jishi_linearLayout.setVisibility(View.GONE);
                    }
                });
                sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (!setWindowXY(seekBar.getProgress(), null)) {
                            seekBar.setProgress(0);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
                    }
                });
                sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (!setWindowXY(null, seekBar.getProgress())) {
                            seekBar.setProgress(0);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
                    }
                });
                sb1_2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        miniMapWindow.setViewSize(seekBar.getProgress());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
                    }
                });
                sb2_2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        maxMapWindow.setEntitySize(seekBar.getProgress());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
                    }
                });
                update_jishi_x.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        setMaxMapInfoWindowXY(seekBar.getProgress(), null);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
                    }
                });
                update_jishi_y.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        setMaxMapInfoWindowXY(null, seekBar.getProgress());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
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
                        updateSetData();
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
                        updateSetData();
                    }
                });
                refresh_rate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        refreshMs = seekBar.getProgress();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
                    }
                });
                refresh_rate2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        refreshMs2 = seekBar.getProgress();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
                    }
                });
                seekbar_icon.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        iconSize = seekBar.getProgress();
                        iconJNWindow.setIconSize(iconSize);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        updateSetData();
                    }
                });
                start_huizhi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (isRoot) {
                                try {
                                    if (!isThreadCmdRun) {
                                        isThreadCmdRun = true;
                                        startReadThread.start();
                                        startReadThread2.start();
                                        JedisUtil.setList("home", MainActivity.uuid);
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                start_fangkuang.setChecked(true);
                            } else if (!isRoot && null == homeId) {
                                MainActivity.opentLayer("选择房间后再试", false, mContext, null);
                                start_huizhi.setChecked(false);
                                return;
                            }
                            start_touxiang.setChecked(true);
                            start_jineng_jishi.setChecked(true);
                            start_yeguai_jishi.setChecked(true);
                            start_show_bingxian.setChecked(true);
                            menuView.setVisibility(View.GONE);
                            FloatWindow.fwView.setVisibility(View.VISIBLE);
                            maxMapInfoWindow.showFloatWindow();
                            miniMapWindow.showFloatWindow();
                            maxMapWindow.showFloatWindow();
                            isThreadRun = true;
                            observable.subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            (String s) -> updateView(s),
                                            Functions.<Throwable>emptyConsumer(),
                                            () -> System.out.println("线程完成")
                                    );
                            if (!isThreadSocket && isRoot) {
                                menuView.setVisibility(View.GONE);
                                FloatWindow.fwView.setVisibility(View.VISIBLE);
                                maxMapInfoWindow.showFloatWindow();
                                miniMapWindow.showFloatWindow();
                                if (isOneSharkThread) {
                                    sharkDataThread.start();
                                    isOneSharkThread = false;
                                }
                                maxMapWindow.showFloatWindow();
                                miniMapWindow.setX(MainActivity.y);
                            }
                            MainActivity.saveFile("status", "true");
                            try {
                                Thread.sleep(1000);
                                if (null != setData && !"".equals(setData) && "{".equals(setData.substring(0, 1))) {
                                    JSONObject jsonObject = new JSONObject(setData);
                                    if (!setWindowXY(Integer.valueOf(jsonObject.getString("miniMapX")), Integer.valueOf(jsonObject.getString("miniMapY")))) {
                                        throw new RuntimeException("解析设置数据异常");
                                    }
                                } else {
                                    throw new RuntimeException("解析设置数据异常");
                                }
                            } catch (Exception e) {

                            }
                        } else {
                            hideWindow();
                            isThreadRun = false;
                            isThreadSocket = false;
//                            MainActivity.saveFile("status", "false");
                            CommandExecution.execCommand("rm -rf /sdcard/ST/status", true);
                        }
                        UpdStartAll();
                        setSet();
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
                        UpdStartAll();
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
                        UpdStartAll();
                    }
                });
                start_fangkuang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        Toast.makeText(mContext, "方框暂时关闭", Toast.LENGTH_LONG).show();
//                        start_fangkuang.setChecked(false);
                        if (isChecked) {
                            start_fangkuang.setChecked(true);
                        } else {
                            start_fangkuang.setChecked(false);
                        }
                        UpdStartAll();
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
                        UpdStartAll();
                    }
                });
                start_show_bingxian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Toast.makeText(mContext, "兵线暂时关闭", Toast.LENGTH_LONG).show();
                        start_show_bingxian.setChecked(false);
//                        if (isChecked) {
//                            start_show_bingxian.setChecked(true);
//                        } else {
//                            start_show_bingxian.setChecked(false);
//                        }
//                        miniMapWindow.updateSoldieView(isChecked);
//                        UpdStartAll();
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
                start_shark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isRoot) {
                            isShark = true;
                        } else {
                            MainActivity.opentLayer("您不是ROOT玩家", false, mContext, null);
                            start_shark.setChecked(false);
                            isShark = false;
                        }
                    }
                });
                open_tpis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            maxMapInfoWindow.openTpis = true;
                        } else {
                            maxMapInfoWindow.openTpis = false;
                        }
                    }
                });
                update_yeguai_rd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            miniMapWindow.update_yeguai_rd = true;
                        } else {
                            miniMapWindow.update_yeguai_rd = false;
                        }
                    }
                });
                end.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        System.exit(0);
                    }
                });
                outLink.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        outLink.setVisibility(View.GONE);
                        linkName.setText("未连接");
                        homeId = null;
                        start_huizhi.setChecked(false);
                    }
                });
                clearSet.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setData = "{\"miniMapX\":0,\"miniMapY\":0,\"maxMapX\":0,\"maxMapY\":0,\"maxMapInfoX\":0,\"maxMapInfoY\":0,\"refreshMs\":5,\"refreshMs2\":50,\"entitySize\":250}";
                        setSet();
                        MainActivity.saveFile("set", setData);
                    }
                });

                userMap = HttpURLConnectionUtil.getUserInfo();
                List<String> homeList = null;
                HashMap<String, String> data_h = null;
                try {
                    homeList = JedisUtil.getList("home", 0, 50);
                } catch (Exception e) {
                    System.exit(0);
                }
                if (null != homeList) {
                    for (String uuid : homeList) {
                        System.out.println(MainActivity.uuid + "==" + uuid);
                        if (!MainActivity.uuid.equals(uuid)) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("uuid", uuid);
                            map.put("bz", userMap.get(uuid));
                            lists.add(map);
                        }
                    }
                }
                adapter = new SimpleAdapter(tabHost.getContext(),
                        lists, R.layout.item, new String[]{"uuid", "bz"},
                        new int[]{R.id.text2, R.id.text1});
                listView = tabHost.getTabContentView().findViewById(R.id.listview);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (isRoot) {
                            MainActivity.opentLayer("这是给没有ROOT的伙伴准备的", false, mContext, null);
                            homeId = null;
                            return;
                        }
                        Map<String, Object> str = lists.get(i);
                        link(str.get("uuid").toString());
                    }
                });
                refresh_home.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        lists.removeAll(lists);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                        List<String> homeList = JedisUtil.getList("home", 0, -1);
                        if (null != homeList && 0 < homeList.size()) {
                            for (String uuid : homeList) {
                                if (!MainActivity.uuid.equals(uuid)) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("uuid", uuid);
                                    map.put("bz", userMap.get(uuid));
                                    lists.add(map);
                                }
                            }
                        }
                    }
                });
                getHData();
                adapter_h = new ListItemAdapter(lists_h, tabHost.getContext());
                adapter_h.setOnInnerItemOnClickListener(this);
                listView_h = tabHost.getTabContentView().findViewById(R.id.listview_h);
                listView_h.setAdapter(adapter_h);
                gridView = tabHost.getTabContentView().findViewById(R.id.icon_select_gridView);
                List<Map<String, Object>> item = getData();
                SimpleAdapter simpleAdapter = new SimpleAdapter(tabHost.getContext(), item,
                        R.layout.icon_select_grid_view_img,
                        new String[]{"itemImage"},
                        new int[]{R.id.icon_select_img});
                gridView.setAdapter(simpleAdapter);
                gridView.setAdapter(simpleAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (0 == position) {
                            iconJNWindow.hideFloatWindow();
                        } else {
                            iconJNWindow.showFloatWindow(MainActivity.lruCacheUtils.getPicFromMemory("icon_" + position));
                        }
                    }
                });

                reyx_h.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Button button = (Button) v;
                        if (" 调整按钮 ".equals(button.getText())) {
                            HWindow.isUpdateXY = true;
                            button.setBackgroundResource(R.mipmap.btn3);
                            button.setText(" 确 定 ");
                        } else if (" 确 定 ".equals(button.getText())) {
                            HWindow.isUpdateXY = false;
                            button.setBackgroundResource(R.mipmap.btn4);
                            button.setText(" 调整按钮 ");
                        }
                    }
                });
                add_h.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                    }
                });
                minMapColorBar.setHeight(100);
            }
        }
    }

    public void setMaxMapInfoWindowXY(Integer x, Integer y) {
        WindowManager.LayoutParams updataMaxInfoWM = maxMapInfoWindow.mWindowParams;
        if (null != x) {
            updataMaxInfoWM.x = maxMapInfoWindow.getWindowX() + x;
        }
        if (null != y) {
            updataMaxInfoWM.y = maxMapInfoWindow.getWindowY() + y;
        }
        try {
            maxMapInfoWindow.mWindowManager.updateViewLayout(maxMapInfoWindow.maxMapInfoView, updataMaxInfoWM);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("设置失败");
        }
    }

    public boolean setWindowXY(Integer x, Integer y) {
        WindowManager.LayoutParams updataMinWM = miniMapWindow.mWindowParams;
        WindowManager.LayoutParams updataMaxInfoWM = maxMapInfoWindow.mWindowParams;
        WindowManager.LayoutParams updataFloatWM = FloatWindow.mWindowParams;
        if (null != x) {
            updataMinWM.x = x;
            updataFloatWM.x = 600 + x;
//            updataMaxInfoWM.x = maxMapInfoWindow.getWindowX() + x;
        }
        if (null != y) {
            updataMinWM.y = y;
//            updataMaxInfoWM.y = maxMapInfoWindow.getWindowY() + y;
        }
        try {
            FloatWindow.mWindowManager.updateViewLayout(FloatWindow.fwView, updataFloatWM);
            miniMapWindow.mWindowManager.updateViewLayout(miniMapWindow.miniMapView, updataMinWM);
//            maxMapInfoWindow.mWindowManager.updateViewLayout(maxMapInfoWindow.maxMapInfoView, updataMaxInfoWM);
            return true;
        } catch (Exception e) {
            System.out.println("设置失败");
        }
        return false;
    }

    public void setSet() {
        try {
            if (null != setData && !"".equals(setData) && "{".equals(setData.substring(0, 1))) {
                JSONObject jsonObject = new JSONObject(setData);
                int miniMapX = Integer.valueOf(jsonObject.getString("miniMapX"));
                int miniMapY = Integer.valueOf(jsonObject.getString("miniMapY"));
                int maxMapX = Integer.valueOf(jsonObject.getString("maxMapX"));
                int maxMapY = Integer.valueOf(jsonObject.getString("maxMapY"));
                int refreshMs = Integer.valueOf(jsonObject.getString("refreshMs"));
                int refreshMs2 = Integer.valueOf(jsonObject.getString("refreshMs2"));
                int entitySize = Integer.valueOf(jsonObject.getString("entitySize"));
                int maxMapInfoX = Integer.valueOf(jsonObject.getString("maxMapInfoX"));
                int maxMapInfoY = Integer.valueOf(jsonObject.getString("maxMapInfoY"));
                sb1.setProgress(miniMapX);
                sb2.setProgress(miniMapY);
                sb3.setProgress(maxMapX);
                sb4.setProgress(maxMapY);
                refresh_rate.setProgress(refreshMs);
                refresh_rate2.setProgress(refreshMs2);
                sb2_2.setProgress(entitySize);
                update_jishi_x.setProgress(maxMapInfoX);
                update_jishi_y.setProgress(maxMapInfoY);
                maxMapWindow.setX(maxMapX);
                maxMapWindow.setY(maxMapY);
                maxMapWindow.setEntitySize(entitySize);
                setMaxMapInfoWindowXY(maxMapInfoX, maxMapInfoY);
            } else {
//                throw new RuntimeException("解析设置数据异常");
            }
        } catch (JSONException e) {
//            throw new RuntimeException("解析设置数据异常");
        }
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<Integer> intImg = new ArrayList<>();
        try {
            intImg.add(MainActivity.lruCacheUtils.getPicFromMemory("_" + 888).getInt(R.mipmap.class));
            for (int i = 0; i < 1500; i++) {
                Field field = MainActivity.lruCacheUtils.getPicFromMemory("icon_" + i);
                if (null != field) {
                    intImg.add(field.getInt(R.mipmap.class));
                }
            }
            for (Integer id : intImg) {
                Map<String, Object> item = new HashMap<>();
                item.put("itemImage", id);
                items.add(item);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("加载图标异常");
        }
        return items;

    }

    public void UpdStartAll() {
//        if (start_touxiang.isChecked() && start_fangkuang.isChecked()
//                && start_jineng_jishi.isChecked() && start_yeguai_jishi.isChecked()) {
//            start_all.setChecked(true);
//        } else {
//            start_all.setChecked(false);
//        }
    }

    public void link(String id) {
        try {
            homeId = null;
            String data = JedisUtil.getDate(id);
            if (null == data || "".equals(data) || 5 > data.length() || "null".equals(data) || "Main".equals(data)) {
                linkName.setText("当前房间没开放");
                outLink.setVisibility(View.GONE);
                return;
            }
            homeId = id;
            linkName.setText("连接房间：" + homeId);
            outLink.setVisibility(View.VISIBLE);
            start_huizhi.setChecked(true);
        } catch (Exception e) {
            Toast.makeText(mContext, "进入房间失败", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null)
            return;
        menuView = inflater.inflate(R.layout.tab_title, null);
        menuView.setOnTouchListener(this);
        tabHost = menuView.findViewById(android.R.id.tabhost);
        tabHost.setOnTouchListener(this);
        close_btn = menuView.findViewById(R.id.close_btn);
        end = menuView.findViewById(R.id.end);
//        lGvideoview = menuView.findViewById(R.id.bg_video);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SECURE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mWindowParams.windowAnimations = R.style.popupAnimation;
        showFloatWindow();
    }

    private void updateTab(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = tabHost.getTabWidget().getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.height = 120;
            params.width = 300;
            view.setLayoutParams(params);
            if (tabHost.getCurrentTab() == i) {//选中
                view.setBackground(mContext.getResources().getDrawable(R.mipmap.menu_title_left));
            } else {//不选中
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return false;
    }

    public static void updateIP(String IP) {
        ip.setText(IP);
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

    public static String readFileContent(File file) {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }


    public void getHData() {
        List<File> fileData = getAllFile("/sdcard/ST/脚本/");
        if (null != fileData) {
            for (File file : fileData) {
                lists_h.add(file.getName());
                H h = GameData.parseHJson(readFileContent(file));
                hHashMap.put(file.getName(), h);
            }
        }
    }

    public List<File> getAllFile(String dirFilePath) {
        if (null == dirFilePath || "".equals(dirFilePath) || 0 == dirFilePath.length())
            return null;
        return getAllFile(new File(dirFilePath));
    }

    public List<File> getAllFile(File dirFile) {
        if (Objects.isNull(dirFile) || !dirFile.exists() || dirFile.isFile())
            return null;

        File[] childrenFiles = dirFile.listFiles();
        if (Objects.isNull(childrenFiles) || childrenFiles.length == 0)
            return null;

        List<File> files = new ArrayList<>();
        for (File childFile : childrenFiles) {

            // 如果时文件，直接添加到结果集合
            if (childFile.isFile()) {
                files.add(childFile);
            } else {
                // 如果是文件夹，则将其内部文件添加进结果集合
                List<File> cFiles = getAllFile(childFile);
                if (Objects.isNull(cFiles) || cFiles.isEmpty()) continue;
                files.addAll(cFiles);
            }

        }

        return files;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("整体item----->", position + "");
    }

    @Override
    public void itemClick(View view) {
        try {
            int position = (Integer) view.getTag();
            String title = lists_h.get(position);
            HWindow hWindow = hWindowHashMap.get(title);
            Button button = (Button) view;
            switch (view.getId()) {
                case R.id.rename_h:
                    Log.e("点击了重命名", title + "");
                    break;
                case R.id.delete_h:
                    lists_h.remove(title);
                    break;
                case R.id.start_h:
                    if (" 启 用 ".equals(button.getText()) && null == hWindow) {
                        hWindowHashMap.put(title, new HWindow(mContext, hHashMap.get(title)));
                        button.setBackgroundResource(R.mipmap.btn4);
                        button.setText(" 停 用 ");
                    } else if (" 停 用 ".equals(button.getText()) && null != hWindow) {
                        hWindow.deleteWindow();
                        hWindowHashMap.remove(title);
                        button.setBackgroundResource(R.mipmap.btn3);
                        button.setText(" 启 用 ");
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("点击脚本菜单列表出现了异常");
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}