package com.ismth.bean;

import java.util.List;

/**
 * 分析后的网页源码封装成的Bean
 * @author wangjianfeia
 *
 */
public class HtmlSourceBean {

	//上一页
	public String prepageLink="";
	//下一页
	public String nextpageLink="";
	
	public List<HtmlContentBean> list;
}
