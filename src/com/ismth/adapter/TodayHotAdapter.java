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
import com.ismth.bean.TodayHotBean;

/**
 *@Time:2012-2-10
 *@Author:wangjianfei
 *@Version:
 */
public class TodayHotAdapter extends BaseAdapter{
	//十大帖子的集合
	private List<TodayHotBean> thbList=new ArrayList<TodayHotBean>();
	private Context context;
	private TextView title;
	private TextView author;
	private TextView link;
	private TodayHotBean thb;
	
	public TodayHotAdapter(Context con,List<TodayHotBean> list) {
		context=con;
		thbList=list;
	}
	
	/**
	 * 获取集合的大小
	 */
	@Override
	public int getCount() {
		return thbList.size();
	}

	@Override
	public Object getItem(int position) {
		return thbList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.todayhot_item, null);
		}
		title=(TextView)convertView.findViewById(R.id.title);
		author=(TextView)convertView.findViewById(R.id.author);
		thb=thbList.get(position);
		title.setText(thb.title);
//		title.setTextColor(R.color.text_color);
		title.setTextColor(context.getResources().getColor(R.color.text_color));
//		title.setTextAppearance(context, R.color.text_color);
		author.setText("作者："+thb.author);
		author.setTextColor(context.getResources().getColor(R.color.text_color));
		return convertView;
	}

}
