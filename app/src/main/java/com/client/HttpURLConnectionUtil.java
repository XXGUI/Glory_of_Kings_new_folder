package com.client;

import com.JedisUtil;
import com.Xor;
import com.nf.st.MainActivity;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

public class HttpURLConnectionUtil {

    public static String redisStatus = JedisUtil.getDate("status");
    public static String redisCode = JedisUtil.getDate("code");
    public static String timeBetween = JedisUtil.getDate("time");
    public static Long time = System.currentTimeMillis();
    public static String data = Xor.newAutoGenericCode2(0, "QY-Servic", time + "", "192.168.1.111", 1, 1, 3, 0, 2, 0, 1).replaceAll(" ", "");
    //    public static String httpUrl = "http://192.168.1.5:8090?type=5&uuid=" + MainActivity.uuid + "&hexadecimal=" + data + "&data=" + MainActivity.phoneInfo;
    public static String httpUrl = "http://" + MainActivity.authUrl + ":8090?type=5&uuid=" + MainActivity.uuid + "&hexadecimal=" + data + "&data=" + MainActivity.phoneInfo;

    public static boolean auth() {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
                data = String.valueOf(result).replaceAll(" ", "").replaceAll("\"", "");
                if (136 == data.length() && null != redisCode && null != redisStatus && null != timeBetween) {
                    Long time2 = Long.valueOf(Xor.decode(data.substring(36, 62)));
                    String code = Xor.decode(data.substring(62, 80));
                    String status = data.substring(116, 118);
                    if ("26574D5338".equals(data.substring(0, 10))
                            && "0D0A".equals(data.substring(data.length() - 4, data.length()))
                            && redisCode.equals(code) && redisStatus.equals(status)
                            && (time - time2) < Long.valueOf(timeBetween)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != connection) {
                connection.disconnect();
            }
        }
        return false;
    }

    public static String setBz(String bz) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            httpUrl = "http://" + MainActivity.authUrl + ":8090?type=8&uuid=" + MainActivity.uuid + "&bz=" + bz;
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
                return String.valueOf(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != connection) {
                connection.disconnect();
            }
        }
        return "修改失败";
    }

    public static String getBz(String uuid) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            httpUrl = "http://" + MainActivity.authUrl + ":8090?type=9&uuid=" + MainActivity.uuid;
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
                return String.valueOf(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != connection) {
                connection.disconnect();
            }
        }
        return null;
    }

    public static Map<String, String> getUserInfo() {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        Map<String, String> data = new HashMap<>();
        try {
            httpUrl = "http://" + MainActivity.authUrl + ":8090?type=1";
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
                JSONArray jsonArray = new JSONArray(result);
                for (Object o : jsonArray) {
                    JSONObject jsonObject = new JSONObject(o);
                    String uuid = String.valueOf(jsonObject.get("uuid"));
                    String bz = String.valueOf(jsonObject.get("bz"));
                    data.put(uuid, null == bz || "".equals(bz) || 5 >= bz.length() ? uuid : bz);
                }
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != connection) {
                connection.disconnect();
            }
        }
        return null;
    }

    public static void uploadDjData(String uuid, String title, String data) {
        new Thread(new Thread() {
            @Override
            public void run() {
                String authHost = "http://" + MainActivity.authUrl + ":8090?type=8&uuid=" + uuid + "&data=" + data;
                try {
                    URL url = new URL(authHost);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 设置请求方式
                    connection.setRequestMethod("POST");
                    // 设置是否向HttpURLConnection输出
                    connection.setDoOutput(true);
                    // 设置是否从httpUrlConnection读入
                    connection.setDoInput(true);
                    // 设置是否使用缓存
                    connection.setUseCaches(false);
                    //设置参数类型是json格式
                    connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    connection.connect();
                    String body = "{uuid:" + uuid + ",title:" + title + ",data:" + data + "}";
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(body);
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}
