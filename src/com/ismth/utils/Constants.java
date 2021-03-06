package com.ismth.utils;
/**
 *@Time:2012-2-8
 *@Author:wangjianfei
 *@Version:
 */
public class Constants {
	
	//登陆的URL
	public static final String LOGINURL="http://www.newsmth.net/bbslogin1203.php";
	//连接超时为10秒
	public static final int CONNECTIONTIMEOUT=10000;
	//读取超时为10秒
	public static final int READTIMEOUT=10000;
	
	public static final String USERNAME="username";
	
	public static final String PASSWORD="password";
	
	public static final String TAG="ISmth";
	//今日十大
	public static final int TODAYHOT=1;
	//单篇文章
	public static final int ARTICLE=2;
	//十大的RSSURL
	public static final String TODAYHOTURL="http://www.newsmth.net/rssi.php?h=1";
	//文章主贴的链接@bid,@id会在发起链接时替换成相应的参数
	public static  String ARTICLEURL="http://www.newsmth.net/bbscon.php?bid=@bid&id=@id";
	//文章附件的链接@bid,@id,@attachid会在发起链接时替换成相应的参数
	public static String ATTACHURL="http://att.newsmth.net/att.php?p.@bid.@id.@attachid.jpg";
	//隐藏加框对话框
	public static final int CONNECTIONSUCCESS=1;
	//已服务器通信出错
	public static final int CONNECTIONERROR=2;
	//从服务器获取附件
	public static final int CONNECTIONATTACH=3;
	//搜索讨论区
	public static final int QUERYDISCUSSION=1;
	//个人信息
	public static final int PERSONINFO=2;
	
	public static final String BIDURLKEY="bidUrlKey";
	
	public static final String IDKEY="idKey";
	//有附件
	public static final int ATTACH=1;
	//无附件
	public static final int NOATTACH=0;
	//gallery一次加载图片的数量
	public static final int GALLERYLOADNUM=5;
	//左滑
	public static final int LEFTSLIPPAGE=1;
	//右滑
	public static final int RIGHTSLIPPAGE=2;
	
}
