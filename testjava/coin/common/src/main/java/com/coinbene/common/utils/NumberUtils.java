package com.coinbene.common.utils;


import android.text.TextUtils;

import com.coinbene.common.Constants;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtils {

	//float 换成 String
	public static String floatToString(float f) {
		return floatToString(f, Constants.newScale);
	}

	//float 换成 String 保留小数点后n位
	public static String floatToString(float f, int n) {
		if (f == 0) {
			return "0";
		}
		if (Float.isInfinite(f) || Float.isNaN(f)) {
			return "NaN";
		}
		DecimalFormat format = new DecimalFormat(PrecisionUtils.changeNumToPrecisionStr(n));
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		BigDecimal bigDecimal = new BigDecimal(f);
		String maStr = format.format(bigDecimal.setScale(n, BigDecimal.ROUND_DOWN));
		return maStr;
	}


	public static long stringToLong(String s) {
		if (TextUtils.isEmpty(s)) {
			s = "0";
		}
		BigDecimal bigDecimal = new BigDecimal(s);
		return bigDecimal.longValue();

	}



//	public static String longToString(long l) {
//		if (l == 0) {
//			return "0";
//		}
//		if (Float.isInfinite(l) || Float.isNaN(l)) {
//			return "NaN";
//		}
//		DecimalFormat format = new DecimalFormat("0.01");
//		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
//		BigDecimal bigDecimal = new BigDecimal(l);
//		String maStr = format.format(bigDecimal.setScale(2, BigDecimal.ROUND_DOWN));
//		return maStr;
//
//	}
}
