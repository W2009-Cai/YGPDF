package com.yg.ad.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 对SharedPreference的封装
 * 在包名目录下创建一个shared_pres目录，并维护一个config.xml文件
 * 所有数据的读取和存入都是对这个文件的操作
 * Created by Administrator on 2017/6/15.
 */

public class PDFSPUtils {
    private static SharedPreferences sp = null;

    /**
     * 将一个boolean值存入sp文件中
     *
     * @param key   存储节点名称
     * @param value 存储节点的值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        //如果sp为空，则获取创建一个sp对象
        if (sp == null) {
            sp = context.getSharedPreferences("ad_config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();//获取sp编辑器,放入bool值，并提交

    }

    /**
     * 根据key读取一个boolean值value，没有的话使用defvalue代替
     *
     * @param key
     * @param defvalue
     */
    public static boolean getBoolean(Context context, String key, boolean defvalue) {
        //如果sp为空，则获取创建一个sp对象
        if (sp == null) {
            sp = context.getSharedPreferences("ad_config", Context.MODE_PRIVATE);
        }
        boolean b = sp.getBoolean(key, defvalue);
        return b;

    }

    /**
     * 从sp中根据key取出Long值
     *
     * @param key
     * @param defValue
     * @return
     */
    public static Long getLong(Context context, String key, long defValue) {
        if (sp == null) {//如果sp文件不存在，则创建该文件
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        long i = sp.getLong(key, defValue);
        return i;
    }

    /**
     * 将一个long值存入sp文件中
     *
     * @param key
     * @param value
     */
    public static void putLong(Context context, String key, long value) {
        if (sp == null) {//如果sp文件不存在，则创建该文件
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putLong(key, value).apply();
    }


}