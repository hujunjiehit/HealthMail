package com.june.healthmail.untils;

import android.content.SharedPreferences;
import android.nfc.tech.NfcA;

import com.june.healthmail.base.BasePerference;

/**
 * Created by june on 2017/3/4.
 */

public class PreferenceHelper extends BasePerference{

    public final static String PREFERENCE_KEY_PINGJIA_WORD = "pingjia_words";//评价语
    public final static String MAC_ADDRESS = "mac_address";//本机Mac地址

    public final static String KEY_MIN_PINGJIA_TIME = "min_pingjia_time";//本机Mac地址
    public final static String KEY_MAX_PINGJIA_TIME = "max_pingjia_time";//本机Mac地址
    public final static String KEY_MIN_YUEKE_TIME = "min_yeke_time";//本机Mac地址
    public final static String KEY_MAX_YUEKE_TIME = "max_yueke_time";//本机Mac地址
    public final static String KEY_MAX_SIJIAO = "max_sijiao";//本机Mac地址

    private static PreferenceHelper instance;

    public static PreferenceHelper getInstance() {
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
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
        int time = 800;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MIN_PINGJIA_TIME,800);
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
        int time = 1000;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MAX_PINGJIA_TIME,1000);
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
        int time = 800;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MIN_YUEKE_TIME,800);
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
        int time = 1000;
        checkPrefs();
        if (prefs != null) {
            time = prefs.getInt(KEY_MAX_YUEKE_TIME,1000);
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
}
