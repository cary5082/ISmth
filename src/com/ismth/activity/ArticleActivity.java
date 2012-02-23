package com.ismth.activity;

import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import com.ismth.adapter.GalleryAdapter;
import com.ismth.thread.SmthConnectionHandlerInstance;
import com.ismth.utils.BitmapUtils;
import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SmthInstance;
import com.ismth.utils.SmthUtils;

/**
 * 单篇文章阅读
 *@Time:2012-2-13
 *@Author:wangjianfei
 *@Version:
 */
public class ArticleActivity extends Activity implements OnItemClickListener,OnItemSelectedListener{

	TextView article;
	GalleryAdapter adapter;
	Gallery gallery;
	//是否正在显示大图标志位，TRUE为正在显示
	boolean showBigPicFlag=false;
	private TextView title;
	Bitmap bigBitmap;
	//大图
	ImageView bigPic;
	
	ScrollView scroll;
	
	LinearLayout linearLayout;
	
	LinearLayout topbarline;
	
	
	public Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case Constants.CONNECTIONSUCCESS:
				String result=(String)msg.obj;
//				article.setText(Html.fromHtml(result));
				//如果贴子有附件重新定义scrollview的高
				if(msg.arg1==Constants.ATTACH) {
					WindowManager wm = (WindowManager)ArticleActivity.this.getSystemService(ArticleActivity.this.WINDOW_SERVICE);
					int height = wm.getDefaultDisplay().getHeight();//屏幕高度
					//如果有附件把scrollView高度设为屏幕高度一半
					scroll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height/2));
				}
				article.setText(result);
				break;
			case Constants.CONNECTIONERROR:
				showErrorDialog();
				break;
			case Constants.CONNECTIONATTACH:
				int articleId=(Integer)msg.arg1;
				ISmthLog.d(Constants.TAG, "into show gallery==");
				showGalleryPic(articleId);
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.article);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
        title=(TextView)findViewById(R.id.title);
		article=(TextView)findViewById(R.id.article);
		gallery=(Gallery)findViewById(R.id.gallery);
		scroll=(ScrollView)findViewById(R.id.scroll);
		bigPic=(ImageView)findViewById(R.id.bigpic);
		adapter=new GalleryAdapter(this);
		gallery.setAdapter(adapter);
		//设置图片边距
		gallery.setSpacing(15);
		gallery.setOnItemClickListener(this);
		gallery.setOnItemSelectedListener(this);
		linearLayout=(LinearLayout)findViewById(R.id.topbar);
		topbarline=(LinearLayout)findViewById(R.id.topbarline);
		process();
	}

	/**
	 * 开始业务的处理
	 */
	public void process(){
		//从Intent中拿到用户点击的URL
		Intent intent=getIntent();
		if(intent!=null){
			String url=intent.getStringExtra(Constants.BIDURLKEY);
			String titleString=intent.getStringExtra(Constants.TITLEBAR);
			title.setText(SmthUtils.getTitleForHtml(titleString));
			String id=SmthUtils.getIdForUrl(url);
			//判断是否能取到正确的ID值
			if(id!=null) {
				Message message=Message.obtain();
				message.what=Constants.ARTICLE;
				message.obj=handler;
				Bundle bundle=new Bundle();
				bundle.putString(Constants.BIDURLKEY, url);
				bundle.putString(Constants.IDKEY, id);
				message.setData(bundle);
				SmthConnectionHandlerInstance.getInstance().sendMessage(message);
			}
		}
	}
	
	/**
	 * 显示加载出错对话框
	 */
    public void showErrorDialog(){
    	AlertDialog.Builder builder=new Builder(this);
    	builder.setMessage("文章加载出错。");
    	builder.setTitle("温馨提示：");
    	builder.setNegativeButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	builder.create().show();
    }
    
    /**
     * 显示gallery里的图片
     * @param articleId
     */
    public void showGalleryPic(int articleId) {
    	SmthInstance instance=SmthInstance.getInstance();
    	LinkedList<byte[]> list=instance.getPicMapValue(articleId);
    	adapter.setArticleId(articleId);
    	try {
    		if(list!=null) {
    			byte[] bytearray=null;
    			Bitmap bm=null;
    			int i=0;
    			for(Iterator it=list.iterator();it.hasNext();) {
    				if(i<Constants.GALLERYLOADNUM) {
    					bytearray=(byte[])it.next();
        				bm=BitmapUtils.decodeBitmap(bytearray, 200, 200);
    				}else {
    					break;
    				}
    				i++;
    				adapter.add(bm);
    			}
    		}
//    		conMap=null;
    		list=null;
        	if(adapter.getCount()>0) {
        		gallery.setVisibility(View.VISIBLE);
        		adapter.notifyDataSetChanged();
        	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    /**
     * gallery选中的图片
     */
	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position,long id) {
		int articleId=adapter.getArticleId();
		int direction=adapter.leftOrRightSlippage(position);
		releaseBitmapForGallery(direction);
		addItemToAdapter(articleId,direction);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	/**
	 * gallery单击的图片
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
//		Bitmap bm=(Bitmap)adapter.getItem(position);
		SmthInstance instance=SmthInstance.getInstance();
		int articleId=adapter.getArticleId();
		LinkedList<byte[]> ll=instance.getPicMapValue(articleId);
		byte[] bytearray=ll.get(position);
		bigBitmap=BitmapUtils.Bytes2Bimap(bytearray);
		BitmapDrawable bd= new BitmapDrawable(ArticleActivity.this.getResources(), bigBitmap);
		showBigPicFlag=true;
		bigPic.setBackgroundDrawable(bd);
		bigPic.setVisibility(View.VISIBLE);
		gallery.setVisibility(View.GONE);
		article.setVisibility(View.GONE);
		scroll.setVisibility(View.GONE);
		linearLayout.setVisibility(View.GONE);
		topbarline.setVisibility(View.GONE);
	}
	
	/**
	 * 释放gallery里的图片资源，释放规则是可见区域的前后各5张
	 */
	private void releaseBitmapForGallery(int direction){
		int start=gallery.getFirstVisiblePosition()-Constants.GALLERYLOADNUM;
		int end=gallery.getLastVisiblePosition()+Constants.GALLERYLOADNUM;
		int count=adapter.getCount();
		//先把可见图片之前的释放
		int tempStart=start-1;
		for(int i=tempStart;i>=0;i--) {
			adapter.releaseForList(i);
		}
		//把最后一张可见图片之后的图片释放
		int tempEnd=end+1;
		for(int i=tempEnd;i<count;i++) {
			adapter.releaseForList(i);
		}
	}
	
	/**
	 * 根据滑动方向的不同，预加载图片
	 * @param articleId 附件存在缓存里的KEY
	 * @param direction	滑动方向
	 */
	public void addItemToAdapter(int articleId,int direction) {
		SmthInstance instance=SmthInstance.getInstance();
		int listCount=instance.getPicMapValueSize(articleId);
    	//根据滑动方向的不同，预加载不同位置的图片
    	if(direction==Constants.LEFTSLIPPAGE) {
    		int tempStart=gallery.getFirstVisiblePosition()-1;
    		if(tempStart>=0) {
    			for(int i=0;i<=Constants.GALLERYLOADNUM;i++) {
    				int tempPosition=tempStart-i;
    				if(tempPosition>=0 && tempPosition<listCount) {
    					addItemToAdapterList(tempPosition, articleId);
    				}
    			}
    			adapter.notifyDataSetChanged();
    		}
    	}else if(direction==Constants.RIGHTSLIPPAGE) {
        	int tempEnd=gallery.getLastVisiblePosition()+1;
        	if(tempEnd<listCount) {
    	    	for(int i=0;i<Constants.GALLERYLOADNUM;i++) {
    	    		int tempPosition=tempEnd+i;
    	    		if(tempPosition<listCount) {
    	    			addItemToAdapterList(tempPosition,articleId);
    	    		}
    	    	}
    	    	adapter.notifyDataSetChanged();
        	}
    	}
	}
	
	/**
	 * 从缓存中取出图片加入到ADAPTER里的LIST中
	 * @param position 加入的位置
	 * @param articleId	缓存中的key
	 */
	public void addItemToAdapterList(int position,int articleId) {
		SmthInstance instance=SmthInstance.getInstance();
		LinkedList<byte[]> list=instance.getPicMapValue(articleId);
		Object o=adapter.getItem(position);
		Bitmap bm=null;
		if(o==null) {
			byte[] array=list.get(position);
    		bm=BitmapUtils.decodeBitmap(array, 200, 200);
    		adapter.add(bm,position);
    		bm=null;
		}
		o=null;
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.exitGallery();
		adapter=null;
	}

	/**
	 * 监听键盘
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			//正在显示大图，按back键此时处理为回到普通模式
			if(showBigPicFlag) {
				bigPic.setVisibility(View.GONE);
				article.setVisibility(View.VISIBLE);
				gallery.setVisibility(View.VISIBLE);
				scroll.setVisibility(View.VISIBLE);
				linearLayout.setVisibility(View.VISIBLE);
				topbarline.setVisibility(View.VISIBLE);
				showBigPicFlag=false;
				bigBitmap.recycle();
				bigBitmap=null;
				return true;
			}else{
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}
