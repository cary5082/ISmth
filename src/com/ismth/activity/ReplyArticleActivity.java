package com.ismth.activity;

import com.ismth.utils.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * 回复帖子
 * @author wangjianfeia
 *
 */
public class ReplyArticleActivity extends Activity{

	String titleString;
	TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.reply_article);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		Intent intent=getIntent();
		if(intent!=null) {
			titleString=intent.getStringExtra(Constants.TITLEBAR);
		}
		title=(TextView)findViewById(R.id.title);
        title.setText("RE:"+titleString);
	}

}
