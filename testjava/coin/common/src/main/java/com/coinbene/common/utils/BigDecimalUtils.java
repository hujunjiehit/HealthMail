package com.coinbene.common.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class BigDecimalUtils {


	/**
	 * http://blog.csdn.net/qq_24483127/article/details/74279085
	 * 将科学技术发转换为数字型字符串的问题，一般通过BigDecimal来转换，
	 * 但这样变量为科学计数法的可以正常转换，
	 * 但变量为正常类型就会出现问题（会出现很长的数值），为保证科学计数法和普通数值可以通用，
	 * 此时可以通过setScale保留相应的位数来实现
	 *
	 * @param floatValue
	 * @return
	 */
	public static String parseENum(float floatValue) {
		String tempStr = String.valueOf(floatValue);
		if (tempStr.toUpperCase().contains("E")) {
			BigDecimal bd1 = new BigDecimal(tempStr);
			return bd1.toPlainString();
		} else {
			return tempStr;
		}
	}


	/**
	 * 提供精确的加法运算。
	 *
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 两个参数的和
	 */
	public static String add(String v1, String v2) {
		if (TextUtils.isEmpty(v2) && TextUtils.isEmpty(v2)) {
			return "0";
		}
		if (TextUtils.isEmpty(v1)) {
			return v2;
		}
		if (TextUtils.isEmpty(v2)) {
			return v1;
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.add(b2).toPlainString();//toString();
	}


	/**
	 * 提供精确的减法运算。
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 两个参数的差
	 */
	public static String subtract(String v1, String v2) {
		if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
			return "0";
		}


		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.subtract(b2).toPlainString();//toString();
	}


	/**
	 * 乘法
	 *
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static String multiplyToStr(String v1, String v2) {
		if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
			return "";
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).toPlainString();
	}

	/**
	 * 乘法 保留几位精度  向下取整
	 *
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return
	 */
	public static String multiplyDown(String v1, String v2, int precision) {
		if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
			return "";
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();
	}

	public static String multiplyUp(String v1, String v2, int precision) {
		if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
			return "";
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).setScale(precision, BigDecimal.ROUND_UP).toPlainString();
	}

	public static BigDecimal multiply(String v1, String v2) {
		if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
			return new BigDecimal(0);
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2);
	}


	public static String multiplyHalfUp(String v1, String v2,int precision) {
		if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
			return "";
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).setScale(precision,BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	/**
	 * 除法  保留几位精度向下取整
	 *
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return Float
	 */
	public static Float divide(String v1, String v2, int precision) {

		if (isEmptyOrZero(v1)) {
			return 0f;
		}
		if (isEmptyOrZero(v2)) {
			return 0f;
		}


		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, precision, BigDecimal.ROUND_DOWN).floatValue();
	}


	/**
	 * 除法  不保留小数
	 *
	 * @param v1
	 * @param v2
	 * @return Float
	 */
	public static Float toPercentageFloat(String v1, String v2) {
		if (isEmptyOrZero(v1)) {
			return 0f;
		}
		if (isEmptyOrZero(v2)) {
			return 0f;
		}


		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);

		DecimalFormat df = new DecimalFormat("0");//格式化小数
		return Float.valueOf(df.format(b1.divide(b2, 2, BigDecimal.ROUND_DOWN)));
	}

	/**
	 * 除法  保留几位精度向下取整
	 *
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return Double
	 */

	public static Double divideDouble(String v1, String v2, int precision) {
		if (TextUtils.isEmpty(v1)) {
			return 0d;
		}
		if (isEmptyOrZero(v2)) {
			return 0d;
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, precision, BigDecimal.ROUND_DOWN).doubleValue();
	}

	/**
	 * 除法  保留几位精度四舍五入
	 *
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return Integer
	 */
	public static Integer divideHalfUp(String v1, String v2, int precision) {
		if (isEmptyOrZero(v1)) {
			return 0;
		}
		if (isEmptyOrZero(v2)) {
			return 0;
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, precision, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
	}

	/**
	 * 除法  保留几位精度向下取整
	 *
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return BigDecimal
	 */
	public static BigDecimal divideBigDmal(String v1, String v2, int precision) {
		if (isEmptyOrZero(v1)) {
			return new BigDecimal(0);
		}
		if (isEmptyOrZero(v2)) {
			return new BigDecimal(0);
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, precision, BigDecimal.ROUND_DOWN);
	}


	/**
	 * 除法  保留几位精度向下取整
	 *
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return String
	 */
	public static String divideToStr(String v1, String v2, int precision) {
		if (TextUtils.isEmpty(v1)) {
			return "0";
		}
        if (isEmptyOrZero(v2)) {
			return "0";
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, precision, BigDecimal.ROUND_DOWN).toPlainString();
	}


	public static String setScaleDown(String v1, int precision) {
		if (TextUtils.isEmpty(v1)) {
			return "0";
		}

		BigDecimal b1 = new BigDecimal(v1);
		return b1.setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();
	}

	public static String setScaleHalfUp(String v1, int precision) {
		if (TextUtils.isEmpty(v1)) {
			return "0";
		}

		BigDecimal b1 = new BigDecimal(v1);
		return b1.setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
	}


	public static String setScaleUp(String v1, int precision) {
		if (TextUtils.isEmpty(v1)) {
			return "0";
		}


		BigDecimal b1 = new BigDecimal(v1);
		return b1.setScale(precision, BigDecimal.ROUND_UP).toPlainString();

	}

	/**
	 * 除法  保留几位精度 四舍五入
	 *
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return String
	 */
	public static String divideToStrHalfUp(String v1, String v2, int precision) {
		if (TextUtils.isEmpty(v1)) {
			return "0";
		}
        if (isEmptyOrZero(v2)) {
			return "0";
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, precision, BigDecimal.ROUND_HALF_UP).toPlainString();
	}


	/**
	 * 除法  保留几位精度向上取整
	 *
	 * @param v1
	 * @param v2
	 * @param precision
	 * @return String
	 */
	public static String divideToStrUp(String v1, String v2, int precision) {
		if (TextUtils.isEmpty(v1)) {
			return "0";
		}
        if (isEmptyOrZero(v2)) {
			return "0";
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, precision, BigDecimal.ROUND_UP).toPlainString();
	}

	/**
	 * 两个BigDecal比较    v1是否小于 v2
	 */
	public static boolean isLessThan(String v1, String v2) {
		if (TextUtils.isEmpty(v1) || v1.equals("-")) {
			return false;
		}
		if (TextUtils.isEmpty(v2) || v2.equals("-")) {
			return false;
		}

		try {
			BigDecimal b1 = new BigDecimal(v1);
			BigDecimal b2 = new BigDecimal(v2);
			return b1.compareTo(b2) < 0;
		} catch (Exception e) {
			return false;
		}

	}


	/**
	 * 两个BigDecal比较    v1是否大于 v2
	 */
	public static boolean isGreaterThan(String v1, String v2) {
		if (TextUtils.isEmpty(v1)) {
			return false;
		}
		if (TextUtils.isEmpty(v2)) {
			return false;
		}
		try {
			BigDecimal b1 = new BigDecimal(v1);
			BigDecimal b2 = new BigDecimal(v2);
			return b1.compareTo(b2) > 0;
		} catch (Exception e) {
			return false;
		}

	}


	/**
	 * 是否等于空或者等于0
	 *
	 * @param string
	 * @return
	 */
	public static boolean isEmptyOrZero(String string) {
		if (TextUtils.isEmpty(string)) {
			return true;
		}

		BigDecimal bigDecimal = new BigDecimal(string);
		return bigDecimal.compareTo(BigDecimal.ZERO) == 0;
	}


	/**
	 * 是否小于0
	 */
	public static boolean lessThanToZero(String v1) {
		if (TextUtils.isEmpty(v1)) {
			return false;
		}

		BigDecimal b1 = new BigDecimal(v1);
		int i = b1.compareTo(BigDecimal.ZERO);
		return i < 0;
	}

	/**
	 * 判断第一个数字  是否大于等于第二个数字
	 */
	public static boolean isThanOrEqual(String v1, BigDecimal b2) {
		if (TextUtils.isEmpty(v1)) {
			return false;
		}
		try {
			BigDecimal b1 = new BigDecimal(v1);
			return b1.compareTo(b2) >= 0;
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * 跟0相比
	 */
	public static int compareZero(String v1) {
		if (TextUtils.isEmpty(v1)) {
			return 0;
		}

		try {
			BigDecimal b1 = new BigDecimal(v1);
			return b1.compareTo(BigDecimal.ZERO);
		} catch (Exception e) {
			return 0;
		}
	}


	/**
	 * 两个数相比
	 */
	public static int compare(String v1, String v2) {
		if (TextUtils.isEmpty(v1)) {
			v1 = "0";
		}
		if (TextUtils.isEmpty(v2)) {
			v2 = "0";
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.compareTo(b2);
	}


	/**
	 * 小数转成百分比   保留两位精度
	 */
	public static String toPercentage(String v1) {
		if (BigDecimalUtils.isEmptyOrZero(v1)) {
			return "0.00%";
		}
		DecimalFormat df = new DecimalFormat("0.00%");
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		return df.format(Double.valueOf(v1));
	}

	/**
	 * 小数转成百分比   保留n位精度
	 */
	public static String toPercentage(String v1, int n) {
		StringBuilder patternBuilder = new StringBuilder();
		patternBuilder.append("0.");
		while (n > 0) {
			patternBuilder.append("0");
			n--;
		}
		patternBuilder.append("%");
		if (BigDecimalUtils.isEmptyOrZero(v1)) {
			return patternBuilder.toString();
		}
		DecimalFormat df = new DecimalFormat(patternBuilder.toString());
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		return df.format(Double.valueOf(v1));
	}


	/**
	 * 小数转成百分比,第二个参数 传百分号和小数点几位0.00%
	 */
	public static String toPercentage(String v1, String pattern) {
		if (BigDecimalUtils.isEmptyOrZero(v1)) {
			return pattern;
		}
		DecimalFormat df = new DecimalFormat(pattern);
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		return df.format(Double.valueOf(v1));
	}


	/**
	 * 相除转百分比  不保留小数
	 *
	 * @param v1 分子
	 * @param v2 分母
	 * @return
	 */
	public static String divideToPercentage(String v1, String v2) {

		if (isEmptyOrZero(v1)) {
			return "0%";
		}
		if (isEmptyOrZero(v2)) {
			return "0%";
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);

		DecimalFormat df = new DecimalFormat("0%");
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

		return df.format(Double.valueOf(b1.divide(b2, 2, BigDecimal.ROUND_DOWN).doubleValue()));
	}


	/**
	 * long转str   保留精度
	 */
	public static String floatToString(float v1,int p){
		BigDecimal bigDecimal = new BigDecimal(v1);
		return bigDecimal.setScale(p,BigDecimal.ROUND_DOWN).toPlainString();
	}



}
