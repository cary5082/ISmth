package com.ismth.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.ismth.adapter.SearchBoardAdapter;
import com.ismth.bean.HtmlContentBean;
import com.ismth.bean.HtmlSourceBean;
import com.ismth.thread.SmthConnectionHandlerInstance;
import com.ismth.utils.Constants;
import com.ismth.utils.SmthUtils;

/**
 * 查询版块
 * @author wangjianfeia
 *
 */
public class SearchBoardActivity extends Activity implements View.OnClickListener,OnItemClickListener{

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
	private SearchBoardAdapter adapter;
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			SmthUtils.hideLoadingDialog(quanquanLayout, quanMsg, quanquan);
			search.setClickable(true);
			searchName.setEnabled(true);
			searchName.setInputType(InputType.TYPE_CLASS_TEXT);
			switch(msg.what) {
			case Constants.CONNECTIONSUCCESS:
				HtmlSourceBean htmlSource=(HtmlSourceBean)msg.obj;
				List<HtmlContentBean> hcList=htmlSource.list;
				//说明获取成功
				if(hcList!=null && hcList.size()>0) {
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
		setContentView(R.layout.search_board);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        title=(TextView)findViewById(R.id.title);
        title.setText("版块查询");
        searchName=(EditText)findViewById(R.id.searchName);
        search=(Button)findViewById(R.id.search);
        search.setOnClickListener(this);
        listSearch=(ListView)findViewById(R.id.list_search);
        adapter=new SearchBoardAdapter(getApplicationContext());
        listSearch.setAdapter(adapter);
        listSearch.setOnItemClickListener(this);
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
				search.setClickable(false);
				//设置为不可用状态。但实际点击还是会出现软键盘
				searchName.setEnabled(false);
				//为了不显示软键盘
				searchName.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchName.getWindowToken(), 0);
				SmthUtils.showLoadingDialog(quanquanLayout, quanquan, quanMsg, rotateAnimation, "正在载入...");
				Message msg=Message.obtain();
				msg.what=Constants.SEARCHBOARD;
				msg.obj=handler;
				Bundle data=new Bundle();
				data.putString(Constants.SEARCHNAMEKEY, sn);
				msg.setData(data);
				data=null;
				SmthConnectionHandlerInstance.getInstance().sendMessage(msg);
			}
			break;
		}
	}

	/**
	 * 显示加载出错对话框
	 */
    public void showErrorDialog(){
    	AlertDialog.Builder builder=new Builder(this);
    	builder.setMessage("查询出错。");
    	builder.setTitle("温馨提示：");
    	builder.setNegativeButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	builder.create().show();
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		HtmlContentBean hcb=(HtmlContentBean)adapter.getItem(position);
		
	}
    
    
}
