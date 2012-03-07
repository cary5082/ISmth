package com.ismth.activity;

import java.net.HttpURLConnection;

import com.ismth.utils.ConnectionManager;
import com.ismth.utils.Constants;
import com.ismth.utils.HtmlParser;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SmthUtils;

import android.app.IntentService;
import android.content.Intent;

public class LogoutIntentService extends IntentService{

	public LogoutIntentService(){
		super("");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		HtmlParser.logoutSmth(Constants.MOBILELOGOUTURL);
	}

}
