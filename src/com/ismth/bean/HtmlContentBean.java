package com.ismth.bean;

import java.util.List;

/**
 * 分析网页源码后，需要用到的网页内容
 * @author wangjianfeia
 *
 */
public class HtmlContentBean {

	/** 回复帖子的地址*/
	public String replyUrl;
	/** 帖子内容*/
	public String content;
	/** 是否有回帖*/
	public boolean isReply=false;
	/** 附件的URL地址*/
	public List<String> attUrl;
}
