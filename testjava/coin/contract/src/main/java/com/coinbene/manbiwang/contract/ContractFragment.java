package com.coinbene.manbiwang.contract;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.coinbene.common.MainActivityDelegate;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.utils.DayNightHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.contract.contractbtc.ContractBtcFragment;
import com.coinbene.manbiwang.contract.contractusdt.ContractUsdtFragment;
import com.coinbene.manbiwang.contract.newcontract.FragmentDrawerParent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

public class ContractFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.frag_container)
	FrameLayout fragContainer;
	@BindView(R2.id.rb_usdt)
	RadioButton rbUsdt;
	@BindView(R2.id.rb_btc)
	RadioButton rbBtc;
	@BindView(R2.id.rg_tab)
	RadioGroup rgTab;
	@BindView(R2.id.drawer_layout)
	DrawerLayout drawerayout;


	private Fragment mContractBtcFragment;
	private Fragment mContractUsdtFragment;

	private Fragment mCurrentFragment = new Fragment();


	@Override
	public int initLayout() {
		return R.layout.fragment_contract;
	}

	@Override
	public void initView(View rootView) {
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void setListener() {
		drawerayout.addDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

			}

			@Override
			public void onDrawerOpened(@NonNull View drawerView) {
				drawerayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
			}

			@Override
			public void onDrawerClosed(@NonNull View drawerView) {
				drawerayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			}

			@Override
			public void onDrawerStateChanged(int newState) {

			}
		});

		rgTab.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.rb_usdt) {

				switchToFragment(0);
			} else if (checkedId == R.id.rb_btc) {
				switchToFragment(1);
			}
		});

		rbUsdt.setChecked(true);

		drawerayout.setOnTouchListener((v, event) -> {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					drawerayout.closeDrawers();
					break;
			}
			return false;
		});


		FragmentDrawerParent drawerFragment = getDrawerFragment();
		if (drawerFragment != null) {
			drawerFragment.setOnDrawerListener(isLast -> {
				if (isLast) {
					drawerayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				} else if (drawerayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_UNLOCKED) {
					drawerayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				}
			});
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onBuyOrSellPriceOneMessage(WsMarketData message) {
		if (message == null) {
			return;
		}
		if (message.getContractType() == ContractType.USDT) {
			switchToFragment(0);
			rbUsdt.setChecked(true);
			ContractUsdtFragment contractUsdtFragment = (ContractUsdtFragment) mContractUsdtFragment;
			contractUsdtFragment.changeSymbol((message).getSymbol());
		} else {
			rbBtc.setChecked(true);
			switchToFragment(1);
			ContractBtcFragment contractBtcFragment = (ContractBtcFragment) mContractBtcFragment;
			contractBtcFragment.changeSymbol(message.getSymbol());
		}
		drawerayout.closeDrawers();
	}


	private FragmentDrawerParent getDrawerFragment() {
		List<Fragment> fragments = getChildFragmentManager().getFragments();
		for (Fragment fragment : fragments) {
			if (fragment instanceof FragmentDrawerParent) {
				return (FragmentDrawerParent) fragment;
			}
		}
		return null;
	}


	private void switchToFragment(int index) {
		if (index == 0) {
			if (mContractUsdtFragment == null) {
				mContractUsdtFragment = ContractUsdtFragment.newInstance();
			}
			switchFragment(mContractUsdtFragment);
		} else if (index == 1) {
			if (mContractBtcFragment == null) {
				mContractBtcFragment = ContractBtcFragment.newInstance();
			}
			switchFragment(mContractBtcFragment);
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

		transaction.commit();

		//更新当前Fragment为targetFragment
		mCurrentFragment = targetFragment;
	}


	@Override
	public void initData() {
		switch (SpUtil.getContractOptions()) {
			case "btc":
				rgTab.check(R.id.rb_btc);
				break;
			case "usdt":
				rgTab.check(R.id.rb_usdt);
				break;
		}
	}

	@Override
	public void onFragmentHide() {
	}

	@Override
	public void onFragmentShow() {
		if (DayNightHelper.isNight(getContext())) {
			setMerBarWhite();
		} else {
			setMerBarBlack();
		}

		if (!TextUtils.isEmpty(MainActivityDelegate.getInstance().getSubTab())) {
			if (MainActivityDelegate.getInstance().getSubTab().equals("btc")) {
				rgTab.check(R.id.rb_btc);
			} else if (MainActivityDelegate.getInstance().getSubTab().equals("usdt")) {
				rgTab.check(R.id.rb_usdt);
			}
			MainActivityDelegate.getInstance().clearSubTab();
		}
	}

	public void openDrawer() {
		drawerayout.openDrawer(GravityCompat.START);
	}
}
