package com.ismth.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ismth.bean.TodayHotBean;

/**
 * XML解析类
 *@Time:2012-2-9
 *@Author:wangjianfei
 *@Version:
 */
public class XmlParserInstance {
	private XmlParserInstance(){};
	private static XmlParserInstance instance=new XmlParserInstance();
	
	public static XmlParserInstance getInstance(){
		return instance;
	}
	
	/**
	 * 从网络端获取BSS上十大内容
	 * @param todayHotXml 从网络端读取过来的XML字节内容
	 * @return 把十大的内容封装成LIST返回
	 */
	public List<TodayHotBean> readTodayHotBean(String xml){
		List<TodayHotBean> list=null;
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser parser=parserFactory.newSAXParser();
			TodayHotHandler handler=new TodayHotHandler();
			parser.parse(new ByteArrayInputStream(xml.getBytes()), handler);
			list=handler.getList();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 解析十大XML的Handler
	 * @author wangjianfeia
	 *
	 */
	private class TodayHotHandler extends DefaultHandler{
		private List<TodayHotBean> list=null;
		private String tagName=null;
		private TodayHotBean thb=null;
		//TRUEΪ���ITEM��㿪ʼ
		private boolean flag=false;
		
		public List<TodayHotBean> getList(){
			return list;
		}
		
		/**
		 * 在characters如果字符串中出现&等特殊字符，则会多次调用，所以会出现不连续的字符串
		 * 解决方法是把多次调用的字符拼接起来。
		 */
		@Override
		public void characters(char[] ch, int start, int length)throws SAXException {
			if(tagName!=null && thb!=null) {
				String data=new String(ch,start,length);
				if("title".equals(tagName)) {
					if(thb.title!=null && thb.title.length()>0) {
						thb.title+=data;
					}else {
						thb.title=data;
					}
				}else if("author".equals(tagName)) {
					if(thb.author!=null && thb.author.length()>0) {
						thb.author+=data;
					}else {
						thb.author=data;
					}
				}else if("link".equals(tagName)) {
					if(thb.link!=null && thb.link.length()>0) {
						thb.link+=data;
					}else {
						thb.link=data;
					}
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)throws SAXException {
			if("item".equals(localName)) {
				list.add(thb);
				thb=null;
			}
			tagName=null;
		}

		@Override
		public void startDocument() throws SAXException {
			list=new ArrayList<TodayHotBean>();
		}

		@Override
		public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException {
			if("item".equals(localName)) {
				thb=new TodayHotBean();
				flag=false;
			}
			tagName=localName;
		}
		
	}
}
