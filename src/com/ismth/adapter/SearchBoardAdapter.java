package com.ismth.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ismth.activity.R;
import com.ismth.bean.HtmlContentBean;

public class SearchBoardAdapter extends BaseAdapter{

	private List<HtmlContentBean> list=new ArrayList<HtmlContentBean>();

	private Context context;
	
	private TextView boardName;
	
	public SearchBoardAdapter(Context con) {
		context=con;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.search_board_item, null);
		}
		boardName=(TextView)convertView.findViewById(R.id.boardName);
		HtmlContentBean hcb=list.get(position);
		boardName.setText(hcb.content);
		return convertView;
	}
	
	public void setList(List<HtmlContentBean> list) {
		this.list=list;
	}
}
