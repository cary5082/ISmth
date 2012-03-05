package com.ismth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SmthInstance;

public class WebViewActivity extends Activity{

	WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		webView=(WebView)findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setVerticalScrollBarEnabled(false);
		webView.setWebViewClient(new MyWebViewClient());
		Intent intent=getIntent();
		String url=intent.getStringExtra(Constants.SEARCHNAMEKEY);
//		url=Constants.SEARCHBOARDURL.replaceAll("@name", url);
		url=Constants.SEARCHBOARDURL+url;
//		CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(getApplicationContext());
//		CookieManager cookieManager = CookieManager.getInstance();
//		cookieManager.setAcceptCookie(true);
//		cookieManager.setCookie("www.newsmth.net",SmthInstance.getCookieValue());
//		cookieSyncManager.sync();
		ISmthLog.d(Constants.TAG, "url=="+url);
		webView.loadUrl(url);
	}

	
	private class MyWebViewClient extends WebViewClient{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
