package com.ismth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ismth.utils.Constants;
import com.ismth.utils.SmthUtils;

/**
 * 查询版块
 * @author wangjianfeia
 *
 */
public class SearchBoardActivity extends Activity implements OnClickListener{

	private TextView title;
	//查询内容
	private EditText searchName;
	//查询按钮
	private Button search;
	private ListView listSearch;
	private RelativeLayout quanquanLayout; 
	private ImageView quanquan; 
	private TextView quanMsg; 
	private Animation rotateAnimation;
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.search_board);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        title=(TextView)findViewById(R.id.title);
        title.setText("版块查询");
        searchName=(EditText)findViewById(R.id.searchName);
        search=(Button)findViewById(R.id.search);
        search.setOnClickListener(this);
        listSearch=(ListView)findViewById(R.id.list_search);
        //加载框
        quanMsg = (TextView) findViewById(R.id.quanMsg);
		quanquanLayout = (RelativeLayout) findViewById(R.id.quanquanLayout);
		quanquan = (ImageView) findViewById(R.id.quanquan);
		rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.quanquan);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		SmthUtils.hideLoadingDialog(quanquanLayout, quanMsg, quanquan);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		//查询按钮
		case R.id.search:
			String sn=searchName.getText().toString();
			if(sn.length()>0) {
				Intent intent=new Intent(getApplicationContext(),WebViewActivity.class);
				intent.putExtra(Constants.SEARCHNAMEKEY, SmthUtils.encodeURL(sn, "gb2312"));
				startActivity(intent);
				finish();
//				search.setClickable(false);
//				//设置为不可用状态。但实际点击还是会出现软键盘
//				searchName.setEnabled(false);
//				//为了不显示软键盘
//				searchName.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
//				SmthUtils.showLoadingDialog(quanquanLayout, quanquan, quanMsg, rotateAnimation, "正在载入...");
//				Message msg=Message.obtain();
//				msg.what=Constants.SEARCHBOARD;
//				msg.obj=handler;
//				Bundle data=new Bundle();
//				data.putString(Constants.SEARCHNAMEKEY, SmthUtils.encodeURL(sn, "gb2312"));
//				msg.setData(data);
//				data=null;
			}
			break;
		}
	}

	
}
