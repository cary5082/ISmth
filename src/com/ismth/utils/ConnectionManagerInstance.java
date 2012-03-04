package com.ismth.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 负责连接的单例模式
 *@Time:2012-2-8
 *@Author:wangjianfei
 *@Version:
 */
public class ConnectionManagerInstance {

	private ConnectionManagerInstance(){};
	
	private static ConnectionManagerInstance instance=null;
	
	//把登录成功后的COOKIE记录放到此变量中
	private static String cookieValue="";
	
	public static synchronized ConnectionManagerInstance getInstance(){
		if(instance==null) {
			instance=new ConnectionManagerInstance();
		}
		return instance;
	}
	
	
	/**
	 * 和服务器进行连接
	 * @param address 需要连接的URL
	 * @param method 提交服务器的方式，POST或GET
	 * @param content 要发送给服务器的内容，如果不需要送给则给NULL值
	 * @return 
	 */
	public HttpURLConnection connectionServer(String address,String method,String content,String title) {
		HttpURLConnection conn=null;
		URL url=null;
		OutputStream os=null;
		InputStream is=null;
		try {
			url= new URL(address);
			conn= (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECTIONTIMEOUT);
			conn.setReadTimeout(Constants.READTIMEOUT);
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			conn.setRequestProperty("User-Agent","Mozilla/4.7 [en] (Win98; I)");
			conn.setRequestProperty("Accept-Charset", "GB2312");
			//如果cookieValue里的值不为空时，说明之前登录过了，此时只需要把上次记录的COOKIE放到此次登录中就行, 反之则需要直接登录
			String username=SharePreferencesUtils.getString(Constants.USERNAME,"guest");
			if(cookieValue.length()==0 && !"guest".equals(username) && address.equals(Constants.LOGINURL)) {
				StringBuffer sb = new StringBuffer();
				String password=SharePreferencesUtils.getString(Constants.PASSWORD, "");
				sb.append("id=").append(username).append("&passwd=").append(password);
				os = conn.getOutputStream();
				os.write(sb.toString().getBytes("GBK"));
			}else if(cookieValue.length()!=0){
				conn.setRequestProperty("Cookie", cookieValue);
			}
			//说明有内容发送给服务器
			if(content!=null && content.length()>0) {
				StringBuffer sb = new StringBuffer();
				sb.append("text=").append(content).append("&title=").append(title);
				os = conn.getOutputStream();
				os.write(sb.toString().getBytes("GBK"));
			}
			//开始连接服务器
			conn.connect();
			//说明并没有保存登录后的cookie，把登录后的cookie保存下来，供下次连接使用
			if(cookieValue.length()==0 && !"guest".equals(username) && address.equals(Constants.LOGINURL)) {
				Map<String, List<String>> hfMap=conn.getHeaderFields();
				String tempCookieValue="";
				Set<String> keys=hfMap.keySet();
				for(String key:keys) {
					if(key!=null && ("set-cookie").equals(key.toLowerCase())) {
						List<String> vs=(List<String>)hfMap.get(key);
						for(String v:vs){
							tempCookieValue+=v;
						}
					}
				}
				if(tempCookieValue.length()>0) {
					cookieValue="Hm_lvt_9c7f4d9b7c00cb5aba2c637c64a41567=1328491921147;"+getCookie(tempCookieValue);
				}
			}
//			BufferedReader br;
//			Reader reader=null;
//			//除获取十大和登陆的URL外，其他的URL返回的字节码要进行编码转换
//			if(address.equals(Constants.TODAYHOTURL) || address.equals(Constants.LOGINURL)) {
//				reader = new InputStreamReader(conn.getInputStream());
//			}else {
//				reader = new InputStreamReader(conn.getInputStream(), "gb2312");
//			}
//			int len;
//			char buffer[] = new char[1024];
//			while((len=reader.read(buffer))>0) {
//				result.append(buffer,0,len);
//			}
//			String line = br.readLine();
//			while(line != null) {
//				result.append(line);
//				line = br.readLine();
//			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(url!=null) {
					url=null;
				}
				if(os!=null) {
					os.close();
					os=null;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return conn;
	}
	
	/**
	 * 登录后拿到COOKIE的值，把一些无用的COOKIE信息去掉。
	 * @param cookie cookie的值
	 * @return 返回有用的cookie信息
	 */
    private String getCookie(String cookie) {
		StringBuilder sb=new StringBuilder();
		String[] b=cookie.split(";");
		for(int i=0;i<b.length;i++) {
			if(i==0) {
				sb.append(b[i]);
			}else {
				String[] st=b[i].split("\\.");
				if(st.length>1) {
					if(i+1<b.length) {
						sb.append(";");
					}
					String r=st[st.length-1].replaceAll("net", "").replaceAll("\\s*","");
					sb.append(r);
				}else {		//如果当前字符串中没有.判断是不是有UTMPKEY字符串存在
					if(b[i].contains("UTMPKEY")) {
						sb.append(";").append(b[i].replaceAll("path=\\/", ""));
					}
				}
			}
		}
		return sb.toString();
    }
    
    public String getCookieValue(){
    	return cookieValue;
    }
    
    public void destroy(){
    	cookieValue="";
    	instance=null;
    }
}
