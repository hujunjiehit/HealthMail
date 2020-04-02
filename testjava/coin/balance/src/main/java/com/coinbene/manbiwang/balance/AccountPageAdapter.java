package com.coinbene.manbiwang.balance;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.balance.products.contract.BalanceContractFragment;
import com.coinbene.manbiwang.balance.products.fortune.FortuneAccountFragment;
import com.coinbene.manbiwang.balance.products.game.BalanceGameFragment;
import com.coinbene.manbiwang.balance.products.margin.BalanceMarginFragment;
import com.coinbene.manbiwang.balance.products.options.BalanceOptionFragment;
import com.coinbene.manbiwang.balance.products.spot.BalanceCoinFragment;
import com.coinbene.common.balance.Product;

import java.util.List;

/**
 * Created by june
 * on 2019-08-15
 * 资产账户的pageAdapter
 */
public class AccountPageAdapter extends FragmentPagerAdapter {

	List<Product> list;

	public AccountPageAdapter(FragmentManager fm, List<Product> list) {
		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		this.list = list;
	}

	@Override
	public Fragment getItem(int position) {
		switch (list.get(position).getType()) {
			case Product.TYPE_SPOT:
				//币币账户
				return BalanceCoinFragment.newInstance();

			case Product.TYPE_MARGIN:
				//杠杆账户
				return BalanceMarginFragment.newInstance();

			case Product.TYPE_BTC_CONTRACT:
				//BTC合约账户
				return BalanceContractFragment.newInstance(Constants.CONTRACT_TYPE_BTC);

			case Product.TYPE_USDT_CONTRACT:
				//USDT合约账户
				return BalanceContractFragment.newInstance(Constants.CONTRACT_TYPE_USDT);

			case Product.TYPE_OPTIONS:
				//猜涨跌账户
				return BalanceOptionFragment.newInstance();

			case Product.TYPE_GAME:
				//游乐场账户
				return BalanceGameFragment.newInstance();

			case Product.TYPE_FORTUNE:
				//财富账户
				return FortuneAccountFragment.newInstance();

			default:
				break;
		}
		return BalanceCoinFragment.newInstance();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return list.get(position).getProductName();
	}
}
