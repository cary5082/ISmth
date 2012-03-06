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

/**
 * 
 *2012-3-6 下午8:39:44
 *author:cary
 */
public class BoardAdapter extends BaseAdapter{

	private List<HtmlContentBean> list=new ArrayList<HtmlContentBean>();
	
	private Context context;
	
	private TextView boardArticle;
	
	private TextView boardAuth;
	
	public BoardAdapter(Context context) {
		this.context=context;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.board_item, null);
		}
		HtmlContentBean hcb=list.get(position);
		boardArticle=(TextView)convertView.findViewById(R.id.board_article);
		boardAuth=(TextView)convertView.findViewById(R.id.board_auth);
		boardArticle.setText(hcb.content);
		boardAuth.setText("test");
		hcb=null;
		return convertView;
	}
	
	public void setList(List<HtmlContentBean> list) {
		this.list=list;
	}

}
