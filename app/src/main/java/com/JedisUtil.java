package com;

import com.nf.st.MainActivity;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

public class JedisUtil {
    private static String ip = MainActivity.redisUrl;
    //    private static String ip = "101.33.206.28";
    private static int prot = 6379;
    private static String password = "123456";
    //    private static String password = "zhouyehao";
    static Jedis jedis = null;

    static {
        jedis = new Jedis(ip, prot, 10000);
        jedis.auth(password);
    }

    public static void setDate(String key, String data) {
        try {
            synchronized (JedisUtil.class) {
                SetParams setParams = new SetParams();
                setParams.ex(5);
                jedis.set(key, data, setParams);
            }
        } catch (Exception e) {
            System.out.println("setDate设置值异常");
        }
    }

    public static String getDate(String key) {
        String val = null;
        try {
            synchronized (JedisUtil.class) {
                val = jedis.get(key);
            }
        } catch (Exception e) {
            System.out.println("getDate请求数据异常");
        }
        return val;
    }

    /**
     * 获取指定范围的记录
     * lrange 下标从0开始 -1表示最后一个元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static List<String> getList(String key, long start, long end) {
        List<String> data = null;
        try {
            data = jedis.lrange(key, start, end);
        } catch (Exception e) {
            System.out.println("getDate获取集合错误");
        }
        return data;
    }

    /**
     * List尾部追加记录
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)
     * 如果list不存在，一个空列表会被创建并执行 RPUSH 操作
     *
     * @param key
     * @param value
     * @return
     */
    public static long setList(String key, String... value) {
        long resultStatus = 0;
        try {
            resultStatus = jedis.rpush(key, value);
        } catch (Exception e) {
            System.out.println("setList追加集合异常");
        }
        return resultStatus;
    }

    public static long getCount() {
        return jedis.dbSize();
    }

    public static Long getKeySize(String key) {
//        return jedis.memoryUsage(key);
        return jedis.strlen(key);
    }


    public static Map<String, String> getHash(String key) {
        Map<String, String> data = null;
        try {
            data = jedis.hgetAll(key);
        } catch (Exception e) {
            System.out.println("getDate获取集合错误");
        }
        return data;
    }
}
