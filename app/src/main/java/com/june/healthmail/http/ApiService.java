package com.june.healthmail.http;

import com.june.healthmail.http.bean.BaseBean;
import com.june.healthmail.http.bean.GetActivityConfigBean;
import com.june.healthmail.http.bean.GetConfigsBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by june on 2017/10/28.
 */

public interface ApiService {

    public static final String BASE_URL = "http://192.168.199.109:3389/";

    @GET("getPermission")
    public Call<BaseBean> getPermission(@Query("username") String username, @Query("userType") int userType, @Query("appVersion") int appVersion);

    @POST("{name}")
    public Call<BaseBean> getPayInfo(@Path("name") String name, @Body RequestBody body);


    @GET("getConfigs")
    public Call<GetConfigsBean> getConfigs();

    @GET("getActivityConfig")
    public Call<GetActivityConfigBean> getActivityConfig();
}
