package com;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import java.lang.reflect.Field;

public class LruCacheUtils {
    //申明内存缓存
    private LruCache<String, Field> mLruCache;


    //在构造方法中进行初使化
    public LruCacheUtils(Context context) {
        //得到当前应用程序的内存
        int maxMemory=(int)Runtime.getRuntime().maxMemory();
        //内存缓存为当前应用程序的8分之1
        int cacheMemory=maxMemory/8;


        //进行初使化
        mLruCache=new LruCache<String, Field>(cacheMemory);
    }


    /**
     * 保存图片到内存缓存
     * @param key 图片的url
     * @param Field 图片
     */
    public void savePicToMemory(String key,Field Field){
        try {
            mLruCache.put(key,Field);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 通过key值得到缓存的图片
     * @param key 图片的url地址
     * @return Field 或 null
     */
    public  Field getPicFromMemory(String key){


        Field Field=null;
        try {
            //通过key获取图片
            Field= mLruCache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return Field;
    }
 /**
     * 清除缓存
     * evict:驱逐 逐出
     */
    public void clearCache() {
        if (mLruCache != null) {
            if (mLruCache.size() > 0) {
                Log.d("CacheUtils", "mMemoryCache.size() " + mLruCache.size());
                mLruCache.evictAll();


                Log.d("CacheUtils", "mMemoryCache.size()" + mLruCache.size());
            }
            mLruCache = null;
        }
    }



}

