package com.coinbene.common.network.newokgo;

import android.text.TextUtils;

import com.coinbene.common.utils.SpUtil;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by june
 * on 2020-02-29
 */
public class BaseUrlReplaceIntercepter implements Interceptor {

	private Boolean hasConfigUrl;

	@Override
	public Response intercept(Chain chain) throws IOException {

		//获取request
		Request request = chain.request();

		if (hasConfigUrl == null) {
			if (SpUtil.getAppConfig() == null || SpUtil.getAppConfig().getUrl_config() == null || SpUtil.getAppConfig().getUrl_config().getBase_url() == null ||
					SpUtil.getAppConfig().getUrl_config().getBase_url().size() == 0 || TextUtils.isEmpty(SpUtil.getAppConfig().getUrl_config().getBase_url().get(0))) {
				hasConfigUrl = false;
			} else {
				hasConfigUrl = true;
			}
		}

		if (!hasConfigUrl) {
			return chain.proceed(request);
		}

		if (!SpUtil.getCurrentBaseUrl().contains(request.url().host())) {
			//不是base_url的请求不需要处理
			return chain.proceed(request);
		}

		//需要处理BaseUrl替换

		//从request中获取原有的HttpUrl实例oldHttpUrl
		HttpUrl oldHttpUrl = request.url();

		//获取AppConfig中配置的base_url
		HttpUrl newHttpUrl = HttpUrl.parse(SpUtil.getAppConfig().getUrl_config().getBase_url().get(0));
		if (oldHttpUrl.host().equals(newHttpUrl.host()) && oldHttpUrl.scheme().equals(newHttpUrl.scheme())) {
			//两个url一致，不需要替换
			return chain.proceed(request);
		} else {
			//重建新的HttpUrl，修改需要修改的url部分
			HttpUrl replaceUrl = oldHttpUrl.newBuilder()
					.scheme(newHttpUrl.scheme())
					.host(newHttpUrl.host())
					.build();


			return chain.proceed(request.newBuilder().url(replaceUrl).build());
		}

	}
}
