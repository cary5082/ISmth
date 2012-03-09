package com.ismth.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ismth.bean.HtmlContentBean;
import com.ismth.bean.HtmlSourceBean;

/**
 * HTML解析
 *2012-3-5 下午8:02:11
 *author:cary
 */
public class HtmlParser {
	
	/**
	 * 获取回复的帖子
	 * @param url
	 * @return
	 */
	public static HtmlSourceBean getListReply(String url) {
		HtmlSourceBean hsb=new HtmlSourceBean();
		try {
			Connection conn=getConnection(url);
			Document doc=conn.get();
			//获取上一页，下一页相关信息
			Elements els=doc.select("div.sec.nav");
			Elements els2=els.select("a[href]");
			for(Element ele:els2) {
				if(ele.text().equals("上页")) {
					hsb.prepageLink=ele.attr("href");
				}else if(ele.text().equals("下页")) {
					hsb.nextpageLink=ele.attr("href");
				}
			}
			Elements els3=doc.select("ul.list.sec");
			Elements els4=els3.select("li");
			List<HtmlContentBean> hcbList=new ArrayList<HtmlContentBean>();
			for(Element el:els4) {
				HtmlContentBean hcb=new HtmlContentBean();
				//获取回复帖子的URL
				Elements replys=el.select("div.nav.hl");
				Elements re=replys.select("a[href]");
				for(Element e:re) {
					if("回复".equals(e.text())) {
						hcb.replyUrl=e.attr("href");
					}
				}
				//获取回复内容
				Elements els5=el.select("div.sp");
				//获取附件的URL
				Elements els6=els5.select("a[href]");
				List<String> attList=new ArrayList<String>();
				for(Element el2:els6) {
					String tempUrl=el2.attr("href");
					if(tempUrl.indexOf("att")>-1) {
						attList.add(tempUrl);
					}
				}
				hcb.attUrl=attList;
				int last=els5.html().indexOf("<img");
				if(last==-1) {
					hcb.content=els5.html();
				}else {
					hcb.content=els5.html().substring(0,last);
				}
				if(hcb.content.length()>0){
					hcbList.add(hcb);
				}
				hcb=null;
			}
			hsb.list=hcbList;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return hsb;
	}
	
	/**
	 * 登录手机站点
	 * @param url 
	 * @param userName 用户名
	 * @param password 密码
	 * @return
	 */
	public static String loginSmth(String url,String userName,String password) {
		String result="";
		try {
			Connection conn=Jsoup.connect(url).data("id",userName).data("passwd",password).method(Method.POST);
			Response response=conn.execute();
			Document doc=response.parse();
			Elements e=doc.select("div.hl");
			result=e.text();
			SmthInstance.getInstance().setCookie(response.cookies());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 退出
	 * @param url
	 */
	public static void logoutSmth(String url) {
		try {
			Connection conn=getConnection(url);
			Response response=conn.execute();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection(String url) {
		Connection conn=Jsoup.connect(url).timeout(Constants.CONNECTIONTIMEOUT);
		Map<String,String> cookie=SmthInstance.getInstance().getCookie();
		if(cookie!=null) {
			for(Entry<String,String> entry : cookie.entrySet()) {
				conn.cookie(entry.getKey(), entry.getValue());
			}
		}
		return conn;
	}
	
	/**
	 * 发帖子给服务器.
	 * @param url 发帖子的URL
	 * @param title 帖子标题
	 * @param content 帖子内容
	 * @return rturn代表成功,false代表失败
	 */
	public static boolean postArticleToServer(String url,String title,String content) {
		boolean result=true;
		try {
			Connection conn=getConnection(url);
			conn.timeout(Constants.CONNECTIONTIMEOUT).data("subject",title).data("content",content).method(Method.POST);
			Response response=conn.execute();
			Document doc=response.parse();
			Elements els=doc.select("#m_main");
			Elements els2=els.select("div.f");
			if(!"发表成功".equals(els2.text())) {
				result=false;
			}
		}catch(Exception e) {
			result=false;
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据URL读取附件内容
	 * @param url
	 * @return
	 */
	public static byte[] getByteForUrl(String url) {
		byte[] bytearray=null;
		try {
			Connection conn=getConnection(url);
			conn.timeout(Constants.CONNECTIONTIMEOUT);
			Response response=conn.execute();
			bytearray=response.bodyAsBytes();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return bytearray;
	}
	
	/**
	 * 根据URL获取网页源码
	 * @param url
	 * @return
	 */
	public static String getStringForUrl(String url){
		String result="";
		try {
			Connection conn=getConnection(url);
			Response response=conn.execute();
			result=response.body();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
