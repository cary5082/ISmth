package com.ismth.utils;
/**
 *@Time:2012-2-8
 *@Author:wangjianfei
 *@Version:
 */
public class Constants {
	
	/** 登陆的URL*/
	public static final String LOGINURL="http://www.newsmth.net/bbslogin1203.php";
	/** 退出登陆URL*/
	public static final String LOGOUTURL="http://www.newsmth.net/bbslogout.php";
	/** 手机站点*/
	public static final String MOBILEURL="http://m.newsmth.net";
	/** 连接超时为10秒*/
	public static final int CONNECTIONTIMEOUT=10000;
	/** 读取超时为10秒*/
	public static final int READTIMEOUT=10000;
	
	public static final String USERNAME="username";
	
	public static final String PASSWORD="password";
	
	public static final String TAG="ISmth";
	/** 今日十大*/
	public static final int TODAYHOT=1;
	/** 单篇文章*/
	public static final int ARTICLE=2;
	/** 查看回帖*/
	public static final int LISTREPLY=3;
	/** 发送帖子内容给服务器*/
	public static final int SENDARTICLE=4;
	/** 查询版块*/
	public static final int SEARCHBOARD=5;
	
	/** 十大的RSSURL*/
	public static final String TODAYHOTURL="http://www.newsmth.net/rssi.php?h=1";
	/** 文章主贴的链接@bid,@id会在发起链接时替换成相应的参数*/
	public static  String ARTICLEURL="http://www.newsmth.net/bbscon.php?bid=@bid&id=@id";
	/** 文章附件的链接@bid,@id,@attachid会在发起链接时替换成相应的参数*/
	public static String ATTACHURL="http://att.newsmth.net/att.php?p.@bid.@id.@attachid.jpg";
	/** 新发文章或回帖的URL*/
	public static String SENDNEWARTICLEURL="http://www.newsmth.net/bbssnd.php?";
	/** 搜索讨论区,@name会在发起链接时替换成相应的搜索名*/
	public static String SEARCHBOARDURL="http://m.newsmth.net/go?name=";
	/** 服务器通信成功*/
	public static final int CONNECTIONSUCCESS=1;
	/** 服务器通信出错*/
	public static final int CONNECTIONERROR=2;
	/** 从服务器获取附件*/
	public static final int CONNECTIONATTACH=3;
	/** 最大分页数*/
	public static final int MAXPAGENUM=4;
	/** 登录成功KEY*/
	public static final int LOGINSUCCESSKEY=5;
	/** 登录失败KEY*/
	public static final int LOGINFAILKEY=6;
	/** 搜索讨论区*/
	public static final int QUERYDISCUSSION=1;
	/** 个人信息*/
	public static final int PERSONINFO=2;
	
	public static final String BIDURLKEY="bidUrlKey";
	
	public static final String TITLEBAR="titlebar";
	
	public static final String IDKEY="idKey";
	/** 有附件*/
	public static final int ATTACH=1;
	/** 无附件*/
	public static final int NOATTACH=0;
	/** gallery一次加载图片的数量*/
	public static final int GALLERYLOADNUM=5;
	/** 左滑*/
	public static final int LEFTSLIPPAGE=1;
	/** 右滑*/
	public static final int RIGHTSLIPPAGE=2;
	/** 获取跟贴ID的URl*/
	public static final String REPLYURLKEY="replyurlkey";
	/** 跟贴ID的KEY*/
	public static final String REPLYIDKEY="replyidkey";
	/** BID*/
	public static final String BIDKEY="bidkey";
	/** 帖子分页*/
	public static final String PNOKEY="pnokey";
	
	public static final String ARTICLEBUNDLE="articleBundle";
	/** 帖子内容的KEY*/
	public static final String ARTICLECONTENTKEY="article_content_key";
	/** 帖子内容发送的URL*/
	public static final String SENDARTICLEURLKEY="send_article_url_key";
	/** 回复帖子标题*/
	public static final String SENDTITLEKEY="send_title_key";
	/** 十大主题的回调handler的key*/
	public static final String TODAYHOTHANDLERKEY="today_hot_handler_key";
	/** 查询版块名称的KEY*/
	public static final String SEARCHNAMEKEY="search_name_key";
	/** 版块名称的KEY*/
	public static final String BOARDNAMEKEY="board_name_key";
	/** 版块地址的KEY*/
	public static final String BOARDURLKEY="board_url_key";
	/** 获取网址的URL key*/
	public static final String GETURLKEY="get_url_key";
}
