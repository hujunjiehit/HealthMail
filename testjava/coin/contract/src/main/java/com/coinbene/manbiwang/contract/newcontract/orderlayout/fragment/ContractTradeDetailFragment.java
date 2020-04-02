package com.coinbene.manbiwang.contract.newcontract.orderlayout.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.core.WebsocketOperatiron;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.common.widget.AppBarStateChangeListener;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.adapter.OrderDetailItemBinder;
import com.coinbene.manbiwang.contract.newcontract.ContractViewModel;
import com.coinbene.manbiwang.contract.newcontract.orderlayout.listener.ContractDataListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-12-02
 */
public class ContractTradeDetailFragment extends CoinbeneBaseFragment implements ContractDataListener {
	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R2.id.tv_price)
	TextView tvPrice;
	@BindView(R2.id.tv_quantity)
	TextView tvQuantity;

	private WebsocketOperatiron.TradeListDataListener mTradeDetailListener;

	private MultiTypeAdapter mContentAdapter;
	private OrderDetailItemBinder tradeDetailItemBinder;

	private List<WsTradeList> tradeList;

	private String symbol;

	private ContractType contractType = ContractType.USDT;

	private ContractViewModel contractViewModel;
	private boolean showHideContractPlan;
	private boolean showContractHighLever;
	private int numbers = Constants.MARKET_NUMBER_TWELVE;

	private boolean isScrolling = false;

	@Override
	public int initLayout() {
		return R.layout.contract_trade_detail_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		tradeDetailItemBinder = new OrderDetailItemBinder();

		mContentAdapter.register(WsTradeList.class, tradeDetailItemBinder);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(mContentAdapter);

		contractViewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);
	}

	@Override
	public void setListener() {

		contractViewModel.getShowContractPlan().observe(this, aBoolean -> {
			showHideContractPlan = aBoolean;
			pullTradeDetailData();
		});
		contractViewModel.getShowContractHighLever().observe(this, show -> {
			showContractHighLever = show;
			pullTradeDetailData();
		});

		contractViewModel.getUnitType().observe(this, integer -> {
			initUnit(symbol);
			pullTradeDetailData();
		});
//		contractViewModel.getSymbol().observe(this, new Observer<String>() {
//			@Override
//			public void onChanged(String s) {
//				initUnit(s);
//			}
//		});

	}

	@Override
	public void initData() {
	}


	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onFragmentShow() {
		initUnit(symbol);
		initItemBinderParms();
		pullTradeDetailData();

	}


	public void initItemBinderParms() {
		if (tradeDetailItemBinder != null) {
			tradeDetailItemBinder.setContractType(contractType);
			tradeDetailItemBinder.setSymbol(symbol);
		}
	}

	private void pullTradeDetailData() {
		if (contractType == ContractType.USDT) {
			NewContractUsdtWebsocket.getInstance().pullTradeDetailData();
		} else {
			NewContractBtcWebsocket.getInstance().pullTradeDetailData();
		}
	}

	@Override
	public void onFragmentHide() {
	}

	public void setTradeDetail(List<WsTradeList> dealMedel, String tradeSymbol) {
		if (!TextUtils.isEmpty(tradeSymbol) && !tradeSymbol.equals(symbol)) {
			return;
		}
		if (mContentAdapter == null && !isShowing) {
			return;
		}
		if (isScrolling) {
			return;
		}
		if (contractType == ContractType.USDT) {
			if (showContractHighLever || !showHideContractPlan) {
				numbers = Constants.MARKET_NUMBER_FORETEEN;
			} else {
				numbers = Constants.MARKET_NUMBER_SEVENTEN;
			}
		} else {
			if (showContractHighLever || !showHideContractPlan) {
				numbers = 14;
			} else {
				numbers = 18;
			}

		}
		if (dealMedel == null) {
			dealMedel = new ArrayList<>();
		}
		int size = dealMedel.size();
		if (tradeList == null) {
			tradeList = Collections.synchronizedList(new ArrayList<>());
		}
		if (tradeList.size() > 0) {
			tradeList.clear();
		}

		if (dealMedel.size() > 0) {
			synchronized (dealMedel) {
				for (int i = dealMedel.size() - 1; i >= 0; i--) {
					tradeList.add(dealMedel.get(i));
					if (tradeList.size() == numbers) {
						break;
					}
				}
			}
		}
		if (tradeList.size() < numbers) {
			for (int i = 0; i < numbers - size; i++) {
				tradeList.add(new WsTradeList());
			}
		}

		List<Object> items = new ArrayList<>();
		items.addAll(tradeList);
		mRecyclerView.post(() -> {
			mContentAdapter.setItems(items);
			mContentAdapter.notifyDataSetChanged();
		});
	}


	public void registerDataListener() {
		if (mTradeDetailListener == null) {
			mTradeDetailListener = (tradeLists, symbol) -> setTradeDetail(tradeLists, symbol);
		}
		switch (contractType) {
			case USDT:
				NewContractUsdtWebsocket.getInstance().registerTradeListListener(mTradeDetailListener);
				break;
			case BTC:
				NewContractBtcWebsocket.getInstance().registerTradeListListener(mTradeDetailListener);
				break;
		}
	}

	@Override
	public void unRegisterDataListener() {
		switch (contractType) {
			case USDT:
				NewContractUsdtWebsocket.getInstance().unregisterTradeListListener(mTradeDetailListener);
				break;
			case BTC:
				NewContractBtcWebsocket.getInstance().unregisterTradeListListener(mTradeDetailListener);
				break;
		}
	}

	@Override
	public void setSymbol(String symbol) {

		unRegisterDataListener();

		this.symbol = symbol;
		if (tradeList != null) {
			tradeList.clear();
		}
		contractType = TradeUtils.isUsdtContract(symbol) ? ContractType.USDT : ContractType.BTC;
		initUnit(symbol);
		registerDataListener();

		initItemBinderParms();
	}

	@Override
	public void onScrollStatedChanged(AppBarStateChangeListener.ScrollState scrollState) {
		if (scrollState == AppBarStateChangeListener.ScrollState.SCROLLING) {
			isScrolling = true;
		} else {
			isScrolling = false;
			pullTradeDetailData();
		}
	}

	private void initUnit(String symbol) {
		if (tvPrice == null) {
			return;
		}

//		if (!isShowing) {
//			return;
//		}
		if (contractType == ContractType.USDT) {
			//tvPrice.setText(String.format("%s(%s)", getString(R.string.price), TradeUtils.getContractUsdtQoute(symbol)));
			tvQuantity.setText(String.format("%s(%s)", getString(R.string.contract_trade_number), TradeUtils.getContractUsdtUnit(symbol)));
		} else {
			//tvPrice.setText(String.format("%s(%s)", getString(R.string.price), TradeUtils.getContractBtcQoute(symbol)));
			tvQuantity.setText(String.format("%s(%s)", getString(R.string.contract_trade_number), getString(R.string.number)));
		}
	}
}
