package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mxd on 2018/7/30.
 */

public class HistoryItemModel extends BaseRes {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        public String amount;
        public int direction;

        public String fee;
        public String price;
        public String quantity;
        public String tradeTime;
        public String quoteAsset;//分母
        public String feeByConi;//-1,不是coni；否则是coni，并且代表具体的值
    }
}
