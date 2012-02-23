package com.ismth.activity;

import java.net.HttpURLConnection;

import android.app.IntentService;
import android.content.Intent;

import com.ismth.utils.ConnectionManagerInstance;
import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SharePreferencesUtils;
import com.ismth.utils.SmthUtils;

/**
 * 登录线程
 *@Time:2012-2-10
 *@Author:wangjianfei
 *@Version:
 */
public class LoginIntentService extends IntentService{

	@Override
	public void onCreate() {
		super.onCreate();
		String userName=SharePreferencesUtils.getString(Constants.USERNAME, "");
		String password=SharePreferencesUtils.getString(Constants.PASSWORD, "");
		if(!"".equals(userName) && !"".equals(password)) {
			String result=null;
			HttpURLConnection conn=ConnectionManagerInstance.getInstance().connectionServer(Constants.LOGINURL, "POST");
			if(conn!=null) {
				result=SmthUtils.getStringForHttp(conn, false, null);
			}
			if(result!=null && result.length()>0) {
				ISmthLog.d(Constants.TAG, "登录成功。");
			}else {
				ISmthLog.d(Constants.TAG, "登录失败。");
			}
		}
	}

	public LoginIntentService() {
		super("");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		
	}

}
