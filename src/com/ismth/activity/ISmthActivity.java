package com.ismth.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ismth.adapter.TodayHotAdapter;
import com.ismth.bean.TodayHotBean;
import com.ismth.thread.SmthConnectionHandlerInstance;
import com.ismth.utils.Constants;
import com.ismth.utils.SharePreferencesUtils;

public class ISmthActivity extends Activity implements OnItemClickListener{
	
	private Animation alphaAnimation; 
	private RelativeLayout quanquanLayout; 
	private ImageView quanquan; 
	private TextView quanMsg; 
	private Animation rotateAnimation; 
	private ListView listView;
	private TodayHotAdapter todayHotAdapter;
	private List<TodayHotBean>list;
	private TextView title;
	
	public Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			hideDialog();
			switch(msg.what) {
			case Constants.CONNECTIONSUCCESS:
				list=(List<TodayHotBean>)msg.obj;
				setListViewByTodayHot(list);
				break;
			case Constants.CONNECTIONERROR:
				showErrorDialog();
				break;
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        title=(TextView)findViewById(R.id.title);
        title.setText("十大主题排行");
        quanMsg = (TextView) findViewById(R.id.quanMsg);
		quanquanLayout = (RelativeLayout) findViewById(R.id.quanquanLayout);
		quanquan = (ImageView) findViewById(R.id.quanquan);
		rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.quanquan);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.shade_alpha);
		listView=(ListView)findViewById(R.id.todayhot);
		listView.setOnItemClickListener(this);
		SharePreferencesUtils.setContext(this);
        //登录
        Intent intent=new Intent(this,LoginIntentService.class);
        startService(intent);
        //获取十大的内容
        SmthConnectionHandlerInstance.getInstance().startThread();
        getTodayHot();
    }
    
    /**
     * 获取十大的内容
     */
    public void getTodayHot(){
    	showDialog();
    	Message msg=Message.obtain();
    	msg.what=Constants.TODAYHOT;
    	msg.obj=handler;
    	SmthConnectionHandlerInstance.getInstance().sendMessage(msg);
    }
    
    /**
     * 显示加载对话框
     */
    public void showDialog(){
		quanMsg.setText("正在载入...");
		quanquanLayout.setVisibility(View.VISIBLE);
		quanquan.startAnimation(rotateAnimation);
    }
    
    /**
     * 隐藏加载对话框
     */
    public void hideDialog(){
    	quanquanLayout.setVisibility(View.GONE);
    	quanquan.clearAnimation();
    }
    
    /**
     * ListView适配器
     * @param list listView里的值
     */
    public void setListViewByTodayHot(List<TodayHotBean> list) {
    	todayHotAdapter=new TodayHotAdapter(this,list);
		listView.setAdapter(todayHotAdapter);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Intent intent=new Intent(this,ArticleActivity.class);
		intent.putExtra(Constants.BIDURLKEY, list.get(position).link);
		startActivity(intent);
//		ISmthLog.d(Constants.TAG, "title==="+list.get(position).title+"===link=="+list.get(position).link);
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
				getTodayHot();
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
     * 创建MENU菜单
     * oncreateOptionsMenu为Activity自动调用
     * menu.add方法四个参数：
     * 第一个:组别。第二个：ID是menu识别编号，供识别menu用的。
     * 第三个：顺序。这个参数的大小决定菜单出现的先后顺序。顺序是参数由小到大，菜单从左到右，从上到下排列。一行最多三个。
     * 第四个:显示文本
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,Constants.QUERYDISCUSSION,0,"查询版块");
		menu.add(0,Constants.PERSONINFO,1,"个人信息");
		return true;
	}

	/**
	 * menu菜单选中
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case Constants.QUERYDISCUSSION:
			
			break;
		case Constants.PERSONINFO:
			Intent intent=new Intent(this,PersonInfoActivity.class);
			startActivity(intent);
			break;
		}
		return false;
	}

	/**
	 * 监听键盘
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			showExitDialog();
		}
		return false;
	}
	
	/**
	 * 显示退出对话框
	 */
	public void showExitDialog(){
    	AlertDialog.Builder builder=new Builder(this);
    	builder.setMessage("是否退出应用。");
    	builder.setTitle("退出应用");
    	builder.setPositiveButton("退出", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
    	builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	builder.create().show();
	}
}