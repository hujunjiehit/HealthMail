package com.coinbene.manbiwang.contract.listener;

/**
 * 用于 viewgroup和 fragment 交互
 */
public interface CenterViewGroupListener {


    /**
     * 点击挂单
     */
    void clickOrderBook();

    /**
     * 点击成交
     */
    void clickOrderDetail();

    /**
     * 交易
     *
     * @param currentTradeDirection 1;//买入开多2;//卖出开空 3;//买入平空4;//卖出平多
     * @param fixPriceType          0限价   1 市价
     * @param lever                 杠杆倍数
     * @param price                 限价价格
     * @param account               数量
     */
    void clickPlaceOrder(int currentTradeDirection, int fixPriceType,int curHighLeverEntrust, int lever, String price, String account,String contractName,String profitPrice,String lossPrice);

	void agreeProtocol();
}
