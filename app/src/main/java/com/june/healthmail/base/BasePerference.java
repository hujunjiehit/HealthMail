package com.june.healthmail.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

@SuppressLint("NewApi")
public class BasePerference {

  protected static SharedPreferences prefs;
  protected Context context = null;
  protected boolean mUseApply;

  protected BasePerference() {
    mUseApply = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
  }

  public boolean isContextLive() {
    return context != null;
  }

  public void setContext(Context context) {
    this.context = context;
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  protected void checkPrefs() {
    if (prefs == null && context != null) {
      prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
  }

  public void save(String key, String value) {
    checkPrefs();
    if (prefs != null) {
      Editor editor = prefs.edit();
      editor.putString(key, value);
      if (mUseApply) {
        editor.apply();
      } else {
        editor.commit();
      }
    }
  }

  public void save(String key, int value) {
    checkPrefs();
    if (prefs != null) {
      Editor editor = prefs.edit();
      editor.putInt(key, value);
      if (mUseApply) {
        editor.apply();
      } else {
        editor.commit();
      }
    }
  }

  public void save(String key, long value) {
    checkPrefs();
    if (prefs != null) {
      Editor editor = prefs.edit();
      editor.putLong(key, value);
      if (mUseApply) {
        editor.apply();
      } else {
        editor.commit();
      }
    }
  }

  public void save(String key, boolean value) {
    checkPrefs();
    if (prefs != null) {
      Editor editor = prefs.edit();
      editor.putBoolean(key, value);
      if (mUseApply) {
        editor.apply();
      } else {
        editor.commit();
      }
    }
  }

  public int getInt(String key) {
    checkPrefs();
    if (prefs != null) {
      return prefs.getInt(key, 0);
    } else {
      return 0;
    }
  }

  public int getInt(String key, int defaultValue) {
    checkPrefs();
    if (prefs != null) {
      return prefs.getInt(key, defaultValue);
    } else {
      return defaultValue;
    }
  }

  public String getString(String key) {
    checkPrefs();
    if (prefs != null) {
      return prefs.getString(key, "");
    } else {
      return "";
    }
  }

  public boolean getBoolean(String key) {
    checkPrefs();
    if (prefs != null) {
      return prefs.getBoolean(key, false);
    } else {
      return false;
    }
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    checkPrefs();
    if (prefs != null) {
      return prefs.getBoolean(key, defaultValue);
    } else {
      return defaultValue;
    }
  }

  public void delete(String key) {
    checkPrefs();
    if (prefs != null) {
      Editor editor = prefs.edit();
      editor.remove(key);
      if (mUseApply) {
        editor.apply();
      } else {
        editor.commit();
      }
    }
  }

}
