package com.coinbene.manbiwang.model.http;

import androidx.annotation.NonNull;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * ding
 * 2020-01-06
 * com.coinbene.manbiwang.model.http
 */
public class HomeMarketModel extends BaseRes {
	/**
	 * data : {"list":[{"tradePair":"ETHUSDT","baseAsset":"ETH","quoteAsset":"USDT","baseAssetName":"Ether"},{"tradePair":"LTCUSDT","baseAsset":"LTC","quoteAsset":"USDT","baseAssetName":"Litecoin"},{"tradePair":"CONIBTC","baseAsset":"CONI","quoteAsset":"BTC","baseAssetName":"Coni"}]}
	 */

	private DataBean data;

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		private List<ListBean> list;

		public List<ListBean> getList() {
			return list;
		}

		public void setList(List<ListBean> list) {
			this.list = list;
		}



		public static class ListBean {
			/**
			 * tradePair : ETHUSDT
			 * baseAsset : ETH
			 * quoteAsset : USDT
			 * baseAssetName : Ether
			 */

			private String tradePair;
			private String baseAsset;
			private String quoteAsset;
			private String baseAssetName;

			public String getTradePair() {
				return tradePair;
			}

			public void setTradePair(String tradePair) {
				this.tradePair = tradePair;
			}

			public String getBaseAsset() {
				return baseAsset;
			}

			public void setBaseAsset(String baseAsset) {
				this.baseAsset = baseAsset;
			}

			public String getQuoteAsset() {
				return quoteAsset;
			}

			public void setQuoteAsset(String quoteAsset) {
				this.quoteAsset = quoteAsset;
			}

			public String getBaseAssetName() {
				return baseAssetName;
			}

			public void setBaseAssetName(String baseAssetName) {
				this.baseAssetName = baseAssetName;
			}
		}
	}
}
