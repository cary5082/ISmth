package com.ismth.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	 * 获取主帖后面跟贴的ID
	 * @param html
	 * @return
	 */
	public static ArrayList<String> getReplyId(String html) {
		ArrayList<String> list=new ArrayList<String>();
		String temp=html.substring(html.indexOf("o.o")+3, html.length());
		if(temp.indexOf("o.h()")>-1) {
			temp=temp.substring(0,temp.indexOf("o.h()")-1);
			String[] array=temp.split("]");
			String result="";
			for(String str:array) {
				if(str!=null && str.length()>0 && str.contains(",")) {
					str=str.replaceAll("\\[", "");
					if(str.contains("(")) {
						result=str.substring(1, str.indexOf(","));
					}else {
						result=str.substring(1,str.indexOf("'")-1);
					}
					list.add(result);
				}
			}			
		}
		return list;
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
//				articleContent=articleContent.replaceAll("n", "\n");
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
	
	/**
	 * 获得文章的TITLE
	 * @param html
	 * @return
	 */
	public static String getTitleForHtml(String html) {
		return html.substring(html.indexOf("]")+1, html.length());
	}
	
	/**
	 * 显示正在加载对话框
	 * @param layout 加载对话框布局文件
	 * @param image imageView
	 * @param textView 加载对话框文件
	 * @param ani 加载动画
	 * @param str 加载文本
	 */
	public static void showLoadingDialog(RelativeLayout layout,View image,TextView textView,Animation ani,String str) {
		image.setVisibility(View.VISIBLE);
		textView.setVisibility(View.VISIBLE);
		layout.setVisibility(View.VISIBLE);
		textView.setText(str);
		image.startAnimation(ani);
	}
	
	/**
	 * 隐藏加载对话框
	 * @param layout
	 * @param textView
	 * @param image
	 */
	public static void hideLoadingDialog(RelativeLayout layout,TextView textView,View image) {
		image.clearAnimation();
		layout.setVisibility(View.GONE);
		image.setVisibility(View.GONE);
		textView.setVisibility(View.GONE);
	}
	
	
	/**
	 * 从主帖URL得到回复帖子的URL，把主帖URL后面的gid替换成reid
	 * @param url 
	 * @param reid 回复帖子的ID
	 * @param isReply 是新发帖子还是回复帖子。TRUE为回复，
	 * @return
	 */
	public static String getReplyArticleUrl(String url,String reid,boolean isReply) {
		try {
			String[] temp=url.split("&");
			String[] temp2=temp[0].split("\\?");
			url=Constants.SENDNEWARTICLEURL+temp2[1];
			//如果为新发帖子。则不用后面的GID
			if(isReply) {
				url+="&reid="+reid;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	
	/**
	 * 显示toast
	 * @param context applicationcontext
	 * @param content toast要显示的内容
	 */
	public static void showToast(Context context,String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 把URL中的中文编码传输
	 * @param name	需要编码的中文名
	 * @param enc	
	 * @return
	 */
	public static String encodeURL(String name,String enc) {
		String result=null;
		try {
			result = URLEncoder.encode(name, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据URL地址得到版块名称
	 * @param url
	 * @return
	 */
	public static String getBoardName(String url){
		String boardName=url.substring(url.indexOf("=")+1,url.indexOf("&"));
		return boardName;
	}
	
	/**
	 * 根据URL地址得到GID
	 * @param url
	 * @return
	 */
	public static String getGid(String url) {
		String gid=url.substring(url.lastIndexOf("=")+1,url.length());
		return gid;
	}
	
	public static String getGidForMobile(String url) {
		String gid=url.substring(url.lastIndexOf("/")+1,url.length());
		return gid;
	}
}
