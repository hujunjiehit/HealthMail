package com.june.healthmail.untils;

import android.content.Context;

import com.june.healthmail.http.HttpManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by june on 2017/3/4.
 */

public class HttpUntils {

    private static HttpUntils instance;
    private OkHttpClient mOkHttpClient;
    private Context mContext;

    private HttpUntils(){
        mOkHttpClient = HttpManager.getInstance().getOkHttpClient();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public static HttpUntils getInstance(){
        if(instance == null){
            synchronized (HttpUntils.class){
                if(instance == null){
                    instance = new HttpUntils();
                }
            }
        }
        return instance;
    }

    public void postForm(String url, FormBody body, Callback callback){

        Request.Builder builder  = new Request.Builder().url(url).post(body);
        //builder.addHeader(key,value);  //将请求头以键值对形式添加，可添加多个请求头
        builder.addHeader("User-Agent","okhttp/2.5.0"); //必须
        builder.addHeader("appId","101"); //必须
        builder.addHeader("deviceId","android_" + CommonUntils.getLocalMacAddressFromIp(mContext)); //非必须
        builder.addHeader("deviceType","1"); //非必须
        builder.addHeader("timeStamp",String.valueOf(System.currentTimeMillis()));  //必须
        builder.addHeader("versionCode","67"); //非必须
        builder.addHeader("versionName","2.5.2.1"); //非必须
        Request request = builder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }
}
