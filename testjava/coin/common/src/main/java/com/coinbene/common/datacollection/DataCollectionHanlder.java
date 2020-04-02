package com.coinbene.common.datacollection;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.HashMap;

public class DataCollectionHanlder {

    private long postTime = 10 * 60 * 1000;//10分钟

    public static String appInstall = "app_install";
    public static String appLogin = "app_login";
    public static String appUnlock = "app_unlock";
    public static String appForeground = "app_foreground";
    public static String appGroundFore = "app_groundfore";

    public static String appPlaceOrder = "app_place_order";
    public static String appCancelOrder = "app_cancel_order";

    public static String appScheme = "app_scheme";

    private Gson gson;
    private HashMap<String, Object> dataCecheMap = new HashMap<>();

    private volatile static DataCollectionHanlder dataCollectionHanlder;

    private DataCollectionHanlder(){

    }

    public static DataCollectionHanlder getInstance() {
        if (dataCollectionHanlder == null) {
            synchronized (DataCollectionHanlder.class) {
                if (dataCollectionHanlder == null) {
                    dataCollectionHanlder = new DataCollectionHanlder();
                }
            }
        }
        return dataCollectionHanlder;
    }

    public HashMap<String, Object> getClientData() {
        return dataCecheMap;
    }

    /**
     * 这个地方的value不能传 null    不然map里面的size就读不出来
     * <p>
     * 收集信息
     */

    public void putClientData(String key, Object value) {
        dataCecheMap.put(key, value);
        postClientInfo();
    }

    public synchronized void clearData() {
        dataCecheMap.clear();
    }

    /**
     * 上报client信息
     */
    public synchronized void postClientInfo() {

//        long lastPostTime = SpUtil.get(CBRepository.getContext(), SpUtil.POST_CLIENT_DATA_TIME, 0l);
        if (getClientData().isEmpty() || getClientData().size() == 0) {
            return;
        }
        if (gson == null) {
            gson = new Gson();
        }
        OkGo.<BaseRes>post(Constants.POST_CLIENT_INFO).params(
                "behaviourType", gson.toJson(getClientData()))
                .execute(new NewJsonSubCallBack<BaseRes>() {
                    @Override
                    public void onSuc(Response<BaseRes> response) {
//                        SpUtil.put(CBRepository.getContext(), SpUtil.POST_CLIENT_DATA_TIME, System.currentTimeMillis(), true);
                        clearData();
                    }

                    @Override
                    public void onE(Response<BaseRes> response) {
                    }

                });
    }


}
