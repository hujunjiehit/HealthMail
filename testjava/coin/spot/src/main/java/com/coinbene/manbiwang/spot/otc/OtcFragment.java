package com.coinbene.manbiwang.spot.otc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.OtcConfigController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.widget.app.EnableViewPager;
import com.coinbene.manbiwang.model.http.OtcAdConfigModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.otc.dialog.OtcBuySellChangeListener;
import com.coinbene.manbiwang.spot.otc.dialog.OtcFilterWindowNew;
import com.coinbene.manbiwang.spot.otc.dialog.OtcSelectListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcFragment extends CoinbeneBaseFragment implements View.OnClickListener {

	@BindView(R2.id.view_pager)
	EnableViewPager mViewPager;

	@BindView(R2.id.buy_btn_layout)
	TextView buyLayout;

	@BindView(R2.id.sell_btn_layout)
	TextView sellLayout;

	@BindView(R2.id.tv_price)
	TextView tv_price;

	@BindView(R2.id.tv_pay_type)
	TextView tv_pay_type;

	@BindView(R2.id.tv_order_list)
	ImageView tv_order_list;

	@BindView(R2.id.tv_exchange)
	TextView tv_exchange;
	@BindView(R2.id.iv_order_filter)
	ImageView ivOrderFilter;
	@BindView(R2.id.rl_coins)
	RecyclerView rlCoins;
	@BindView(R2.id.rl_title)
	RelativeLayout rlTitle;
	@BindView(R2.id.view_buy)
	View viewBuy;
	@BindView(R2.id.view_sell)
	View viewSell;

	private int mCurrentPosition = 0;
	private OtcFilterWindowNew popPayWindowNew;
	private List<OtcBuySellChangeListener> changeListenerArray;
	private ArrayList<Fragment> pagerFragments;
	private int accountType = Constants.PAY_TYPE_ALL;
	private int[] curPriceRange = Constants.PRICE_TYPE_ALL;
	private String curCurrency;
	private OtcCoinAdapter otcCoinAdapter;
	private String coinOptions;

	public static Fragment newInstance() {
		Bundle args = new Bundle();
		OtcFragment fragment = new OtcFragment();
		fragment.setArguments(args);
		return fragment;
	}


	public int getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}

	@Override
	public int initLayout() {
		return R.layout.spot_fragment_otc;
	}

	@Override
	public void initView(View rootView) {
		if (getActivity() != null) {
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		}
		mViewPager.setScroll(false);
		buyLayout.setEnabled(false);
		setTabRedOrGreen();
	}

	@Override
	public void setListener() {
		tv_price.setOnClickListener(this);
		tv_pay_type.setOnClickListener(this);
		tv_order_list.setOnClickListener(this);
		buyLayout.setOnClickListener(this);
		sellLayout.setOnClickListener(this);
		ivOrderFilter.setOnClickListener(this);
	}

	private void setTabRedOrGreen() {
		boolean isRedRise = SwitchUtils.isRedRise();
		if (isRedRise) {
			buyLayout.setTextColor(getResources().getColorStateList(R.color.res_selector_tv2_select_red));
			sellLayout.setTextColor(getResources().getColorStateList(R.color.res_selector_tv2_select_green));
		} else {
			sellLayout.setTextColor(getResources().getColorStateList(R.color.res_selector_tv2_select_red));
			buyLayout.setTextColor(getResources().getColorStateList(R.color.res_selector_tv2_select_green));
		}
	}


	private OtcSelectListener selectListener = new OtcSelectListener() {

		@Override
		public void selectPayType(int viewId, String text, int selectPayType) {
			tv_pay_type.setText(text);
			accountType = selectPayType;
			BuyOrSellFragment currentFragment = (BuyOrSellFragment) pagerFragments.get(mCurrentPosition);
			if (currentFragment != null) {
				currentFragment.refreshData();
			}
		}

		@Override
		public void selectFilter(int[] priceRange, int payType, String currency) {
			curPriceRange = priceRange;
			accountType = payType;
			curCurrency = currency;
			BuyOrSellFragment currentFragment = (BuyOrSellFragment) pagerFragments.get(mCurrentPosition);
			if (currentFragment != null) {
				currentFragment.refreshData();
			}
		}
	};

	public int[] getPriceRange() {
		return curPriceRange;
	}

	public String getCurCurrency() {
		return TextUtils.isEmpty(curCurrency) ? LanguageHelper.getCurrencyString() : curCurrency;
	}

	public void addChangeListener(OtcBuySellChangeListener changeListener) {
		if (changeListenerArray == null) {
			changeListenerArray = new ArrayList<>();
		}
		changeListenerArray.add(changeListener);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_price) {
		} else if (id == R.id.tv_pay_type) {
		} else if (id == R.id.tv_order_list) {
			ShopsOrderListActivity.startMe(getActivity());
		} else if (id == R.id.buy_btn_layout) {
			buyLayout.setEnabled(false);
			sellLayout.setEnabled(true);
			if (mViewPager != null) {
				mViewPager.setCurrentItem(0);
				mCurrentPosition = 0;
			}
		} else if (id == R.id.sell_btn_layout) {
			buyLayout.setEnabled(true);
			sellLayout.setEnabled(false);
			if (mViewPager != null) {
				mViewPager.setCurrentItem(1);
				mCurrentPosition = 1;
			}
		} else if (id == R.id.iv_order_filter) {
			if (getActivity() != null) {
				if (popPayWindowNew == null) {
					popPayWindowNew = new OtcFilterWindowNew(rlTitle, curCurrency);
					popPayWindowNew.setTypeChangeListener(selectListener);
				} else {
					popPayWindowNew.notifyData();
				}
				popPayWindowNew.showBelowAnchor(getActivity());
			}
		}

	}

	@Override
	public void initData() {
		curCurrency = LanguageHelper.getCurrencyString();

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		rlCoins.setLayoutManager(linearLayoutManager);
		otcCoinAdapter = new OtcCoinAdapter();
		rlCoins.setAdapter(otcCoinAdapter);
		otcCoinAdapter.setLists(OtcConfigController.getInstance().queryAssetList(), 0);
		otcCoinAdapter.setOnItemClickLisenter((coinName, position) -> {
			otcCoinAdapter.setSelectPosition(position);
			setFragmentCoinRefresh(coinName);
		});

		pagerFragments = new ArrayList<>();
		String[] titles = {getString(R.string.trade_buy), getString(R.string.trade_sell)};
		int[] type = {2, 1};//因为获取广告列表买入传2  卖出传1

		if (OtcConfigController.getInstance().checkAssetExist()) {
			for (int i = 0; i < titles.length; i++) {
				pagerFragments.add(BuyOrSellFragment.newInstance(type[i], OtcConfigController.getInstance().queryAssetList().get(0).asset));
			}
		} else {

			for (int i = 0; i < titles.length; i++) {
				pagerFragments.add(BuyOrSellFragment.newInstance(type[i], ""));
			}

			getOtcConfig();

		}

		mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
			@Override
			public Fragment getItem(int position) {
				if (position < pagerFragments.size()) {
					return pagerFragments.get(position);
				}
				return null;
			}

			@Override
			public int getCount() {
				return pagerFragments.size();
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return titles[position];
			}
		});

		mViewPager.setOffscreenPageLimit(titles.length);
		mViewPager.setCurrentItem(mCurrentPosition);
	}

	@Override
	public void onFragmentShow() {
		SpUtil.setSpotTradingOptions("otc");
		if (ServiceRepo.getAppService() != null) {
			ServiceRepo.getAppService().updateUserInfo();
		}
		curCurrency = LanguageHelper.getCurrencyString();
		PostPointHandler.postBrowerData(PostPointHandler.otc_brower);

		setTabRedOrGreen();
		if (pagerFragments != null && pagerFragments.size() > 0) {
			for (int i = 0; i < pagerFragments.size(); i++) {
				Fragment fragment = pagerFragments.get(i);
				if (fragment instanceof BuyOrSellFragment) {
					BuyOrSellFragment otcFragment = (BuyOrSellFragment) fragment;
					otcFragment.setIsRedRise(SwitchUtils.isRedRise());
				}
			}
		}
		if (changeListenerArray == null || changeListenerArray.size() == 0) {
			return;
		}
		for (int i = 0; i < changeListenerArray.size(); i++) {
			changeListenerArray.get(i).onTabChangeTome();
		}

	}

	@Override
	public void onFragmentHide() {

	}

	private void setFragmentCoinRefresh(String coinName) {
		if (pagerFragments != null && pagerFragments.size() > 0) {
			for (Fragment fragment : pagerFragments) {
				if (fragment instanceof BuyOrSellFragment) {
					((BuyOrSellFragment) fragment).setCurCoinName(coinName);
				}
			}
			BuyOrSellFragment currentFragment = (BuyOrSellFragment) pagerFragments.get(mCurrentPosition);
			if (currentFragment != null) {
				currentFragment.cancelHttp();
				currentFragment.refreshData();
			}
		}
	}

	public void getOtcConfig() {
		OkGo.<OtcAdConfigModel>post(Constants.OTC_GET_CONFIG).execute(new NewJsonSubCallBack<OtcAdConfigModel>() {
			@Override
			public void onSuc(Response<OtcAdConfigModel> response) {
				if (OtcConfigController.getInstance().checkAssetExist()) {
					otcCoinAdapter.setLists(OtcConfigController.getInstance().queryAssetList(), 0);
					setFragmentCoinRefresh(OtcConfigController.getInstance().queryAssetList().get(0).asset);
				}


			}

			@Override
			public OtcAdConfigModel dealJSONConvertedResult(OtcAdConfigModel otcAdConfigModel) {
				if (otcAdConfigModel == null || otcAdConfigModel.getData() == null) {
					return null;
				}
				OtcConfigController.getInstance().addAssetInToDatabase(otcAdConfigModel.getData().getAssetList());
				OtcConfigController.getInstance().addCurrencyInToDatabase(otcAdConfigModel.getData().getCurrencyList());
				OtcConfigController.getInstance().addPayTypeInToDatabase(otcAdConfigModel.getData().getPayTypesList());
				return super.dealJSONConvertedResult(otcAdConfigModel);
			}

			@Override
			public void onE(Response<OtcAdConfigModel> response) {
			}


		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		OkGo.getInstance().cancelTag(this);
	}
}
