package com.coinbene.common.balance;

import android.content.Context;

import androidx.annotation.NonNull;

import com.coinbene.common.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by june
 * on 2019-09-10
 *
 * 	划转账户，不同的id对应不同的账户
 *
 * 	1、资金账户 预留
 *  2、币币账户
 *  3、BTC合约账户
 *  4、USDT合约账户
 *  5、杠杆账户
 *  6、余币宝
 *  7、游乐场账户
 */
public class Product {

	public static final int TYPE_ACCOUNT = 1;  //资金账户， 预留		"account"

	public static final int TYPE_SPOT = 2;  //币币账户				"spot"
	public static final int TYPE_BTC_CONTRACT = 3;  //BTC合约账户    "btccontract"
	public static final int TYPE_USDT_CONTRACT = 4;  //USDT合约账户	"usdtcontract"
	public static final int TYPE_MARGIN = 5;  //杠杆账户				"margin"
	public static final int TYPE_FORTUNE = 6;  //财富账户			"fortune"
	public static final int TYPE_GAME = 7;  //游乐场账户				"game"
	public static final int TYPE_OPTIONS = 8;  //猜涨跌账户			"options"

	public static final String NAME_ACCOUNT = "account";
	public static final String NAME_SPOT = "spot";
	public static final String NAME_BTC_CONTRACT = "btccontract";
	public static final String NAME_USDT_CONTRACT = "usdtcontract";
	public static final String NAME_MARGIN = "margin";
	public static final String NAME_FORTUNE = "fortune";
	public static final String NAME_GAME = "game";
	public static final String NAME_OPTIONS = "options";

	public static final int STATUS_TRANSFER_OFF = 0;		//转入转出均不可用
	public static final int STATUS_TRANSFER_IN = 1;			//转入可用
	public static final int STATUS_TRANSFER_OUT = 2;		//转出可用
	public static final int STATUS_TRANSFER_ON = 3;			//转入转出均可用

	public static Map<Integer, String> nameMap;

	static {
		nameMap = new HashMap<>();
		nameMap.put(TYPE_ACCOUNT, NAME_ACCOUNT);
		nameMap.put(TYPE_SPOT, NAME_SPOT);
		nameMap.put(TYPE_BTC_CONTRACT, NAME_BTC_CONTRACT);
		nameMap.put(TYPE_USDT_CONTRACT, NAME_USDT_CONTRACT);
		nameMap.put(TYPE_MARGIN, NAME_MARGIN);
		nameMap.put(TYPE_FORTUNE, NAME_FORTUNE);
		nameMap.put(TYPE_GAME, NAME_GAME);
		nameMap.put(TYPE_OPTIONS, NAME_OPTIONS);
	}

	private int type;
	private String productName;
	private int transferStatus;  // 0-均不可用，1-转入可用，2-转出可用，3-均可用

	protected Product() {

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(int transferAvailable) {
		this.transferStatus = transferAvailable;
	}

	public String getName() {
		if (nameMap == null) {
			return "";
		}
		return nameMap.get(type) == null ? "" : nameMap.get(type);
	}


	//账户是否可以转出
	public boolean canTransferOut() {
		return transferStatus == STATUS_TRANSFER_ON || transferStatus == STATUS_TRANSFER_OUT;
	}

	//账户是否可以转入
	public boolean canTransferIn() {
		return transferStatus == STATUS_TRANSFER_ON || transferStatus == STATUS_TRANSFER_IN;
	}

	public String getDisableDesc(Context context) {
		if (transferStatus == STATUS_TRANSFER_IN) {
			return String.format("%s %s", productName, context.getResources().getString(R.string.res_disable_transfer_out));
		} else if (transferStatus == STATUS_TRANSFER_OUT) {
			return String.format("%s %s", productName, context.getResources().getString(R.string.res_disable_transfer_in));
		} else if (transferStatus == STATUS_TRANSFER_OFF) {
			return String.format("%s %s", productName, context.getResources().getString(R.string.res_disable_transfer));
		}
		return "";
	}

	public static class Builder {

		private int type;
		private String productName;
		private int transferStatus;

		public Product build(){
			Product product = new Product();
			product.setType(type);
			product.setProductName(productName);
			product.setTransferStatus(transferStatus);
			return product;
		}

		public Builder setType(int type){
			this.type = type;
			return this;
		}

		public Builder setProductName(String productName) {
			this.productName = productName;
			return this;
		}

		public Builder setTransferStatus(int status) {
			this.transferStatus = status;
			return this;
		}
	}

	@NonNull
	@Override
	public String toString() {
		return getProductName();
	}
}
