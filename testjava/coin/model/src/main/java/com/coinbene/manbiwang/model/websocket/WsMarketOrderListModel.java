package com.coinbene.manbiwang.model.websocket;

import com.coinbene.manbiwang.model.http.BookOrderRes;

/**
 * @author huyong
 */
public class WsMarketOrderListModel extends WsBaseResponse {

    public BookOrderRes.DataBean data;



    public BookOrderRes.DataBean getData() {
        return data;
    }

}
