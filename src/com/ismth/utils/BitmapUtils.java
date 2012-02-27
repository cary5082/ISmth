package com.ismth.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

/**
 * Bitmap工具类
 *@Time:2012-2-15
 *@Author:wangjianfei
 *@Version:
 */
public final class BitmapUtils {
	
	
	public static Bitmap Bytes2Bimap(byte[] b) {  
	    if (b.length != 0) {  
	        return BitmapFactory.decodeByteArray(b, 0, b.length);  
	    } else {  
	        return null;  
	    }  
	} 
	
	/**
	 * 根据路径生成BITMAP
	 * @param path
	 * @param displayWidth
	 * @param displayHeight
	 * @return
	 */
	public static Bitmap decodeBitmap(String path, int displayWidth, int displayHeight){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息
		//获取比例大小
		int wRatio = (int)Math.ceil(op.outWidth/(float)displayWidth);
		int hRatio = (int)Math.ceil(op.outHeight/(float)displayHeight);
		//如果超出指定大小，则缩小相应的比例
		if(wRatio > 1 && hRatio > 1){
			if(wRatio > hRatio){
				op.inSampleSize = wRatio;
			}else{
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(path, op);
		return Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);
	}
	
	/**
	 * 根据资源ID生成bitmap
	 * @param resource
	 * @param id
	 * @param displayWidth
	 * @param displayHeight
	 * @return
	 */
	public static Bitmap decodeBitmap(Resources resource,int id, int displayWidth, int displayHeight){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		//获取比例大小
		int wRatio = (int)Math.ceil(op.outWidth/(float)displayWidth);
		int hRatio = (int)Math.ceil(op.outHeight/(float)displayHeight);
		//如果超出指定大小，则缩小相应的比例
		if(wRatio > 1 && hRatio > 1){
			if(wRatio > hRatio){
				op.inSampleSize = wRatio;
			}else{
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false;
		Bitmap bmp=BitmapFactory.decodeResource(resource, id);
		return Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);
	}
	
	/**
	 * 根据byte数组生成bitmap
	 * @param resource
	 * @param id
	 * @param displayWidth
	 * @param displayHeight
	 * @return
	 */
	public static Bitmap decodeBitmap(byte[] bytearray, int displayWidth, int displayHeight){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		//获取比例大小
		int wRatio = (int)Math.ceil(op.outWidth/(float)displayWidth);
		int hRatio = (int)Math.ceil(op.outHeight/(float)displayHeight);
		//如果超出指定大小，则缩小相应的比例
		if(wRatio > 1 && hRatio > 1){
			if(wRatio > hRatio){
				op.inSampleSize = wRatio;
			}else{
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false;
		Bitmap bmp=BitmapFactory.decodeByteArray(bytearray,0,bytearray.length,op);
		return Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);
	}
	
	
	/**
	 * 采用复杂计算来决定缩放
	 * @param path
	 * @param maxImageSize
	 * @return
	 */
	public static Bitmap decodeBitmap(String path, int maxImageSize){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息
		int scale = 1;
		if(op.outWidth > maxImageSize || op.outHeight > maxImageSize){
			scale = (int)Math.pow(2, (int)Math.round(Math.log(maxImageSize/(double)Math.max(op.outWidth, op.outHeight))/Math.log(0.5)));
		}
		op.inJustDecodeBounds = false;
		op.inSampleSize = scale;
		bmp = BitmapFactory.decodeFile(path, op);
		return bmp;		
	}
	
	/**
	 * 采用复杂计算来决定缩放
	 * @param path
	 * @param maxImageSize
	 * @return
	 */
	public static Bitmap decodeBitmap(byte[] bytearray, int maxImageSize){
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length,op); //获取尺寸信息
		int scale = 1;
		if(op.outWidth > maxImageSize || op.outHeight > maxImageSize){
			scale = (int)Math.pow(2, (int)Math.round(Math.log(maxImageSize/(double)Math.max(op.outWidth, op.outHeight))/Math.log(0.5)));
		}
		op.inJustDecodeBounds = false;
		op.inSampleSize = scale;
		bmp = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length,op);
		return bmp;		
	}

}

