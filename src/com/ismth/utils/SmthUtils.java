package com.ismth.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.ismth.bean.ArticleBean;


/**
 * 工具类
 *@Time:2012-2-13
 *@Author:wangjianfei
 *@Version:
 */
public class SmthUtils {

	/**
	 * 在HTML内容中，找出BID的值
	 * @param html 网页的内容
	 * @return
	 */
	public static String getBidForHtml(String html){
		String bid=null;
		String[] temp=html.split("new tconWriter");
		if(temp.length==2) {
			String[] temp2=temp[1].split(";");
			String[] temp3=temp2[0].split("\\,");
			if(temp3.length>1) {
				bid=temp3[1];
			}
		}
		return bid;
	}
	
	/**
	 * 在一个URL链接中获得主贴的ID值
	 * @param url
	 * @return 返回主贴ID值
	 */
	public static String getIdForUrl(String url) {
		String id=null;
		String[] temp=url.split("gid=");
		if(temp.length==2) {
			String[] temp2=temp[1].split("&");
			id=temp2[0];
		}
		return id;
	}
	
	/**
	 * 从HTML源码中获得单篇文章的内容和附件链接ID
	 * @param html 网页源码
	 * @return 单篇文章的内容
	 */
	public static ArticleBean getArticleForHtml(String html) {
		ArticleBean ab=new ArticleBean();
		String articleContent=null;
		List<String> attachIds=null;
		try {
			String[] temp=html.split("站内");
			//length=2说明是正常帖子，
			if(temp.length==2) {
				String[] temp2=temp[1].split("--");
				articleContent=temp2[0];
				//4长度是为了去掉前面\n\n
				articleContent=articleContent.substring(4,articleContent.length());
				//根据页面内容判断是否有附件，如果有附件的话把附件ID拿到
				attachIds=getAttachId(temp2);
//				articleContent=articleContent.replace((char)92, (char)32);
//				articleContent=articleContent.replace((char)110, (char)10);
			}else if(temp.length==3) { //length=3说明帖子是转贴。内容中有两上站内的字样。
				articleContent=temp[1];
				String[] temp3=temp[2].split("--");
				articleContent+=temp3[0];
				attachIds=getAttachId(temp3);
			}
			if(articleContent!=null) {
				articleContent=articleContent.replaceAll("\\\\", "");
				articleContent=articleContent.replaceAll("n", "\n");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		ab.attachIds=attachIds;
		ab.content=articleContent;
		return ab;
	}
	
	/**
	 * 获取网页附件的ID
	 * @param str	网页源码
	 * @return 把附件ID放入数组中。
	 */
	public static List<String> getAttachId(String[] str){
		List<String> attachId=new ArrayList<String>();
		int i=0;
		int attachLength=str.length;
		try {
			//遍历得到的HTML
			for(String html:str) {
				//判断字符串不为空，避免发生空指针异常
				if(html!=null && html.length()>0) {
					int indexOf=html.indexOf("attach");
					//判断当前字符串里是否含有attache
					if(indexOf>-1) {
						String html2=html.substring(indexOf, html.length());
						String[] attachArray=html2.split("attach");
						for(String att:attachArray) {
							if(att!=null && att.length()>0) {
								att=att.replaceAll("\\(", "");
								int length=att.indexOf(")");
								att=att.substring(0, length);
								String[] ids=att.split(",");
								attachId.add(ids[ids.length-1].trim());
							}
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return attachId;
	}
	
	/**
	 * URLHTTPCONNECTION从服务返回字节流，把字节流转成STRING返回
	 * @param is 服务器返回字节流
	 * @param encFlag 是否对字节流进行转码,true：对字节流进行转码
	 * @param enc	字符编码集
	 * @return
	 */
	public static String getStringForHttp(HttpURLConnection conn,boolean encFlag,String enc) {
		StringBuilder result=new StringBuilder();
		try {
			Reader reader=null;
			//为TRUE的话对字节流进行转码
			if(encFlag) {
				reader = new InputStreamReader(conn.getInputStream(), enc);
			}else {
				reader = new InputStreamReader(conn.getInputStream());
			}
			int len;
			char buffer[] = new char[1024];
			while((len=reader.read(buffer))>0) {
				result.append(buffer,0,len);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(conn!=null) {
				conn.disconnect();
				conn=null;
			}
		}
		return result.toString();
	}
	
	/**
	 * URLHTTPCONNECTION从服务返回字节流，把字节流转成byte[]返回
	 * @param conn
	 */
	public static byte[] getByteArrayForHttp(HttpURLConnection conn) {
		byte[] b=null;
		InputStream is=null;
		try {
			is=conn.getInputStream();
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			b=outStream.toByteArray();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is=null;
			}
			if(conn!=null) {
				conn.disconnect();
				conn=null;
			}
		}
		return b;
	}
	
	public static String getTitleForHtml(String html) {
		return html.substring(html.indexOf("]")+1, html.length());
	}
}
