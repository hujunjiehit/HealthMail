package com.coinbene.common.network.newokgo;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.NetUtil;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return response;
        }
        String url = request.url().toString();

        if (url.contains(".zendesk.com")) {
            //zendesk 返回的401不处理
            return response;
        }

        if (url.contains(Constants.REFRESH_TOKEN)) {
            gotoLogin();//避免死循环，多次刷新refresh_token
            return response;
        }
        UserInfoTable userInfoTable = UserInfoController.getInstance().getUserInfo();
        String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
        if (userInfoTable == null || TextUtils.isEmpty(userInfoTable.refreshToken)) {
            gotoLogin();
            return response;
        }
        Response responseToken = OkGo.post(Constants.USER_REFRESH_TOKEN).headers("site", SiteController.getInstance().getSiteName())
                .headers("clientData", NetUtil.getSystemParam().toString())
                .headers("lang", LanguageHelper.getProcessedCode(localeCode))
                .headers("timeZone", TimeUtils.getCurrentTimeZone())
                .headers("refresh-token", userInfoTable.refreshToken)
                .execute();
        String responseBody = responseToken.body().string();
        Gson gson = new Gson();
        UserInfoResponse baseRes = null;
        try {
            baseRes = gson.fromJson(responseBody, UserInfoResponse.class);
        } catch (Exception e) {
            gotoLogin();
            return response;
        }
        if (baseRes != null && baseRes.isSuccess() && baseRes.getData() != null) {
            UserInfoController.getInstance().updateUserInfo(baseRes.getData());
            userInfoTable = UserInfoController.getInstance().getUserInfo();
            String token = "";
            String bank = "";
            if (userInfoTable != null) {
                token = userInfoTable.token;
                bank = userInfoTable.bank;
            }
            Request newRequest = request
                    .newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .header("bank", bank)
                    .build();
            return chain.proceed(newRequest);
        } else {
            gotoLogin();
            return response;
        }

    }

    private void gotoLogin() {

        ServiceRepo.getUserService().logOut();

        if (CBRepository.getLifeCallback().getCurrentAcitivty().getClass().getName().contains("SplashActivity")) {
            return;
        }

        //token 失效跳转到登陆页面，如果用户不登陆，按返回键回到行情页面
        ARouter.getInstance().build(RouteHub.User.loginActivity)
                .withBoolean("forceQuit",true)
                .navigation(CBRepository.getLifeCallback().getCurrentAcitivty());
    }
}
