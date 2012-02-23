package com.ismth.utils;

import android.util.Log;

/**
 * 日志打印类
 *@Time:2012-2-8
 *@Author:wangjianfei
 *@Version:
 */
public class ISmthLog {

	//是否打印日志
	public static boolean isDebug = true;
	private ISmthLog(){};
	
	public static void v(String tag,String msg){
		if(isDebug){
			Log.v(tag, msg);
		}
	}
	public static void i(String tag,String msg){
		if(isDebug){
			Log.i(tag, msg);
		}
	}
	public static void d(String tag,String msg){
		if(isDebug){
			Log.d(tag, msg);
		}
	}
	public static void w(String tag,String msg){
		if(isDebug){
			Log.w(tag, msg);
		}
	}
}
