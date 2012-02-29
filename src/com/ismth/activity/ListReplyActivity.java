package com.ismth.activity;

import java.util.LinkedList;

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
	//跟贴ID集合
	LinkedList<String> replyIds=null;
	//回帖的内容
	LinkedList<String> replyContent=new LinkedList<String>();
	//获取下一页帖子的URL;
	String replyUrl;
	String titleString;
	//帖子分页，默认第一页
	int pno=1;
	String bid;
	ListReplyAdapter adapter;
	String id;
	TextView page;
	//是否清空replyIds里的数据
	boolean clearReplyIds=false;
	
	public Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Bundle data=msg.getData();
			switch(msg.what) {
			case Constants.CONNECTIONSUCCESS:
				SmthUtils.hideLoadingDialog(quanquanLayout, quanquan);
				replyContent=(LinkedList<String>)msg.obj;
				replyIds=(LinkedList)data.getSerializable(Constants.REPLYIDKEY);
				data=null;
				//说明获取数据正确
				if(replyContent.size()>0) {
					clearReplyIds=true;
					pno++;
					listView.setVisibility(View.VISIBLE);
					page.setVisibility(View.VISIBLE);
					adapter.setListReply(replyContent);
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
        	Bundle data=intent.getBundleExtra(Constants.ARTICLEBUNDLE);
        	replyIds=(LinkedList<String>)data.getSerializable(Constants.REPLYIDKEY);
        	replyUrl=data.getString(Constants.REPLYURLKEY);
        	titleString=data.getString(Constants.TITLEBAR);
        	bid=data.getString(Constants.BIDKEY);
        	id=data.getString(Constants.IDKEY);
//        	replyIds=(LinkedList<String>)intent.getSerializableExtra(Constants.REPLYIDKEY);
//        	replyUrl=intent.getStringExtra(Constants.REPLYURLKEY);
//        	titleString=intent.getStringExtra(Constants.TITLEBAR);
//        	bid=intent.getStringExtra(Constants.BIDKEY);
//        	id=intent.getStringExtra(Constants.IDKEY);
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
		page=(TextView)findViewById(R.id.page);
		listView.setOnItemClickListener(this);
		adapter=new ListReplyAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		page.setOnClickListener(this);
		startProcess();
		initMenuForListView();
	}

	/**
	 * 开始具体的业务处理
	 */
	public void startProcess() {
		page.setVisibility(View.INVISIBLE);
		listView.setVisibility(View.GONE);
		SmthUtils.showLoadingDialog(quanquanLayout,quanquan,quanMsg,rotateAnimation,"正在载入.....");
		Message msg=Message.obtain();
		Bundle data=new Bundle();
		//如果清除的标志位为真是每次请求新数据把当前页面的跟帖ID清空。之所以要这个变量是因为第一次进入这个页面，会把第一页的跟帖传入，节省流量
		if(clearReplyIds) {
			replyIds=null;
		}
		data.putSerializable(Constants.REPLYIDKEY, replyIds);
		data.putString(Constants.REPLYURLKEY, replyUrl);
		data.putInt(Constants.PNOKEY, pno);
		data.putString(Constants.BIDKEY, bid);
		data.putString(Constants.IDKEY, id);
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
    	builder.setNegativeButton("退出", new OnClickListener() {
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
		case R.id.page:
			startProcess();
			break;
		}
	}

	/**
	 * 选中菜单ITEM后触发
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		ISmthLog.d(Constants.TAG, "position is ====="+menuInfo.position+"linked list=="+replyIds.get(menuInfo.position));
		return true;
	}
	
}
