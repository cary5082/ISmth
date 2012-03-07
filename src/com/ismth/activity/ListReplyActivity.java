package com.ismth.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
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

import com.ismth.adapter.ListReplyAdapter;
import com.ismth.bean.HtmlSourceBean;
import com.ismth.thread.SmthConnectionHandlerInstance;
import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SmthUtils;

/**
 * 查看回贴
 *2012-2-25 上午11:32:26
 *author:cary
 */
public class ListReplyActivity extends Activity implements OnItemClickListener,android.view.View.OnClickListener{

	private RelativeLayout quanquanLayout; 
	private ImageView quanquan; 
	private TextView quanMsg; 
	private Animation rotateAnimation; 
	private ListView listView;
	private TextView title;
	//获取帖子的URL;
	String url;
	String titleString;
	ListReplyAdapter adapter;
	String id;
	//下一页
	TextView nextpage;
	//上一页
	TextView prepage;
	HtmlSourceBean hsb;
	
	public Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			SmthUtils.hideLoadingDialog(quanquanLayout, quanMsg, quanquan);
			listView.setVisibility(View.VISIBLE);
			switch(msg.what) {
			case Constants.CONNECTIONSUCCESS:
				hsb=(HtmlSourceBean)msg.obj;
				//说明获取数据正确
				if(hsb.list!=null && hsb.list.size()>0) {
					if(hsb.nextpageLink.length() > 0) {
						nextpage.setVisibility(View.VISIBLE);
					}
					if(hsb.prepageLink.length() >0) {
						prepage.setVisibility(View.VISIBLE);
					}
					adapter.setListReply(hsb.list);
					adapter.notifyDataSetChanged();
				}else {	//提示用户获取数据失败
					showErrorDialog();
				}
				break;
			}
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
        	url=intent.getStringExtra(Constants.REPLYURLKEY);
        	titleString=intent.getStringExtra(Constants.TITLEBAR);
        	id=intent.getStringExtra(Constants.IDKEY);
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
		nextpage=(TextView)findViewById(R.id.nextpage);
		prepage=(TextView)findViewById(R.id.prepage);
		listView.setOnItemClickListener(this);
		adapter=new ListReplyAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		nextpage.setOnClickListener(this);
		prepage.setOnClickListener(this);
		startProcess();
		initMenuForListView();
	}

	/**
	 * 开始具体的业务处理
	 */
	public void startProcess() {
		nextpage.setVisibility(View.INVISIBLE);
		prepage.setVisibility(View.INVISIBLE);
		listView.setVisibility(View.GONE);
		SmthUtils.showLoadingDialog(quanquanLayout,quanquan,quanMsg,rotateAnimation,"正在载入.....");
		Message msg=Message.obtain();
		Bundle data=new Bundle();
		data.putString(Constants.GETURLKEY, url);
		msg.setData(data);
		msg.what=Constants.LISTREPLY;
		//handler用于回调
		msg.obj=handler;
		SmthConnectionHandlerInstance.getInstance().sendMessage(msg);
	}
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		
	}

	/**
	 * 显示加载出错对话框
	 */
    public void showErrorDialog(){
    	AlertDialog.Builder builder=new Builder(this);
    	builder.setMessage("网络加载出错。");
    	builder.setTitle("温馨提示：");
    	builder.setPositiveButton("重试", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startProcess();
			}
		});
    	builder.setNegativeButton("返回", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
    	builder.create().show();
    }
	
    /**
     * 初始化listview长按的menu选项
     */
    public void initMenuForListView(){
    	listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("菜单");
				menu.add(0, 0, 0, "回复");
			}
    	});
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.nextpage:
			url=Constants.MOBILEURL+hsb.nextpageLink;
			break;
		case R.id.prepage:
			url=Constants.MOBILEURL+hsb.prepageLink;
			break;
		}
		startProcess();
	}

	/**
	 * 选中菜单ITEM后触发
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		String replyUrl=adapter.getLinkUrl(menuInfo.position);
//		Intent i=new Intent(getApplicationContext(),ReplyArticleActivity.class);
//		i.putExtra(Constants.TITLEBAR, titleString);
//		replyUrl=SmthUtils.getReplyArticleUrl(replyUrl,replyIds.get(menuInfo.position),true);
//		i.putExtra(Constants.SENDARTICLEURLKEY,replyUrl);
//		startActivity(i);
		return true;
	}
	
}
