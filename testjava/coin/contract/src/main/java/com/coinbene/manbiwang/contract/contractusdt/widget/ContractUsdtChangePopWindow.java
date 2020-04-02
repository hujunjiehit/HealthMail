package com.coinbene.manbiwang.contract.contractusdt.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.market.MarketType;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.SupportPopupWindow;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.adapter.ContractQouteBinder;
import com.coinbene.manbiwang.model.websocket.WsContractQouteResponse;
import com.coinbene.manbiwang.service.contract.ContractChangePopWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.drakeet.multitype.MultiTypeAdapter;


/**
 * @author huyong
 */
public class ContractUsdtChangePopWindow implements View.OnClickListener, ContractChangePopWindow {
	private String usdtOrBtc = "";
	private View mAnchor;
	private SupportPopupWindow mPopupWindow;
	boolean mDismissed = false;
	private View mPanel, llRoot;
	private RecyclerView rlContrack;
	private MultiTypeAdapter adapter;
	private List<WsMarketData> datas;
	private ContractQouteBinder contractQouteBinder;
	private NewMarketWebsocket.MarketDataListener contractQouteListener;

	/**
	 * @param anchor   显示在这个view下面
	 * @param useKline 是否是在k线界面使用
	 */
	public ContractUsdtChangePopWindow(View anchor, boolean useKline) {
		mAnchor = anchor;

		int[] point = new int[2];
		mAnchor.getLocationInWindow(point);

		mPopupWindow = new SupportPopupWindow(anchor);
		mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT - (point[1] + mAnchor.getHeight()));
		mPopupWindow.setFocusable(true);
		mPanel = LayoutInflater.from(mAnchor.getContext()).inflate(
				R.layout.contrack_change_popview, null);
		mPopupWindow.setContentView(mPanel);
		initView(useKline);
	}

	@Override
	public void setOnItemClickContrctListener(OnItemClickContractListener onItemClickContrckListener) {
		contractQouteBinder.setOnItemClickContractListener(onItemClickContrckListener);
	}

	private void initView(boolean isUseKline) {
		mPopupWindow.setOnDismissListener(this::dismissQuickActionBar);
		rlContrack = mPanel.findViewById(R.id.rl_contrack);
		llRoot = mPanel.findViewById(R.id.ll_root);

		adapter = new MultiTypeAdapter();
		contractQouteBinder = new ContractQouteBinder(isUseKline);

		if (isUseKline) {
			rlContrack.setBackground(rlContrack.getResources().getDrawable(R.drawable.contract_change_use_kline));
		}
		llRoot.setOnClickListener(v -> onDismiss());
		adapter.register(WsMarketData.class, contractQouteBinder);
		rlContrack.setLayoutManager(new LinearLayoutManager(rlContrack.getContext()));
		rlContrack.setAdapter(adapter);
		datas = new ArrayList<>();

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
				datas.add(bean);
			}
		}
		setData(datas);
	}


	public void setData(List<WsMarketData> datas) {
		adapter.setItems(datas);
		adapter.notifyDataSetChanged();
	}


	public void showBelowAnchor() {
		mDismissed = false;
		mPopupWindow.showAsDropDown(mAnchor);
		if (contractQouteListener == null) {
			contractQouteListener = new NewMarketWebsocket.MarketDataListener() {
				@Override
				public void onDataArrived(Map<String, WsMarketData> dataMap) {
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
					rlContrack.post(() -> setData(datas));
				}
			};
		}
		NewMarketWebsocket.getInstance().registerMarketDataListener(contractQouteListener, MarketType.USDT_CONTRACT);
	}



	@Override
	public void onClick(View v) {
		onDismiss();
	}


	public void onDismiss() {
		dismissQuickActionBar();
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(contractQouteListener, MarketType.USDT_CONTRACT);
	}

	@Override
	public void dismiss() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	private void dismissQuickActionBar() {
		if (mDismissed) {
			return;
		}
		mDismissed = true;
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	public SupportPopupWindow getPopupWindow() {
		return mPopupWindow;
	}


	public void hidePopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	//行情数据组装及刷新
	public void setQuoteDatas(Map<String, WsContractQouteResponse.DataBean> contrackMap) {


	}

}