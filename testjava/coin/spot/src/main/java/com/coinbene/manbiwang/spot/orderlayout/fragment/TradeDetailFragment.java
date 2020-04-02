package com.coinbene.manbiwang.spot.orderlayout.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.websocket.core.WebSocketManager;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.manbiwang.model.http.KlineDealMedel;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.orderlayout.listener.SpotDataListener;
import com.coinbene.manbiwang.spot.spot.adapter.TradeDetailItemBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by june
 * on 2019-12-02
 */
public class TradeDetailFragment extends CoinbeneBaseFragment implements SpotDataListener {
	@BindView(R2.id.recycler_view)
	RecyclerView mRecyclerView;

	private static final long PERIOD_TIME = 2000;
	private Timer timer;

	private NewSpotWebsocket.TradeListDataListener mTradeDetailListener;

	private MultiTypeAdapter mContentAdapter;
	private TradeDetailItemBinder tradeDetailItemBinder;

	private List<WsTradeList> tradeList;

	private String symbol;

	@Override
	public int initLayout() {
		return R.layout.spot_trade_detail_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		tradeDetailItemBinder = new TradeDetailItemBinder();

		mContentAdapter.register(WsTradeList.class, tradeDetailItemBinder);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(mContentAdapter);
	}

	@Override
	public void setListener() {

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
	public void registerDataListener() {
		if (mTradeDetailListener == null) {
			mTradeDetailListener = (tradeLists, symbol) -> setTradeDetail(tradeLists, false);
		}
		NewSpotWebsocket.getInstance().registerTradeListListener(mTradeDetailListener);
	}

	@Override
	public void unRegisterDataListener() {
		NewSpotWebsocket.getInstance().unregisterTradeListListener(mTradeDetailListener);
	}

	@Override
	public void setSymbol(String symbol) {
		this.symbol = symbol;
		if (tradeList != null) {
			tradeList.clear();
		}
	}

	@Override
	public void onFragmentShow() {
		NewSpotWebsocket.getInstance().pullTradeDetailData();
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!WebSocketManager.getInstance().getConnectSubscriberStatus(Constants.BASE_WEBSOCKET)) {
					//getTradeDetail(symbol);
				}
			}
		};
		timer.schedule(timerTask, 0, PERIOD_TIME);
	}

	@Override
	public void onFragmentHide() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void setTradeDetail(List<WsTradeList> dealMedel, boolean isHttp) {
		if (mContentAdapter == null && !isShowing) {
			return;
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
		if (isHttp) {
			if (size < Constants.MARKET_NUMBER_FORETEEN) {
				for (int i = 0; i < Constants.MARKET_NUMBER_FORETEEN - size; i++) {
					dealMedel.add(new WsTradeList());
				}
			}

			tradeList.addAll(dealMedel);
		} else {
			if (dealMedel.size() > 0) {
				synchronized (dealMedel) {
					for (int i = dealMedel.size() - 1; i >= 0; i--) {
						tradeList.add(dealMedel.get(i));
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

		List<Object> items = new ArrayList<>();
		items.addAll(tradeList);
		if (!isActivityExist()) {
			return;
		}
		getActivity().runOnUiThread(() -> {
			mContentAdapter.setItems(items);
			mContentAdapter.notifyDataSetChanged();
		});
	}

	//http获取成交列表
	private void getTradeDetail(String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			return;
		}
		String tradePair = symbol.replace("/", "");
		OkGo.<KlineDealMedel>get(Constants.MARKET_TRADEPAIR_LATEST_TRADEDETAIL).params("tradePair", tradePair).tag(this).params("num", Constants.MARKET_NUMBER_FORETEEN).execute(new NewJsonSubCallBack<KlineDealMedel>() {

			@Override
			public void onSuc(Response<KlineDealMedel> response) {
				try {
					if (response.body() != null && response.body().getData() != null) {
						//setTradeDetail(response.body().getData(), true);
					}
				} catch (Exception ex) {
				}
			}

			@Override
			public KlineDealMedel dealJSONConvertedResult(KlineDealMedel klineDealMedel) {
				return super.dealJSONConvertedResult(klineDealMedel);
			}

			@Override
			public void onE(Response<KlineDealMedel> jsonObject) {
			}

		});

	}
}
