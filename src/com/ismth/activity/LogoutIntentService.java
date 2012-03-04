package com.ismth.activity;

import java.net.HttpURLConnection;

import com.ismth.utils.ConnectionManagerInstance;
import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;

import android.app.IntentService;
import android.content.Intent;

public class LogoutIntentService extends IntentService{

	public LogoutIntentService(){
		super("");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		ISmthLog.d(Constants.TAG, "logout====");
		HttpURLConnection conn=ConnectionManagerInstance.getInstance().connectionServer(Constants.LOGOUTURL, "GET",null,null);
		if(conn!=null) {
			conn.disconnect();
		}
	}

}
