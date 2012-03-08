package com.ismth.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ismth.thread.SmthConnectionHandlerInstance;

/**
 * 存放公用变量
 *@Time:2012-2-16
 *@Author:wangjianfei
 *@Version:
 */
public class SmthInstance {

	private volatile static SmthInstance instance=null;
	
	//缓存附件的内容，KEY为贴子的ID，ARRAYLIST里放在是图片
//	private ConcurrentHashMap<Integer,ArrayList<byte[]>> picMap=new ConcurrentHashMap<Integer,ArrayList<byte[]>>();
	
	private ConcurrentHashMap<String,byte[]> picMap=new ConcurrentHashMap<String,byte[]>();
	
	//把登录成功后的COOKIE记录放到此变量中
	private String cookieValue="";
	
	private Map<String,String> cookie;
	
	public Map<String, String> getCookie() {
		return cookie;
	}

	public void setCookie(Map<String, String> cookie) {
		this.cookie = cookie;
	}

	private SmthInstance(){};
	
	public static SmthInstance getInstance(){
		//先检查实例是否存在，如果不存在才进入下面的同步块 
		if(instance==null) {
			//同步块，线程安全地创建实例
			synchronized (SmthInstance.class) {
				//再次检查实例是否存在，如果不存在才真正地创建实例
				if(instance==null) {
					instance=new SmthInstance();
				}
			}
		}
		return instance;
	}
	
	public ConcurrentHashMap<String,byte[]> getConHashMap(){
		return picMap;
	}
	
	/**
	 * 获得picmap的长度
	 * @return 返回picmap长度
	 */
	public int getPicMapSize(){
		return picMap.size();
	}
	
	/**
	 * 根据KEY从picmap中得到VALUE
	 * @param key picmap中的key
	 * @return
	 */
	public byte[] getPicMapValue(String key) {
		return picMap.get(key);
	}
	
	/**
	 * 把数据缓存到picmap中
	 * @param key 
	 * @param value
	 */
	public void addItemToPicMap(String key,byte[] value) {
		picMap.put(key, value);
	}
	
	/**
	 * 判断是否KEY有相对应的value
	 * @param key
	 * @return
	 */
	public boolean containsKeyForPicMap(String key) {
		return picMap.containsKey(key);
	}
	
	/**
	 * 销毁项目中公用的一些变量值
	 */
	public void destroyCommonObject() {
		picMap=null;
		instance=null;
		cookieValue="";
		SmthConnectionHandlerInstance.getInstance().exitThread();
	}
	
}
