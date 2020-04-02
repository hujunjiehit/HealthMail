package com.coinbene.common.model.websocket;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.AppUtil;

public class WsBaseRequest {
    public static String SUB = "sub";
    public static String UN_SUB = "unsub";
    //market
    public static String TOPIK_MARKET = "market.quote";
    public static String TOPIK_TRADE_DETAIL = "market.tradedetail.";
    public static String TOPIK_MARKET_ORDERLIST = "market.orderlist.";
    //orderlistnumber
    public static String TOPIK_MARKET_ORDERLIST_NUMBER = ".14";

    //trededetail number
    public static String TOPIK_TRADE_DETAIL_NUMBER = ".50";

    //切换站点或者语音的订阅
    public static String CHANGE_SITE_DATA = "changeSiteData";
    public static String WEBSOCKET_CONNECT_SUCC_OP = "challenge";
    public static String RID = AppUtil.getAndroidID(CBRepository.getContext());

    //合约market
    public static String CONTRACT_QOUTE = "quote";

    //合约OrderList
    public static String CONTRACT_ORDERBOOK = "orderBook.";

    //合约User
    public static String CONTRACT_USEREVENT = "userEvent";

    //合约User
    public static String EVENTS = "events";


    //合约OrderDetail
    public static String CONTRACT_TRADEDETAIL = "tradeList.";

    //合约当前币种与法币的汇率
    public static String CONTRACT_EXCHANGE_RATE = "exchangeRate";

    //订阅user之前需要先发送clientdata给服务器  实际是userid
    public static String USER_CLIENT_DATA = "setClientData";


    //合约tradeDetail

    private String op;
    private String rid;
    private String topic;
    private String site;
    private String lang;
    private String token;
    private String authorization;

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public WsBaseRequest(String op, String rid, String topic) {
        this.op = op;
        this.rid = rid;
        this.topic = topic;
    }

    public WsBaseRequest(String op, String rid) {
        this.op = op;
        this.rid = rid;
    }

    public WsBaseRequest(String op, String rid, String site, String lang) {
        this.op = op;
        this.rid = rid;
        this.site = site;
        this.lang = lang;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getOp() {
        return op;
    }

    public String getRid() {
        return rid;
    }

    public String getTopic() {
        return topic;
    }

    public String getSite() {
        return site;
    }

    public String getLang() {
        return lang;
    }

    public String getToken() {
        return token;
    }

    public String getAuthorization() {
        return authorization;
    }
}
