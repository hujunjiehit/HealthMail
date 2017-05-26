package com.june.healthmail.untils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.june.healthmail.model.UserInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by bjhujunjie on 2017/3/3.
 */

public class CommonUntils {

  public static String getUserAgent(Context context) {
    String userAgent = "";
    userAgent = System.getProperty("http.agent");
    StringBuffer sb = new StringBuffer();
    for (int i = 0, length = userAgent.length(); i < length; i++) {
      char c = userAgent.charAt(i);
      if (c <= '\u001f' || c >= '\u007f') {
        sb.append(String.format("\\u%04x", (int) c));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public static String getLocalMacAddressFromIp(Context context) {
    String mac_s= "";
    try {
      byte[] mac;
      String ip = getLocalIpAddress();
      NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
      mac = ne.getHardwareAddress();
      if(mac != null){
        mac_s = byte2hex(mac);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if(mac_s != null && !TextUtils.isEmpty(mac_s)){
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < mac_s.length(); i++){
        sb.append(mac_s.charAt(i));
        if(i % 2 == 1 && i < mac_s.length() - 1){
          sb.append(":");
        }
      }
      mac_s =  sb.toString();
      PreferenceHelper.getInstance().saveMacAddress(mac_s);
    }else {
      //数据流量的时候,需要从sharepreference获取mac地址
      mac_s = PreferenceHelper.getInstance().getMacAddress();
    }
    return mac_s;
  }

  public static  String byte2hex(byte[] b) {
    StringBuffer hs = new StringBuffer(b.length);
    String stmp = "";
    int len = b.length;
    for (int n = 0; n < len; n++) {
      stmp = Integer.toHexString(b[n] & 0xFF);
      if (stmp.length() == 1)
        hs = hs.append("0").append(stmp);
      else {
        hs = hs.append(stmp);
      }
    }
    return String.valueOf(hs);
  }

  public static String getLocalIpAddress() {
    try {
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf
            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
            return inetAddress.getHostAddress().toString();
          }
        }
      }
    } catch (SocketException ex) {
      Log.e("test", ex.toString());
    }
    return null;
  }

  public static String md5(String string) {
    if (TextUtils.isEmpty(string)) {
      return "";
    }
    MessageDigest md5 = null;
    try {
      md5 = MessageDigest.getInstance("MD5");
      byte[] bytes = md5.digest(string.getBytes());
      String result = "";
      for (byte b : bytes) {
        String temp = Integer.toHexString(b & 0xff);
        if (temp.length() == 1) {
          temp = "0" + temp;
        }
        result += temp;
      }
      return result;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static int getRandomInt(int min, int max){
    int result;
    Random random = new Random();
    result = random.nextInt(max - min + 1) + min;
    return result;
  }

  /**
   * 获取版本号
   * @return 当前应用的版本号
   */
  public static String getVersion(Context mContext) {
    try {
      PackageManager manager = mContext.getPackageManager();
      PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
      String version = info.versionName;
      return version;
    } catch (Exception e) {
      e.printStackTrace();
      return "未找到当前版本信息";
    }
  }

  /**
   * 获取版本号
   * @return 当前应用的版本号
   */
  public static int getVersionInt(Context mContext) {
    int result = 0;
    try {
      PackageManager manager = mContext.getPackageManager();
      PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
      String version = info.versionName;
      String[] array = version.split("\\.");
      if(array.length > 0){
        for(String s:array){
          result = result*10 + Integer.parseInt(s);
        }
      }
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * 检查该包名下的应用是否存在
   */
  public static boolean checkPackage(Context context ,String packageName){
    if (packageName == null || "".equals(packageName)){
      return false;
    }
    try{
      context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
      return true;
    }catch (PackageManager.NameNotFoundException e){
      return false;
    }
  }

  public static boolean hasPermission() {
    UserInfo userInfo = BmobUser.getCurrentUser(UserInfo.class);
    if(userInfo.getUserType() == 3 || userInfo.getUserType() == 1 || userInfo.getUserType() == 2 || userInfo.getUserType() >= 99){
      return true;
    }else {
      return false;
    }
  }

  public static void minusPingjiaTimes() {
    PreferenceHelper mHelper = PreferenceHelper.getInstance();
    mHelper.setRemainPingjiaTimes(mHelper.getRemainPingjiaTimes() - 1);
  }

  public static void minusYuekeTimes() {
    PreferenceHelper mHelper = PreferenceHelper.getInstance();
    mHelper.setRemainYuekeTimes(mHelper.getRemainYuekeTimes() - 1);
  }


  public static void update(String msgTime, ArrayList<Double> values) {

  }

  public static boolean isPayUser(UserInfo userInfo) {
    if(userInfo.getPayStatus() == null) {
      return false;
    } else {
      if(userInfo.getPayStatus() > 0) {
        return true;
      }
    }
    return false;
  }
}
