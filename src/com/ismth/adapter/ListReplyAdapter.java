package com.ismth.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.TextView;

import com.ismth.activity.R;
import com.ismth.bean.HtmlContentBean;
import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;

public class ListReplyAdapter extends BaseAdapter{

	private List<HtmlContentBean> listReply=new ArrayList<HtmlContentBean>();
	private Context context;
	private TextView content;
	private Gallery gallery;
	private HashMap<Integer,ArticleAttAdapter> map;
	
	public ListReplyAdapter(Context context) {
		this.context=context;
	}
	
	@Override
	public int getCount() {
		return listReply.size();
	}

	@Override
	public Object getItem(int position) {
		return listReply.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView=LayoutInflater.from(context).inflate(R.layout.reply_item, null);
		HtmlContentBean hcb=listReply.get(position);
		content=(TextView)convertView.findViewById(R.id.reply_con);
		//判断当前帖子是否有附件显示，如果有的话，初始化gallery
		if(hcb.attUrl!=null && hcb.attUrl.size()>0) {
			gallery=(Gallery)convertView.findViewById(R.id.gallery);
			//设置图片边距
			gallery.setSpacing(15);
			ArticleAttAdapter attAdapter=null;
			if(map.containsKey(hcb.attUrl.hashCode())) {
				attAdapter=map.get(hcb.attUrl.hashCode());
			}else {
				attAdapter=new ArticleAttAdapter(context);
				map.put(hcb.attUrl.hashCode(), attAdapter);
			}
			gallery.setAdapter(attAdapter);
			gallery.setVisibility(View.VISIBLE);
			
		}
		content.setText(Html.fromHtml(hcb.content));
		return convertView;
	}

	public void setListReply(List<HtmlContentBean> list) {
		listReply=list;
		map=null;
		map=new HashMap<Integer,ArticleAttAdapter>();
		list=null;
	}
	
	/**
	 * 点击用户的点击位置获取回贴URL
	 * @param position
	 * @return
	 */
	public String getLinkUrl(int position) {
		HtmlContentBean hcb=listReply.get(position);
		return hcb.replyUrl;
	}
	
	/**
	 * 显示帖子附件里的内容
	 */
	public void notifyAttAdapter(List<String> attUrl){
		try {
			ArticleAttAdapter attAdapter=null;
			if(map.containsKey(attUrl.hashCode())) {
				attAdapter=map.get(attUrl.hashCode());
				attAdapter.showAtt(attUrl);
			}else {
				attAdapter=new ArticleAttAdapter(context);
				map.put(attUrl.hashCode(), attAdapter);
				attAdapter.showAtt(attUrl);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
