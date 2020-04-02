package com.coinbene.common.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.activities.fragment.SearchPairFragment;
import com.coinbene.common.activities.fragment.SelectPairFragment;
import com.coinbene.common.base.CoinbeneBaseActivity;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-09-07
 * <p>
 * 选择，搜索交易对
 */
public class SelectSearchPairActivity extends CoinbeneBaseActivity {

	public static final int TYPE_SEARCH = 1;  //搜索交易对
	public static final int TYPE_SELECT = 2;  //选择交易对

	@BindView(R2.id.fragment_container)
	FrameLayout mFragmentContainer;

	private int type;
	private String fenmuAsset;

	private SearchPairFragment mSearchPairFragment;
	private SelectPairFragment mSelectPairFragment;

	//当前的Fragment
	private Fragment mCurFragment = new Fragment();

	public static void startMe(Context context, Bundle bundle) {
		Intent intent = new Intent(context, SelectSearchPairActivity.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.common_activity_select_search_pair;
	}

	@Override
	public void initView() {
		if (getIntent() == null) {
			return;
		}

		mTopBar.setTitle(R.string.title_trade_pair);

		mTopBar.addRightImageButton(R.drawable.res_icon_search, R.id.res_title_right_text)
				.setOnClickListener(v ->  switchToSearchPair());

		type = getIntent().getIntExtra("type", TYPE_SEARCH);
		fenmuAsset = getIntent().getStringExtra("fenmuAsset");

		if (type == TYPE_SEARCH) {
			switchToSearchPair();
		} else if (type == TYPE_SELECT) {
			switchToSelectPair();
		}
	}

	/**
	 * 切换到交易对选择fragment
	 */
	public void switchToSelectPair() {
		mTopBar.setVisibility(View.VISIBLE);


		if (mSelectPairFragment == null) {
			mSelectPairFragment = SelectPairFragment.newInstance(fenmuAsset);
		}

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (mCurFragment == mSelectPairFragment) {
			return;
		}

		if (!mSelectPairFragment.isAdded()) {//如果要显示的targetFragment没有添加过
			transaction.hide(mCurFragment)//隐藏当前Fragment
					.add(R.id.fragment_container, mSelectPairFragment);//添加targetFragment
		} else {
			//如果要显示的targetFragment已经添加过
			transaction.hide(mCurFragment) //隐藏当前Fragment
					.show(mSelectPairFragment);//显示targetFragment
		}

		transaction.commitAllowingStateLoss();

		//更新当前Fragment为targetFragment
		mCurFragment = mSelectPairFragment;
	}

	/**
	 * 切换到交易对搜索fragment
	 */
	private void switchToSearchPair() {
		mTopBar.setVisibility(View.GONE);

		if (mSearchPairFragment == null) {
			mSearchPairFragment = SearchPairFragment.newInstance(type);
		}

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (mCurFragment == mSearchPairFragment) {
			return;
		}

		if (!mSearchPairFragment.isAdded()) {//如果要显示的targetFragment没有添加过
			transaction.hide(mCurFragment)//隐藏当前Fragment
					.add(R.id.fragment_container, mSearchPairFragment);//添加targetFragment
		} else {
			//如果要显示的targetFragment已经添加过
			transaction.hide(mCurFragment) //隐藏当前Fragment
					.show(mSearchPairFragment);//显示targetFragment
		}

		transaction.commitAllowingStateLoss();

		//更新当前Fragment为targetFragment
		mCurFragment = mSearchPairFragment;
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return false;
	}
}
