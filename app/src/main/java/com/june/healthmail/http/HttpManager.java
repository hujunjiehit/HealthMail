package com.june.healthmail.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by june on 2017/10/28.
 */

public class HttpManager {

  public static HttpManager mInstance;

  private HttpLoggingInterceptor logging;
  private OkHttpClient mOkHttpClient;
  private Retrofit mRetrofit;

  private HttpManager(){
    //log 拦截器
    logging = new HttpLoggingInterceptor();

    //开发模式记录整个body，否则只记录基本信息，如返回200，http协议等
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

    //如果使用到https，需要创建SSLSocketFactory，并设置到client
    //SSLSocketFactory sslSocketFactory = null;

    mOkHttpClient = new OkHttpClient.Builder()
        //HeadInterceptor实现了Interceptor用来往Request Header添加一些业务相关数据，如APP版本，token信息
        //.addInterceptor(new HeadInterceptor())
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15,TimeUnit.SECONDS)
        .writeTimeout(15,TimeUnit.SECONDS)
        .build();

    mRetrofit = new Retrofit.Builder()
        .baseUrl(ApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .client(mOkHttpClient).build();
  }

  public static HttpManager getInstance() {
    if(mInstance == null) {
      synchronized (HttpManager.class) {
        if(mInstance == null) {
          mInstance = new HttpManager();
        }
      }
    }
    return mInstance;
  }

  public Retrofit getRetrofit() {
    return mRetrofit;
  }

  public OkHttpClient getOkHttpClient() {
    return mOkHttpClient;
  }
}
