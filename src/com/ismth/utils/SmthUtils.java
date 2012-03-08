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
