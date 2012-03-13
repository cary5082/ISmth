package com.ismth.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.ismth.utils.Constants;

/**
 * Gallery适配器
 *@Time:2012-2-16
 *@Author:wangjianfei
 *@Version:
 */
public class GalleryAdapterTemp extends BaseAdapter{

	private ArrayList<Bitmap> list=new ArrayList<Bitmap>();
	
	private Context context;
	
	private int articleId;
	//上次滑动位置，用此位置来判断此次滑动是左滑还是右滑
	private int beforePosition=0;
	
	public GalleryAdapterTemp(Context context) {
		this.context=context;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		Object o=null;
		//判断当前位置是不是存在于list当中
		if(position<list.size()) {
			o=list.get(position);
		}
		return o;
	}

	/**
	 * 释放某一位置的图片资源
	 * @param position
	 */
	public void releaseForList(int position) {
		if(position<list.size() && position>0) {
			Bitmap bm=list.get(position);
			if(bm!=null) {
				bm.recycle();
//				ISmthLog.d(Constants.TAG, "recycle bitmap=====");
				bm=null;
				list.set(position, null);
			}
		}
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv = new ImageView(context);
		Bitmap bm=list.get(position);
		iv.setImageBitmap(bm);
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		iv.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		return iv;
	}
	
	public void add(Bitmap bitmap) {
		list.add(bitmap);
		bitmap=null;
	}
	
	public void add(Bitmap bitmap,int position) {
		//判断当前位置是不是存在于list中，如果存在用新VALUE代替老VALUE，反之直接加到最后位置
		if(position<list.size()) {
			list.set(position, bitmap);
		}else {
			list.add(bitmap);
		}
		bitmap=null;
	}

	public void setArticleId(int id) {
		articleId=id;
	}
	
	public int getArticleId(){
		return articleId;
	}
	
	public int leftOrRightSlippage(int currentPosition) {
		int result=0;
		if(beforePosition<currentPosition) {
			result=Constants.RIGHTSLIPPAGE;
		}else if(beforePosition>currentPosition){
			result=Constants.LEFTSLIPPAGE;
		}
		beforePosition=currentPosition;
		return result;
	}
	
	public void exitGallery(){
		for(Bitmap bm:list) {
			if(bm!=null) {
				bm.recycle();
			}
		}
	}
}
