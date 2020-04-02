package com.coinbene.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.coinbene.common.R;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.DayNightHelper;
import com.coinbene.common.utils.LanguageHelper;
import com.lzy.okgo.OkGo;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by june
 * on 2019-08-14
 */
public abstract class CoinbeneBaseFragment extends Fragment {
	protected View mRootView;
	protected QMUITopBarLayout mTopBar;
	private Unbinder mUnbinder;


	private String tag = "CoinbeneBaseFragment";

	protected boolean isShowing = false;

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(LanguageHelper.setLocale(context));
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (mRootView == null && initLayout() != 0) {
			try {
				mRootView = inflater.inflate(initLayout(), container, false);
				//绑定到butterknife
				mUnbinder = ButterKnife.bind(this, mRootView);
				mTopBar = mRootView.findViewById(R.id.top_bar);
			} catch (Exception e) {
				if (e instanceof InflateException) throw e;
				e.printStackTrace();
			}
		}
		return mRootView;
	}


	@Override
	public void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
//		super.onSaveInstanceState(outState);
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
//		AutoSizeConfig.getInstance().setCustomFragment(true);
		initView(mRootView);
		setListener();
		initData();
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		OkGo.getInstance().cancelTag(this);
		if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
			mUnbinder.unbind();
		}

		this.mRootView = null;
		this.mUnbinder = null;
	}

	public abstract @LayoutRes
	int initLayout();

	public abstract void initView(View rootView);

	public abstract void setListener();

	public abstract void initData();

	public abstract void onFragmentShow();

	public abstract void onFragmentHide();

	@Override
	public void onResume() {
		super.onResume();
		isShowing = true;
		onFragmentShow();
		if (getActivity() != null) {
			DayNightHelper.updateConfig(getActivity(), CBRepository.uiMode);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isShowing = false;
		onFragmentHide();
	}

	private List<Fragment> getAllChildFragment() {
		List<Fragment> result = new ArrayList<>();
		collectFragments(this, result);
		return result;
	}

	private void collectFragments(Fragment fragment, List<Fragment> result) {
		if (fragment.getChildFragmentManager().getFragments().size() == 0) {
			return;
		}

		for (Fragment item : fragment.getChildFragmentManager().getFragments()) {
			result.add(item);
			collectFragments(item, result);
		}
	}

	private void setTargetTag(String tag) {
		this.tag = tag;
	}

	private String getTargetTag() {
		return this.tag;
	}

	public boolean isActivityExist() {
		if (getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) {
			return false;
		}
		return true;
	}

	public void setMerBarWhite() {
		QMUIStatusBarHelper.setStatusBarDarkMode(getActivity());
	}

	public void setMerBarBlack() {
		if (DayNightHelper.isNight(getContext())) {
			QMUIStatusBarHelper.setStatusBarDarkMode(getActivity());
		} else {
			QMUIStatusBarHelper.setStatusBarLightMode(getActivity());
		}
	}

	/**
	 * 非分页加载设置数据
	 *
	 * @param mAdapter
	 * @param dataList
	 */
	protected void setAdapterData(BaseQuickAdapter mAdapter, List dataList) {
		if (mAdapter.getEmptyView() == null) {
			mAdapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.common_base_empty, null));
		}
		mAdapter.setNewData(dataList);
	}

	/**
	 * 分页加载设置数据
	 *
	 * @param mAdapter
	 * @param dataList
	 * @param pageNum
	 * @param pageSize
	 */
	protected void setAdapterData(BaseQuickAdapter mAdapter, List dataList, int pageNum, int pageSize) {
		if (mAdapter.getEmptyView() == null) {
			mAdapter.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.common_base_empty, null));
		}

		if (dataList == null) {
			mAdapter.notifyDataSetChanged();
			mAdapter.loadMoreEnd();
			return;
		}

		if (pageNum == 1) {
			mAdapter.setNewData(dataList);
		} else {
			mAdapter.addData(dataList);
		}

		if (dataList.size() < pageSize) {
			//没有更多数据
			mAdapter.loadMoreEnd();
		} else {
			//还有数据，可以继续请求
			mAdapter.loadMoreComplete();
		}
	}
}
