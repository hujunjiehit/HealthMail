package com.coinbene.common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.EditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * 精度设置
 *
 * @author ding
 * 精度工具类
 */
@SuppressLint("SetTextI18n")
public class PrecisionUtils {

	private static String lastInput;


	/**
	 * 合约输入价格设置最小精度
	 * <p>
	 * 入股minPriceChange为null或者是包含5  说明是btc或者eth   默认设置为.5进度，否则直接设置输入精度即可
	 *
	 * @param precision      设置精度
	 * @param minPriceChange 最小交易价格
	 */
	public static void setPrecisionMinPriceChange(EditText editText, CharSequence s, int precision, String minPriceChange) {
		if (TextUtils.isEmpty(minPriceChange) || minPriceChange.contains("5")) {
			setPrecisionFive(editText, s, precision);
		} else {
			setPrecision(editText, s, precision);
		}
	}

	/**
	 * 设置固定精度的edittext
	 *
	 * @param editText
	 * @param s
	 * @param precision
	 */
	public static void setPrecision(EditText editText, CharSequence s, int precision) {

		if (s.toString().trim().equals(".") || s.toString().trim().startsWith(".")) {
			s = "0" + s;
			editText.setText(s);
			int selection = editText.getSelectionEnd();
			editText.setSelection(selection < s.length() ? selection : s.length());
		}

		if (s.toString().contains(".")) {
			if (s.length() - 1 - s.toString().indexOf(".") > precision) {
				s = s.toString().subSequence(0,
						s.toString().indexOf(".") + (precision + 1));
				editText.setText(s);
				editText.setSelection(s.length());

			}
		}

		if (s.toString().trim().equals(".")) {
			s = "0" + s;
			editText.setText(s);
			editText.setSelection(s.length());
		}

//		if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
//			if (!s.toString().substring(1, 2).equals(".")) {
//				editText.setText(s.subSequence(0, 1));
//				editText.setSelection(1);
//			}
//		}

	}


	/**
	 * 设置固定精度的edittext  并且以当前精度5进位   小数点后大于等于5则是5     小于5则是0
	 *
	 * @param editText
	 * @param s
	 * @param precision
	 */

	public static void setPrecisionFive(EditText editText, CharSequence s, int precision) {
		if (s.toString().trim().equals(".") || s.toString().trim().startsWith(".")) {
			s = "0" + s;
			editText.setText(s);
			editText.setSelection(s.length());
		}

		//最大限制输入 99999999
		if (!TextUtils.isEmpty(s.toString().trim())) {
			if (BigDecimalUtils.isGreaterThan(s.toString(), "99999999")) {
				editText.setText("99999999");
				editText.setSelection(editText.getText().toString().length());
				return;
			}
		}

		boolean isDelete = false;
		//删除的时候不需要做处理   避免下面的代码在删除的时候移动光标
		if (!TextUtils.isEmpty(lastInput) && !TextUtils.isEmpty(s.toString()) && lastInput.length() > s.toString().length()) {
			isDelete = true;
		}

		if (s.toString().contains(".")) {
			if (s.length() - 1 - s.toString().indexOf(".") > precision) {
				s = s.toString().subSequence(0, s.toString().indexOf(".") + (precision + 1));
				editText.setText(s);
				editText.setSelection(s.length());

			}
			if (!isDelete && !s.toString().substring(s.length() - 1).equals(".") && s.length() - 1 - s.toString().indexOf(".") >= precision) {
				if (Integer.valueOf(s.toString().substring(s.length() - 1)) >= 5) {
					s = s.toString().substring(0, s.toString().length() - 1) + "5";
				} else {
					s = s.toString().substring(0, s.toString().length() - 1) + "0";
				}
				int selection = editText.getSelectionEnd();
				editText.setText(s);
				editText.setSelection(selection < s.length() ? selection : s.length());
			}
		}


//		if (s.toString().startsWith("0")
//				&& s.toString().trim().length() > 1) {
//			if (!s.toString().substring(1, 2).equals(".")) {
//				editText.setText(s.subSequence(0, 1));
//				editText.setSelection(1);
//			}
//		}
		lastInput = s.toString();
	}


	/**
	 * 把int精度类型转换为  0.01   比如  2  --> 0.01
	 *
	 * @param count
	 * @return
	 */
	public static String changeNumToPrecisionStr(int count) {
		if (count <= 0) {
			return "0";
		}
		StringBuilder sb = new StringBuilder("0.");
		for (int i = 0; i < count; i++) {
			if (i == (count - 1)) {
				sb.append("1");
			} else
				sb.append("0");
		}
		return sb.toString();
	}


	/**
	 * 将 String =2 ,类型转化成 string 0.01
	 *
	 * @param num
	 * @return
	 */
	public static String changeNumToPrecisionStr(String num) {
		int numInt = 0;
		if (TextUtils.isEmpty(num)) {
			numInt = 0;
		} else {
			numInt = Integer.valueOf(num);
		}
		return changeNumToPrecisionStr(numInt);
	}


	/**
	 * 精度截取问题，保留小数点后几位，进行四舍五入
	 * http://blog.csdn.net/u012246458/article/details/52634175
	 *
	 * @param price
	 * @param pricePrecision
	 * @return
	 */
	public static String getPrecision(String price, String pricePrecision) {
		if (TextUtils.isEmpty(price)) {
			price = "0";
		}
		if (TextUtils.isEmpty(pricePrecision)) {
			return price;
		}
		if (pricePrecision.contains("1")) {
			pricePrecision = pricePrecision.substring(0, pricePrecision.length() - 1) + "0";
		}
		int newScale = 0;
		if (pricePrecision.contains(".")) {
			String subPrecision = pricePrecision.substring(pricePrecision.indexOf('.') + 1);
			if (!TextUtils.isEmpty(subPrecision)) {
				newScale = subPrecision.length();
			}
		}

		BigDecimal bd = new BigDecimal(price);
		DecimalFormat format = new DecimalFormat(pricePrecision);
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		return format.format(bd.setScale(newScale, BigDecimal.ROUND_HALF_UP));
//        return new DecimalFormat(pricePrecision).format(Float.parseFloat(price));
	}


	/**
	 * 得到精度string 长度
	 *
	 * @param pricePrecision
	 * @return
	 */
	public static String getPrecisionLength(String pricePrecision) {
		if (TextUtils.isEmpty(pricePrecision)) {
			pricePrecision = "0.00";
		}

		if (pricePrecision.contains("1")) {
			pricePrecision = pricePrecision.substring(0, pricePrecision.length() - 1) + "0";
		}
		return pricePrecision;
	}


	/**
	 * 向上取整，不进行四舍五入，直接把最后一位入上去
	 *
	 * @param price
	 * @param pricePrecision
	 * @return
	 */
	public static String getRoundUp(String price, String pricePrecision) {
		if (TextUtils.isEmpty(price)) {
			price = "0";
		}
		if (TextUtils.isEmpty(pricePrecision)) {
			return price;
		}
		if (pricePrecision.contains("1")) {
			pricePrecision = pricePrecision.substring(0, pricePrecision.length() - 1) + "0";
		}
		int newScale = 0;
		if (pricePrecision.contains(".")) {
			String subPrecision = pricePrecision.substring(pricePrecision.indexOf('.') + 1);
			if (!TextUtils.isEmpty(subPrecision)) {
				newScale = subPrecision.length();
			}
		}
		BigDecimal bd = new BigDecimal(price);
		DecimalFormat format = new DecimalFormat(pricePrecision);
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		return format.format(bd.setScale(newScale, BigDecimal.ROUND_UP));
	}


	/**
	 * 向下取整，不进行四舍五入，直接舍掉最后一位
	 *
	 * @param price
	 * @param pricePrecision
	 * @return
	 */
	public static String getRoundDown(String price, String pricePrecision) {
		if (TextUtils.isEmpty(price)) {
			price = "0";
		}
		if (TextUtils.isEmpty(pricePrecision)) {
			return price;
		}
		if (pricePrecision.contains("1")) {
			pricePrecision = pricePrecision.substring(0, pricePrecision.length() - 1) + "0";
		}
		int newScale = 0;
		if (pricePrecision.contains(".")) {
			String subPrecision = pricePrecision.substring(pricePrecision.indexOf('.') + 1);
			if (!TextUtils.isEmpty(subPrecision)) {
				newScale = subPrecision.length();
			}
		}
		BigDecimal bd = new BigDecimal(price);
		DecimalFormat format = new DecimalFormat(pricePrecision);
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		return format.format(bd.setScale(newScale, BigDecimal.ROUND_DOWN));
	}

	public static String getRoundDownPrecision(String price, int precision) {
		if (TextUtils.isEmpty(price)) {
			price = "0";
		}
		BigDecimal bd = new BigDecimal(price);
		return bd.setScale(precision, BigDecimal.ROUND_DOWN).toPlainString();
	}


	/**
	 * 中文的交易量缩进
	 *
	 * @param volume
	 * @return
	 */
	public static String getVolume(String volume, String cntPrecision) {
		if (TextUtils.isEmpty(volume) || volume.equals("null")) {
			return "0";
		}
		int volumeInt = (int) Float.parseFloat(volume);
		if (volumeInt == 0) {
			return "0";
		}
		String volumeIntStr = String.valueOf(volumeInt);//123.01用整数部分做比较
		if (volumeIntStr.length() > 8) {
			float yi = Float.valueOf(volume) / 100000000;
//            return String.format(Locale.ENGLISH, "%.01f亿", yi);
			return getDecimal1(yi) + "亿";
		} else if (volumeIntStr.length() > 7) {
			//123456789 123
			float yi = Float.valueOf(volume) / 10000000;
//            return String.format(Locale.ENGLISH, "%.01f千万", yi);
			return getDecimal1(yi) + "千万";
		} else if (volumeIntStr.length() > 4) {
			float yi = Float.valueOf(volume) / 10000;
//            return String.format(Locale.ENGLISH, "%.01f万", yi);
			return getDecimal1(yi) + "万";
		} else {
			return getPrecision(volume, cntPrecision);
		}
	}

	/**
	 * 小数点后一位,直接去掉，不进行四舍五入
	 *
	 * @param inputValue
	 * @return
	 */
	public static String getDecimal1(float inputValue) {
		String valueStr = String.valueOf(inputValue);//4.6通过下面会转成4.61
		if (valueStr.contains(".") && valueStr.indexOf(".") == (valueStr.length() - 2)) {
			valueStr = valueStr + "1";
			inputValue = Float.valueOf(valueStr);
		}
		DecimalFormat formater = new DecimalFormat("#0.00");
		formater.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		formater.setRoundingMode(RoundingMode.FLOOR);
		return formater.format(inputValue);
	}


	/**
	 * 英文的交易量缩进
	 *
	 * @param volume
	 * @return
	 */
	public static String getVolumeEn(String volume, String cntPrecision) {
		if (TextUtils.isEmpty(volume) || volume.equals("null")) {
			return "0";
		}
		int volumeInt = (int) Float.parseFloat(volume);
		if (volumeInt == 0) {
			return getPrecision(volume, cntPrecision);
		}
		String volumeIntStr = String.valueOf(volumeInt);//123.01用整数部分做比较

		if (volumeIntStr.length() > 9) {
			float yi = Float.valueOf(volume) / 1000000000;
//            String.format(Locale.ENGLISH)
			return getDecimal1(yi) + "b";
//            return String.format(Locale.ENGLISH, "%.01fb", yi);
		} else if (volumeIntStr.length() > 6) {
			float yi = Float.valueOf(volume) / 1000000;
//            return String.format(Locale.ENGLISH, "%.01fm", yi);
			return getDecimal1(yi) + "m";
		} else if (volumeIntStr.length() > 3) {
			float yi = Float.valueOf(volume) / 1000;
//            return String.format(Locale.ENGLISH, "%.01fk", yi);
			return getDecimal1(yi) + "k";
		} else {
			return getRoundDown(volume, cntPrecision);
		}
	}


	/**
	 * 英文的交易页面的价格数量缩进
	 *
	 * @param volume
	 * @return
	 */
	public static String getCntNum_En(String volume) {
		if (TextUtils.isEmpty(volume)) {
			return "0";
		}
		String volumeIntStr = "";
		if (volume.contains(".")) {
			volumeIntStr = volume.substring(0, volume.indexOf("."));
		} else {
			volumeIntStr = volume;
		}
		//123.01用整数部分做比较
		if (volumeIntStr.length() > 9) {
			float yi = Float.valueOf(volume) / 1000000000;
			return getDecimal1(yi) + "b";
//            return String.format("%.1fb", yi);
		} else if (volumeIntStr.length() > 6) {
			float yi = Float.valueOf(volume) / 1000000;
//            return String.format("%.1fm", yi);
			return getDecimal1(yi) + "m";
		} else if (volumeIntStr.length() > 3) {
			float yi = Float.valueOf(volume) / 1000;
//            return String.format("%.1fk", yi);
			return getDecimal1(yi) + "k";
		} else {
			return volume;
//            return getRoundDown(volume, cntPrecision);
		}
	}


	/**
	 * 英文的交易页面的价格数量缩进
	 *
	 * @param volume
	 * @return
	 */
	public static String getCntNumContractEn(String volume) {
		if (TextUtils.isEmpty(volume)) {
			return "0";
		}
		String newVol = "";
		if (volume.contains(".")) {
			newVol = volume.substring(0, volume.indexOf("."));
		} else {
			newVol = volume;

		}        //123.01用整数部分做比较
		if (newVol.length() > 9) {
			return BigDecimalUtils.divideBigDmal(newVol, "100000000", 2).toPlainString() + "b";
		} else if (newVol.length() > 6) {
			return BigDecimalUtils.divideBigDmal(newVol, "1000000", 2).toPlainString() + "m";
		} else if (newVol.length() > 3) {
			return BigDecimalUtils.divideBigDmal(newVol, "1000", 2).toPlainString() + "k";
		} else {
			return volume;
		}
	}


	/**
	 * 数量每三位增加,规则
	 *
	 * @param quantity
	 * @return
	 */
	public static String getQuantityContractRule(String quantity) {
		if (getStartStringLengthIndex(quantity) <= 3) {
			return quantity;
		}

		double data = Double.parseDouble(quantity);
		StringBuffer end = new StringBuffer();
		if (quantity.contains(".")) {
			for (int i = 0; i < quantity.substring(quantity.indexOf(".")).length() - 1; i++) {
				if (TextUtils.isEmpty(end)) {
					end.append(".");
				}
				end.append("0");
			}
			//当quantity以.结尾  上面的循环走不进去，需要手动拼一个
			if (TextUtils.isEmpty(end)) {
				end.append(".");
			}
		}
		DecimalFormat df = new DecimalFormat("#,##0" + end);
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
//		df.setGroupingUsed(false);
		return df.format(data);
	}

	/**
	 * 按照某个精度在数值后面拼接0
	 *
	 * @param string
	 * @param precision
	 * @return
	 */
	public static String appendZero(String string, int precision) {
		if (BigDecimalUtils.isEmptyOrZero(string)) {
			return "0";
		}
		BigDecimal bigDecimal = new BigDecimal(string).setScale(precision);
		return bigDecimal.toPlainString();

	}


	/**
	 * @param s
	 * @param pattern
	 * @return 按精度返回
	 */
	public static String getSingleRoundDown(String s, String pattern) {

		if (TextUtils.isEmpty(s)) {
			return "0";
		}

		DecimalFormat format = new DecimalFormat(pattern);
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		format.setRoundingMode(RoundingMode.DOWN);

		return format.format(new BigDecimal(s));
	}


	/**
	 * 得到小数点后面的长度
	 */

	public static int getStringLengthIndex(String s) {
		if (TextUtils.isEmpty(s) || !s.contains(".")) {
			return 0;
		}
		return s.substring(s.indexOf(".") + 1).length();

	}

	/**
	 * 得到小数点前面的长度
	 *
	 * @param s
	 * @return
	 */
	public static int getStartStringLengthIndex(String s) {
		if (TextUtils.isEmpty(s)) {
			return 0;
		}
		if (!s.contains(".")) {
			return s.length();
		}
		return s.substring(0, s.indexOf(".") + 1).length();

	}
}
