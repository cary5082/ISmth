package com.ismth.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.ismth.utils.BitmapUtils;
import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SmthInstance;

/**
 * 文章附件的adapter
 * @author wangjianfeia
 *
 */
public class ArticleAttAdapter extends BaseAdapter{

	private List<Bitmap> list=new ArrayList<Bitmap>();
	
	private Context context;
	
	private ArrayList<String> attUrl=new ArrayList<String>();
	
	public ArticleAttAdapter(Context context) {
		this.context=context;
	}
	
	public ArticleAttAdapter(Context context,List<Bitmap> li) {
		this.context=context;
		this.list=li;
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

	public void add(Bitmap bitmap,String url) {
		list.add(bitmap);
		attUrl.add(url);
		bitmap=null;
	}
	
	public void add(Bitmap bitmap,int position,String url) {
		//判断当前位置是不是存在于list中，如果存在用新VALUE代替老VALUE，反之直接加到最后位置
		if(position<list.size()) {
			list.set(position, bitmap);
		}else {
			list.add(bitmap);
			attUrl.add(url);
		}
		bitmap=null;
	}
	
	/**
	 * 根据URL去缓存中拿到数据放入adapter里
	 * @param url
	 */
	public void showAtt(List<String> url) {
		SmthInstance instance=SmthInstance.getInstance();
		try {
			Bitmap bm=null;
			for(String str:url) {
				int position=attUrl.indexOf(str);
				//从list里加入数据时判断是否存在于LIST中
				if((position==-1) || (list.size()>position && list.get(position)!=null)) {
					byte[] temp=instance.getPicMapValue(str);
					if(temp!=null) {
						bm=BitmapUtils.decodeBitmap(temp, 200, 200);
						add(bm, str);
					}
				}
			}
			if(list.size()>0) {
				notifyDataSetChanged();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
