package com.coinbene.manbiwang.model.websocket;

import com.coinbene.manbiwang.model.http.BookOrderRes;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huyong
 */
public class OrderListMapsModel {
    public ConcurrentHashMap<String, String> buyMap;
    public ConcurrentHashMap<String, String> sellMap;
    public BookOrderRes.DataBean.QuoteBean quote;
    public OrderLineItemModel localPriceModel;

    public ConcurrentHashMap<String, String> getBuyMap() {
        return buyMap;
    }

    public void setBuyMap(ConcurrentHashMap<String, String> buyMap) {
        this.buyMap = buyMap;
    }

    public ConcurrentHashMap<String, String> getSellMap() {
        return sellMap;
    }

    public void setSellMap(ConcurrentHashMap<String, String> sellMap) {
        this.sellMap = sellMap;
    }

    public BookOrderRes.DataBean.QuoteBean getQuote() {
        return quote;
    }

    public void setQuote(BookOrderRes.DataBean.QuoteBean quote) {
        this.quote = quote;
    }

    public OrderLineItemModel getLocalPriceModel() {
        return localPriceModel;
    }

    public void setLocalPriceModel(OrderLineItemModel localPriceModel) {
        this.localPriceModel = localPriceModel;
    }
}
