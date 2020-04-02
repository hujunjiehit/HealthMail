package com.coinbene.manbiwang.model.websocket;


/**
 * websocket 订阅类型
 */
public enum ResponseType {

    /**
     * 现货  行情 挂单  成交
     */
    marketQuote, marketOrderList, marketTradeDetail,

    /**
     * 合约  挂单 行情  成交  用户  汇率
     */

    contractOrderBook, contractQuote, contractTradeDetail, contractUserEvent, contractExchangeRate


}
