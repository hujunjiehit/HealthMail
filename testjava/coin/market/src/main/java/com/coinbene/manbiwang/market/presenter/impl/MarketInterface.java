package com.coinbene.manbiwang.market.presenter.impl;

import com.coinbene.common.base.BasePresenter;
import com.coinbene.common.base.BaseView;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.manbiwang.model.http.BannerList;
import com.coinbene.manbiwang.model.http.ZendeskArticlesResponse;

import java.util.List;

/**
 * @author huyong
 */

public interface MarketInterface {

    interface View extends BaseView<Presenter> {
        void setTabData(List<TradePairGroupTable> pairGroups);

        void setTabDataError();

        void refreshMarketDataError(String errorCode);

        void onGetBannerList(List<BannerList.DataBean> bannerList);

        void onGetZendeskArticleList(List<ZendeskArticlesResponse.ArticlesBean> articles);
    }

    interface Presenter extends BasePresenter {

        void getTradePair(boolean firstLoad);

        void getContractList();

        void getBannerList();

        void getZendeskArticleList();

        void getNewMarginSymbol();

        void getOptionalMarket(String id,String tradePairList,String sortField,String sortType);

        void getHttpSpotMarket(String id,String sortField,String sortType);
    }
}
