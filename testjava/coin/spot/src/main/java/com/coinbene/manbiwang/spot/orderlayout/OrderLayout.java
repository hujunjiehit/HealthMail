package com.coinbene.manbiwang.spot.orderlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.orderlayout.fragment.OrderBookFagment;
import com.coinbene.manbiwang.spot.orderlayout.fragment.TradeDetailFragment;
import com.coinbene.manbiwang.spot.orderlayout.listener.OrderBookListener;
import com.coinbene.manbiwang.spot.orderlayout.listener.SpotDataListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june
 * on 2019-12-02
 */
public class OrderLayout extends LinearLayout implements SpotDataListener {

	@BindView(R2.id.rb_order_book)
	RadioButton mRbOrderBook;
	@BindView(R2.id.rb_trade_detail)
	RadioButton mRbTradeDetail;
	@BindView(R2.id.rg_tab)
	RadioGroup mRgTab;
	@BindView(R2.id.frag_container)
	FrameLayout mFragmentContainer;

	private Context mContext;

	private Fragment mOrderBookFragment;
	private Fragment mTradeDetailFragment;

	private Fragment mCurrentFragment = new Fragment();

	private Fragment mParentFragment;

	public OrderLayout(Context context) {
		super(context);
		initView(context);
	}

	public OrderLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public OrderLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.spot_new_order_layout, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		mRgTab.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.rb_order_book) {
				if (mOrderBookFragment == null) {
					mOrderBookFragment = new OrderBookFagment();
				}
				switchToFragment(mOrderBookFragment);
			} else if (checkedId == R.id.rb_trade_detail) {
				if (mTradeDetailFragment == null) {
					mTradeDetailFragment = new TradeDetailFragment();
				}
				switchToFragment(mTradeDetailFragment);
			}
		});

		mOrderBookFragment = new OrderBookFagment();
		mTradeDetailFragment = new TradeDetailFragment();
	}


	public void setParentFragment(Fragment mParentFragment, OrderBookListener mOrderBookListener) {
		this.mParentFragment = mParentFragment;
		if (mOrderBookFragment == null) {
			mOrderBookFragment = new OrderBookFagment();
		}
		((OrderBookFagment)mOrderBookFragment).setPriceClickListener(mOrderBookListener);

		mRgTab.check(R.id.rb_order_book);
		switchToFragment(mOrderBookFragment);
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


	@Override
	public void registerDataListener() {
		if (mOrderBookFragment instanceof SpotDataListener) {
			((SpotDataListener) mOrderBookFragment).registerDataListener();
		}
		if (mTradeDetailFragment instanceof SpotDataListener) {
			((SpotDataListener) mTradeDetailFragment).registerDataListener();
		}
	}

	@Override
	public void unRegisterDataListener() {
		if (mOrderBookFragment instanceof SpotDataListener) {
			((SpotDataListener) mOrderBookFragment).unRegisterDataListener();
		}
		if (mTradeDetailFragment instanceof SpotDataListener) {
			((SpotDataListener) mTradeDetailFragment).unRegisterDataListener();
		}
	}

	@Override
	public void setSymbol(String symbol) {
		if (!symbol.contains("/")) {
			TradePairInfoTable table = TradePairInfoController.getInstance().queryDataByTradePair(symbol);
			symbol = table.tradePairName;
		}
		if (mOrderBookFragment instanceof SpotDataListener) {
			((SpotDataListener) mOrderBookFragment).setSymbol(symbol);
		}
		if (mTradeDetailFragment instanceof SpotDataListener) {
			((SpotDataListener) mTradeDetailFragment).setSymbol(symbol);
		}
	}
}
