package com.coinbene.manbiwang.spot;

import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.coinbene.common.MainActivityDelegate;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.utils.DayNightHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.spot.margin.MarginFragment;
import com.coinbene.manbiwang.spot.otc.OtcFragment;
import com.coinbene.manbiwang.spot.spot.SpotTradeFragment;

import java.util.List;

public class SpotGoodsFragment extends CoinbeneBaseFragment {

	private RadioGroup rgTab;
	private RadioButton rbCoin, rbOtc, rbMargin;

	private Fragment spotFragment, marginFragment, otcFragment;
	private Fragment mCurrentFragment = new Fragment();

	@Override
	public int initLayout() {
		return R.layout.spot_fr_spot_goods;
	}

	@Override
	public void initView(View rootView) {
		rgTab = rootView.findViewById(R.id.rg_tab);
		rbCoin = rootView.findViewById(R.id.rb_coin);
		rbOtc = rootView.findViewById(R.id.rb_legal_currency);
		rbMargin = rootView.findViewById(R.id.rb_margin);


		if (!SwitchUtils.isOpenOTC() && !SwitchUtils.isOpenMargin()) {
			rgTab.setVisibility(View.GONE);
		} else {
			if (!SwitchUtils.isOpenOTC()) {
				rbOtc.setVisibility(View.GONE);
			}
			if (!SwitchUtils.isOpenMargin()) {
				rbMargin.setVisibility(View.GONE);
			}
		}

	}


	private void switchToFragment(int index) {
		if (index == 0) {
			if (spotFragment == null) {
				spotFragment = SpotTradeFragment.newInstance();
			}

			switchFragment(spotFragment);

		} else if (index == 1) {
			if (marginFragment == null) {
				marginFragment = MarginFragment.newInstance();
			}

			switchFragment(marginFragment);
		} else if (index == 2) {
			if (otcFragment == null) {
				otcFragment = OtcFragment.newInstance();
			}

			switchFragment(otcFragment);
		}
	}

	private void switchFragment(Fragment targetFragment) {
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

		if (mCurrentFragment == targetFragment) {
			return;
		}

		if (!targetFragment.isAdded()) {//如果要显示的targetFragment没有添加过
			transaction.hide(mCurrentFragment)//隐藏当前Fragment
					.add(R.id.frag_container, targetFragment);//添加targetFragment
		} else {
			//如果要显示的targetFragment已经添加过
			transaction.hide(mCurrentFragment) //隐藏当前Fragment
					.show(targetFragment);//显示targetFragment
		}
		if (mCurrentFragment.isAdded()) {
			transaction.setMaxLifecycle(mCurrentFragment, Lifecycle.State.STARTED);
		}
		transaction.setMaxLifecycle(targetFragment, Lifecycle.State.RESUMED);
		transaction.commit();

		//更新当前Fragment为targetFragment
		mCurrentFragment = targetFragment;
	}


	@Override
	public void setListener() {
		initListener();
	}

	@Override
	public void initData() {
		if ("margin".equals(SpUtil.getSpotTradingOptions()) && SwitchUtils.isOpenMargin()) {
			setChangeToMargin();
		} else if ("otc".equals(SpUtil.getSpotTradingOptions()) && SwitchUtils.isOpenOTC()) {
			setChangeToOTC();
		} else {
			setChangeToTrade();
		}
	}

	@Override
	public void onFragmentShow() {
		if (DayNightHelper.isNight(getContext())) {
			setMerBarWhite();
		} else {
			setMerBarBlack();
		}

		if (!TextUtils.isEmpty(MainActivityDelegate.getInstance().getSubTab())) {
			if (MainActivityDelegate.getInstance().getSubTab().equals("coin")) {
				setChangeToTrade();
			} else if (MainActivityDelegate.getInstance().getSubTab().equals("margin")) {
				setChangeToMargin();
			} else if (MainActivityDelegate.getInstance().getSubTab().equals("otc")) {
				setChangeToOTC();
			}
			MainActivityDelegate.getInstance().clearSubTab();
		}
	}

	@Override
	public void onFragmentHide() {

	}

	private void initListener() {

		rgTab.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.rb_coin) {
				switchToFragment(0);
				PostPointHandler.postClickData(PostPointHandler.tab_coin);
			} else if (checkedId == R.id.rb_margin) {
				PostPointHandler.postClickData(PostPointHandler.tab_margin);
				switchToFragment(1);
			} else if (checkedId == R.id.rb_legal_currency) {
				PostPointHandler.postClickData(PostPointHandler.tab_otc);
//				switchToFragment(findIndexForName(OtcFragment.class.toString()));
				switchToFragment(2);
			}
		});
		//setChangeToTrade();
	}

	/**
	 * 听过当前类名找到当前index
	 *
	 * @param className
	 * @return
	 */
	private int findIndexForName(String className) {

		List<Fragment> fragments = getFragmentManager().getFragments();
		int pisition = -1;
		if (TextUtils.isEmpty(className)) {
			return pisition;
		}
		if (fragments == null || fragments.size() == 0) {
			return pisition;
		}

		for (int i = 0; i < fragments.size(); i++) {
			if (className.equals(fragments.get(i).toString())) {
				pisition = i;
			}
		}

		return pisition;

	}


	public void setChangeToTrade() {
		if (rbCoin != null)
			rbCoin.setChecked(true);
	}

	public void setChangeToMargin() {
		if (!SwitchUtils.isOpenMargin()) {
			return;
		}

		if (rgTab.getVisibility() == View.VISIBLE && rgTab != null)
			rgTab.check(R.id.rb_margin);
	}

	public void setChangeToOTC() {
		if (!SwitchUtils.isOpenOTC()) {
			return;
		}

		if (rgTab.getVisibility() == View.VISIBLE && rgTab != null)
			rgTab.check(R.id.rb_legal_currency);
	}


}
