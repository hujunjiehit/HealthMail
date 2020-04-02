package com.coinbene.manbiwang.kline.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.common.widget.WrapperLinearLayoutManager;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.fragment.adapter.DealItemViewBinder;
import com.coinbene.manbiwang.kline.fragment.klineinterface.ActivityInterface;
import com.coinbene.manbiwang.model.http.KlineDealMedel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-11-20
 */
public class KlineTradeDetailFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.recycler_view_deal)
	RecyclerView mRecyclerViewDeal;
	@BindView(R2.id.tv_trade_detail_price)
	TextView tvTradeDetailPrice;
	@BindView(R2.id.tv_v_key)
	TextView tvTradeDetailVol;


	private MultiTypeAdapter dellAdapter;

	private NewSpotWebsocket.TradeListDataListener mTradeDetailListener;

	private List<WsTradeList> tradeList;

	private String tradePairName;
	private boolean isContract;
	private int contractType;

	private int riseType = Constants.RISE_DEFAULT;

	ActivityInterface.IActivityListener mActivityListener;
	private DealItemViewBinder dealItemViewBinder;

	public static KlineTradeDetailFragment newInstance() {

		Bundle args = new Bundle();

		KlineTradeDetailFragment fragment = new KlineTradeDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.kline_fragment_trade_detail;
	}

	@Override
	public void initView(View rootView) {
		boolean isRedRise = SwitchUtils.isRedRise();

		dellAdapter = new MultiTypeAdapter();
		dealItemViewBinder = new DealItemViewBinder(isRedRise);
		dellAdapter.register(WsTradeList.class, dealItemViewBinder);
		mRecyclerViewDeal.setLayoutManager(new WrapperLinearLayoutManager(getActivity()));
		mRecyclerViewDeal.setAdapter(dellAdapter);
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {
	}

	private void initType(String tradePairName) {
		this.tradePairName = tradePairName;

		if (tradePairName.contains("/")) {
			isContract = false;
		} else {
			isContract = true;
			contractType = tradePairName.contains("-") ? Constants.CONTRACT_TYPE_USDT : Constants.CONTRACT_TYPE_BTC;
		}
		String quote = "";
		String base = "";
		if (isContract) {
			if (contractType == Constants.CONTRACT_TYPE_USDT) {
				ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(tradePairName);
				quote = table.quoteAsset;
				base = TradeUtils.getContractUsdtUnit(table);
				dealItemViewBinder.setContractType(Constants.CONTRACT_TYPE_USDT);
				dealItemViewBinder.setContractTable(table);
			} else {
				ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(tradePairName);
				dealItemViewBinder.setContractType(Constants.CONTRACT_TYPE_BTC);
				quote = table.quoteAsset;
				base = getString(R.string.number);
			}
		} else {
			String[] strings = TradeUtils.parseSymbol(tradePairName);
			base = strings[0];
			quote = strings[1];
		}

		tvTradeDetailPrice.setText(String.format("%s(%s)", getString(R.string.price), quote));
		tvTradeDetailVol.setText(String.format("%s(%s)", getString(R.string.vol), base));

	}

	@Override
	public void onStart() {
		super.onStart();
		if (getActivity() instanceof ActivityInterface) {
			if (mActivityListener == null) {
				mActivityListener = tradePairName -> initType(tradePairName);
			}
			((ActivityInterface) getActivity()).registerActivityListener(mActivityListener);
		}

		if (mTradeDetailListener == null) {
			mTradeDetailListener = (tradeLists, symbol) -> {
				setDealData(tradeLists, false);

				updateView();
			};
		}

		if (!isContract) {
			//现货
			NewSpotWebsocket.getInstance().registerTradeListListener(mTradeDetailListener);
		} else {
			if (contractType == Constants.CONTRACT_TYPE_BTC) {
				NewContractBtcWebsocket.getInstance().registerTradeListListener(mTradeDetailListener);
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				NewContractUsdtWebsocket.getInstance().registerTradeListListener(mTradeDetailListener);
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (!isContract) {
			//现货
			NewSpotWebsocket.getInstance().unregisterTradeListListener(mTradeDetailListener);
		} else {
			if (contractType == Constants.CONTRACT_TYPE_BTC) {
				NewContractBtcWebsocket.getInstance().unregisterTradeListListener(mTradeDetailListener);
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				NewContractUsdtWebsocket.getInstance().unregisterTradeListListener(mTradeDetailListener);
			}
		}
	}

	@Override
	public void onFragmentShow() {

		if (!isContract) {
			NewSpotWebsocket.getInstance().pullTradeDetailData();
		} else {
			if (contractType == Constants.CONTRACT_TYPE_BTC) {
				NewContractBtcWebsocket.getInstance().pullTradeDetailData();
			} else if (contractType == Constants.CONTRACT_TYPE_USDT) {
				NewContractUsdtWebsocket.getInstance().pullTradeDetailData();
			}
		}
	}

	@Override
	public void onFragmentHide() {

	}

	private void updateView() {
		if (isActivityExist()) {
			getActivity().runOnUiThread(() -> {
				if (dellAdapter != null && tradeList != null && tradeList.size() > 0) {
					dellAdapter.setItems(tradeList);
					dellAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	private synchronized void setDealData(List<WsTradeList> dealList, boolean isHttp) {
		if (dealList != null && dealList.size() > 1) {
			int nRiseType = BigDecimalUtils.compare(dealList.get(0).getPrice(), dealList.get(1).getPrice());
			if (riseType != nRiseType) {
				riseType = nRiseType;
			}
		}

		int size = dealList.size();
		if (tradeList == null) {
			tradeList = Collections.synchronizedList(new ArrayList<>());
		}

		if (tradeList != null && tradeList.size() > 0) {
			tradeList.clear();
		}
		if (isHttp) {
			if (size < Constants.MARKET_NUMBER_FORETEEN) {
				for (int i = 0; i < Constants.MARKET_NUMBER_FORETEEN - size; i++) {
					dealList.add(new WsTradeList());
				}
			}
			tradeList.addAll(dealList);
		} else {
			if (dealList.size() > 0) {
				synchronized (dealList) {
					for (int i = dealList.size() - 1; i >= 0; i--) {
						tradeList.add(dealList.get(i));
						if (tradeList.size() == Constants.MARKET_NUMBER_FORETEEN) {
							break;
						}
					}
				}
			}
			if (tradeList.size() < Constants.MARKET_NUMBER_FORETEEN) {
				for (int i = 0; i < Constants.MARKET_NUMBER_FORETEEN - size; i++) {
					tradeList.add(new WsTradeList());
				}
			}
		}
	}


	//交易
	private void loadHttpTradeDetail(String market) {
		OkGo.<KlineDealMedel>get(Constants.MARKET_TRADEPAIR_LATEST_TRADEDETAIL)
				.params("tradePair", market)
				.tag(this).params("num", Constants.MARKET_NUMBER_FORETEEN)
				.execute(new NewJsonSubCallBack<KlineDealMedel>() {

					@Override
					public void onSuc(Response<KlineDealMedel> response) {

						if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {

							//setDealData(response.body().getData(), true);

							//updateView();
						}
					}

					@Override
					public KlineDealMedel dealJSONConvertedResult(KlineDealMedel klineDealMedel) {
						if (klineDealMedel.getData() != null) {
							int size = klineDealMedel.getData().size();
							if (size < Constants.MARKET_NUMBER_FORETEEN) {
								for (int i = 0; i < Constants.MARKET_NUMBER_FORETEEN - size; i++) {
									klineDealMedel.getData().add(new KlineDealMedel.DataBean());
								}
							}
						}
						return klineDealMedel;
					}

					@Override
					public void onE(Response<KlineDealMedel> jsonObject) {

					}
				});

	}
}
