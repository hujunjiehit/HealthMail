package com.coinbene.common.activities.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.activities.SelectSearchPairActivity;
import com.coinbene.common.activities.fragment.adapter.SearchPairAdapter;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.AllCapTransformationMethod;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.market.MarketService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

import static com.qmuiteam.qmui.util.QMUIStatusBarHelper.getStatusbarHeight;

/**
 * Created by june
 * on 2019-09-07
 */
public class SearchPairFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.search_tile)
	View mSearchTile;
	@BindView(R2.id.icon_search)
	ImageView mIconSearch;
	@BindView(R2.id.edit_search)
	EditText mEditSearch;
	@BindView(R2.id.clear_search)
	ImageView mClearSearch;
	@BindView(R2.id.text_cancel_search)
	TextView mTextCancelSearch;
	@BindView(R2.id.divider_view)
	View mDividerView;
	@BindView(R2.id.search_pair_RecyclerView)
	RecyclerView mRecyclerView;

	private SearchPairAdapter adapter;

	private List<TradePairInfoTable> optionalResults;
	private Set<String> tradePairSet;

	private int type;

	public static SearchPairFragment newInstance(int type) {
		Bundle args = new Bundle();
		args.putInt("type", type);
		SearchPairFragment fragment = new SearchPairFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.common_fragment_search_tradepair;
	}

	@Override
	public void initView(View rootView) {
		ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mSearchTile.getLayoutParams();
		layoutParams.topMargin = getStatusbarHeight(getContext());
		mSearchTile.setLayoutParams(layoutParams);

		Bundle bundle = getArguments();
		if (bundle != null) {
			type = bundle.getInt("type");
		}

		mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
		adapter = new SearchPairAdapter();
		adapter.bindToRecyclerView(mRecyclerView);
		adapter.setEnableLoadMore(false);
		adapter.setUpFetchEnable(false);
		adapter.disableLoadMoreIfNotFullPage();
	}

	@Override
	public void setListener() {
		mClearSearch.setOnClickListener(v -> mEditSearch.setText(""));

		mEditSearch.addTextChangedListener(new BaseTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				dataProcessing(s.toString());
			}
		});

		mTextCancelSearch.setOnClickListener(v -> {
			if (type == SelectSearchPairActivity.TYPE_SEARCH) {
				KeyboardUtils.hideKeyboard(v);
				getActivity().finish();
			} else if (type == SelectSearchPairActivity.TYPE_SELECT) {
				if (getActivity() instanceof SelectSearchPairActivity) {
					((SelectSearchPairActivity) getActivity()).switchToSelectPair();
				}
			}
		});

		mEditSearch.setTransformationMethod(new AllCapTransformationMethod(true));

		adapter.setOnItemClickListener((adapter, view, position) -> {
			KeyboardUtils.hideKeyboard(view);

			if (type == SelectSearchPairActivity.TYPE_SEARCH) {
				//直接进入search的情况下，跳转到k线
				TradePairInfoTable item = (TradePairInfoTable) adapter.getData().get(position);
				Bundle bundle = new Bundle();
				bundle.putString("pairName", item.tradePairName);
				UIBusService.getInstance().openUri(view.getContext(), UrlUtil.getCoinbeneUrl("SpotKline"), bundle);
				getActivity().finish();
			} else if (type == SelectSearchPairActivity.TYPE_SELECT){
				//从选择交易对页面进入search，需要设置好setResult
				TradePairInfoTable item = (TradePairInfoTable) adapter.getData().get(position);
				Intent intent = new Intent();
				intent.putExtra("tradePairName", item.tradePairName);
				getActivity().setResult(Activity.RESULT_OK, intent);
				getActivity().finish();
			}
		});

		adapter.setOnItemChildClickListener((adapter, view, position) -> {
			TradePairInfoTable item = (TradePairInfoTable) adapter.getItem(position);
			if (ServiceRepo.getMarketService() != null) {
				//添加或者删除自选
				ServiceRepo.getMarketService().addOrDeleteOptional(item.tradePair, new MarketService.CallBack() {
					@Override
					public void onSuccess() {
						item.isOptional = !item.isOptional;
						adapter.notifyItemChanged(position);
					}

					@Override
					public void onFailed() {

					}
				});
			}
		});
	}

	@Override
	public void initData() {
		optionalResults = TradePairOptionalController.getInstance().queryTradePairOptional();

	}

	@Override
	public void onFragmentHide() {

		//关闭输入法键盘
		KeyboardUtils.hideKeyboard(mEditSearch);
	}

	@Override
	public void onFragmentShow() {

		//弹出输入法键盘
		KeyboardUtils.showKeyboard(mEditSearch);
	}


	private void dataProcessing(String currentInput) {
		if (TextUtils.isEmpty(currentInput)) {
			mClearSearch.setVisibility(View.GONE);
			adapter.setNewData(null);
			return;
		}

		mClearSearch.setVisibility(View.VISIBLE);

		optionalResults = TradePairOptionalController.getInstance().queryTradePairOptional();

		List<TradePairInfoTable> queryResult = TradePairInfoController.getInstance().
				queryDataLikeStr(LanguageHelper.isChinese(getContext()), currentInput);
		if (optionalResults != null && optionalResults.size() > 0) {
			for (int i = 0; i < optionalResults.size(); i++) {
				for (int j = 0; j < queryResult.size(); j++) {
					if (optionalResults.get(i).tradePairName.equals(queryResult.get(j).tradePairName)) {
						queryResult.get(j).isOptional = true;
						break;
					}
				}
			}
		}

		if (tradePairSet == null) {
			tradePairSet = new HashSet<>();
		}
		tradePairSet.clear();
		Iterator<TradePairInfoTable> iterator = queryResult.iterator();
		while (iterator.hasNext()) {
			TradePairInfoTable tradePairInfoTable = iterator.next();
			if (tradePairSet.contains(tradePairInfoTable.tradePair)) {
				iterator.remove();
			} else {
				tradePairSet.add(tradePairInfoTable.tradePair);
			}
		}

		adapter.setNewData(queryResult);
	}
}
