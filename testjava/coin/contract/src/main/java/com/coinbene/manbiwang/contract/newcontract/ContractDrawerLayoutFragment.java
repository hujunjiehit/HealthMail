package com.coinbene.manbiwang.contract.newcontract;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.market.MarketType;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.adapter.ContractDrawerLayoutBinder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

public class ContractDrawerLayoutFragment extends CoinbeneBaseFragment {
	@BindView(R2.id.rl_contract)
	RecyclerView recyclerView;
	private MultiTypeAdapter adapter;
	private NewMarketWebsocket.MarketDataListener contractQouteListener;
	private List<WsMarketData> datas;
	private ContractDrawerLayoutBinder contractDrawerLayoutBinder;
	private int contractType = Constants.CONTRACT_TYPE_USDT;
	private ContractViewModel contractViewModel;
	private String tag = "ContractDrawerLayoutFragment";

	public static ContractDrawerLayoutFragment newInstance(Integer contractType) {
		Bundle args = new Bundle();
		args.putInt("contractType", contractType);
		ContractDrawerLayoutFragment fragment = new ContractDrawerLayoutFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.fragment_contract_drawer_layout;
	}

	@Override
	public void initView(View rootView) {
		contractViewModel = new ViewModelProvider(requireActivity()).get(ContractViewModel.class);
	}


	@Override
	public void onResume() {
		super.onResume();
		DLog.e(tag,"onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		DLog.e(tag,"onPause");
	}

	@Override
	public void setListener() {

		contractViewModel.getDrawerOpenStatus().observe(this, aBoolean -> {
			if (aBoolean != null && aBoolean) {
				registerMarketDataListener();
			} else {
				unRegisterMarketDataListener();
			}
		});

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
	public void initData() {


		contractType = getArguments().getInt("contractType", 0);
		datas = new ArrayList<>();
		if (contractType == Constants.CONTRACT_TYPE_USDT) {
			List<ContractUsdtInfoTable> tables = ContractUsdtInfoController.getInstance().queryContrackList();
			if (tables != null && tables.size() > 0) {
				for (int i = 0; i < tables.size(); i++) {
					ContractUsdtInfoTable table = tables.get(i);
					if (table == null) {
						continue;
					}
					WsMarketData bean = new WsMarketData();
					bean.setBaseAsset(table.baseAsset);
					bean.setSymbol(table.name);
					bean.setContractType(ContractType.USDT);
					datas.add(bean);
				}
			}
		} else {
			List<ContractInfoTable> tables = ContractInfoController.getInstance().queryContrackList();
			if (tables != null && tables.size() > 0) {
				for (int i = 0; i < tables.size(); i++) {
					ContractInfoTable table = tables.get(i);
					if (table == null) {
						continue;
					}
					WsMarketData bean = new WsMarketData();
					bean.setBaseAsset(table.baseAsset);
					bean.setSymbol(table.name);
					bean.setContractType(ContractType.BTC);
					datas.add(bean);
				}
			}
		}

		adapter = new MultiTypeAdapter();
		contractDrawerLayoutBinder = new ContractDrawerLayoutBinder();
		contractDrawerLayoutBinder.setOnItemClickContractListener(data -> {
			if (data instanceof WsMarketData) {
				if (contractViewModel != null) {
					contractViewModel.getSymbol().postValue(((WsMarketData) data).getSymbol());
				}
			}
		});
		adapter.register(WsMarketData.class, contractDrawerLayoutBinder);
		recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
		recyclerView.setAdapter(adapter);
		setData(datas);

	}


	public void setData(List<WsMarketData> datas) {
		DLog.d(tag, "setData");
		adapter.setItems(datas);
		adapter.notifyDataSetChanged();
	}

	private void registerMarketDataListener() {
		DLog.d(tag, "registerMarketDataListener");
		if (contractQouteListener == null) {
			contractQouteListener = dataMap -> {
				if (dataMap == null || dataMap.size() == 0) {
					return;
				}
				if (datas != null && datas.size() > 0) {
					for (int i = 0; i < datas.size(); i++) {
						if (dataMap.get(datas.get(i).getSymbol()) == null) {
							continue;
						}
						TradeUtils.getMarketDataFromMap(datas.get(i), dataMap);
					}
				}
				recyclerView.post(() -> setData(datas));
			};
		}

		NewMarketWebsocket.getInstance().registerMarketDataListener(contractQouteListener, contractType == Constants.CONTRACT_TYPE_USDT ? MarketType.USDT_CONTRACT : MarketType.BTC_CONTRACT);
	}

	private void unRegisterMarketDataListener() {
		DLog.d(tag, "unRegisterMarketDataListener");
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(contractQouteListener, contractType == Constants.CONTRACT_TYPE_USDT ? MarketType.USDT_CONTRACT : MarketType.BTC_CONTRACT);
	}

	@Override
	public void onFragmentShow() {

	}


	@Override
	public void onFragmentHide() {
	}
}
