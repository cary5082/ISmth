package com.ismth.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *@Time:2012-2-8
 *@Author:wangjianfei
 *@Version:
 */
public class SharePreferencesUtils {

	private static SharedPreferences pref;
	
	/**
     * 设置上下文，并且初始化文件操作对象
     * @param context
     */
    public static void setContext(Context context) {
        pref = context.getSharedPreferences("ismth", 0); // �ļ���������
    }
	
    /**
	 * 从共享文件中拿数据
	 * @param key 数据标识名
	 * @param defValue 取不到数据时用这个值代替
	 * @return
	 */
	public static String getString(String key, String defValue) {
        return pref.getString(key, defValue);
    }
	
	/**
	 * 把数据存到共享文件中
	 * @param key :数据标识名
	 * @param defValue 默认值
	 */
	public static void setString(String key, String defValue) {
        pref.edit().putString(key, defValue).commit();
    }
	
	
}
