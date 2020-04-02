package com.coinbene.common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckMatcherUtils {


    /**
     * 是否是真实的身份证号
     * @param idNo
     * @return
     */
    public static boolean isIdNoPattern(String idNo) {
        return Pattern.matches("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$", idNo);
    }
    /**
     * 检查密码是否符合规则
     *
     * @param password
     * @return
     */
    public static boolean checkSixDigits(String password) {

        if (password.length() < 6 || password.length() > 8) {
            return false;
        }

        Pattern p = Pattern.compile("^[0-9]*$");

        Matcher matcher = p.matcher(password);

        return matcher.matches();
    }


    /**
     * 判断str中是否有汉字
     *
     * @param str
     * @return true: 包含中文字符；false：不包含中文字符
     */
    public static boolean checkChiness(String str) {
        String reg = "^*[\\u4E00-\\u9FA5]+";
        Pattern pat = Pattern.compile(reg);
        return pat.matcher(str).find();
    }


    /**
     * 6-20位,数字+字母+特殊字符
     *
     * @param pwd
     * @return
     */
    public static boolean checkPwd6_20(String pwd) {
        boolean containChinese = checkChiness(pwd);
        if (!containChinese) {
//            Pattern p = Pattern.compile("^([a-zA-Z0-9\\.\\=\\`\\~\\-\\_\\+\\[\\]\\{\\}\\;\\:\\'\\\"\\,\\<\\>\\/\\?\\@\\!\\#\\$\\%\\^\\&\\*\\(\\)]){5,20}$");
            Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\.\\=\\`\\~\\-\\_\\+\\[\\]\\{\\}\\;\\:\\'\\\"\\,\\<\\>\\/\\?\\@\\!\\#\\$\\%\\^\\&\\*\\(\\)]{6,20}$");

//            Pattern p = Pattern.compile("(?!.*\\\\s)(?!^[\\u4E00-\\u9FA5]+$)(?!^[a-zA-Z]+$)(?!^[\\\\d]+$)(?!^[^\\u4E00-\\u9FA5a-zA-Z\\\\d]+$)^.{6,20}$");
            Matcher m = p.matcher(pwd);
            return m.matches();
        } else {
            return false;
        }
    }

    // 检查邮箱正确性
    public static boolean checkEmail(String email) {
        boolean containChinese = checkChiness(email);
        if (!containChinese) {
            Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
            Matcher m = p.matcher(email);
            return m.matches();
        } else {
            return false;
        }
    }


    /**
     * @param password
     * @return true：符合；false：不符合
     */
    public static boolean checkPassword(String password) {

        if (password.length() < 6 || password.length() > 20) {
            return false;
        }

        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\.\\=\\`\\~\\-\\_\\+\\[\\]\\{\\}\\;\\:\\'\\\"\\,\\<\\>\\/\\?\\@\\!\\#\\$\\%\\^\\&\\*\\(\\)]{6,20}$");

        Matcher matcher = p.matcher(password);

        return matcher.matches();
    }


    /**
     * 判断是否是手机号
     * @param number
     * @return
     */
    public static boolean checkPhoneNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        Pattern numPattern = Pattern.compile("^\\d{6,20}$");
        Matcher numMatcher = numPattern.matcher(number);
        return numMatcher.matches();
    }




    /**
     * 移动号段正则表达式
     */
    private static final String yidongPhone = "^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}|(1705)\\d{7}$";
    /**
     * 联通号段正则表达式
     */
    private static final String liantongPhone = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}|(1709)\\d{7}$";
    /**
     * 电信号段正则表达式
     */
    private static final String dianxinPhone = "^((133)|(153)|(177)|(173)|(149)|(18[0,1,9]))\\d{8}$";

    /**
     * 马来西亚 111 112,113信号段
     */
    private static final String malaiPhone11 = "^((111)|(112)|(113))\\d{7}$";
    /**
     * 马来西亚 10 11 12 13 14 16 17  19 信号段
     */
    private static final String malaiPhone10 = "^((10)|(11)|(12)|(13)|(14)|(16)|(17)|(19))\\d{7}$";
    /**
     * 检查中国的手机号
     *
     * @param number
     * @return
     */
    public static boolean checkPhoneNum_china(String number) {
        Pattern p = Pattern.compile(yidongPhone);
        Matcher yidongM = p.matcher(number);

        Pattern liantongp = Pattern.compile(liantongPhone);
        Matcher liantongM = liantongp.matcher(number);

        Pattern dianxingp = Pattern.compile(dianxinPhone);
        Matcher dianxingM = dianxingp.matcher(number);

        return yidongM.matches() || liantongM.matches() || dianxingM.matches();
    }

    /**
     * 检查马来西亚的手机号
     *
     * @param number
     * @return
     */
    public static boolean checkPhoneNum_malai(String number) {
        Pattern malaip = Pattern.compile(malaiPhone11);
        Matcher malaiM = malaip.matcher(number);

        Pattern malaip10 = Pattern.compile(malaiPhone10);
        Matcher malaiM10 = malaip10.matcher(number);
        return malaiM.matches() || malaiM10.matches();
    }
}
