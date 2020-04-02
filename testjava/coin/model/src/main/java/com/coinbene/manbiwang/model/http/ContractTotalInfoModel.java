package com.coinbene.manbiwang.model.http;

import android.text.TextUtils;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by june
 * on 2019-10-25
 */
public class ContractTotalInfoModel extends BaseRes {


	/**
	 * message : null
	 * timezone : null
	 * data : {"btcTotalPreestimate":"1.0000","localTotalPreestimate":"8033.60","currencySymbol":"$","currencyCode":"USD","accountInfo":{"symbol":"BTC","balance":"0.9999","frozenBalance":"0.0150","availableBalance":"0.9848","marginBalance":"1.0000","liquidationPrice":null,"positionMargin":"0.0150","unrealisedPnl":"0.0000","marginRate":"0.0150","marginMode":null,"active":1,"longQuantity":0,"shortQuantity":0,"roe":"0.0024"}}
	 */

	private DataBean data;

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * btcTotalPreestimate : 1.0000
		 * localTotalPreestimate : 8033.60
		 * currencySymbol : $
		 * currencyCode : USD
		 * accountInfo : {"symbol":"BTC","balance":"0.9999","frozenBalance":"0.0150","availableBalance":"0.9848","marginBalance":"1.0000","liquidationPrice":null,"positionMargin":"0.0150","unrealisedPnl":"0.0000","marginRate":"0.0150","marginMode":null,"active":1,"longQuantity":0,"shortQuantity":0,"roe":"0.0024"}
		 */

		private String btcTotalPreestimate;
		private String localTotalPreestimate;
		private String currencySymbol;
		private String currencyCode;
		private AccountInfoBean accountInfo;

		public String getBtcTotalPreestimate() {
			if (TextUtils.isEmpty(btcTotalPreestimate)) {
				return "--";
			}
			return btcTotalPreestimate;
		}

		public void setBtcTotalPreestimate(String btcTotalPreestimate) {
			this.btcTotalPreestimate = btcTotalPreestimate;
		}

		public String getLocalTotalPreestimate() {
			if (TextUtils.isEmpty(localTotalPreestimate)) {
				return "--";
			}
			return localTotalPreestimate;
		}

		public void setLocalTotalPreestimate(String localTotalPreestimate) {
			this.localTotalPreestimate = localTotalPreestimate;
		}

		public String getCurrencySymbol() {
			return currencySymbol;
		}

		public void setCurrencySymbol(String currencySymbol) {
			this.currencySymbol = currencySymbol;
		}

		public String getCurrencyCode() {
			return currencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}

		public AccountInfoBean getAccountInfo() {
			return accountInfo;
		}

		public void setAccountInfo(AccountInfoBean accountInfo) {
			this.accountInfo = accountInfo;
		}

		public static class AccountInfoBean {
			/**
			 * symbol : BTC
			 * balance : 0.9999
			 * frozenBalance : 0.0150
			 * availableBalance : 0.9848
			 * marginBalance : 1.0000
			 * liquidationPrice : null
			 * positionMargin : 0.0150
			 * unrealisedPnl : 0.0000
			 * marginRate : 0.0150
			 * marginMode : null
			 * active : 1
			 * longQuantity : 0
			 * shortQuantity : 0
			 * roe : 0.0024
			 */

			private String symbol;
			private String balance;
			private String frozenBalance;
			private String availableBalance;
			private String marginBalance;
			private Object liquidationPrice;
			private String positionMargin;
			private String unrealisedPnl;
			private String marginRate;
			private Object marginMode;
			private int active;
			private int longQuantity;
			private int shortQuantity;
			private String roe;

			public String getSymbol() {
				return symbol;
			}

			public void setSymbol(String symbol) {
				this.symbol = symbol;
			}

			public String getBalance() {
				if (TextUtils.isEmpty(balance)) {
					return "--";
				}
				return balance;
			}

			public void setBalance(String balance) {
				this.balance = balance;
			}

			public String getFrozenBalance() {
				return frozenBalance;
			}

			public void setFrozenBalance(String frozenBalance) {
				this.frozenBalance = frozenBalance;
			}

			public String getAvailableBalance() {
				if (TextUtils.isEmpty(availableBalance)) {
					return "--";
				}
				return availableBalance;
			}

			public void setAvailableBalance(String availableBalance) {
				this.availableBalance = availableBalance;
			}

			public String getMarginBalance() {
				if (TextUtils.isEmpty(marginBalance)) {
					return "--";
				}
				return marginBalance;
			}

			public void setMarginBalance(String marginBalance) {
				this.marginBalance = marginBalance;
			}

			public Object getLiquidationPrice() {
				return liquidationPrice;
			}

			public void setLiquidationPrice(Object liquidationPrice) {
				this.liquidationPrice = liquidationPrice;
			}

			public String getPositionMargin() {
				return positionMargin;
			}

			public void setPositionMargin(String positionMargin) {
				this.positionMargin = positionMargin;
			}

			public String getUnrealisedPnl() {
				if (TextUtils.isEmpty(unrealisedPnl)) {
					return "--";
				}
				return unrealisedPnl;
			}

			public void setUnrealisedPnl(String unrealisedPnl) {
				this.unrealisedPnl = unrealisedPnl;
			}

			public String getMarginRate() {
				return marginRate;
			}

			public void setMarginRate(String marginRate) {
				this.marginRate = marginRate;
			}

			public Object getMarginMode() {
				return marginMode;
			}

			public void setMarginMode(Object marginMode) {
				this.marginMode = marginMode;
			}

			public int getActive() {
				return active;
			}

			public void setActive(int active) {
				this.active = active;
			}

			public int getLongQuantity() {
				return longQuantity;
			}

			public void setLongQuantity(int longQuantity) {
				this.longQuantity = longQuantity;
			}

			public int getShortQuantity() {
				return shortQuantity;
			}

			public void setShortQuantity(int shortQuantity) {
				this.shortQuantity = shortQuantity;
			}

			public String getRoe() {
				return roe;
			}

			public void setRoe(String roe) {
				this.roe = roe;
			}
		}
	}
}
