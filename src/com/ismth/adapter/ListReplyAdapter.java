package com.ismth.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ismth.activity.R;
import com.ismth.bean.HtmlContentBean;

public class ListReplyAdapter extends BaseAdapter{

	private List<HtmlContentBean> listReply=new ArrayList<HtmlContentBean>();
	private Context context;
	private TextView content;
	
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
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.reply_item, null);
		}
		content=(TextView)convertView.findViewById(R.id.reply_con);
		HtmlContentBean hcb=listReply.get(position);
		content.setText(Html.fromHtml(hcb.content));
		return convertView;
	}

	public void setListReply(List<HtmlContentBean> list) {
		listReply=list;
		list=null;
	}
	
	public String getReplyUrl(int position) {
		HtmlContentBean hcb=listReply.get(position);
		return hcb.replyUrl;
	}
}
