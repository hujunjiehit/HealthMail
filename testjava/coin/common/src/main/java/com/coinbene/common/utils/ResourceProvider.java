package com.coinbene.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.coinbene.common.context.CBRepository;

/**
 * ding
 * 2019-07-09
 * com.coinbene.common.utils
 */
public class ResourceProvider {

	/**
	 * @param resId
	 * @return Color
	 */
	public static int getColor(Context context,int resId) {
		return context.getResources().getColor(resId);
	}

	public static int getColor(String color) {
		return Color.parseColor(color);
	}

	/**
	 * @param resId
	 * @return String
	 */
	public static String getString(Context context,int resId) {
		return context.getResources().getString(resId);
	}

	/**
	 * @param resId
	 * @return Drawable
	 */
	public static Drawable getDrawable(Context context,int resId) {
		return context.getResources().getDrawable(resId);
	}

	public static String[] getStringArray(Context context,int resID) {
		return context.getResources().getStringArray(resID);
	}


	/**
	 * @param radius 圆角
	 * @param solidColor 填充颜色
	 * @param strokeColor 描边颜色
	 * @return 举行shape
	 */
	public static GradientDrawable getRectShape(float radius, int solidColor,int strokeColor) {
		GradientDrawable shape = new GradientDrawable();
		shape.setShape(GradientDrawable.RECTANGLE);
		shape.setCornerRadius(radius);
		shape.setStroke(2,strokeColor);
		shape.setColor(solidColor);
		return shape;
	}

	/**
	 * @param radius 圆角
	 * @param solidColor 填充颜色
	 * @return 举行shape
	 */
	public static GradientDrawable getRectShape(float radius, int solidColor) {
		GradientDrawable shape = new GradientDrawable();
		shape.setShape(GradientDrawable.RECTANGLE);
		shape.setCornerRadius(radius);
		shape.setColor(solidColor);
		return shape;
	}

	/**
	 * @param radius 圆角
	 * @param solidColor 填充颜色
	 * @param strokeColor 描边颜色
	 * @return 举行shape
	 */
	public static GradientDrawable getRectShape(float[] radius, int solidColor,int strokeColor) {
		GradientDrawable shape = new GradientDrawable();
		shape.setShape(GradientDrawable.RECTANGLE);
		shape.setCornerRadii(radius);
		shape.setStroke(2,strokeColor);
		shape.setColor(solidColor);
		return shape;
	}

//	private static Context getContext(){
//		if (CBRepository.getLifeCallback().getCurrentAcitivty() != null) {
//			return CBRepository.getLifeCallback().getCurrentAcitivty();
//		} else {
//			return CBRepository.getContext();
//		}
//	}
}
