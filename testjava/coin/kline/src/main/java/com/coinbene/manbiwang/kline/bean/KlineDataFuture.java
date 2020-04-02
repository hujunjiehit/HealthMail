package com.coinbene.manbiwang.kline.bean;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by mxd on 2019/2/20.
 */

public class KlineDataFuture extends BaseRes {


    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        List<KlineData.DataBean> contract;
        List<KlineData.DataBean> mark;
    }

}
