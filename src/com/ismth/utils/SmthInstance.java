package com.ismth.utils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.ismth.thread.SmthConnectionHandlerInstance;

/**
 * 存放公用变量
 *@Time:2012-2-16
 *@Author:wangjianfei
 *@Version:
 */
public class SmthInstance {

	private static SmthInstance instance=null;
	
	//缓存附件的内容，KEY为贴子的ID，ARRAYLIST里放在是图片
	private ConcurrentHashMap<Integer,ArrayList<byte[]>> picMap=new ConcurrentHashMap<Integer,ArrayList<byte[]>>();
	
	private SmthInstance(){};
	
	public static synchronized SmthInstance getInstance(){
		if(instance==null) {
			instance=new SmthInstance();
		}
		return instance;
	}
	
	public ConcurrentHashMap<Integer,ArrayList<byte[]>> getConHashMap(){
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
	 * 根据key取出相应对的VALUE，把VALUE长度返回
	 * @param key
	 * @return
	 */
	public int getPicMapValueSize(int key) {
		int size=0;
		ArrayList<byte[]> list=picMap.get(key);
		if(list!=null) {
			size=list.size();
		}
		return size;
	}
	
	/**
	 * 根据KEY从picmap中得到VALUE
	 * @param key picmap中的key
	 * @return
	 */
	public ArrayList<byte[]> getPicMapValue(int key) {
		return picMap.get(key);
	}
	
	/**
	 * 把数据缓存到picmap中
	 * @param key 
	 * @param value
	 */
	public void addItemToPicMap(int key,ArrayList<byte[]> value) {
		picMap.put(key, value);
	}
	
	/**
	 * 判断是否KEY有相对应的value
	 * @param key
	 * @return
	 */
	public boolean containsKeyForPicMap(int key) {
		return picMap.containsKey(key);
	}
	
	/**
	 * 销毁项目中公用的一些变量值
	 */
	public void destroyCommonObject() {
		picMap=null;
		instance=null;
		SmthConnectionHandlerInstance.getInstance().exitThread();
		ConnectionManagerInstance.getInstance().destroy();
	}
}
