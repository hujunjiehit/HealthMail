package com.coinbene.common.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

import com.coinbene.common.R;
import com.coinbene.common.context.CBRepository;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {


	/**
	 * 清除string所有空格
	 *
	 * @param str
	 * @return
	 */

	public static String replaceAllSpace(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		str = str.replaceAll(" ", "");
		return str;
	}


	/**
	 * 替换  ,  为  .
	 */
	public static String rePlaceDot(String inputStr) {
		if (TextUtils.isEmpty(inputStr)) {
			return "0";

		}
		if (!TextUtils.isEmpty(inputStr) && inputStr.contains(",")) {
			inputStr = inputStr.replace(",", ".");
		}
		return inputStr;
	}


	/**
	 * 邮箱显示前三后和@后面
	 *
	 * @return
	 */
	public static String settingEmail(String email) {
		if (TextUtils.isEmpty(email)) {
			return "";
		}
		String emails = email.replaceAll("(\\w{3})(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$4");
		return emails;
	}


	/**
	 * 手机号显示前三位后三号
	 *
	 * @return
	 */
	public static String settingPhone(String phone) {
		if (TextUtils.isEmpty(phone)) {
			return "";
		}
		String phones;
		if (phone.length() == 11) {
			phones = phone.replaceAll("(\\d{3})\\d{5}(\\d{3})", "$1****$2");
		} else {
			phones = phone;
		}
		return phones;
	}


	/**
	 * /**
	 * 转换为不带音调的拼音字符串
	 *
	 * @param pinYinStr 需转换的汉字
	 * @return 拼音字符串
	 */
	public static String changeToTonePinYin(String pinYinStr) {
		if (TextUtils.isEmpty(pinYinStr)) {
			return null;
		}
		String tempStr = null;
		try {
			tempStr = PinyinHelper.convertToPinyinString(pinYinStr, "", PinyinFormat.WITHOUT_TONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempStr;
	}


	/**
	 * 转换为每个汉字对应拼音首字母字符串
	 *
	 * @param pinYinStr 需转换的汉字
	 * @return 拼音字符串
	 */
	public static String changeToGetShortPinYin(String pinYinStr) {
		if (TextUtils.isEmpty(pinYinStr)) {
			return null;
		}
		String tempStr = null;
		try {
			tempStr = PinyinHelper.getShortPinyin(pinYinStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempStr;
	}


	/**
	 * 部分华为手机人民币符号错误  解决
	 *
	 * @param lastStr
	 * @return
	 */
	public static String getCnyReplace(String lastStr) {
		if (TextUtils.isEmpty(lastStr)) {
			return "";
		}
		if (lastStr.contains("￥")) {
			lastStr = lastStr.replace("￥", Html.fromHtml("&yen"));
		}
		return lastStr;
	}


	/**
	 * 半角字符转换为全角字符
	 *
	 * @param input
	 * @return
	 */
	public static String ToSBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}


	/**
	 * @Description 解决textview的问题---半角字符与全角字符混乱所致；这种情况一般就是汉字与数字、英文字母混用
	 * @param input String类型
	 * @return String 返回的String为半角（英文）类型
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i< c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}if (c[i]> 65280&& c[i]< 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}


	/**
	 *   * 去除特殊字符或将所有中文标号替换为英文标号
	 *   *
	 *   * @param str
	 *   * @return
	 *   
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	//复制
	public static void copyStrToClip(String string) {
		if (TextUtils.isEmpty(string)) {
			return;
		}
		ClipboardManager cm = (ClipboardManager) CBRepository.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData data = ClipData.newPlainText("Label", string);
		cm.setPrimaryClip(data);
		ToastUtil.show(CBRepository.getContext().getString(R.string.qr_copy_success));
	}
}
