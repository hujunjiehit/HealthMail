package com.coinbene.manbiwang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Locale;

public class LanguageReceiver extends BroadcastReceiver {
    private static final String LAST_LANGUAGE = "lastLanguage";

    @Override
    public void onReceive(Context context, Intent intent) {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String localeStr = sp.getString(LAST_LANGUAGE, "");
        String curLocaleStr = getLocaleString(locale);
        if (TextUtils.isEmpty(localeStr)) {
            sp.edit().putString(LAST_LANGUAGE, curLocaleStr).commit();
            return;
        } else {
            if (localeStr.equals(curLocaleStr)) {
                return;
            } else {
                sp.edit().putString(LAST_LANGUAGE, curLocaleStr).commit();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
    }

    private static String getLocaleString(Locale locale) {
        if (locale == null) {
            return "";
        } else {
            return locale.getCountry() + locale.getLanguage();
        }
    }
}
