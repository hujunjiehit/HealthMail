package com.coinbene.manbiwang.contract.newcontract;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.coinbene.common.MainActivityDelegate;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.DayNightHelper;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.widget.AppBarStateChangeListener;
import com.coinbene.common.widget.QMUITabSelectedListener;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.bean.ContractBottomTab;
import com.coinbene.manbiwang.contract.bean.ContractTabType;
import com.coinbene.manbiwang.contract.contractbtc.widget.ContractMenu;
import com.coinbene.manbiwang.contract.contractusdt.widget.ContractUsdtMenu;
import com.coinbene.manbiwang.contract.dialog.ContractParmsDialog;
import com.coinbene.manbiwang.contract.dialog.PositionModelDialog;
import com.coinbene.manbiwang.contract.newcontract.bottom.adapter.ContractBottomPageAdapter;
import com.coinbene.manbiwang.contract.newcontract.orderlayout.ContractOrderLayout;
import com.coinbene.manbiwang.model.http.ContractLeverModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.google.android.material.appbar.AppBarLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.qmuiteam.qmui.util.QMUIStatusBarHelper.getStatusbarHeight;

public class NewContractFragment extends CoinbeneBaseFragment implements ContractTopLayout.ContractTopClickLisenter {

	//	@BindView(R2.id.drawer_layout)
//	DrawerLayout drawerLayout;
	@BindView(R2.id.app_bar)
	AppBarLayout mAppBar;
	@BindView(R2.id.layout_root)
	View mLayoutRoot;
	@BindView(R2.id.layout_top)
	ContractTopLayout layoutTop;
	@BindView(R2.id.layout_trade)
	ContractTradeLayout layoutTrade;
	@BindView(R2.id.layout_order)
	ContractOrderLayout orderLayout;
	@BindView(R2.id.view_pager)
	ViewPager mViewPager;
	@BindView(R2.id.tab_segment)
	QMUITabSegment mTabSegment;
//	@BindView(R2.id.ll_drawer)
//	LinearLayout llDrawer;


	private String currentSymbol;
	private ContractViewModel contractViewModel;
	private ContractUsdtMenu menu;

	private List<ContractBottomTab> contractBottomTabs;
	private ContractBottomPageAdapter contractBtcTypePageAdapter;
	private ContractParmsDialog parmsDialog;
	private PositionModelDialog dialog;
	private FragmentDrawerParent drawerFragment;
	private DrawerLayout drawerLayout;

	@Override
	public int initLayout() {
		return R.layout.fragment_contract_new;
	}

	@Override
	public void initView(View rootView) {
		ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layoutTop.getLayoutParams();
		layoutParams.topMargin = getStatusbarHeight(getContext());
		layoutTop.setLayoutParams(layoutParams);

		layoutTrade.initPresenter();

		orderLayout.setParentFragment(this);

		contractViewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);

		initTabSegment();

		drawerLayout = ServiceRepo.getAppService().getMainDrawerLayout();


	}

	@Override
	public void setListener() {
		layoutTop.setContractTopClickLisenter(this);

		initDrawerLisenter();
		layoutTrade.setTradeLayoutLisenter(new ContractTradeLayout.TradeLayoutLisenter() {
			@Override
			public void showHideContractPlan(boolean showContractType) {
				SpUtil.putShowContractPlan(showContractType);
				contractViewModel.getShowContractPlan().postValue(showContractType);
			}

			@Override
			public void showHideContractHighLever(boolean showHide) {
				contractViewModel.getShowContractHighLever().postValue(showHide);
			}

			@Override
			public void holdPositionChange(int total) {
				if (mTabSegment != null) {
					if (total > 99) {
						mTabSegment.updateTabText(0, String.format("%s[%s]", getString(R.string.all_hold_position), "99+"));
					} else
						mTabSegment.updateTabText(0, String.format("%s[%d]", getString(R.string.all_hold_position), total));
				}
			}

			@Override
			public void setCurOrderListSize(int total) {
				if (mTabSegment != null) {
					if (total > 99) {
						mTabSegment.updateTabText(1, String.format("%s[%s]", getString(R.string.br_current_delegation), "99+"));
					} else {
						mTabSegment.updateTabText(1, String.format("%s[%d]", getString(R.string.br_current_delegation), total));

					}
				}
			}

			@Override
			public void onMarginModeChange(ContractLeverModel.DataBean dataBean) {
				if (layoutTop != null) {
					layoutTop.setMarginSetting(dataBean);
				}
			}

			@Override
			public void onMarginModeUpdate(String mode) {
				if (layoutTop != null) {
					layoutTop.setMarginText(mode);
				}
			}
		});

		mAppBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
			@Override
			public void onStateChanged(AppBarLayout appBarLayout, State state) {

			}

			@Override
			public void onOffsetValueChanged(AppBarLayout appBarLayout, int offset) {

			}

			@Override
			public void onScrollStateChanged(ScrollState scrollState) {
				if (orderLayout != null) {
					orderLayout.onScrollStatedChanged(scrollState);
				}
			}
		});
	}


	@SuppressLint("ClickableViewAccessibility")
	private void initDrawerLisenter() {

		if (drawerFragment == null)
			drawerFragment = getFragmentDrawerParent();

		drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
			}

			@Override
			public void onDrawerOpened(@NonNull View drawerView) {
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				contractViewModel.getDrawerOpenStatus().postValue(true);
//				if (drawerFragment != null) {
//					drawerFragment.scrollTo(currentSymbol);
//				}

			}

			@Override
			public void onDrawerClosed(@NonNull View drawerView) {
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				contractViewModel.getDrawerOpenStatus().postValue(false);
				if (drawerFragment != null) {
					drawerFragment.scrollTo(currentSymbol);
				}
			}

			@Override
			public void onDrawerStateChanged(int newState) {
			}
		});

		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		drawerLayout.setOnTouchListener((v, event) -> {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					drawerLayout.closeDrawers();
					break;
			}
			return false;
		});


		if (drawerFragment != null) {
			drawerFragment.setOnDrawerListener(isLast -> {
				if (isLast) {
					drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				} else if (drawerLayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_UNLOCKED) {
					drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				}
			});
		}

	}

	@Override
	public void initData() {
		contractViewModel.getSymbol().observe(this, symbol -> {
			//交易对变化
			changeSymbol(symbol);
			drawerLayout.closeDrawers();
		});

		contractViewModel.getPriceParamsModel().observe(this, priceParamsModel -> {
			if (isShowing) {
				layoutTrade.setPriceParams(priceParamsModel);
			}
		});

		contractViewModel.getClickPrice().observe(this, price -> {
			//点击orderlist价格
			layoutTrade.setClickPrice(price);
		});

		contractViewModel.getUnitType().observe(this, integer -> {
			if (layoutTrade != null) {
				//切换交易单位
				layoutTrade.initInput();
			}
		});
	}


	@Override
	public void onFragmentShow() {
		if (DayNightHelper.isNight(getContext())) {
			setMerBarWhite();
		} else {
			setMerBarBlack();
		}


		if (!TextUtils.isEmpty(MainActivityDelegate.getInstance().getSubTab())) {
			MainActivityDelegate.getInstance().clearSubTab();
		}

		if (!TextUtils.isEmpty(MainActivityDelegate.getInstance().getSymbol())) {
//			changeSymbol(MainActivityDelegate.getInstance().getSymbol());
			currentSymbol = SpUtil.checkContractCion(MainActivityDelegate.getInstance().getSymbol());
			contractViewModel.getSymbol().postValue(currentSymbol);
//			changeSymbol(currentSymbol);
			MainActivityDelegate.getInstance().clearSymbol();
		} else {
			if (layoutTrade != null) {

				layoutTrade.subAll();
				layoutTrade.getAllData();
			}
		}
		if (!ServiceRepo.getUserService().isLogin()) {
			if (mTabSegment != null) {
				mTabSegment.updateTabText(0, String.format("%s[%d]", getString(R.string.all_hold_position), 0));
				mTabSegment.updateTabText(1, String.format("%s[%d]", getString(R.string.br_current_delegation), 0));
			}

		}
		layoutTrade.setUserState();
		if (!TextUtils.isEmpty(currentSymbol)) {
			if (TradeUtils.isUsdtContract(currentSymbol)) {
				NewContractUsdtWebsocket.getInstance().changeSymbol(currentSymbol);
			} else {
				NewContractBtcWebsocket.getInstance().changeSymbol(currentSymbol);
			}
		}

		if (orderLayout != null) {
			orderLayout.registerDataListener();
		}


	}

	@Override
	public void onFragmentHide() {

		if (orderLayout != null) {
			orderLayout.unRegisterDataListener();
		}
		if (layoutTrade != null) {
			layoutTrade.initInput();
		}

		if (layoutTrade != null) {
			layoutTrade.unSubAll();
		}
	}

	private void initTabSegment() {
		mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setTabTextSize(QMUIDisplayHelper.dp2px(getContext(), 15));
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_1));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(getContext(), 20));
		mTabSegment.setElevation(0);
		if (contractBottomTabs == null) {
			contractBottomTabs = new ArrayList<>();
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.all_hold_position) + "[0]", ContractTabType.TAB_HOLD_POSITION));
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.br_current_delegation) + "[0]", ContractTabType.TAB_CUR_ENTRUST));
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.contract_plan), ContractTabType.TAB_PLAN_ENTRIST));
			contractBottomTabs.add(new ContractBottomTab(getString(R.string.history_entrust_label_new), ContractTabType.TAB_HIS_ENTRUST));
			contractBtcTypePageAdapter = new ContractBottomPageAdapter(getChildFragmentManager(), contractBottomTabs);
		}

		mViewPager.setOffscreenPageLimit(contractBottomTabs.size());
		mViewPager.setAdapter(contractBtcTypePageAdapter);
		mTabSegment.setupWithViewPager(mViewPager);

		mTabSegment.addOnTabSelectedListener(new QMUITabSelectedListener() {
			@Override
			public void onTabSelected(int index) {
			}
		});
	}


	@Override
	public void onChangeSymbole() {
		if (drawerLayout != null) {
			drawerLayout.openDrawer(GravityCompat.START);
			KeyboardUtils.hideKeyboard(drawerLayout);
		}
//		EventBus.getDefault().post(new DrawerStutas(true));
//		contractViewModel.getOpenOrCloseDrawer().postValue(true);
	}

	@Override
	public void goKline() {
		UIBusService.getInstance().openUri(getContext(),
				"coinbene://ContractKline?symbol=" + currentSymbol, null);
	}

	@Override
	public void showMore(View v) {
		if (TradeUtils.isUsdtContract(currentSymbol)) {
			if (menu == null) {
				menu = new ContractUsdtMenu(getContext());
				menu.setOnMenuClickListener(new ContractUsdtMenu.onMenuClickListener() {
					@Override
					public void clickUnitSwitch() {
					}

					@Override
					public void marginMode() {
						showChangeModelDialog();
					}
				});
			}
			menu.setCanChangeMode(layoutTop.getMarginMode());
			menu.setDefaultSymbol(currentSymbol);
			menu.setDefaultLever(layoutTrade.getCurrentLever() + "X");
			menu.showAtLocation(v);
		} else {
			ContractMenu menu = new ContractMenu(getContext());
			menu.setCanChangeMode(layoutTop.getMarginMode());
			menu.setDefaultSymbol(currentSymbol);
			menu.setDefaultLever(layoutTrade.getCurrentLever() + "X");
			menu.showAtLocation(v);
			menu.setListener(() -> showChangeModelDialog());
		}
	}

	@NeedLogin(jump = true)
	private void showChangeModelDialog() {
		if (dialog == null)
			dialog = new PositionModelDialog(getContext());

		dialog.setDefaultMode(layoutTop.getMarginSetting());
		dialog.setChangeListener(mode -> {
			if (!TextUtils.isEmpty(mode) && mode.equals(layoutTop.getMarginSetting())) {
				return;
			}
			layoutTrade.updatePositionMode(mode);
		});

		dialog.show();
	}


	@Override
	public void showContractPrams() {
		if (parmsDialog == null) {
			parmsDialog = new ContractParmsDialog(getContext());
		}
		parmsDialog.initDialog(currentSymbol);
		parmsDialog.show();
	}

	@Override
	public void showChangeMarginMode() {
		if (!TextUtils.isEmpty(layoutTop.getMarginMode())) {
			ToastUtil.show(getString(R.string.marginMode_change_remind));
			return;
		}

		showChangeModelDialog();
	}

	private void changeSymbol(String symbol) {
//
//		if(!TextUtils.isEmpty(currentSymbol)&&currentSymbol.equals(symbol)){
//			return;
//		}
		SpUtil.putContractNew(symbol);
		currentSymbol = symbol;
		layoutTop.setData(currentSymbol);
		layoutTrade.setSymbol(currentSymbol);
		orderLayout.setSymbol(currentSymbol);
		scrollToTop();
	}

	private void scrollToTop() {
		CoordinatorLayout.Behavior behavior =
				((CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams()).getBehavior();
		if (behavior instanceof AppBarLayout.Behavior) {
			AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
			int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
			if (topAndBottomOffset != 0) {
				appBarLayoutBehavior.setTopAndBottomOffset(0);
			}
		}
	}

	//
	private FragmentDrawerParent getFragmentDrawerParent() {
		return (FragmentDrawerParent) getFragmentManager().findFragmentByTag("drawer_parent_main");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (layoutTrade != null)
			layoutTrade.onDestory();
		if (orderLayout != null)
			orderLayout.onDestory();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
