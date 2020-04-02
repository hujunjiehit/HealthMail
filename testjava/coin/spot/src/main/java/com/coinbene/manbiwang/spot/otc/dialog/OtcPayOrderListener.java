package com.coinbene.manbiwang.spot.otc.dialog;

import com.coinbene.manbiwang.model.http.OtcAdListModel;

public interface OtcPayOrderListener {

    void payOrder(String adId,String usdtNum,int buyorsell);
    void onClickBuyOrSell(OtcAdListModel.DataBean.ListBean bean);

}
