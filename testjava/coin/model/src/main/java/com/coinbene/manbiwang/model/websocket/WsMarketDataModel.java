package com.coinbene.manbiwang.model.websocket;

import com.coinbene.manbiwang.model.http.TradePairMarketRes;

import java.util.List;

/**
 * @author huyong
 */
public class WsMarketDataModel extends WsBaseResponse {
    private List<TradePairMarketRes.DataBean> data;

    public List<TradePairMarketRes.DataBean> getData() {
        return data;
    }

    public void setData(List<TradePairMarketRes.DataBean> data) {
        this.data = data;
    }
}
