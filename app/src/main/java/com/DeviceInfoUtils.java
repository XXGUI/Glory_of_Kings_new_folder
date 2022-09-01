package com;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

public class DeviceInfoUtils {
 
    /**
     * 获取设备宽度（px）
     * 
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
 
    /**
     * 获取设备的唯一标识， 需要 “android.permission.READ_Phone_STATE”权限
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (deviceId == null) {
            return "UnKnown";
        } else {
            return deviceId;
        }
    }
 
    /**
     * 获取厂商名
     * **/
    public static String getDeviceManufacturer() {
        return android.os.Build.MANUFACTURER;
    }
 
    /**
     * 获取产品名
     * **/
    public static String getDeviceProduct() {
        return android.os.Build.PRODUCT;
    }
 
    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
 
    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }
 
    /**
     * 获取手机主板名
     */
    public static String getDeviceBoard() {
        return android.os.Build.BOARD;
    }
 
    /**
     * 设备名
     * **/
    public static String getDeviceDevice() {
        return android.os.Build.DEVICE;
    }
 
    /**
     * 
     * 
     * fingerprit 信息
     * **/
    public static String getDeviceFubgerprint() {
        return android.os.Build.FINGERPRINT;
    }
 
    /**
     * 硬件名
     * 
     * **/
    public static String getDeviceHardware() {
        return android.os.Build.HARDWARE;
    }
 
    /**
     * 主机
     * 
     * **/
    public static String getDeviceHost() {
        return android.os.Build.HOST;
    }
 
    /**
     * 
     * 显示ID
     * **/
    public static String getDeviceDisplay() {
        return android.os.Build.DISPLAY;
    }
 
    /**
     * ID
     * 
     * **/
    public static String getDeviceId() {
        return android.os.Build.ID;
    }
 
    /**
     * 获取手机用户名
     * 
     * **/
    public static String getDeviceUser() {
        return android.os.Build.USER;
    }
 
    /**
     * 获取手机 硬件序列号
     * **/
    public static String getDeviceSerial() {
        return android.os.Build.SERIAL;
    }
 
    /**
     * 获取手机Android 系统SDK
     * 
     * @return
     */
    public static int getDeviceSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }
 
    /**
     * 获取手机Android 版本
     * 
     * @return
     */
    public static String getDeviceAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
 
    /**
     * 获取当前手机系统语言。
     */
    public static String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }
 
    /**
     * 获取当前系统上的语言列表(Locale列表)
     */
    public static String getDeviceSupportLanguage() {
        Log.e("wangjie", "Local:" + Locale.GERMAN);
        Log.e("wangjie", "Local:" + Locale.ENGLISH);
        Log.e("wangjie", "Local:" + Locale.US);
        Log.e("wangjie", "Local:" + Locale.CHINESE);
        Log.e("wangjie", "Local:" + Locale.TAIWAN);
        Log.e("wangjie", "Local:" + Locale.FRANCE);
        Log.e("wangjie", "Local:" + Locale.FRENCH);
        Log.e("wangjie", "Local:" + Locale.GERMANY);
        Log.e("wangjie", "Local:" + Locale.ITALIAN);
        Log.e("wangjie", "Local:" + Locale.JAPAN);
        Log.e("wangjie", "Local:" + Locale.JAPANESE);
        return Locale.getAvailableLocales().toString();
    }


    /**
     * 获取ip
     * @return
     */
    public static String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("BaseScanTvDeviceClient", "获取本机IP false =" +  ex.toString());
        }
        return null;
    }
 
    public static String getDeviceAllInfo(Context context) {
        return "IMEI: " + getIMEI(context)
                + "\n宽高度: " + getDeviceWidth(context)+"*"+getDeviceHeight(context)
                + "\n否有内置SD卡: " + SDCardUtils.isSDCardMount()
                + "\n内存信息: " + SDCardUtils.getRAMInfo(context)
                + "\n存储信息: " + SDCardUtils.getStorageInfo(context, 0)
                + "\nSD卡信息: " + SDCardUtils.getStorageInfo(context, 1)
                + "\n系统语言: " + getDeviceDefaultLanguage()
                + "\n硬件序列号: " + android.os.Build.SERIAL
                + "\n手机型号: " + android.os.Build.MODEL
                + "\n生产厂商: " + android.os.Build.MANUFACTURER
                + "\nAndroid版本: " + android.os.Build.VERSION.RELEASE
                + "\nAndroid SDK版本: " + android.os.Build.VERSION.SDK_INT
                + "\n安全patch时间: " + android.os.Build.VERSION.SECURITY_PATCH
                + "\n版本类型: " + android.os.Build.TYPE
                + "\n用户名: " + android.os.Build.USER
                + "\n产品名: " + android.os.Build.PRODUCT
                + "\nID: " + android.os.Build.ID
                + "\n硬件名: " + android.os.Build.HARDWARE
                + "\n产品名: " + android.os.Build.DEVICE
                + "\nIP: " + getLocalIPAddress()
                + "\n主板名: " + android.os.Build.BOARD;
    }
}
 