package com.ismth.thread;

import java.net.HttpURLConnection;

import com.ismth.utils.ConnectionManagerInstance;
import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SharePreferencesUtils;
import com.ismth.utils.SmthUtils;

import android.content.Context;
import android.os.AsyncTask;

public class LoginAsyncTask extends AsyncTask<String,Integer,String>{

	private Context context;
	
	public void setContext(Context context) {
		this.context=context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String result=null;
		String userName=SharePreferencesUtils.getString(Constants.USERNAME, "");
		String password=SharePreferencesUtils.getString(Constants.PASSWORD, "");
		if(!"".equals(userName) && !"".equals(password)) {
			HttpURLConnection conn=ConnectionManagerInstance.getInstance().connectionServer(Constants.LOGINURL, "POST",null,null);
			if(conn!=null) {
				result=SmthUtils.getStringForHttp(conn, true, "gb2312");
			}else {		//用空和NULL来区别是否执行登录操作。
				result="";
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		//如果result为null说明没有执行登录方法，所以不要显示TOAST成功或者失败
		if(result!=null) {
			if(result.indexOf("frames.html")>-1) {
				SmthUtils.showToast(context, "登录成功!");
			}else {
				SmthUtils.showToast(context, "登录失败!");
			}
		}
		context=null;
	}
}
