package com.coinbene.common.network.newokgo;

import android.text.TextUtils;

import com.coinbene.common.utils.DLog;
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
public class WsUrlReplaceIntercepter implements Interceptor {

	@Override
	public Response intercept(Chain chain) throws IOException {

		//获取request
		Request request = chain.request();

		DLog.d("WsUrlReplaceIntercepter", "====> before");

		if (SpUtil.getAppConfig() == null || SpUtil.getAppConfig().getUrl_config() == null || SpUtil.getAppConfig().getUrl_config().getWs_url() == null ||
				SpUtil.getAppConfig().getUrl_config().getWs_url().size() == 0 || TextUtils.isEmpty(SpUtil.getAppConfig().getUrl_config().getWs_url().get(0))) {
			DLog.d("WsUrlReplaceIntercepter", "====> 无可用的配置");
			return chain.proceed(request);
		}

		//需要处理WsUrl替换

		//从request中获取原有的HttpUrl实例oldHttpUrl
		HttpUrl oldHttpUrl = request.url();

		//获取AppConfig中配置的base_url
		HttpUrl newHttpUrl = HttpUrl.parse(SpUtil.getAppConfig().getUrl_config().getWs_url().get(0).replace("wss:", "https:"));

		DLog.d("WsUrlReplaceIntercepter", "oldHttpUrl ====> " + oldHttpUrl);
		DLog.d("WsUrlReplaceIntercepter", "newHttpUrl ====> " + newHttpUrl);

		if (oldHttpUrl.host().equals(newHttpUrl.host())) {
			//两个url一致，不需要替换
			DLog.d("WsUrlReplaceIntercepter", "====> no need replace ws_url");
			//两个url一致，不需要替换
			return chain.proceed(request);
		} else {
			//重建新的HttpUrl，修改需要修改的url部分
			HttpUrl replaceUrl = oldHttpUrl.newBuilder()
					.host(newHttpUrl.host())
					.build();

			DLog.d("WsUrlReplaceIntercepter", "replace ws url:" + oldHttpUrl + " ===> " + replaceUrl);

			return chain.proceed(request.newBuilder().url(replaceUrl).build());
		}

	}
}
