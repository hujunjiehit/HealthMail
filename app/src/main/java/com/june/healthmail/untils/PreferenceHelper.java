package com.june.healthmail.untils;

import android.content.SharedPreferences;

import com.june.healthmail.base.BasePerference;

/**
 * Created by june on 2017/3/4.
 */

public class PreferenceHelper extends BasePerference{

    public final static String PREFERENCE_KEY_PINGJIA_WORD = "pingjia_words";//评价语

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
}
