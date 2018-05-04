package com.june.healthmail.ContentResolver;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by june on 2017/9/11.
 */

public class SMSContentObserver extends ContentObserver {

  private Context mContext; // 上下文
  private Handler mHandler; // 更新UI线程
  private String code; // 验证码

  public SMSContentObserver(Context context, Handler handler) { super(handler);
    mContext = context;
    mHandler = handler;
  }

  /*
  * 回调函数, 当所监听的Uri发生改变时，就会回调此方法
  * 注意当收到短信的时候会回调两次 *
  * @param selfChange
  *此值意义不大 一般情况下该回调值false */
  @Override
  public void onChange(boolean selfChange, Uri uri) {
    Log.e("test", uri.toString());
    // 第一次回调 不是我们想要的 直接返回
    if (uri.toString().equals("content://sms/raw")) {
      return;
    }

    // 第二次回调 查询收件箱里的内容
    Uri inboxUri = Uri.parse("content://sms/inbox");

    try {
      // 按时间顺序排序短信数据库
      Cursor c = mContext.getContentResolver().query(inboxUri, null, null, null, "date desc");
      if (c != null) {
        if (c.moveToFirst()) {

          final long smsdate = Long.parseLong(c.getString(c.getColumnIndex("date")));
          final long nowdate = System.currentTimeMillis();
          // 如果当前时间和短信时间间隔超过10秒,认为这条短信无效
          if (nowdate - smsdate > 10*1000) {
            return;
          }

          // 获取手机号
          String address = c.getString(c.getColumnIndex("address"));

          // 获取短信内容
          String body = c.getString(c.getColumnIndex("body"));

          Log.e("test","address:" + address);
          Log.e("test", "body:" + body);

          // 判断手机号是否为目标号码，服务号号码不固定请用正则表达式判断前几位。
          if (!body.contains("验证码")) {
            //不是验证码短信，直接返回
            return;
          }

          // 正则表达式截取短信中的6位验证码
          Pattern pattern = Pattern.compile("(\\d{6})");
          Matcher matcher = pattern.matcher(body);

          // 如果找到通过Handler发送给主线程
          if (matcher.find()) {
            code = matcher.group(0);
            //mHandler.obtainMessage(1, code).sendToTarget();
            Message msg = new Message();
            msg.what = 1;
            msg.obj = code;
            mHandler.sendMessageDelayed(msg, 3000);
          }
        }
      }
      c.close();
    }catch (Exception e) {
      Log.e("test", "SMSContentObserver exception : " + e.getMessage());
      e.printStackTrace();
      Toast.makeText(mContext,"权限异常，请开启相关权限",Toast.LENGTH_LONG).show();
    }
  }
}
