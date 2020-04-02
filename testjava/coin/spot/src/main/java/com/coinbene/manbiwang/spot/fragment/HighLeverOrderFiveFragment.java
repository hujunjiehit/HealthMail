package com.coinbene.manbiwang.spot.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.HighLeverOrderModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.RecordType;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.fragment.adapter.HighLeverOrderFiveAdapter;
import com.coinbene.manbiwang.spot.fragment.adapter.SpotBottomTabAdapter;
import com.coinbene.manbiwang.spot.fragment.bean.AccountType;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;


/**
 * 交易页面历史委托--展示五条记录
 */
public class HighLeverOrderFiveFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.cb_hide_other_coin)
	CheckBox mCbHideOtherCoin;
	@BindView(R2.id.layout_hide_other_coin)
	LinearLayout mLayoutHideOtherCoin;
	@BindView(R2.id.tv_look_all)
	TextView mTvLookAll;
	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;

	private AccountType accountType;

	private HighLeverOrderFiveAdapter mAdapter;

	private MarginSymbolTable marginTable;


	private View footerView;
	private String base = "", qoute = "";

	private BaseUserEventListener userEventListener;

	public static HighLeverOrderFiveFragment newInstance(AccountType accountType) {
		Bundle args = new Bundle();
		args.putSerializable("accountType", accountType);
		HighLeverOrderFiveFragment fragment = new HighLeverOrderFiveFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.spot_history_order_five_fragment;
	}

	@Override
	public void initView(View rootView) {
		footerView = getLayoutInflater().inflate(R.layout.commom_footer_look_all, null);
		((TextView) footerView.findViewById(R.id.view_more)).setText(R.string.view_more_high_lever);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		mAdapter = new HighLeverOrderFiveAdapter();
		mAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_base_empty, null));
		mAdapter.bindToRecyclerView(mRecyclerView);
	}

	@Override
	public void setListener() {
		mLayoutHideOtherCoin.setOnClickListener(v -> {
			mCbHideOtherCoin.setChecked(!mCbHideOtherCoin.isChecked());
			setHideOtherCoin(mCbHideOtherCoin.isChecked());
			getHighLeverOrder();
		});
		mTvLookAll.setVisibility(View.GONE);
		mTvLookAll.setOnClickListener(v -> {
//			switch (accountType) {
//				case SPOT:
			ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.SPOT_HIGH_LEVER_ORDER);
//					break;
//				case MARGIN:
//					ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.SPOT_HIGH_LEVER_ORDER);
//					break;
//			}

		});

		footerView.setOnClickListener(v -> {
//			switch (accountType) {
//				case SPOT:
			ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.SPOT_HIGH_LEVER_ORDER);
//					break;
//				case MARGIN:
//					ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.SPOT_HIGH_LEVER_ORDER);
//					break;
//			}
		});

		mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
//			if (accountType == AccountType.SPOT) {
			HighLeverOrderModel.DataBean.ListBean item = (HighLeverOrderModel.DataBean.ListBean) adapter.getItem(position);

			cancelOrder(item.getPlanOrderId());
//			}
		});
	}


	public void cancelOrder(String orderId) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("planOrderId", orderId);
		OkGo.<BaseRes>post(Constants.TRADE_HIGH_LEVER_ORDER_CANCEL).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(getActivity()) {

			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes t = response.body();
				if (t.isSuccess()) {
					ToastUtil.show(R.string.cancel_entrust_success);
					getHighLeverOrder();
//					mAdapter.removeTradeById(orderId);
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});

	}

	@Override
	public void initData() {
		accountType = (AccountType) getArguments().getSerializable("accountType");
	}

	@Override
	public void onFragmentShow() {
		mCbHideOtherCoin.setChecked(getHideOtherCoin());

		if (ServiceRepo.getUserService().getUserStatus() == UserStatus.LOGIN) {
//			mTvLookAll.setVisibility(View.VISIBLE);
		} else {
//			mTvLookAll.setVisibility(View.GONE);
			mAdapter.setNewData(null);
		}

		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onPlanorderChanged() {
					getHighLeverOrder();
				}
			};
		}

		if (accountType == AccountType.MARGIN) {
			UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.MARGIN, userEventListener);
		} else {
			UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.SPOT, userEventListener);
		}
		getHighLeverOrder();
	}

	@Override
	public void onFragmentHide() {
		if (accountType == AccountType.MARGIN) {
			UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.MARGIN, userEventListener);
		} else {
			UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.SPOT, userEventListener);
		}
	}

//	@AddFlowControl(strategy = FlowControlStrategy.throttleFirst)
	@NeedLogin
	public void getHighLeverOrder() {
		HttpParams params = new HttpParams();
		params.put("pageNum", 1);
		params.put("pageSize", 10);

		parseCoin(getSymbol());

		if (accountType == AccountType.MARGIN) {
			params.put("accountType", "margin");
		}

		if (mCbHideOtherCoin.isChecked()) {
			params.put("tradePair", getSymbol().replace("/", ""));
		}
		OkGo.<HighLeverOrderModel>get(Constants.TRADE_HIGH_lEVER_LIST).params(params).tag(this).execute(new NewJsonSubCallBack<HighLeverOrderModel>() {

			@Override
			public void onSuc(Response<HighLeverOrderModel> response) {
				if (response.body() != null) {
					if (response.body().getData() != null && response.body().getData().getList().size() > 5) {
						//超过五个显示footerView
						if (mAdapter.getFooterLayoutCount() == 0) {
							mAdapter.addFooterView(footerView);
						}
						setAdapterData(mAdapter, response.body().getData().getList().subList(0, 5));
					} else {
						mAdapter.removeAllFooterView();
						setAdapterData(mAdapter, response.body().getData().getList());
					}
				} else {
					mAdapter.removeAllFooterView();
					setAdapterData(mAdapter, null);
				}
			}

			@Override
			public void onE(Response<HighLeverOrderModel> response) {
			}

		});
	}

	private String getSymbol() {
		if (getParentFragment() instanceof SpotBottomTabAdapter.SpotBottomInterface) {
			return ((SpotBottomTabAdapter.SpotBottomInterface) getParentFragment()).getTradePair();
		}
		return "";
	}

	private void parseCoin(String symbol) {
		if (accountType == AccountType.SPOT) {
			if (!TextUtils.isEmpty(symbol) && symbol.contains("/")) {
				base = symbol.substring(0, symbol.indexOf("/"));
				qoute = symbol.substring(symbol.indexOf("/") + 1);
			}
		} else if (accountType == AccountType.MARGIN) {
			marginTable = MarginSymbolController.getInstance().querySymbolByName(symbol);
			if (marginTable != null) {
				base = marginTable.base;
				qoute = marginTable.quote;
			}
		}

	}

	private boolean getHideOtherCoin() {
		if (getParentFragment() instanceof SpotBottomTabAdapter.SpotBottomInterface) {
			return ((SpotBottomTabAdapter.SpotBottomInterface) getParentFragment()).getHideOtherCoin();
		}
		return false;
	}


	private void setHideOtherCoin(boolean hideOtherCoin) {
		if (getParentFragment() instanceof SpotBottomTabAdapter.SpotBottomInterface) {
			((SpotBottomTabAdapter.SpotBottomInterface) getParentFragment()).setHideOtherCoin(hideOtherCoin);
		}
	}
}

