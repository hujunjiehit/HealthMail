package com.coinbene.common.network.newokgo;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.okgo.JsonConvert;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.NetUtil;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.TimeUtils;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.base.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

public abstract class NewJsonCallback<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;

    public NewJsonCallback() {
    }

    public NewJsonCallback(Type type) {
        this.type = type;
    }

    public NewJsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {

    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);

    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 还可以在这里对所有的参数进行加密，均在这里实现
        if (!NetUtil.isNetworkAvailable()) {
//            ToastUtil.show(R.string.network_error);
            onFinish();
            return;
        }
        UserInfoTable table = UserInfoController.getInstance().getUserInfo();
        String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
        if (request.getUrl().contains(".zendesk.com")){
            //zendesk url 不传任何header

        }else if (table == null || TextUtils.isEmpty(table.token)) {
            request.headers("site", SiteController.getInstance().getSiteName())
                    .headers("clientData", NetUtil.getSystemParam().toString())
                    .headers("lang", LanguageHelper.getProcessedCode(localeCode))
                    .headers("timezone", TimeUtils.getCurrentTimeZone());
        } else {
            request.headers("site", SiteController.getInstance().getSiteName())
                    .headers("clientData", NetUtil.getSystemParam().toString())
//                    .headers("lang", CommonUtil.getLocal())
                    .headers("lang", LanguageHelper.getProcessedCode(localeCode))
                    .headers("timezone", TimeUtils.getCurrentTimeZone())
                    .headers("Authorization", "Bearer " + table.token)
                    .headers("userId", String.valueOf(table.userId))
                    .headers("bank", table.bank);

        }
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的是模板代码,实际使用根据需要修改
     *
     * 网络请求成功,不读取缓存 onStart -> convertResponse -> onSuccess -> onFinish<br>
     */
    @Override
    public T convertResponse(Response response) throws Exception {

        //详细自定义的原理和文档，看这里： https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback

        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }
        }

        JsonConvert<T> convert = new JsonConvert<>(type);
        T result = convert.convertResponse(response);
        return dealJSONConvertedResult(result);
    }

    public T dealJSONConvertedResult(T t) {
        return t;
    }
}
