package com.june.healthmail.untils;

import android.content.SharedPreferences;
import android.nfc.tech.NfcA;

import com.june.healthmail.Config.CommonConfig;
import com.june.healthmail.base.BasePerference;

/**
 * Created by june on 2017/3/4.
 */

public class PreferenceHelper extends BasePerference{

    public final static String PREFERENCE_KEY_PINGJIA_WORD = "pingjia_words";//评价语
    public final static String MAC_ADDRESS = "mac_address";//本机Mac地址

    public final static String KEY_MIN_PINGJIA_TIME = "min_pingjia_time";//最小评价时间
    public final static String KEY_MAX_PINGJIA_TIME = "max_pingjia_time";//最大评价时间
    public final static String KEY_MIN_YUEKE_TIME = "min_yeke_time";//最小约课时间
    public final static String KEY_MAX_YUEKE_TIME = "max_yueke_time";//最大约课时间
    public final static String KEY_MAX_SIJIAO = "max_sijiao";//最大约课私教数

    public final static String KEY_BUY_AUTH_URL = "bug_auth_url";//购买授权淘宝地址
    public final static String KEY_BUY_COINS_URL = "bug_coins_url";//购买金币淘宝地址
    public final static String KEY_UPDATE_LEVEL_URL = "update_level_url";//升级高级永久淘宝地址
    public final static String KEY_COINS_COST_FOR_POST = "coins_cost_for_post";//发帖金币消耗数量
    public final static String KEY_COINS_COST_FOR_POST_WITH_PICTURE = "coins_cost_for_post_with_picture";//发带图片的帖子金币消耗数量

    public final static String KEY_FREE_TIMES_A_DAY = "free_times_a_day";//每天免费的评价、约课次数
    public final static String KEY_REMAIN_YUEKE_TIMES = "remain_yueke_times";//剩余约课次数
    public final static String KEY_REMAIN_PINGJIA_TIMES = "remain_pingjia_times";//剩余评价次数

    public final static String KEY_UID = "uid";//存储的uid

    private static PreferenceHelper instance;

    public static PreferenceHelper getInstance() {
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    /**
     * 存储uid
     */
    public void saveUid(String uid) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_UID, uid);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    /**
     * 获取uid
     */
    public String getUid() {
        String word = null;
        checkPrefs();
        if (prefs != null) {
            word = prefs.getString(KEY_UID,"empty");
        }
        return word;
    }

    /**
     * 存储评价语
     */
    public void savePingjiaWord(String word) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFERENCE_KEY_PINGJIA_WORD, word);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    /**
     * 获取评价语
     */
    public String getPingjiaWord() {
        String word = null;
        checkPrefs();
        if (prefs != null) {
            word = prefs.getString(PREFERENCE_KEY_PINGJIA_WORD,"很好非常好");
        }
        return word;
    }

    /**
     * 存储mac_address
     */
    public void saveMacAddress(String macaddress) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(MAC_ADDRESS, macaddress);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    /**
     * 获取mac_address
     */
    public String getMacAddress() {
        String word = null;
        checkPrefs();
        if (prefs != null) {
            word = prefs.getString(MAC_ADDRESS,"");
        }
        return word;
    }

    public void setMinPingjiaTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MIN_PINGJIA_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMinPingjiaTime() {
        int time = CommonConfig.MinDelayTime;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MIN_PINGJIA_TIME,time);
        }
        if(time < CommonConfig.MinDelayTime) {
            time = CommonConfig.MinDelayTime;
            setMinPingjiaTime(time);
        }
        return time;
    }

    public void setMaxPingjiaTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MAX_PINGJIA_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMaxPingjiaTime() {
        int time = CommonConfig.MinDelayTime + 800;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MAX_PINGJIA_TIME,time);
        }
        if(time < CommonConfig.MinDelayTime) {
            time = CommonConfig.MinDelayTime;
            setMaxPingjiaTime(time);
        }
        return time;
    }

    public void setMinYuekeTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MIN_YUEKE_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMinYuekeTime() {
        int time = CommonConfig.MinDelayTime;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MIN_YUEKE_TIME,time);
        }
        if(time < CommonConfig.MinDelayTime) {
            time = CommonConfig.MinDelayTime;
            setMinYuekeTime(time);
        }
        return time;
    }

    public void setMaxYuekeTime(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MAX_YUEKE_TIME, time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMaxYuekeTime() {
        int time = CommonConfig.MinDelayTime + 800;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MAX_YUEKE_TIME,time);
        }
        if(time < CommonConfig.MinDelayTime) {
            time = CommonConfig.MinDelayTime;
            setMaxYuekeTime(time);
        }
        return time;
    }

    public void setMaxSijiao(int time) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_MAX_SIJIAO,time);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }

    }

    public int getMaxSijiao() {
        int value = 20;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_MAX_SIJIAO,20);
        }
        return value;
    }

    public void setBuyAuthUrl(String url) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_BUY_AUTH_URL,url);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getBuyAuthUrl() {
        String url = "https://www.baidu.com/";
        checkPrefs();
        if (prefs != null) {
            url = prefs.getString(KEY_BUY_AUTH_URL,url);
        }
        return url;
    }

    public void setBuyConisUrl(String url) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_BUY_COINS_URL,url);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getBuyCoinsUrl() {
        String url = "https://www.baidu.com/";
        checkPrefs();
        if (prefs != null) {
            url = prefs.getString(KEY_BUY_COINS_URL,url);
        }
        return url;
    }

    public void setUpdateLevelUrl(String url) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_UPDATE_LEVEL_URL,url);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public String getUpdateLevelUrl() {
        String url = "https://www.baidu.com/";
        checkPrefs();
        if (prefs != null) {
            url = prefs.getString(KEY_UPDATE_LEVEL_URL,url);
        }
        return url;
    }

    public void setCoinsCostForPost(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_COINS_COST_FOR_POST,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getCoinsCostForPost() {
        int value = 1;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_COINS_COST_FOR_POST,value);
        }
        return value;
    }

    public void setCoinsCostForPostWithPicture(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_COINS_COST_FOR_POST_WITH_PICTURE,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getCoinsCostForPostWithPicture() {
        int value = 2;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_COINS_COST_FOR_POST_WITH_PICTURE,value);
        }
        return value;
    }

    public void setFreeTimesPerday(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_FREE_TIMES_A_DAY,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getFreeTimesPerday() {
        int value = 500;
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_FREE_TIMES_A_DAY,value);
        }
        return value;
    }

    public void setRemainYuekeTimes(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_REMAIN_YUEKE_TIMES,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getRemainYuekeTimes() {
        int value = getFreeTimesPerday();
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_REMAIN_YUEKE_TIMES,value);
        }
        return value;
    }

    public void setRemainPingjiaTimes(int cost) {
        checkPrefs();
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_REMAIN_PINGJIA_TIMES,cost);
            if (mUseApply) {
                editor.apply();
            } else {
                editor.commit();
            }
        }
    }

    public int getRemainPingjiaTimes() {
        int value = getFreeTimesPerday();
        checkPrefs();
        if (prefs != null) {
            value = prefs.getInt(KEY_REMAIN_PINGJIA_TIMES,value);
        }
        return value;
    }
}
