package com.coinbene.common.balance;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.StringRes;

import com.coinbene.common.R;
import com.coinbene.manbiwang.model.http.TransferInfoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by june
 * on 2019-09-10
 */
public class TransferUtils {

	public static void initTransferProductMap(Context context, SparseArray<Product> mProductMap, List<TransferInfoModel.DataBean.ProductsBean> products) {
		if (products == null || products.size() == 0) {
			return;
		}
		for(TransferInfoModel.DataBean.ProductsBean product : products) {
			switch (product.getId()) {
				case Product.TYPE_ACCOUNT:
					//资金账户 -- 预留
					mProductMap.put(product.getId(),
							new Product.Builder()
									.setType(product.getId())
									.setProductName(getString(context, R.string.res_balance_account))
									.setTransferStatus(product.getTransferAvailable())
									.build());
					break;
				case Product.TYPE_SPOT:
					//币币账户
					mProductMap.put(product.getId(),
							new Product.Builder()
									.setType(product.getId())
									.setProductName(getString(context, R.string.balance_coin_str))
									.setTransferStatus(product.getTransferAvailable())
									.build());
					break;
				case Product.TYPE_BTC_CONTRACT:
					//BTC合约账户
					mProductMap.put(product.getId(),
							new Product.Builder()
									.setType(product.getId())
									.setProductName(getString(context, R.string.res_btc_contract_account))
									.setTransferStatus(product.getTransferAvailable())
									.build());
					break;
				case Product.TYPE_USDT_CONTRACT:
					//USDT合约账户
					mProductMap.put(product.getId(),
							new Product.Builder()
									.setType(product.getId())
									.setProductName(getString(context, R.string.res_usdt_contract_account))
									.setTransferStatus(product.getTransferAvailable())
									.build());
					break;
				case Product.TYPE_MARGIN:
					//杠杆账户
					mProductMap.put(product.getId(),
							new Product.Builder()
									.setType(product.getId())
									.setProductName(getString(context, R.string.res_margin_account))
									.setTransferStatus(product.getTransferAvailable())
									.build());

					break;
				case Product.TYPE_FORTUNE:
					//财富账户
					mProductMap.put(product.getId(),
							new Product.Builder()
									.setType(product.getId())
									.setProductName(getString(context, R.string.res_fortune_account))
									.setTransferStatus(product.getTransferAvailable())
									.build());

					break;
				case Product.TYPE_GAME:
					//游乐场账户
					mProductMap.put(product.getId(),
							new Product.Builder()
									.setType(product.getId())
									.setProductName(getString(context, R.string.res_game_account))
									.setTransferStatus(product.getTransferAvailable())
									.build());

					break;

				case Product.TYPE_OPTIONS:
					//猜涨跌账户
					mProductMap.put(product.getId(),
							new Product.Builder()
									.setType(product.getId())
									.setProductName(getString(context, R.string.balance_up_down_str))
									.setTransferStatus(product.getTransferAvailable())
									.build());
					break;
				default:
					break;
			}
		}
	}

	/**
	 * 获取所有支持的 productId 对应的 productName Map
	 * @param context
	 * @param mProductMap
	 */
	public static void initProductMap(Context context, SparseArray<Product> mProductMap) {
		List<TransferInfoModel.DataBean.ProductsBean> products = new ArrayList<>();
		for(int  i = 1; i <= 8; i++) {
			TransferInfoModel.DataBean.ProductsBean productsBean = new TransferInfoModel.DataBean.ProductsBean();
			productsBean.setId(i);
			products.add(productsBean);
		}
		initTransferProductMap(context, mProductMap, products);
	}

	private static String getString(Context context, @StringRes int resourseId) {
		return context.getResources().getString(resourseId);
	}
}
