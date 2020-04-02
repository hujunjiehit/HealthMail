package com.coinbene.manbiwang.spot.spot.impl;

import android.content.Context;

import com.coinbene.common.base.BasePresenter;
import com.coinbene.common.base.BaseView;
import com.coinbene.manbiwang.model.http.BookOrderRes;
import com.coinbene.manbiwang.model.http.CurOrderListModel;
import com.coinbene.manbiwang.model.http.KlineDealMedel;
import com.coinbene.manbiwang.model.http.OrderModel;

import java.util.List;

/**
 * @author by KG on 2017/8/2.
 */

public interface BookOrderInterface {

    interface View extends BaseView<Presenter> {
        void setPresenter(BookOrderInterface.Presenter presenter);

//        void setOrderBook(OrderListMapsModel entity, String tradePairId, boolean isHttp, boolean isFull);

        void getBalanceListResultSuccess(String asset, String totalBalance, String frozenBalance, String availableBalance);

        Context getMyContext();

        /**
         * 返回委托记录的历史
         */
        void entrustDataResponse(CurOrderListModel curOrderListModel);


        /**
         * 返回委托记录24小时
         */
        void entrustData24HourResponse(CurOrderListModel curOrderListModel);

        void getEntrustData24HourFail(String errorMsg);

        void setDealMedel(List<KlineDealMedel.DataBean> dealMedel, boolean isHttp);

        void setDealMedelError();

        void getEntrustDataFail(String errorMsg);

        void cancelEntrustSuccess(String tradeId);

        void buyOrsellResult(boolean isSuccess);

        void refreshData(List<OrderModel> orderListMapToList, List<OrderModel> orderListMapToList1, BookOrderRes.DataBean.QuoteBean quote, String symbol);
    }

    interface Presenter extends BasePresenter {
        void subAll();

        void unsubAll();

        void setCurrentAsset(String asset);


        void setPosition(int position);

        void setTradeMarketPosition(int position);

        int getTradeMarketPosition();
        /**
         * 获取挂单信息，返回10条买卖数据
         *
         * @param market
         */
        void startGetBookOrderLooper(String market,String id);

        /**
         * 暂停获取挂单信息
         */
        void stopGetBookOrderLooper(String id);

        void getBalanceOneRequest();

        /**
         * 获取委托单的记录，在记录页面使用
         *
         * @param page
         * @param pageSize
         */
        void getEntrustData(int page, int pageSize, boolean isHideOtherCoin);

        /**
         * 获取委托单的记录，在记录页面使用
         *
         * @param page
         * @param pageSize
         */
        void getEntrustHourData(int page, int pageSize, boolean isHideOtherCoin);


        void cancelEntrustData(String tradeId);

        void cancelEntrustAllData(String tradePair);

        void buyOrSell(int orderType,String tradePairName, String tradeType, String tradePairId, String cnt, String price);

        void setHideOtherCoin(boolean hideOtherCoin);


        /**
         * 在委托记录页面使用
         *
         * @param hasDone
         * @param page
         * @param pageSize
         */
        void getCurrentEntrustData(boolean hasDone, int page, int pageSize);

        /**
         * 在委托记录页面使用
         *
         * @param page
         * @param pageSize
         */
        @Deprecated
        void getHisEntrustData(int page, int pageSize);

        /**
         * 获取未完成的订单,循环获取，如果一旦返回失败，或者数据为null，则停止循环
         */
        void startGetEntrustLooper();

        /**
         * 取消获取未完成的订单
         */
        void cancelGetEntrustLooper();

        void setBottomPosition(int bottomPositon);

        void onResume();

        void onPause();
    }
}
