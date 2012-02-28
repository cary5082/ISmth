package com.ismth.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ismth.thread.SmthConnectionHandlerInstance;
import com.ismth.utils.Constants;
import com.ismth.utils.SmthUtils;

/**
 * 查看回贴
 *2012-2-25 上午11:32:26
 *author:cary
 */
public class ListReplyActivity extends Activity implements OnItemClickListener{

	private RelativeLayout quanquanLayout; 
	private ImageView quanquan; 
	private TextView quanMsg; 
	private Animation rotateAnimation; 
	private ListView listView;
	private TextView title;
	//跟贴ID集合
	ArrayList<String> replyIds;
	//获取下一页帖子的URL;
	String replyUrl;
	String titleString;
	//帖子分页，默认第一页
	int pno=1;
	String bid;
	
	public Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//必须放在super之后第一行的位置
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.list_reply);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        //获取上入页面跳转过来通过intent传入的值
        Intent intent=getIntent();
        if(intent!=null) {
        	replyIds=intent.getStringArrayListExtra(Constants.REPLYIDKEY);
        	replyUrl=intent.getStringExtra(Constants.REPLYURLKEY);
        	titleString=intent.getStringExtra(Constants.TITLEBAR);
        	bid=intent.getStringExtra(Constants.BIDKEY);
        }
        title=(TextView)findViewById(R.id.title);
        title.setText(titleString);
        //加载框
        quanMsg = (TextView) findViewById(R.id.quanMsg);
		quanquanLayout = (RelativeLayout) findViewById(R.id.quanquanLayout);
		quanquan = (ImageView) findViewById(R.id.quanquan);
		rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.quanquan);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		listView=(ListView)findViewById(R.id.list_reply);
		listView.setOnItemClickListener(this);
		startProcess();
		
	}

	/**
	 * 开始具体的业务处理
	 */
	public void startProcess() {
		SmthUtils.showLoadingDialog(quanquanLayout,quanquan,quanMsg,rotateAnimation,"正在载入.....");
		Message msg=Message.obtain();
		Bundle data=new Bundle();
		data.putStringArrayList(Constants.REPLYIDKEY, replyIds);
		data.putString(Constants.BIDKEY, bid);
		msg.setData(data);
		msg.what=Constants.LISTREPLY;
		//handler用于回调
		msg.obj=handler;
		SmthConnectionHandlerInstance.getInstance().sendMessage(msg);
	}
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		
	}

	
}
