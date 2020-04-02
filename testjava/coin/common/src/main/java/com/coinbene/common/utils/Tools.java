package com.coinbene.common.utils;

import android.text.TextUtils;

/**
 * Created by june
 * on 2019-08-05
 */
public class Tools {

	public static int parseInt(String src) {
		return parseInt(src, 0);
	}

	public static int parseInt(String src, int defaultValue) {
		if (TextUtils.isEmpty(src)) {
			return defaultValue;
		}
		int index = src.indexOf(".");
		if (index > 0) {
			src = src.substring(0, index);
		}
		try {
			return Integer.parseInt(src);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long parseLong(String src) {
		return parseLong(src, 0);
	}

	public static long parseLong(String src, int defaultValue) {
		if (TextUtils.isEmpty(src)) {
			return defaultValue;
		}
		int index = src.indexOf(".");
		if (index > 0) {
			src = src.substring(0, index);
		}
		try {
			return Long.parseLong(src);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float parseFloat(String src) {
		float result = 0f;
		if (!TextUtils.isEmpty(src)) {
			try {
				result = Float.parseFloat(src);
			} catch (NumberFormatException e) {
				e.printStackTrace();

			}
		}
		return result;
	}

	public static float parseFloat(String src, float defaultValues) {

		float result = defaultValues;
		if (!TextUtils.isEmpty(src)) {
			try {
				result = Float.parseFloat(src);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static double parseDouble(String src, double defaultValues) {

		double result = defaultValues;
		if (!TextUtils.isEmpty(src)) {
			try {
				result = Double.parseDouble(src);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return result;
	}


}
