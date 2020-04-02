package com.coinbene.manbiwang.balance.products.game;

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
import com.coinbene.manbiwang.model.http.GameTotalInfoModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.DLog;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.products.game.adapter.GameHeaderViewBinder;
import com.coinbene.manbiwang.balance.products.game.adapter.GameItemViewBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 游乐场资产页面
 */
public class BalanceGameFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.asset_list)
	RecyclerView recyclerView;
	@BindView(R2.id.login_regis_tv)
	TextView loginRegisTv;
	@BindView(R2.id.login_layout)
	LinearLayout loginLayout;

	private String currencySymbol;//$
	private Items newOriginItems = new Items();

	private GameItemViewBinder gameItemViewBinder;
	private GameHeaderViewBinder gameHeaderViewBinder;
	private MultiTypeAdapter mContentAdapter;

	private AssetManager.TotalAccountListener mTotalAccountListener;

	public static BalanceGameFragment newInstance() {
		Bundle args = new Bundle();
		BalanceGameFragment fragment = new BalanceGameFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.balance_game_fragment;
	}

	@Override
	public void initView(View rootView) {
		mContentAdapter = new MultiTypeAdapter();
		gameItemViewBinder = new GameItemViewBinder();
		gameHeaderViewBinder = new GameHeaderViewBinder();

		mContentAdapter.register(GameTotalInfoModel.DataBean.class, gameHeaderViewBinder);
		mContentAdapter.register(GameTotalInfoModel.DataBean.ListBean.class, gameItemViewBinder);

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
					if (currentProduct.getType() == Product.TYPE_GAME) {
						getGameBalance();
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
		PostPointHandler.postClickData(PostPointHandler.balance_tab_option);

		getGameBalance();
	}

	@NeedLogin
	private void getGameBalance() {
		if (!isActivityExist()) {
			return;
		}
		OkGo.<GameTotalInfoModel>get(Constants.BALANCE_GAME).tag(this).execute(new NewJsonSubCallBack<GameTotalInfoModel>() {
			@Override
			public void onSuc(Response<GameTotalInfoModel> response) {

			}

			@Override
			public GameTotalInfoModel dealJSONConvertedResult(GameTotalInfoModel gameTotalInfoModel) {
				if (gameTotalInfoModel != null && gameTotalInfoModel.getData() != null) {
					handleGameData(gameTotalInfoModel.getData());
				}
				return super.dealJSONConvertedResult(gameTotalInfoModel);
			}

			@Override
			public void onE(Response<GameTotalInfoModel> response) {

			}
		});

	}

	private void handleGameData(GameTotalInfoModel.DataBean data) {

		List<GameTotalInfoModel.DataBean.ListBean> originItems = data.getList();

		if (newOriginItems != null && newOriginItems.size() > 0) {
			newOriginItems.clear();
		}
		newOriginItems.add(data);
		newOriginItems.addAll(originItems);
		currencySymbol = data.getCurrencySymbol();
		if (gameItemViewBinder != null) {
			gameItemViewBinder.setCurrencySymbol(currencySymbol);
		}

		getActivity().runOnUiThread(() -> {
			mContentAdapter.setItems(newOriginItems);
			mContentAdapter.notifyDataSetChanged();
		});
	}
}
