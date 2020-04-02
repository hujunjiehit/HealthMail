package com.coinbene.manbiwang.contract.contractusdt.presenter;

import android.app.Activity;

import com.coinbene.common.base.BasePresenter;
import com.coinbene.common.base.BaseView;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.manbiwang.model.http.ContractAccountInfoModel;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.model.http.CurrentDelegationModel;
import com.coinbene.manbiwang.model.http.OrderModel;

import java.util.List;

public interface ContractInterface {


    interface View extends BaseView<ContractInterface.Presenter> {
        void setPresenter(ContractInterface.Presenter presenter);

        void setOrderListData(List<OrderModel> buyList, List<OrderModel> sellList, WsMarketData tickerData, String contrackName);

        void setOrderDetail(List<WsTradeList> tradeDetailList, String contrackName);

        void setSettlement(long time);

        void setQuoData(WsMarketData tickerData);

        void setCurLeverData(int lever);

        void setContractAccountInfo(ContractAccountInfoModel.DataBean dataBean);

        void setPisitionListData(List<ContractPositionListModel.DataBean> listData);

        void setAllPisitionListData(List<ContractPositionListModel.DataBean> listData);

        void setCurrentOrderData(List<CurrentDelegationModel.DataBean.ListBean> listData, int total);

//        void setHistoryOrderData(List<HistoryDelegationModel.DataBean.ListBean> listData);

//        void setPlanOrderData(List<ContractPlanModel.DataBean.ListBean> listData);

        void placeOrderSucces();

        void setMarginMode(String mode,String modeSetting);
        void updateModeSuccess();

    }


    interface Presenter extends BasePresenter {

        void subAll();

        void unsubAll();

        void refreshAccountInfo();

        void setContractName(String contrackName);

        void setOrderType(int orderType);

        void getFundingTime();

        void getCurLeverage();

        void cancelOrder(String orderId, Activity activity);

        void getPositionlist();

        void getAllPositionList();

        void getContractAccountInfo();

        void getCurrentOrderList();
//
//        void getHistoryOrder();

//        void getPlanOrderList();


//        void cancelPlanOrder(String planId);

        void setPositionMode(String symbol,String mode);

        /**
         * @param currentTradeDirection 1;//买入开多2;//卖出开空 3;//买入平空4;//卖出平多
         * @param fixPriceType          0限价   1 市价
         * @param lever                 杠杆倍数
         * @param price                 限价价格
         * @param account               数量
         */
        void placeOrder(int currentTradeDirection, int fixPriceType,int curHighLeverEntrust, int lever, String price, String account, String mode, String contractName,String profitPrice,String lossPrice, Activity mActivity);

        void onResume();

        void onPause();

	    void openContract();

	    void agreeProtocol(String key,String value);
    }
}
