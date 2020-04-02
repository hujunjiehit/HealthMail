package com.coinbene.common.base;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.coinbene.common.BuildConfig;
import com.coinbene.common.R;
import com.coinbene.common.utils.LockUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.lzy.okgo.OkGo;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.objectbox.reactive.DataSubscriptionList;

import static com.qmuiteam.qmui.util.QMUIStatusBarHelper.getStatusbarHeight;

/**
 * Created by june
 * on 2019-09-12
 */
public abstract class CoinbeneBaseActivity extends BaseActivity {

	protected Unbinder mUnbinder;
	protected QMUITopBarLayout mTopBar;
	protected UserStatus mUserStatus;

	protected DataSubscriptionList subscriptionList;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ARouter.getInstance().inject(this);
		if (subscriptionList == null) {
			subscriptionList = new DataSubscriptionList();
		}
		try {
			int layoutResID = initLayout();
			//如果initView返回0,则不会调用setContentView(),当然也不会 Bind ButterKnife
			if (layoutResID != 0) {
				setContentView(layoutResID);
				//绑定到butterknife
				mUnbinder = ButterKnife.bind(this);
			}
		} catch (Exception e) {
			if (e instanceof InflateException) throw e;
			e.printStackTrace();
			if (BuildConfig.DEBUG)
				ToastUtil.show(e.getMessage());
		}

		mTopBar = (QMUITopBarLayout) findViewById(R.id.top_bar);

		if (mTopBar != null) {
			mTopBar.addLeftImageButton(R.drawable.res_icon_back, R.id.iv_left_image).setOnClickListener(v -> onBack());
			mTopBar.setBackgroundColor(getResources().getColor(R.color.transparent));

			QMUIWindowInsetLayout.LayoutParams layoutParams = (QMUIWindowInsetLayout.LayoutParams) mTopBar.getLayoutParams();
			layoutParams.topMargin = getStatusbarHeight(this);
			mTopBar.setLayoutParams(layoutParams);
		} else {
			setFitsSystemWindows(true);
		}

		initView();

		setListener();

		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mUserStatus = ServiceRepo.getUserService().getUserStatus();
		if (needLock()) {
			//需要解锁
			switch (ServiceRepo.getUserService().getUserStatus()) {
				case UN_LOGIN:
					//需要锁住的页面,如果未登陆需要跳转到登陆页面
					//如果用户不登陆，按返回键，需要回到行情页面
					ARouter.getInstance().build(RouteHub.User.loginActivity)
							.withBoolean("forceQuit", true)
							.navigation(this);
					break;
				case LOCKED:
					LockUtils.showLockPage(this);
					break;
				case LOGIN:
					break;
			}
		}
	}

	/**
	 * @param text 文案
	 *             为标题栏设置右侧文字
	 */
	protected Button setRightTextForTitleBar(String text) {

		if (mTopBar == null) {
			return new Button(this);
		}

		return mTopBar.addRightTextButton(text, R.id.res_title_right_text);
	}

	/**
	 * 点击标题栏返回
	 */
	protected void onBack() {
		finish();
	}

	public abstract @LayoutRes
	int initLayout();

	public abstract void initView();

	public abstract void setListener();

	public abstract void initData();

	public abstract boolean needLock();

	@Override
	protected void onDestroy() {
		super.onDestroy();

		//先取消请求再解绑
		OkGo.getInstance().cancelTag(this);

		if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
			mUnbinder.unbind();
		}
		this.mTopBar = null;
		this.mUnbinder = null;

		if (subscriptionList != null) {
			subscriptionList.cancel();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		onBack();
	}

	/**
	 * 非分页加载设置数据
	 *
	 * @param mAdapter
	 * @param dataList
	 */
	protected void setAdapterData(BaseQuickAdapter mAdapter, List dataList) {
		if (mAdapter.getEmptyView() == null) {
			mAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.common_base_empty, null));
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
			mAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.common_base_empty, null));
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

	public void setTopbarMargin(QMUITopBar topBar) {
		//给topBar设置topMargin
		if (topBar.getParent() instanceof FrameLayout) {
			FrameLayout.LayoutParams layoutParams = (QMUICollapsingTopBarLayout.LayoutParams) topBar.getLayoutParams();
			layoutParams.topMargin = getStatusbarHeight(this);
			topBar.setLayoutParams(layoutParams);
		}
	}
}
