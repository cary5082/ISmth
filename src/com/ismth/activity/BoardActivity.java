package com.ismth.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ismth.adapter.BoardAdapter;
import com.ismth.adapter.SearchBoardAdapter;
import com.ismth.bean.HtmlContentBean;
import com.ismth.bean.HtmlSourceBean;
import com.ismth.thread.SmthConnectionHandlerInstance;
import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SmthUtils;

/**
 * 展现版面文章的ARTICLE
 * @author wangjianfeia
 *
 */
public class BoardActivity extends Activity implements android.view.View.OnClickListener{

	private TextView title;
	private String boardUrl;
	private HtmlSourceBean htmlSource;
	private RelativeLayout quanquanLayout; 
	private ImageView quanquan; 
	private TextView quanMsg; 
	private Animation rotateAnimation; 
	private BoardAdapter adapter;
	private ListView listView;
	//下一页
	private TextView nextpage;
	//上一页
	private TextView prepage;	
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			SmthUtils.hideLoadingDialog(quanquanLayout, quanMsg, quanquan);
			switch(msg.what) {
			case Constants.CONNECTIONSUCCESS:
				HtmlSourceBean htmlSource=(HtmlSourceBean)msg.obj;
				List<HtmlContentBean> hcList=htmlSource.list;
				ISmthLog.d(Constants.TAG, "list size==="+hcList.size());
				if(!"".equals(htmlSource.nextpageLink)){
					nextpage.setVisibility(View.VISIBLE);
				}
				if(!"".equals(htmlSource.prepageLink)) {
					prepage.setVisibility(View.VISIBLE);
				}
				//说明获取成功
				if(hcList!=null && hcList.size()>0) {
					listView.setVisibility(View.VISIBLE);
					adapter.setList(hcList);
					adapter.notifyDataSetChanged();
				}else {
					showErrorDialog();
				}
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.board);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        title=(TextView)findViewById(R.id.title);
        Intent intent=getIntent();
        title.setText(intent.getStringExtra(Constants.BOARDNAMEKEY));
        boardUrl=intent.getStringExtra(Constants.BOARDURLKEY);
        //加载框
        quanMsg = (TextView) findViewById(R.id.quanMsg);
		quanquanLayout = (RelativeLayout) findViewById(R.id.quanquanLayout);
		quanquan = (ImageView) findViewById(R.id.quanquan);
		rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.quanquan);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		listView=(ListView)findViewById(R.id.board_article);
		adapter=new BoardAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		nextpage=(TextView)findViewById(R.id.nextpage);
		prepage=(TextView)findViewById(R.id.prepage);
		nextpage.setOnClickListener(this);
		prepage.setOnClickListener(this);
		startProcess();
	}

	/**
	 * 开始流程处理
	 */
	public void startProcess(){
		SmthUtils.showLoadingDialog(quanquanLayout, quanquan, quanMsg, rotateAnimation, "正在加载....");
		Message msg=Message.obtain();
		msg.what=Constants.SEARCHBOARD;
		msg.obj=handler;
		Bundle data=new Bundle();
		boardUrl=Constants.MOBILEURL+boardUrl;
		data.putString(Constants.GETURLKEY, boardUrl);
		msg.setData(data);
		data=null;
		SmthConnectionHandlerInstance.getInstance().sendMessage(msg);
	}
	
	@Override
	public void onClick(View v) {
		
	}

	/**
	 * 显示加载出错对话框
	 */
    public void showErrorDialog(){
    	AlertDialog.Builder builder=new Builder(this);
    	builder.setMessage("加载出错。");
    	builder.setTitle("温馨提示：");
    	builder.setNegativeButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	builder.create().show();
    }
}
