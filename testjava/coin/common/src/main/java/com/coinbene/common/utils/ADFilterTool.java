package com.coinbene.common.utils;

import android.text.TextUtils;

import com.coinbene.common.R;
import com.coinbene.common.context.CBRepository;

import java.util.regex.Pattern;

/**
 * Created by mengxiangdong on 2018/1/24.
 *
 * 广告过滤工具类
 */

public class ADFilterTool {
    /**
     * 正则表达式
     */
    private static String PATTERN = "";

    /**
     * 判断url的域名是否合法
     * <p>
     * 域名是否合法：自己项目中使用的域名，为合法域名；其它域名皆为不合法域名，进行屏蔽
     *
     * @param url
     * @return
     */
    public static boolean hasNotAd(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (TextUtils.isEmpty(PATTERN)) {
            PATTERN = getPatternStr();
        }
        return Pattern.matches(PATTERN, url);
    }

    /**
     * 拼接正则表达式
     *
     * @return
     */
    private static String getPatternStr() {
        String[] adUrls = CBRepository.getContext().getResources().getStringArray(R.array.normal_domain);
        if (null != adUrls && adUrls.length > 0) {
            StringBuffer sb = new StringBuffer("^(https|http)://.*(");
            for (String a : adUrls) {
                if (null != a && a.length() > 0) {
                    sb.append(a).append("|");
                }
            }
            return sb.substring(0, sb.length() - 1) + ").*";
        }
        return null;
    }

}
