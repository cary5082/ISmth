package com.ismth.thread;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ismth.bean.HtmlContentBean;
import com.ismth.bean.HtmlSourceBean;
import com.ismth.bean.TodayHotBean;
import com.ismth.utils.ConnectionManager;
import com.ismth.utils.Constants;
import com.ismth.utils.HtmlParser;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SmthInstance;
import com.ismth.utils.SmthUtils;
import com.ismth.utils.XmlParserInstance;

/**
 * 连接服务器队列
 *@Time:2012-2-10
 *@Author:wangjianfei
 *@Version:
 */
public class SmthConnectionHandlerInstance {

	private HandlerThread handlerThread;
	
	private Looper looper;
	
	private MyHandler myHandler;
	//是否继续执行当前循环的方法false为退出当前循环
	private boolean breakFlag=true;
	
	private SmthConnectionHandlerInstance(){};
	
	private volatile static SmthConnectionHandlerInstance instance=null;
	
	public static SmthConnectionHandlerInstance getInstance(){
		if(instance==null) {
			synchronized (SmthConnectionHandlerInstance.class) {
				if(instance==null) {
					instance=new SmthConnectionHandlerInstance();
				}
			}
		}
		return instance;
	}
	
	private class MyHandler extends Handler{
		private MyHandler(){};
		
		public MyHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			//回调的handler
			Handler handler=(Handler)msg.obj;
			//读取URL中的内容
			String result=null;
			HttpURLConnection conn=null;
			Bundle bundle=msg.getData();
			String getUrl=bundle.getString(Constants.GETURLKEY);;
			String gid=null;
			HtmlSourceBean hsb=null;
			Message message=Message.obtain();
			ConnectionManager cm=new ConnectionManager();
			switch(msg.what) {
			//获取十大
			case Constants.TODAYHOT:
				//先获取十大帖子
				result=HtmlParser.getStringForUrl(Constants.TODAYHOTURL);
				//如果返回流不为空，则进行XML解析拿到十大的数据
				if(result!=null && result.length()>0) {
					List<TodayHotBean> list=XmlParserInstance.getInstance().readTodayHotBean(result);
					message.what=Constants.CONNECTIONSUCCESS;
					message.obj=list;
					if(list==null) {
						message.what=Constants.CONNECTIONERROR;
					}
				}else {
					message.what=Constants.CONNECTIONERROR;
				}
				//把结果返回activity
				handler.sendMessage(message);
				break;
			//获取回帖
			case Constants.LISTREPLY:
				hsb=HtmlParser.getListReply(getUrl);
				message.what=Constants.CONNECTIONSUCCESS;
				message.obj=hsb;
				handler.sendMessage(message);
				if(hsb.list!=null && hsb.list.size()>0) {
					for(HtmlContentBean hcb:hsb.list) {
						if(hcb.attUrl!=null && hcb.attUrl.size()>0) {
							getAttachSource(hcb.attUrl);
							Message attMessage=Message.obtain();
							attMessage.what=Constants.CONNECTIONATTACH;
							attMessage.obj=hcb.attUrl;
							handler.sendMessage(attMessage);
						}
					}
				}
				break;
			//发表和回复帖子给服务器
			case Constants.SENDARTICLE:
				String content=bundle.getString(Constants.ARTICLECONTENTKEY);
				String titleString=bundle.getString(Constants.SENDTITLEKEY);
				boolean isSuccess=HtmlParser.postArticleToServer(getUrl, titleString, content);
				message.obj=isSuccess;
				message.what=Constants.CONNECTIONSUCCESS;
				handler.sendMessage(message);
				break;
			//搜索版块
			case Constants.SEARCHBOARD:
//				bundle=msg.getData();
//				String getUrl=bundle.getString(Constants.GETURLKEY);
//				HtmlSourceBean htmlSource=HtmlParser.getHtmlSourceForUrl(getUrl,"div.sec.nav","ul.list.sec");
//				message.obj=htmlSource;
//				message.what=Constants.CONNECTIONSUCCESS;
//				handler.sendMessage(message);
//				break;
			}
			cm=null;
		}
	}
	
	/**
	 * 启动队列线程
	 */
	public void startThread(){
		if(handlerThread==null) {
			handlerThread=new HandlerThread("ISmth");
			handlerThread.start();
			looper=handlerThread.getLooper();
			myHandler=new MyHandler(looper);
		}
	}
	
	/**
	 * 退出队列线程
	 */
	public void exitThread(){
		if(looper!=null) {
			looper.quit();
			looper=null;
		}
		handlerThread=null;
		myHandler=null;
		instance=null;
	}
	
	/**
	 * 发送消息去HANDLER
	 * @param message
	 */
	public void sendMessage(Message message){
		removeBeforeMessage();
		if(hasMessageInQueue()) {
			breakFlag=false;
		}
		myHandler.sendMessage(message);
	}
	
	/**
	 * 把队列中之前排队的消息删除掉,让当前消息获得最高优先级
	 * 发送帖子内容在队列中不会被删除
	 */
	public void removeBeforeMessage() {
		myHandler.removeMessages(Constants.TODAYHOT);
		myHandler.removeMessages(Constants.LISTREPLY);
	}
	
	/**
	 * 判断对列是否还有消息在里面。
	 * @return true代表有消息，
	 */
	public boolean hasMessageInQueue(){
		boolean result=false;
		result=myHandler.hasMessages(Constants.TODAYHOT);
		return result;
	}
	
	/**
	 * 根据附件ID去获取附件,附件内容放入BYTE数组.
	 * @param attUrl 下载地址
	 * @param id 帖子主ID
	 */
	public void getAttachSource(List<String> attUrl) {
		SmthInstance instance=SmthInstance.getInstance();
		try {
			for(String url:attUrl) {
				//根据帖子的ID看看附件数据是否有缓存到本地
				boolean cacheFlag=instance.containsKeyForPicMap(url);
				if(!cacheFlag) {
					byte[] temp=HtmlParser.getByteForUrl(url);
					if(temp!=null && temp.length>0) {
						instance.addItemToPicMap(url,temp);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
