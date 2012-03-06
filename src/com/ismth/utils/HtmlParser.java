package com.ismth.utils;

import java.util.ArrayList;
import java.util.List;

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
	 * 根据URL，分析网页源码
	 * @param url 要分析网页源码的URL	
	 * @param topTag 获取上一页，下一页内容的TAG
	 * @param contentTag 获取具体内容的TAG
	 * @return
	 */
	public HtmlSourceBean getHtmlSourceForUrl(String url,String topTag,String contentTag){
		HtmlSourceBean htmlSource=new HtmlSourceBean();
		List<HtmlContentBean> list=new ArrayList<HtmlContentBean>();
		try {
			Document doc=Jsoup.connect(url).userAgent("Mozilla").timeout(Constants.CONNECTIONTIMEOUT).get();
			//获取上一页，下一页相关信息
			Elements elems=doc.select(topTag);
			Elements elemsTwo=elems.select("a[href]");
			for(Element ele:elemsTwo) {
				if(ele.text().equals("上页")) {
					htmlSource.prepageLink=ele.attr("href");
				}else if(ele.text().equals("下页")) {
					htmlSource.nextpageLink=ele.attr("href");
				}
			}
			Elements temp_elements=doc.select(contentTag);
			//说明找到这个属性了，可以开始从这个ELEMENTS遍历
			Elements  elements=temp_elements.select("a[href]");
			//如果不大于0说明遍历出错了。
			for(Element el:elements) {
				HtmlContentBean hcb=new HtmlContentBean();
				hcb.linkUrl=el.attr("href");
				hcb.content=el.text();
				list.add(hcb);
			}
			htmlSource.list=list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlSource;
	}
}
