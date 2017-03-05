package com.june.healthmail.untils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

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
      Log.e("test","ip = " + ip);
      NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
      mac = ne.getHardwareAddress();
      mac_s = byte2hex(mac);
    } catch (Exception e) {
      e.printStackTrace();
    }
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < mac_s.length(); i++){
      sb.append(mac_s.charAt(i));
      if(i % 2 == 1 && i < mac_s.length() - 1){
        sb.append(":");
      }
    }
    Log.e("test","mac_s = " + sb.toString());
    return sb.toString();
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
}
