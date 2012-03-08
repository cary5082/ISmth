package com.ismth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ismth.thread.SmthConnectionHandlerInstance;
import com.ismth.utils.ConnectionManager;
import com.ismth.utils.Constants;
import com.ismth.utils.SmthInstance;
import com.ismth.utils.SmthUtils;

/**
 * 回复帖子
 * @author wangjianfeia
 *
 */
public class ReplyArticleActivity extends Activity implements OnClickListener{

	String titleString;
	TextView title;
	EditText replyArticle;
	Button addReply;
	TextView fbz;
	//发送的URL
	String sendUrl;
	
	public Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case Constants.CONNECTIONSUCCESS:
				boolean result=(Boolean)msg.obj;
				if(result) {
					SmthUtils.showToast(ReplyArticleActivity.this.getApplicationContext(), "发文成功!");
				}else {
					SmthUtils.showToast(ReplyArticleActivity.this.getApplicationContext(), "发文失败!");
				}
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.reply_article);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		Intent intent=getIntent();
		if(intent!=null) {
			titleString=intent.getStringExtra(Constants.SENDTITLEKEY);
			sendUrl=intent.getStringExtra(Constants.SENDARTICLEURLKEY);
		}
		title=(TextView)findViewById(R.id.title);
		titleString=titleString;
        title.setText(titleString);
        replyArticle=(EditText)findViewById(R.id.reply_article);
        addReply=(Button)findViewById(R.id.add_reply_article);
        fbz=(TextView)findViewById(R.id.fbz);
        addReply.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		//点击发表回复的帖子
		case R.id.add_reply_article:
			addReply.setVisibility(View.GONE);
			replyArticle.setVisibility(View.GONE);
			fbz.setVisibility(View.VISIBLE);
			sendReplyToServer();
			break;
		}
	}
	
	/**
	 * 把回复的帖子发送给服务器
	 */
	public void sendReplyToServer(){
		Message msg=Message.obtain();
		Bundle data=new Bundle();
		data.putString(Constants.ARTICLECONTENTKEY, replyArticle.getText().toString());
		data.putString(Constants.GETURLKEY, sendUrl);
		data.putString(Constants.SENDTITLEKEY, titleString);
		msg.setData(data);
		msg.obj=handler;
		msg.what=Constants.SENDARTICLE;
		SmthConnectionHandlerInstance.getInstance().sendMessage(msg);
	}

}
