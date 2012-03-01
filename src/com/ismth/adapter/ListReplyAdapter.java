package com.ismth.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ismth.activity.R;

public class ListReplyAdapter extends BaseAdapter{

	private ArrayList<String> listReply=new ArrayList<String>();
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
		content.setText(listReply.get(position));
		return convertView;
	}

	public void setListReply(ArrayList<String> list) {
		listReply=list;
		list=null;
	}
	
}