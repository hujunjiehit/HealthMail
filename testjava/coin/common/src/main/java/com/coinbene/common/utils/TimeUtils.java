package com.coinbene.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.coinbene.common.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间utils
 */
public class TimeUtils {

	private static final ThreadLocal<SimpleDateFormat> SDF_THREAD_LOCAL = new ThreadLocal<>();

	private static SimpleDateFormat sf = null;

	private static SimpleDateFormat getDefaultFormat() {
		return getDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	private static SimpleDateFormat getDateFormat(String pattern) {
		SimpleDateFormat simpleDateFormat = SDF_THREAD_LOCAL.get();
		if (simpleDateFormat == null) {
			simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
			SDF_THREAD_LOCAL.set(simpleDateFormat);
		} else {
			simpleDateFormat.applyPattern(pattern);
		}
		return simpleDateFormat;
	}

	public static String getStrFromDate(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(date);
	}

	/**
	 * 将毫秒转化成固定格式的时间
	 * 时间格式: yyyy-MM-dd HH:mm:ss
	 *
	 * @param millisecond
	 * @return
	 */
	public static String getDateTimeFromMillisecond(Long millisecond) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(millisecond);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}

	/**
	 * 将毫秒转化成固定格式的时间
	 * 时间格式: yyyy-MM-dd HH:mm
	 *
	 * @param millisecond
	 * @return
	 */
	public static String getYMDHMFromMillisecond(Long millisecond) {
		return getDateFormat("yyyy-MM-dd HH:mm").format(new Date(millisecond));
	}

	/**
	 * 将毫秒转化成固定格式的时间
	 * 时间格式: MM-dd HH:mm:ss
	 *
	 * @param millisecond
	 * @return
	 */
	public static String getMonthDayHMSFromMillisecond(Long millisecond) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
		Date date = new Date(millisecond);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}


	/**
	 * 时间的时分
	 *
	 * @param millisecond
	 * @return
	 */
	public static String getHourMl(Long millisecond) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date(millisecond);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}

	/**
	 * 时间的时分秒
	 *
	 * @param millisecond
	 * @return
	 */
	public static String getHourMlS(Long millisecond) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date(millisecond);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}

	/**
	 * 将时间转化成毫秒
	 * 时间格式: yyyy-MM-dd HH:mm:ss
	 *
	 * @param time
	 * @return
	 */
	public static Long timeStrToSecond(String time) {
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			return -1l;
		}
		return date.getTime();
	}


	/**
	 * 将秒转换为分秒
	 *
	 * @return
	 */
	public static String secondToMinSecond(Context context, int second) {

		if (second < 60) {
			return String.valueOf(second);
		} else {
			int minute = second / 60;
			int lastSecond = second - minute * 60;
			if (lastSecond < 10) {
				return minute + "分0" + lastSecond + context.getResources().getString(R.string.seconds);
			} else {
				return minute + "分" + lastSecond + "秒";
			}

		}

	}


	/**
	 * 得到月日时
	 */
	public static String getMDHMS(long second) {



		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
		Date date = new Date(second * 1000);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}

	/**
	 * 得到月日时
	 */
	public static String getMDHM(long second) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
		Date date = new Date(second * 1000);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}

	/**
	 * 得到年月日时
	 */
	public static String getYMD(long second) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(second * 1000);
		String dateStr = simpleDateFormat.format(date);
		return dateStr;
	}


	/**
	 * 将秒转换为时分秒
	 *
	 * @return
	 */
	public static String secondToHourMinSecond(long second) {

		String hh = String.valueOf(second / 60 / 60 % 60);
		if (hh.length() == 1) {
			hh = "0" + hh;
		}

		String mm = String.valueOf(second / 60 % 60);
		if (mm.length() == 1) {
			mm = "0" + mm;
		}
		String ss = String.valueOf(second % 60);
		if (ss.length() == 1) {
			ss = "0" + ss;
		}
		return hh + ":" + mm + ":" + ss;
	}


	public static String transferFormat(String inTime) {
		if (TextUtils.isEmpty(inTime)) {
			return "";
		} else {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat tosimpleDateFormat = new SimpleDateFormat("MM-dd");
			Date date = null;
			try {
				date = simpleDateFormat.parse(inTime);
			} catch (ParseException e) {


			}
			return tosimpleDateFormat.format(date);
		}
	}

	public static String moveTime(String date, int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String maxDateStr = date;
		String minDateStr;
		Calendar calc = Calendar.getInstance();
		try {
			calc.setTime(sdf.parse(maxDateStr));
			calc.add(Calendar.DATE, -day);
			Date minDate = calc.getTime();
			minDateStr = sdf.format(minDate);
			System.out.println("minDateStr:" + minDateStr);
			return minDateStr;
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return "";
	}

	/**
	 * 得到今天得年月日
	 *
	 * @return
	 */
	public static String getToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}


	public static String transferFormatMMSS(String inTime) throws ParseException {
		if (TextUtils.isEmpty(inTime)) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat tosimpleDateFormat = new SimpleDateFormat("HH:mm");
		Date date = simpleDateFormat.parse(inTime);
		return tosimpleDateFormat.format(date);
	}

	/**
	 * 转行成为时分秒
	 *
	 * @param inTime
	 * @return
	 * @throws ParseException
	 */
	public static String transferToHHmmssFormatMMSS(String inTime) throws ParseException {
		if (TextUtils.isEmpty(inTime)) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat tosimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = simpleDateFormat.parse(inTime);
		return tosimpleDateFormat.format(date);
	}


	/**
	 * 返回当前时间开始之前的N天秒
	 *
	 * @return
	 */
	public static long getOneDayBefore() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, -1);
		return date.getTimeInMillis() / 1000;
	}


	/**
	 * 返回当前时间开始之前的N天的秒
	 *
	 * @return
	 */
	public static long getNDayBefore(int n) {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, -n);
		return date.getTimeInMillis() / 1000;
	}


	/**
	 * 获取当前时区
	 * GMT+08:30-->转成8，GMT-16:20-->-16
	 *
	 * @return
	 */
	public static String getCurrentTimeZone() {
		TimeZone tz = TimeZone.getDefault();
		int zone = 8;
		String strTz = tz.getDisplayName(false, TimeZone.SHORT);
		if (strTz.contains("GMT")) {
			strTz = strTz.replace("GMT", "");
			if (strTz.contains(":")) {
				int index = strTz.indexOf(":");
				strTz = strTz.substring(0, index);
			}
			try {
				zone = Integer.valueOf(strTz);
			} catch (Exception ex) {
				zone = 8;
			}
		}
		return String.valueOf(zone);
	}


	/**
	 * 获取N个月以前的时间，毫秒的
	 *
	 * @param n
	 * @return
	 */
	public static long getN_MonthBefore(int n) {
		Calendar date = Calendar.getInstance();
		date.setTime(new Date());
		date.add(Calendar.MONTH, -n);
		return date.getTimeInMillis();
	}


	/**
	 * 获取N个周以前的时间，毫秒的
	 *
	 * @param n
	 * @return
	 */
	public static long getN_WeekBefore(int n) {
		Calendar date = Calendar.getInstance();
		date.setTime(new Date());
		date.add(Calendar.SECOND, -7 * 24 * 3600 * n);
		return date.getTimeInMillis();
	}


	/**
	 * 返回当前时间开始之前的N天的豪秒
	 *
	 * @return
	 */
	public static long getNDayBefore_new(int n) {
		Calendar date = Calendar.getInstance();
		date.setTime(new Date());
		date.add(Calendar.DATE, -n);
		return date.getTimeInMillis();
	}


	/**
	 * 返回当前时间开始之前的N小时的秒
	 *
	 * @return
	 */
	public static long getNHourBefore(int n) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY) - n);
		return date.getTimeInMillis() / 1000;
	}


	/**
	 * 判断当前时间是否在  两个时间内
	 *
	 * @param fromTime 起始时间
	 * @param toTime   结束时间
	 */

	public static boolean isBolongTime(String fromTime, String toTime) {
		return TimeUtils.timeStrToSecond(fromTime) <= System.currentTimeMillis()
				&& System.currentTimeMillis() <= TimeUtils.timeStrToSecond(toTime);
	}


	/**
	 * 判断是否在合约体验赛期间内
	 */

	public static boolean isContractMatch() {
		return TimeUtils.timeStrToSecond("2019-04-27 11:00:00") <= System.currentTimeMillis()
				&& System.currentTimeMillis() <= TimeUtils.timeStrToSecond("2019-05-06 11:00:00");
	}

	/**
	 * 获取倒计时 hh:mm:ss
	 *
	 * @param seconds
	 * @return
	 */
	public static String getCountDownHMS(long seconds) {
		StringBuilder stringBuilder = new StringBuilder();
		if (seconds / 3600 < 10) {
			stringBuilder.append("0");
		}
		stringBuilder.append(seconds / 3600);
		stringBuilder.append(":");

		seconds = seconds % 3600;

		if (seconds / 60 < 10) {
			stringBuilder.append("0");
		}
		stringBuilder.append(seconds / 60);
		stringBuilder.append(":");


		seconds = seconds % 60;

		if (seconds < 10) {
			stringBuilder.append("0");
		}
		stringBuilder.append(seconds);

		return stringBuilder.toString();
	}
}
