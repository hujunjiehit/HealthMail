package com.coinbene.manbiwang.balance.products.contract;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.balance.Product;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.manbiwang.model.http.ContractTotalInfoModel;
import com.coinbene.manbiwang.model.http.FutureItemEmptyModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.DLog;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.products.contract.adapter.BalanceContractEmptyItemBinder;
import com.coinbene.manbiwang.balance.products.contract.adapter.BalanceContractHeaderBinder;
import com.coinbene.manbiwang.balance.products.contract.adapter.BalanceContractItemBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class BalanceContractFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.asset_list)
	RecyclerView recyclerView;
	@BindView(R2.id.login_regis_tv)
	TextView loginRegisTv;
	@BindView(R2.id.login_layout)
	LinearLayout loginLayout;

	private ContractTotalInfoModel.DataBean contractData;

	private MultiTypeAdapter mContentAdapter;

	private BalanceContractHeaderBinder headerBinder;
	private BalanceContractItemBinder itemBinder;
	private BalanceContractEmptyItemBinder emptyItemBinder;

	private int contractType = Constants.CONTRACT_TYPE_BTC;

	private Items items = new Items();

	private List<ContractPositionListModel.DataBean> contractItems = new ArrayList<>();

	AssetManager.TotalAccountListener mTotalAccountListener;

	public static BalanceContractFragment newInstance(int contractType) {
		Bundle args = new Bundle();
		args.putInt("contractType", contractType);
		BalanceContractFragment fragment = new BalanceContractFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.balance_contract_fragment;
	}

	@Override
	public void initView(View rootView) {
		headerBinder = new BalanceContractHeaderBinder();
		itemBinder = new BalanceContractItemBinder();
		emptyItemBinder = new BalanceContractEmptyItemBinder();

		mContentAdapter = new MultiTypeAdapter();
		mContentAdapter.register(ContractTotalInfoModel.DataBean.class, headerBinder);
		mContentAdapter.register(ContractPositionListModel.DataBean.class, itemBinder);
		mContentAdapter.register(FutureItemEmptyModel.class, emptyItemBinder);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(mContentAdapter);
	}

	@Override
	public void setListener() {
		loginRegisTv.setOnClickListener(v -> gotoLoginOrLock());
	}

	@NeedLogin(jump = true)
	private void gotoLoginOrLock() {

	}

	@Override
	public void initData() {

		Bundle bundle = getArguments();
		if (bundle != null) {
			contractType = bundle.getInt("contractType", Constants.CONTRACT_TYPE_BTC);
			if (itemBinder != null) {
				itemBinder.setContractType(contractType);
			}
			if (headerBinder != null) {
				headerBinder.setContractType(contractType);
			}
		}
	}

	@Override
	public void onFragmentHide() {
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mTotalAccountListener == null) {
			mTotalAccountListener = new AssetManager.TotalAccountListener() {

				@Override
				public void onHideValueChanged(boolean hide) {
					if (mContentAdapter != null) {
						mContentAdapter.notifyDataSetChanged();
					}
				}

				@Override
				public void onLoginStatusChanged(boolean isLogin) {
					if (isLogin) {
						recyclerView.setVisibility(View.VISIBLE);
						loginLayout.setVisibility(View.GONE);
					} else {
						recyclerView.setVisibility(View.GONE);
						loginLayout.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onPullRefresh(Product currentProduct) {
					if ((currentProduct.getType() == Product.TYPE_BTC_CONTRACT && contractType == Constants.CONTRACT_TYPE_BTC) ||
							(currentProduct.getType() == Product.TYPE_USDT_CONTRACT && contractType == Constants.CONTRACT_TYPE_USDT)) {
						getContractBalance();
						getPositionList();
					}
				}

				@Override
				public void onTabSelected(int index) {
					if (recyclerView != null) {
						recyclerView.scrollToPosition(0);
					}
				}

				@Override
				public void onConiSwitchChanged() {
					if (mContentAdapter != null) {
						mContentAdapter.notifyDataSetChanged();
					}
				}
			};
		}
		AssetManager.getInstance().registerTotalAccountListener(mTotalAccountListener);
	}

	@Override
	public void onStop() {
		super.onStop();

		AssetManager.getInstance().unregisterTotalAccountListener(mTotalAccountListener);
	}

	@Override
	public void onFragmentShow() {
		PostPointHandler.postClickData(PostPointHandler.balance_tab_contract);

		getContractBalance();

		getPositionList();
	}

	/**
	 * 展示头部和item数据
	 */
	private void notifyRecycleView() {
		if (contractData == null) {
			return;
		}
		List<ContractTotalInfoModel.DataBean> accountModelList = new ArrayList<>();
		accountModelList.add(contractData);
		items.clear();
		items.addAll(accountModelList);
		if (contractItems == null || contractItems.size() == 0) {
			List<FutureItemEmptyModel> emptyModelArrayList = new ArrayList<>();
			emptyModelArrayList.add(new FutureItemEmptyModel(true));
			items.addAll(emptyModelArrayList);
		} else {
			items.addAll(contractItems);
		}

		if (!isActivityExist()) {
			return;
		}
		getActivity().runOnUiThread(() -> {
			mContentAdapter.setItems(items);
			mContentAdapter.notifyDataSetChanged();
		});
	}

	/**
	 * 获取合约资产信息
	 */
	@NeedLogin
	private void getContractBalance() {
		if (!isActivityExist()) {
			return;
		}

		String url = contractType == Constants.CONTRACT_TYPE_BTC ? Constants.BALANCE_BTC_CONTRACT : Constants.BALANCE_USDT_CONTRACT;
		OkGo.<ContractTotalInfoModel>get(url).tag(this).execute(new NewJsonSubCallBack<ContractTotalInfoModel>() {
			@Override
			public void onSuc(Response<ContractTotalInfoModel> response) {

			}

			@Override
			public ContractTotalInfoModel dealJSONConvertedResult(ContractTotalInfoModel contractTotalInfoModel) {
				if (contractTotalInfoModel != null && contractTotalInfoModel.getData() != null) {
					contractData = contractTotalInfoModel.getData();
					notifyRecycleView();
				}
				return super.dealJSONConvertedResult(contractTotalInfoModel);
			}

			@Override
			public void onE(Response<ContractTotalInfoModel> response) {
			}
		});

	}


	/**
	 * 获取当前持仓
	 */
	@NeedLogin
	public void getPositionList() {
		if (!isActivityExist()) {
			return;
		}

		String url = contractType == Constants.CONTRACT_TYPE_BTC ? Constants.ACCOUNT_POSITION_LIST : Constants.ACCOUNT_POSITION_LIST_USDT;
		OkGo.<ContractPositionListModel>get(url).tag(this).execute(new NewJsonSubCallBack<ContractPositionListModel>() {
			@Override
			public void onSuc(Response<ContractPositionListModel> response) {
				if (response.body().getData() != null && response.body().getData().size() > 0) {
					contractItems = response.body().getData();
					notifyRecycleView();
				} else {
					if (contractItems != null && contractItems.size() > 0) {
						contractItems.clear();
					}
					notifyRecycleView();
				}
			}

			@Override
			public ContractPositionListModel dealJSONConvertedResult(ContractPositionListModel balance30DayModel) {
				return super.dealJSONConvertedResult(balance30DayModel);
			}

			@Override
			public void onE(Response<ContractPositionListModel> response) {
			}
		});
	}
}
