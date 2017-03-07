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
}
