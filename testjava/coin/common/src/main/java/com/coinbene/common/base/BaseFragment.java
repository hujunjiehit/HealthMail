package com.coinbene.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.DayNightHelper;

/**
 * @author mxd on 2017/7/5.
 */

public class BaseFragment extends Fragment implements View.OnClickListener {


	protected Activity mActivity;
	protected View mRootView;

	/**
	 * 是否对用户可见
	 */
	protected boolean mIsVisible;
	/**
	 * 是否加载完成
	 * 当执行完onViewCreated方法后即为true
	 */
	protected boolean mIsPrepare;
//
//    /**
//     * 是否加载完成
//     * 当执行完onViewCreated方法后即为true
//     */
//    protected boolean mIsImmersion;
//
//    protected ImmersionBar mImmersionBar;

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getActivity() != null) {
			DayNightHelper.updateConfig(getActivity(), CBRepository.uiMode);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mActivity = (Activity) context;
	}

	public void setOnClickListener(View... views) {
		if (views == null || views.length <= 0) return;
		for (View view : views) {
			view.setOnClickListener(this);
		}
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mActivity = null;
	}
}
