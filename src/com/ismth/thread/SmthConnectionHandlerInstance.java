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
import com.ismth.utils.ConnectionManager;
import com.ismth.utils.Constants;
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
			Bundle bundle=null;
			String bid=null;
			String id=null;
			Message message=Message.obtain();
			ConnectionManager cm=new ConnectionManager();
			switch(msg.what) {
			//获取十大
			case Constants.TODAYHOT:
				//先获取十大帖子字节流
				conn=cm.connectionServer(Constants.TODAYHOTURL, "GET",null,null);
				if(conn!=null){
					//把字节流转成String字符串
					result=SmthUtils.getStringForHttp(conn, false, null);
				}
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
				//获取单篇文章
			case Constants.ARTICLE:
				bundle=msg.getData();
				ArticleBean ab=null;
				String url=bundle.getString(Constants.BIDURLKEY);
				id=bundle.getString(Constants.IDKEY);
				//先从帖子的URL中读取网页内容，从网页内容中获得BID,先拿到字节流
				conn=cm.connectionServer(url, "GET",null,null);
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
					conn=cm.connectionServer(articleUrl, "GET",null,null);
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
				if(ab!=null && ab.attachIds!=null && ab.attachIds.size()>0 && id!=null) {
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
					conn=cm.connectionServer(replyUrl, "GET",null,null);
					result=SmthUtils.getStringForHttp(conn, true, "gb2312");
					if(result!=null && result.length()>0) {
						replyIds=SmthUtils.getReplyId(result);
					}
				}
				if(replyIds!=null) {
					//如果replyIds里包含主帖ID，并且页数不是第一页，说明本次请求超过帖子最大分页数
					if(pno!=1 && replyIds.contains(id)) {
						message.what=Constants.MAXPAGENUM;
					}else {
						//把集合中的主帖ID删除。
						replyIds.remove(id);
						//遍历每个回贴的主ID，获取回帖内容
						for(String aid:replyIds) {
							String tu=tempUrl.replaceAll("@id", aid);
							conn=cm.connectionServer(tu, "GET",null,null);
							if(conn!=null) {
								result=SmthUtils.getStringForHttp(conn, true, "gb2312");
								ab=SmthUtils.getArticleForHtml(result);
								ll.add(ab.content);
								ab=null;
								result=null;
							}
						}
						bundle.putStringArrayList(Constants.REPLYIDKEY, replyIds);
						message.setData(bundle);
						message.obj=ll;
						message.what=Constants.CONNECTIONSUCCESS;
					}
				}
				//通过HANDLER通知主线程UI
				handler.sendMessage(message);
				break;
			//发表和回复帖子给服务器
			case Constants.SENDARTICLE:
				bundle=msg.getData();
				String sendUrl=bundle.getString(Constants.SENDARTICLEURLKEY);
				String content=bundle.getString(Constants.ARTICLECONTENTKEY);
				String titleString=bundle.getString(Constants.SENDTITLEKEY);
				conn=cm.connectionServer(sendUrl, "POST", content,titleString);
				result=SmthUtils.getStringForHttp(conn, true, "gb2312");
				//通过返回的数据中有没有发文成功的字样，来判断发文是否成功。
				if(result.indexOf("发文成功")>-1) {
					message.what=Constants.CONNECTIONSUCCESS;
				}else {
					message.what=Constants.CONNECTIONERROR;
				}
				handler.sendMessage(message);
				break;
			//搜索版块
			case Constants.SEARCHBOARD:
				bundle=msg.getData();
				String searchName=bundle.getString(Constants.SEARCHNAMEKEY);
				String searchUrl=Constants.SEARCHBOARDURL.replaceAll("@name", searchName);
				
				break;
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
		ConnectionManager cm=new ConnectionManager();
		try {
			//根据帖子的ID看看附件数据是否有缓存到本地
			boolean cacheFlag=instance.containsKeyForPicMap(Integer.valueOf(id));
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
						conn=cm.connectionServer(attachUrl, "GET",null,null);
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
			cm=null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
