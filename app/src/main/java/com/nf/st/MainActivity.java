package com.nf.st;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.LayoutInflaterCompat;
import androidx.core.view.LayoutInflaterFactory;

import com.CrashHandler;
import com.DeviceInfoUtils;
import com.JedisUtil;
import com.LruCacheUtils;
import com.Xor;
import com.client.HttpURLConnectionUtil;
import com.event.GetEventTask;
import com.jni.JniDemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static String path = "/sdcard/ST", appVersionDB, fileVersionDB, fileVersion;
    public static String authUrl = "101.43.176.252";
    public static String fileUrl = "http://101.43.176.252:8091/stfile";
    public static String redisUrl = "101.43.176.252";
    public static final String ABC = "程序已开源自行寻找，无需破解。";
    public FloatWindow floatWindow;
    public static LruCacheUtils lruCacheUtils;
    public static String packName = "com.tencent.tmgp.sgame", uuid = null;
    public static Process p;
    public static int x, y, appVersion = 28;
    public LGvideoview lGvideoview;
    public static Button openGame, out_app, copy_uuid, gameVersion, authUrlBtn, fileUrlBtn, redisUrlBtn, setIp, clearIp;
    public SeekBar downloadFile;
    public LinearLayout linearLayoutBottom, open_game_linearLayout;
    public TextView downloadFileTextTitle;
    public TextView appList;
    public TextView downloadFileSizeText;
    public static TextView bz_or_uid;
    public static boolean isStart = false;
    public static String phoneInfo;
    public static Context mContext, cContext;
    public static Typeface typeface;
    public static ImageView tpis, edit_bz, jc, kefu, bug;
    static String password;

    private static String[] dropdiwnData = null;
    private Spinner version;
    private ArrayAdapter<String> adapter;
    private String versionTitle = "正式服";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Ex_GameFont.ttf");
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                AppCompatDelegate delegate = getDelegate();
                View view = delegate.createView(parent, name, context, attrs);

                if (view != null && (view instanceof TextView)) {
                    ((TextView) view).setTypeface(typeface);
                }
                if (view != null && (view instanceof Button)) {
                    ((TextView) view).setTypeface(typeface);
                }
                return view;
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        String ipconfig = readFileContent("/sdcard/ST/ipconfig");
        if (null == ipconfig || 10 >= ipconfig.length()) {
            String setData = "{\"auth\":\"" + authUrl + "\",\"file\":\"" + fileUrl + "\",\"redis\":\"" + redisUrl + "\"}";
            saveFile("ipconfig", setData);
        }
        try {
            JSONObject jsonObject = new JSONObject(ipconfig);
            authUrl = jsonObject.getString("auth");
            fileUrl = jsonObject.getString("file");
            redisUrl = jsonObject.getString("redis");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        openGame = findViewById(R.id.open_game);
        lGvideoview = findViewById(R.id.bg_index);
        downloadFile = findViewById(R.id.downloadFile);
        linearLayoutBottom = findViewById(R.id.linearLayoutBottom);
        open_game_linearLayout = findViewById(R.id.open_game_linearLayout);
        downloadFileTextTitle = this.findViewById(R.id.downloadFileTextTitle);
        downloadFileSizeText = this.findViewById(R.id.downloadFileSizeText);
        bz_or_uid = this.findViewById(R.id.bz_or_uid);
        edit_bz = this.findViewById(R.id.edit_bz);
        out_app = this.findViewById(R.id.out_app);
        copy_uuid = this.findViewById(R.id.copy_uuid);
        gameVersion = this.findViewById(R.id.gameVersion);
        authUrlBtn = this.findViewById(R.id.authUrlBtn);
        fileUrlBtn = this.findViewById(R.id.fileUrlBtn);
        redisUrlBtn = this.findViewById(R.id.redisUrlBtn);
        setIp = this.findViewById(R.id.setIp);
        clearIp = this.findViewById(R.id.clearIp);
        appList = this.findViewById(R.id.app_list);
        tpis = this.findViewById(R.id.tpis);
        kefu = this.findViewById(R.id.kefu);
        bug = this.findViewById(R.id.bug);
        jc = this.findViewById(R.id.jc);
        path = Environment.getExternalStorageDirectory().toString() + "/ST";
        cContext = getApplicationContext();
        initVideView();
        //初使化内存缓存
        lruCacheUtils = new LruCacheUtils(this);
        for (int i = 0; i < 1000; i++) {
            try {
                Field field = (Field) R.mipmap.class.getDeclaredField("_" + i);
                if (null != field) {
                    lruCacheUtils.savePicToMemory("_" + i, field);
                }
                field = (Field) R.mipmap.class.getDeclaredField("cd_" + i);
                if (null != field) {
                    lruCacheUtils.savePicToMemory("cd_" + i, field);
                }
            } catch (NoSuchFieldException e) {
                System.out.println("缓存英雄头像异常");
            }
        }
        for (int i = 80100; i < 80125; i++) {
            try {
                Field field = (Field) R.mipmap.class.getDeclaredField("_" + i);
                if (null != field) {
                    lruCacheUtils.savePicToMemory("_" + i, field);
                }
            } catch (NoSuchFieldException e) {
                System.out.println("缓存召唤技能异常");
            }
        }
        for (int i = 1; i < 19; i++) {
            try {
                Field field = (Field) R.mipmap.class.getDeclaredField("icon_" + i);
                if (null != field) {
                    lruCacheUtils.savePicToMemory("icon_" + i, field);
                }
            } catch (NoSuchFieldException e) {
                System.out.println("缓存图标异常");
            }
        }
        Field field = null;
        try {
            field = (Field) R.mipmap.class.getDeclaredField("bigcd0");
            if (null != field) {
                lruCacheUtils.savePicToMemory("bigcd0", field);
            }
            field = (Field) R.mipmap.class.getDeclaredField("bigcd1");
            if (null != field) {
                lruCacheUtils.savePicToMemory("bigcd1", field);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        dropdiwnData = new String[3];
        dropdiwnData[0] = "正式服";
        dropdiwnData[1] = "体验服";
        dropdiwnData[2] = "国际服";
        adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, dropdiwnData);
        adapter.setDropDownViewResource(R.layout.dropdown_stytle);
        version = findViewById(R.id.version);
        version.setAdapter(adapter);
        version.setOnItemSelectedListener(new SpinnerSelectedListener());
        version.setVisibility(View.VISIBLE);
        requestMyPermissions();
//        CrashHandler.getInstance().init(getApplicationContext());
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (!is_root()) {
            try {
                p = Runtime.getRuntime().exec("su");
            } catch (Exception e) {
                Toast.makeText(this, "获取ROOT权限失败", Toast.LENGTH_LONG).show();
            }
        }
        appList.setMovementMethod(ScrollingMovementMethod.getInstance());
        File file = new File("/sdcard/ST/fileVersion");
        if (!file.exists()) {
            saveFile("fileVersion", "1");
        }
        fileVersion = readFileContent("/sdcard/ST/fileVersion");
        authUrlBtn.setText(null == authUrl ? "获取验证地址异常" : authUrl);
        fileUrlBtn.setText(null == fileUrl ? "获取文件地址异常" : fileUrl);
        redisUrlBtn.setText(null == redisUrl ? "获取REDIS地址异常" : redisUrl);
        String game_version = null;
        try {
            appVersionDB = JedisUtil.getDate("appVersion");
            fileVersionDB = JedisUtil.getDate("fileVersion");
            game_version = JedisUtil.getDate("gameVersion");
        } catch (ExceptionInInitializerError e) {
            opentLayerUpdate("连接服务器失败", true, mContext, null);
            return;
        } catch (NoClassDefFoundError e) {
            opentLayerUpdate("连接服务器失败", true, mContext, null);
            return;
        }
        gameVersion.setText(null == game_version ? "获取版本异常" : game_version);
        File dir = new File("/sdcard/ST/脚本");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (checkPermission(this)) {
            String appUuid = readFileContent("/sdcard/uuid");
            if (null != appUuid && 10 < appUuid.length()) {
                uuid = appUuid;
            } else {
                uuid = getPhoneSign();
                saveFile2("/sdcard/uuid", uuid);
            }
            x = getDeviceWidth(this);
            y = getDeviceHeight(this);
            phoneInfo = "IP: " + DeviceInfoUtils.getLocalIPAddress() + " \n主板名: TEST";
            phoneInfo = "A" + Xor.encode(phoneInfo);
            boolean auth = false;
            try {
                auth = HttpURLConnectionUtil.auth();
            } catch (NoClassDefFoundError e) {
                opentLayerUpdate("连接服务器失败", true, this, null);
                return;
            }
            if (!auth) {
                opentLayerUpdate("验证失败\n请联系管理员", true, this, null);
                return;
            }
            floatWindow = new FloatWindow(this);
            if (auth && null != floatWindow) {
                try {
                    saveXYToFile();
                    saveFile("status", "false");
                    String gg = null;
                    if (!String.valueOf(appVersion).equals(appVersionDB)) {
                        String gxgg = JedisUtil.getDate("gxgg");
                        opentLayer(null == gxgg ? "辅助有新版本" : gxgg, false, this, null);
                    }
                    if (null != gg) {
                        gg += JedisUtil.getDate("gg");
                    } else {
                        gg = JedisUtil.getDate("gg");
                    }
                    appList.setText(null == gg ? "获取公告失败" : gg);
                } catch (Exception e) {
                    Toast.makeText(this, "系统异常!", Toast.LENGTH_LONG).show();
                    throw new RuntimeException("系统异常!");
                }
                List<String> homeList = JedisUtil.getList("home", 0, -1);
                boolean isE = false;
                for (String uuid : homeList) {
                    if (this.uuid.equals(uuid)) {
                        isE = true;
                        break;
                    }
                }
                if (!isE) {
                    JedisUtil.setList("home", uuid);
                }

                file = new File("/sdcard/ST/stfile");
                if (is_root() && !file.exists() || !fileVersion.equals(fileVersionDB)) {
                    new Thread(new Thread() {
                        @Override
                        public void run() {
                            downloadFile();
                        }
                    }).start();
                } else {
                    linearLayoutBottom.setVisibility(View.GONE);
                    isStart = true;
                }
                if (auth && isStart) {
                    floatWindow.showFloatWindow();
                    if (is_root()) {
                        outSt();
                    }
                    file = new File("/sdcard/ST/set");
                    if (!file.exists()) {
                        String setData = "{\"miniMapX\":0,\"miniMapY\":0,\"maxMapX\":0,\"maxMapY\":0,\"maxMapInfoX\":0,\"maxMapInfoY\":0,\"refreshMs\":10,\"refreshMs2\":50,\"entitySize\":250}";
                        saveFile("set", setData);
                    }
                    open_game_linearLayout.setVisibility(View.VISIBLE);
                    List<String> commands = new ArrayList<>();
                    for (int i = 0; i < 32; i++) {
                        commands.add("chmod 777 " + "/dev/input/event" + i);
                    }
                    commands.add("setenforce 0");
                    commands.add("rm -rf /sdcard/ST/status");
                    CommandExecution.execCommand(commands.toArray(new String[commands.size()]), true);
                }
            }
        } else {
            linearLayoutBottom.setVisibility(View.GONE);
            Toast.makeText(this, "请打开悬浮窗!", Toast.LENGTH_LONG).show();
            opentLayer("请前往系统设置授权悬浮窗权限后重新打开程序", true, this, null);
        }
        openGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openGame();
            }
        });
        out_app.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.exit(0);
            }
        });
        copy_uuid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(uuid);
                opentLayer("复制成功", false, mContext, null);
            }
        });
        tpis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                opentInputLayer(mContext, null);
            }
        });
        jc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                opentJCLayer(mContext);
            }
        });

        String bz = null;
        try {
            bz = HttpURLConnectionUtil.getBz(uuid);
        } catch (NoClassDefFoundError e) {
            opentLayerUpdate("连接服务器失败", true, this, null);
            return;
        }
        if (null == bz || 0 >= bz.length()) {
            bz_or_uid.setText(uuid);
        } else {
            bz_or_uid.setText(bz);
        }
        edit_bz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                opentInputLayer2(mContext, null);
            }
        });
        kefu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                opentLayerKefu("只有电报（相亲相爱一家人）\r\n https://t.me/eeneeneen", mContext);
            }
        });
        bug.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                opentLayer("尚未开发", false, mContext, null);
            }
        });
        setIp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                opentInputSetIPLayer(mContext);
            }
        });
        clearIp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeFile("ipconfig");
                opentLayer("重置成功，请重新进入", true, mContext, null);
            }
        });
        try {
            password = JedisUtil.getDate("password");
        } catch (NoClassDefFoundError e) {
            opentLayerUpdate("连接服务器失败", true, this, null);
            return;
        }
    }


    public static void opentLayer(String text, boolean isOutApp, Context context, Integer gravity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        View view = View.inflate(context, R.layout.layer, null);
        if (null != text) {
            TextView tv = view.findViewById(R.id.layer_content);
            tv.setText(text);
        }
        builder.setView(view);
        builder.setCancelable(false);
        Button btn_comfirm = view.findViewById(R.id.out_game);
        AlertDialog dialog = builder.create();
        if (null != gravity) {
            dialog.getWindow().setGravity(gravity);
        }
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isOutApp) {
                    System.exit(0);
                } else {
                    dialog.cancel();
                }
            }
        });
    }

    public static void opentLayerKefu(String text, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        View view = View.inflate(context, R.layout.layer_kefu, null);
        if (null != text) {
            TextView tv = view.findViewById(R.id.layer_content);
            tv.setText(text);
        }
        builder.setView(view);
        builder.setCancelable(false);
        Button add = view.findViewById(R.id.add);
        Button out = view.findViewById(R.id.out);
        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        out.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public static void opentLayerUpdate(String text, boolean isOutApp, Context context, Integer gravity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        View view = View.inflate(context, R.layout.layer_update, null);
        if (null != text) {
            TextView tv = view.findViewById(R.id.layer_content);
            tv.setText(text);
        }
        builder.setView(view);
        builder.setCancelable(false);
        Button btn_comfirm = view.findViewById(R.id.out_game);
        Button copy_uuid = view.findViewById(R.id.copy_uuid);
        Button setIP = view.findViewById(R.id.setIP);
        AlertDialog dialog = builder.create();
        if (null != gravity) {
            dialog.getWindow().setGravity(gravity);
        }
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isOutApp) {
                    System.exit(0);
                } else {
                    dialog.cancel();
                }
            }
        });
        copy_uuid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(uuid);
                System.exit(0);
            }
        });
        setIP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
                opentInputSetIPLayer(mContext);
            }
        });
    }

    public static void opentLayer(String text, boolean isOutApp, Context context, Integer gravity, boolean copyID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        View view = View.inflate(context, R.layout.layer, null);
        if (null != text) {
            TextView tv = view.findViewById(R.id.layer_content);
            tv.setText(text);
        }
        builder.setView(view);
        builder.setCancelable(false);
        Button btn_comfirm = view.findViewById(R.id.out_game);
        AlertDialog dialog = builder.create();
        if (null != gravity) {
            dialog.getWindow().setGravity(gravity);
        }
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (copyID) {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(uuid);
                }
                if (isOutApp) {
                    System.exit(0);
                } else {
                    dialog.cancel();
                }
            }
        });
    }

    public static void opentInputSetIPLayer(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        View view = View.inflate(context, R.layout.layer_input_set_ip, null);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.height = (int) (d.getHeight() * 0.8); // 改变的是dialog框在屏幕中的位置而不是大小
        params.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.3
        params.x = -55;
        params.y = -82;
        window.setAttributes(params);
        window.setContentView(R.layout.layer_input_set_ip);
        LinearLayout relayout = window.findViewById(R.id.setIp);
        Display display = window.getWindowManager().getDefaultDisplay();
        int minHeight = (int) (display.getHeight() * 0.16);              //使用这种方式更改了dialog的框高
        relayout.setMinimumHeight(minHeight);
        WindowManager.LayoutParams params2 = window.getAttributes();
        params.width = (int) (display.getWidth() * 0.7);                     //使用这种方式更改了dialog的框宽
        window.setAttributes(params2);
        Button yes = relayout.findViewById(R.id.yes);
        Button no = relayout.findViewById(R.id.no);
        EditText auth_ip_content = relayout.findViewById(R.id.auth_ip_content);
        EditText file_ip_content = relayout.findViewById(R.id.file_ip_content);
        EditText redis_ip_content = relayout.findViewById(R.id.redis_ip_content);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String auth = String.valueOf(auth_ip_content.getText());
                String file = String.valueOf(file_ip_content.getText());
                String redis = String.valueOf(redis_ip_content.getText());
                System.out.println(auth);
                System.out.println(file);
                System.out.println(redis);
                if (null == auth || "".equals(auth) || 0 == auth.length()) {
                    opentLayer("验证地址不能为空", false, mContext, null);
                    return;
                }
                if (null == file || "".equals(file) || 0 == file.length()) {
                    opentLayer("文件地址不能为空", false, mContext, null);
                    return;
                }
                if (null == redis || "".equals(redis) || 0 == redis.length()) {
                    opentLayer("中间件地址不能为空", false, mContext, null);
                    return;
                }
                if (null != auth && null != file && null != redis) {
                    String setData = "{\"auth\":\"" + auth + "\",\"file\":\"" + file + "\",\"redis\":\"" + redis + "\"}";
                    saveFile("ipconfig", setData);
                    opentLayer("设置成功，请重新打开应用", true, mContext, null);
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    public static void opentInputLayer(Context context, Integer gravity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        View view = View.inflate(context, R.layout.layer_input, null);
        builder.setView(view);
        builder.setCancelable(false);
        Button yes = view.findViewById(R.id.yes);
        Button no = view.findViewById(R.id.no);
        EditText tpis_content = view.findViewById(R.id.tpis_content);
        EditText tpis_password = view.findViewById(R.id.tpis_password);
        AlertDialog dialog = builder.create();
        if (null != gravity) {
            dialog.getWindow().setGravity(gravity);
        }
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != password && password.equals(String.valueOf(tpis_password.getText()))) {
                    JedisUtil.setDate("nf-tpis", String.valueOf(tpis_content.getText()));
                    dialog.cancel();
                } else {
                    opentLayer("密码错误", false, mContext, null);
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public static void opentJCLayer(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        View view = View.inflate(context, R.layout.layer_input3, null);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.height = (int) (d.getHeight() * 0.9); // 改变的是dialog框在屏幕中的位置而不是大小
        params.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
        window.setAttributes(params);
        window.setContentView(R.layout.layer_input3);
        LinearLayout relayout = window.findViewById(R.id.aaa);
        Display display = window.getWindowManager().getDefaultDisplay();
        int minHeight = (int) (display.getHeight() * 0.16);              //使用这种方式更改了dialog的框高
        relayout.setMinimumHeight(minHeight);
        WindowManager.LayoutParams params2 = window.getAttributes();
        params.width = (int) (display.getWidth() * 0.9);                     //使用这种方式更改了dialog的框宽
        window.setAttributes(params2);
        Button layer_input3_yes = relayout.findViewById(R.id.layer_input3_yes);
        TextView layer_input3_text = relayout.findViewById(R.id.layer_input3_text);
        String jc = null;
        try {
            jc = JedisUtil.getDate("jc");
        } catch (NoClassDefFoundError e) {
            opentLayerUpdate("连接服务器失败", true, mContext, null);
            return;
        }
        layer_input3_text.setText(null == jc ? "暂无教程" : jc);
        layer_input3_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        layer_input3_yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public static void opentInputLayer2(Context context, Integer gravity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentDialog);
        View view = View.inflate(context, R.layout.layer_input2, null);
        builder.setView(view);
        builder.setCancelable(false);
        Button yes = view.findViewById(R.id.bz_yes);
        Button no = view.findViewById(R.id.bz_no);
        EditText content = view.findViewById(R.id.bz_content);
        AlertDialog dialog = builder.create();
        if (null != gravity) {
            dialog.getWindow().setGravity(gravity);
        }
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        dialog.show();
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String bz = String.valueOf(content.getText());
                if (5 > bz.length() || 15 < bz.length()) {
                    opentLayer("名称太短或者太长", false, mContext, null);
                } else {
                    String returnData = null;
                    try {
                        returnData = HttpURLConnectionUtil.setBz(bz);
                    } catch (NoClassDefFoundError e) {
                        opentLayerUpdate("连接服务器失败", true, mContext, null);
                        return;
                    }
                    if ("OK".equals(returnData)) {
                        dialog.cancel();
                        bz_or_uid.setText(bz);
                    } else {
                        opentLayer(returnData, false, mContext, null);
                    }
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    @Override
    protected void onRestart() {
        //返回重新加载
        initVideView();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        //防止锁屏或者弹出的时候，音乐在播放
        lGvideoview.stopPlayback();
        super.onStop();
    }

    /**
     * 播放视频
     */
    private void initVideView() {
        //播放路径
        lGvideoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video));
        //播放
        lGvideoview.start();
        //循环播放
        lGvideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                lGvideoview.start();
            }
        });
    }

    /**
     * 操作二进制
     */
    public static void outSt() {
        CommandExecution.execCommand("rm /data/local/tmp/stfile", true);
        CommandExecution.execCommand("cp -f /sdcard/ST/stfile /data/local/tmp/stfile", true);
        CommandExecution.execCommand("chmod 777 /data/local/tmp/stfile", true);
        CommandExecution.execCommand("rm /sdcard/ST/kkData", true);
    }

    /**
     * 打开游戏
     */
    public void openGame() {
        PackageManager packageManager = getPackageManager();
        if (checkPackInfo(packName)) {
            Intent intent = packageManager.getLaunchIntentForPackage(packName);
            startActivity(intent);
            outSt();
        } else {
            Toast.makeText(this, "打开游戏失败!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 读取文本内容
     *
     * @param fileName
     * @return
     */
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
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

    /**
     * 写入文件
     *
     * @return
     */
    public static boolean saveFile(String fileName, String fileValue) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + "/" + fileName);
            fos.write(fileValue.getBytes());
            fos.flush();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("目录错误，找不到文件");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return false;
    }

    /**
     * 向文件追加内容
     *
     * @param content  写入的内容
     * @param fileName 文件
     */
    public static void writeFile(String content, String fileName) {
        File file = new File(path + "/" + fileName);
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            if (!file.exists()) {
                boolean hasFile = file.createNewFile();
                fos = new FileOutputStream(file);
            } else {
                fos = new FileOutputStream(file, true);
            }
            osw = new OutputStreamWriter(fos, "utf-8");
            osw.write(content);
            osw.write("\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (osw != null) {
                    osw.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean saveFile2(String filePath, String fileValue) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(fileValue.getBytes());
            fos.flush();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("目录错误，找不到文件");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return false;
    }

    /**
     * 删除文件
     * @param filePath
     * @return
     */
    public static boolean removeFile(String filePath) {
        File file = new File(path + "/" + filePath);
        return file.delete();
    }

    /**
     * 屏幕分辨率写入文件
     *
     * @return
     */
    public static boolean saveXYToFile() {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + "/xy");
            String info = x + "," + y;
            fos.write(info.getBytes());
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取设备宽度（px）
     */
    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     */
    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public void copyFile(String filename) {
        InputStream in = null;
        FileOutputStream out = null;
        String path = "/sdcard/ST/" + filename;
        File file = new File(path);
        if (!file.exists()) {
            try {
                in = getAssets().open(filename); // 从assets目录下复制
                out = new FileOutputStream(file);
                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
//            GameData.execRootCmd("mkdir " + path);
            CommandExecution.execCommand("mkdir " + path, true);
        }
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "找不到游戏", Toast.LENGTH_LONG).show();
        }
        return packageInfo != null;
    }


    /**
     * 是否存在su命令，并且有执行权限
     *
     * @return 存在su命令，并且有执行权限返回true
     */
    public static boolean is_root() {
        File file = null;
        String[] paths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/", "/su/bin/"};
        try {
            for (String path : paths) {
                file = new File(path + "su");
                if (file.exists() && file.canExecute()) {
                    return true;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return false;
    }

    /**
     * 检查悬浮窗授权
     *
     * @param activity
     * @return
     */
    public boolean checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(activity)) {
            Toast.makeText(activity, "当前无权限，请授权", Toast.LENGTH_SHORT).show();
            activity.startActivityForResult(
                    new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName())), 0);
            return false;
        }
        return true;
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data,
                                 OnWindowPermissionListener onWindowPermissionListener) {
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(activity)) {
                Toast.makeText(activity.getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
                if (onWindowPermissionListener != null)
                    onWindowPermissionListener.onFailure();
            } else {
                Toast.makeText(activity.getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
                if (onWindowPermissionListener != null)
                    onWindowPermissionListener.onSuccess();
            }
        }
    }


    public interface OnWindowPermissionListener {
        void onSuccess();

        void onFailure();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != floatWindow) {
            floatWindow.hideFloatWindow();
        }
    }

    //获取手机的唯一标识
    public String getPhoneSign() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            getUUID();
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            deviceId.append("id").append(getUUID());
        }
        return deviceId.toString();
    }

    /**
     * 得到全局唯一UUID
     */
    public String getUUID() {
        SharedPreferences mShare = getSharedPreferences("uuid", MODE_PRIVATE);
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).commit();
        }
        return uuid;
    }

    private void requestMyPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {

        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {

        }
    }

    public void downloadFile() {
        try {
            downloadFileTextTitle.setText("等待下载文件");
            Thread.sleep(2000);
            //下载函数
//            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            String filename = "stfile";
            //获取文件名
            URL myURL = new URL(fileUrl);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            File file1 = new File(path);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            //把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(path + "/" + filename);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            String fileSizeText = "正在更新(" + (fileSize / 1024) + "KB)";
            if (1024 <= fileSize / 1024) {
                fileSizeText = "正在更新(" + (fileSize / 1024 / 1024) + "MB)";
            }
            downloadFileTextTitle.setText(fileSizeText);
            do {
                //循环读取
                int numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                //更新进度条
                downloadFile.setProgress((downLoadFileSize * 100 / fileSize));
                downloadFileSizeText.setText((downLoadFileSize * 100 / fileSize) + " %");
            } while (true);
            is.close();
            Thread.sleep(1000);
            Looper.prepare();
//            downloadFileTextTitle.setText("更新完成");
//            linearLayoutBottom.setVisibility(View.GONE);
            saveFile("fileVersion", fileVersionDB);
//            isStart = true;
            opentLayer(null, true, this, null);
            Looper.loop();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "更新失败", Toast.LENGTH_LONG).show();
        }
    }

    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //点击回调
            versionTitle = dropdiwnData[arg2];
            if ("正式服" != versionTitle) {
                opentLayer("当前只支持正式服", false, mContext, null);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}