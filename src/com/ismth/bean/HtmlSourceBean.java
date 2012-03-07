package com.ismth.bean;

import java.util.List;

/**
 * 分析后的网页源码封装成的Bean
 * @author wangjianfeia
 *
 */
public class HtmlSourceBean {

	/** 上一页*/
	public String prepageLink="";
	/** 下一页*/
	public String nextpageLink="";
	/** 回帖的列表数据*/
	public List<HtmlContentBean> list;
	/** 主帖的内容*/
	public String mainArticle;
	/** 是否有回帖*/
	public boolean isReply=false;
	/** 附件的URL地址*/
	public List<String> attUrl;
	/** 回复帖子的地址*/
	public String replyMainUrl;
}
