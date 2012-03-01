package com.ismth.thread;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ismth.bean.ArticleBean;
import com.ismth.bean.TodayHotBean;
import com.ismth.utils.ConnectionManagerInstance;
import com.ismth.utils.Constants;
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
	
	private static SmthConnectionHandlerInstance instance=new SmthConnectionHandlerInstance();
	
	public static SmthConnectionHandlerInstance getInstance(){
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
			Bundle bundle=null;
			String bid=null;
			String id=null;
			Message message=Message.obtain();
			switch(msg.what) {
			//获取十大
			case Constants.TODAYHOT:
				//先获取十大帖子字节流
				conn=ConnectionManagerInstance.getInstance().connectionServer(Constants.TODAYHOTURL, "GET");
				if(conn!=null){
					//把字节流转成String字符串
					result=SmthUtils.getStringForHttp(conn, false, null);
				}
				//如果返回流不为空，则进行XML解析拿到十大的数据
				if(result!=null && result.length()>0) {
					List<TodayHotBean> list=XmlParserInstance.getInstance().readTodayHotBean(result);
					message.what=Constants.CONNECTIONSUCCESS;
					message.obj=list;
				}else {
					message.what=Constants.CONNECTIONERROR;
				}
				//把结果返回activity
				handler.sendMessage(message);
				break;
				//获取单篇文章
			case Constants.ARTICLE:
				bundle=msg.getData();
				ArticleBean ab=null;
				String url=bundle.getString(Constants.BIDURLKEY);
				id=bundle.getString(Constants.IDKEY);
				//先从帖子的URL中读取网页内容，从网页内容中获得BID,先拿到字节流
				conn=ConnectionManagerInstance.getInstance().connectionServer(url, "GET");
				if(conn!=null) {
					result=SmthUtils.getStringForHttp(conn, true, "gb2312");
					if(result!=null && result.length()>0) {
						ArrayList<String>replyIds=SmthUtils.getReplyId(result);
						Bundle data=new Bundle();
						data.putStringArrayList(Constants.REPLYIDKEY, replyIds);
						data.putString(Constants.REPLYURLKEY, url);
						bid=SmthUtils.getBidForHtml(result);
						data.putString(Constants.BIDKEY,bid);
						message.setData(data);
						replyIds=null;
					}
				}
				if(bid!=null) {
					String articleUrl=Constants.ARTICLEURL.replaceAll("@bid", bid).replaceAll("@id", id);
//					String articleUrl="http://www.newsmth.net/bbscon.php?bid=874&id=1792262";
//					bid="874";
//					id="1792262";
					conn=ConnectionManagerInstance.getInstance().connectionServer(articleUrl, "GET");
					if(conn!=null) {
						result=SmthUtils.getStringForHttp(conn, true, "gb2312");
						ab=SmthUtils.getArticleForHtml(result);
						result=ab.content;
					}
					if(result!=null && result.length()>0) {
						message.what=Constants.CONNECTIONSUCCESS;
						message.obj=result;
						if(ab!=null && ab.attachIds!=null && ab.attachIds.size()>0) {
							message.arg1=Constants.ATTACH;
						}else {
							message.arg1=Constants.NOATTACH;
						}
						//把结果返回activity
						handler.sendMessage(message);
					}else if(ab==null || ab.attachIds==null){
						message.what=Constants.CONNECTIONERROR;
						//把结果返回activity
						handler.sendMessage(message);
					}
				}else {
					message.what=Constants.CONNECTIONERROR;
					//把结果返回activity
					handler.sendMessage(message);
				}
				//判断是否有附件，如果有的话去抓取附件
				if(ab!=null && ab.attachIds!=null && ab.attachIds.size()>0) {
					getAttachSource(ab.attachIds, bid, id);
					Message attMessage=Message.obtain();
					attMessage.what=Constants.CONNECTIONATTACH;
					attMessage.arg1=Integer.valueOf(id);
					handler.sendMessage(attMessage);
				}
				break;
			//获取回帖
			case Constants.LISTREPLY:
				ArrayList<String> ll=new ArrayList<String>();
				bundle=msg.getData();
				//回帖的主ID
				ArrayList<String> replyIds=(ArrayList<String>)bundle.getSerializable(Constants.REPLYIDKEY);
				bid=bundle.getString(Constants.BIDKEY);
				int pno=bundle.getInt(Constants.PNOKEY);
				id=bundle.getString(Constants.IDKEY);
				String replyUrl=bundle.getString(Constants.REPLYURLKEY);
				replyUrl+="&pno="+pno;
				String tempUrl=Constants.ARTICLEURL.replaceAll("@bid", bid);
				//如果没有回贴主ID，则获取回帖主ID
				if(replyIds==null) {
					conn=ConnectionManagerInstance.getInstance().connectionServer(replyUrl, "GET");
					result=SmthUtils.getStringForHttp(conn, true, "gb2312");
					if(result!=null && result.length()>0) {
						replyIds=SmthUtils.getReplyId(result);
					}
				}
				if(replyIds!=null) {
					//如果replyIds里包含主帖ID，并且页数是第一页，说明是第一次加载回帖
					if(replyIds.contains(id) && pno==1) {
						//把集合中的主帖ID删除。
						replyIds.remove(id);
						//遍历每个回贴的主ID，获取回帖内容
						for(String aid:replyIds) {
							String tu=tempUrl.replaceAll("@id", aid);
							conn=ConnectionManagerInstance.getInstance().connectionServer(tu, "GET");
							if(conn!=null) {
								result=SmthUtils.getStringForHttp(conn, true, "gb2312");
								ab=SmthUtils.getArticleForHtml(result);
								ll.add(ab.content);
								ab=null;
								result=null;
							}
						}
					}
					bundle.putStringArrayList(Constants.REPLYIDKEY, replyIds);
					message.setData(bundle);
					message.obj=ll;
					message.what=Constants.CONNECTIONSUCCESS;
				}else {			//否则说明页数超过了回帖分页的最大数，此时网站自动跳转到第一页。提醒用户此时是最后一页了
					message.what=Constants.MAXPAGENUM;
				}
				//通过HANDLER通知主线程UI
				handler.sendMessage(message);
				break;
			}
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
	 */
	public void removeBeforeMessage() {
		myHandler.removeMessages(Constants.TODAYHOT);
		myHandler.removeMessages(Constants.ARTICLE);
		myHandler.removeMessages(Constants.LISTREPLY);
	}
	
	/**
	 * 判断对列是否还有消息在里面。
	 * @return true代表有消息，
	 */
	public boolean hasMessageInQueue(){
		boolean result=false;
		result=myHandler.hasMessages(Constants.TODAYHOT);
		result=myHandler.hasMessages(Constants.ARTICLE);
		return result;
	}
	
	/**
	 * 根据附件ID去获取附件,附件内容放入BYTE数组，
	 * @param attacheId 附件ID集合
	 * @param bid 版块ID
	 * @param id 帖子主ID
	 * 
	 */
	public void getAttachSource(List<String> attachId,String bid,String id) {
		String tempUrl=Constants.ATTACHURL.replaceAll("@bid", bid).replaceAll("@id", id);
		SmthInstance instance=SmthInstance.getInstance();
		ArrayList<byte[]> linked=new ArrayList<byte[]>();
		HttpURLConnection conn=null;
		//根据帖子的ID看看附件数据是否有缓存到本地
		boolean cacheFlag=instance.containsKeyForPicMap(Integer.valueOf(id));
		try {
			//附件数据没有被缓存，需要从新下载
			if(!cacheFlag) {
				for(String str:attachId) {
					//说明队列中有新要处理的消息，立刻结束当前方法。
					if(!breakFlag) {
						break;
					}else if(str!=null && str.length()>0) {
						String attachUrl=tempUrl;
						attachUrl=attachUrl.replace("@attachid", str);
						int key=Integer.valueOf(str);
//						attachSource.add(key);
						//如果没有缓存附件数据，则从网上下载附件
						conn=ConnectionManagerInstance.getInstance().connectionServer(attachUrl, "GET");
						if(conn!=null) {
							byte[] temp=SmthUtils.getByteArrayForHttp(conn);
							if(temp!=null && temp.length>0) {
								linked.add(temp);
							}
						}
					}
				}
				instance.addItemToPicMap(Integer.valueOf(id), linked);
			}
			linked=null;
			attachId=null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
