package com.coinbene.manbiwang.contract.newcontract.orderlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.widget.AppBarStateChangeListener;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.newcontract.orderlayout.fragment.ContractOrderBookFagment;
import com.coinbene.manbiwang.contract.newcontract.orderlayout.fragment.ContractTradeDetailFragment;
import com.coinbene.manbiwang.contract.newcontract.orderlayout.listener.ContractDataListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june
 * on 2019-12-02
 */
public class ContractOrderLayout extends LinearLayout implements ContractDataListener {

	@BindView(R2.id.rb_order_book)
	RadioButton mRbOrderBook;
	@BindView(R2.id.rb_trade_detail)
	RadioButton mRbTradeDetail;
	@BindView(R2.id.rg_tab)
	RadioGroup mRgTab;
	@BindView(R2.id.frag_container)
	FrameLayout mFragmentContainer;
	@BindView(R2.id.rl_change_unit)
	RelativeLayout rlChangeUnit;
	@BindView(R2.id.contract_unit_change)
	ContractUnitChange contractUnitChange;


	private Context mContext;

	private Fragment mOrderBookFragment;
	private Fragment mTradeDetailFragment;

	private Fragment mCurrentFragment = new Fragment();

	private Fragment mParentFragment;

	public ContractOrderLayout(Context context) {
		super(context);
		initView(context);
	}

	public ContractOrderLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ContractOrderLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.contract_new_order_layout, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		mRgTab.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.rb_order_book) {
				if (mOrderBookFragment == null) {
					mOrderBookFragment = new ContractOrderBookFagment();
				}
				switchToFragment(mOrderBookFragment);
			} else if (checkedId == R.id.rb_trade_detail) {
				if (mTradeDetailFragment == null) {
					mTradeDetailFragment = new ContractTradeDetailFragment();
				}
				switchToFragment(mTradeDetailFragment);
			}
		});

		mOrderBookFragment = new ContractOrderBookFagment();
		mTradeDetailFragment = new ContractTradeDetailFragment();
	}

	private void switchToFragment(Fragment targetFragment) {
		if (mParentFragment == null) {
			return;
		}
		FragmentTransaction transaction = mParentFragment.getChildFragmentManager().beginTransaction();

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

	public void setParentFragment(Fragment mParentFragment) {
		this.mParentFragment = mParentFragment;
		if (mOrderBookFragment == null) {
			mOrderBookFragment = new ContractOrderBookFagment();
		}
//		((OrderBookFagment)mOrderBookFragment).setPriceClickListener(mOrderBookListener);

		mRgTab.check(R.id.rb_order_book);
		switchToFragment(mOrderBookFragment);
	}

	@Override
	public void registerDataListener() {
		if (mOrderBookFragment instanceof ContractDataListener) {
			((ContractDataListener) mOrderBookFragment).registerDataListener();
		}
		if (mTradeDetailFragment instanceof ContractDataListener) {
			((ContractDataListener) mTradeDetailFragment).registerDataListener();
		}
	}

	@Override
	public void unRegisterDataListener() {
		if (mOrderBookFragment instanceof ContractDataListener) {
			((ContractDataListener) mOrderBookFragment).unRegisterDataListener();
		}
		if (mTradeDetailFragment instanceof ContractDataListener) {
			((ContractDataListener) mTradeDetailFragment).unRegisterDataListener();
		}


	}

	@Override
	public void setSymbol(String symbol) {
		if (TradeUtils.isUsdtContract(symbol)) {
			contractUnitChange.setContractBase(symbol);
			rlChangeUnit.setVisibility(VISIBLE);
			NewContractUsdtWebsocket.getInstance().changeSymbol(symbol);
		} else {
			rlChangeUnit.setVisibility(GONE);
			NewContractBtcWebsocket.getInstance().changeSymbol(symbol);
		}

		if (mOrderBookFragment instanceof ContractDataListener) {
			((ContractDataListener) mOrderBookFragment).setSymbol(symbol);
		}
		if (mTradeDetailFragment instanceof ContractDataListener) {
			((ContractDataListener) mTradeDetailFragment).setSymbol(symbol);
		}
	}

	@Override
	public void onScrollStatedChanged(AppBarStateChangeListener.ScrollState scrollState) {
		if (mOrderBookFragment instanceof ContractDataListener) {
			((ContractDataListener) mOrderBookFragment).onScrollStatedChanged(scrollState);
		}
		if (mTradeDetailFragment instanceof ContractDataListener) {
			((ContractDataListener) mTradeDetailFragment).onScrollStatedChanged(scrollState);
		}
	}

	public void onDestory() {
		unRegisterDataListener();
		mContext = null;
		mOrderBookFragment = null;
		mTradeDetailFragment = null;
	}
}
