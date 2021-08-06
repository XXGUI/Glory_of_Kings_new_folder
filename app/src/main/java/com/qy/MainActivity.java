package com.qy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public FloatWindow floatWindow;
    private static final int REQUEST_CODE = 1024;
    public static String packName = "com.tencent.tmgp.sgame";
    public static Process p;
    public static String mainActivityText = "软件更新只在TG“嗯嗯”发布，其他地方获取到的都是搬运 \n"
            + "当前程序存在Android版本兼容性问题：没效果就是没兼容。\n"
            + "建议使用Android9系统，不支持框架。 \n"
            + "注意：安卓10、11如果没效果请检查存储是否生成了ST文件夹，如果没有请手动创建文件夹“ST”把群内“stupidtencent”文件手动放入“ST”文件内 \n"
            + "功能：只有绘制 \n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (!is_root()) {
            try {
                p = Runtime.getRuntime().exec("su");
            } catch (Exception e) {
                Toast.makeText(this, "获取ROOT权限时出错!", Toast.LENGTH_LONG).show();
            }
        }
        TextView appList = this.findViewById(R.id.app_list);
        if (checkPermission(this)) {
            mainActivityText += "\n嗯嗯YYDS";
            mainActivityText += "\n729：尝试修复兼容Android7、11；\n新增悬浮图标横向跟随小地图偏移,偏移最大值300";
            mainActivityText += "\n802：修复兼容Android7安装时解析包错误；\n新增英雄召唤师技能计时（橘黄色）";

            floatWindow = new FloatWindow(getApplicationContext(), p);
            if (null != floatWindow) {
                PackageManager packageManager = getPackageManager();
                if (checkPackInfo(packName)) {
                    Intent intent = packageManager.getLaunchIntentForPackage(packName);
                    try {
                        startActivity(intent);
                        File file = new File("/data/local/tmp/stupidtencent");
                        if (!file.exists()) {
                            isExist("/sdcard/ST/");
                            copyFile("stupidtencent");
                            GameData.execRootCmd("rm /data/local/tmp/stupidtencent");
                            GameData.execRootCmd("cp -f /storage/emulated/0/ST/stupidtencent /data/local/tmp/stupidtencent");
                            GameData.execRootCmd("chmod 777 /data/local/tmp/stupidtencent");
                        }
                        GameData.execRootCmd("rm /sdcard/ST/stupidtencent");
                        GameData.execRootCmd("rm /data/data/com.qy/kkData");
                        floatWindow.showFloatWindow();
                        appList.setText(mainActivityText);
                    } catch (Exception e) {
                        Toast.makeText(this, "系统异常!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "打开应用失败!", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(this, "请打开悬浮窗!", Toast.LENGTH_LONG).show();
            appList.setText("请前往系统设置授权悬浮窗权限后重新打开程序");
        }
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
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            GameData.execRootCmd("mkdir " + path);
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
            e.printStackTrace();
        }
        return packageInfo != null;
    }


    /**
     * 校验是否有ROOT权限
     *
     * @return
     */
    public static boolean is_root() {
        boolean res = false;
        try {
            if ((!new File("/system/bin/su").exists()) &&
                    (!new File("/system/xbin/su").exists())) {
                res = false;
            } else {
                res = true;
            }
        } catch (Exception e) {

        }
        return res;
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

    public void onActivityResult(Activity activity,
                                 int requestCode,
                                 int resultCode,
                                 Intent data,
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

}