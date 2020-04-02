package com.coinbene.manbiwang.contract.contractbtc.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.ContractPositionListModel;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.widget.LoadMoreView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractbtc.adapter.HoldPositionBtcAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("DefaultLocale")
public class ContractBottomBtcLayout extends LinearLayout {


	@BindView(R2.id.rb_hold_position)
	TextView tvHoldPosition;
	@BindView(R2.id.rl_hold_position)
	RecyclerView rlHoldPosition;


	private HoldPositionBtcAdapter mHoldPositionBinder;
	private boolean isRedRise = SwitchUtils.isRedRise();

	public ContractBottomBtcLayout(Context context) {
		super(context);
	}

	public ContractBottomBtcLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public ContractBottomBtcLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
//        int i = R.layout.fragment_contract_bottom_layout;
		initView();
		initData();
	}


	public void setClickHoldPostion(HoldPositionBtcAdapter.ClickHoldPostionListener clickHoldPostion) {
		mHoldPositionBinder.setClickHoldPostionListener(clickHoldPostion);
	}

	private void initView() {

		//解决 卡顿问题
		rlHoldPosition.setHasFixedSize(true);
		rlHoldPosition.setNestedScrollingEnabled(false);
		rlHoldPosition.setFocusable(false);
	}

	private void initData() {
		tvHoldPosition.setText(String.format("%s[0]", getResources().getString(R.string.hold_position)));
		rlHoldPosition.setLayoutManager(new LinearLayoutManager(getContext()));
		mHoldPositionBinder = new HoldPositionBtcAdapter();
		mHoldPositionBinder.bindToRecyclerView(rlHoldPosition);
		mHoldPositionBinder.setEnableLoadMore(true);
		mHoldPositionBinder.setUpFetchEnable(false);
		mHoldPositionBinder.setLoadMoreView(new LoadMoreView());
		mHoldPositionBinder.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.common_base_empty, null));
		mHoldPositionBinder.disableLoadMoreIfNotFullPage();


	}


	/**
	 * 持仓数据填充
	 *
	 * @param listData
	 */

	public void setHoldPositionData(List<ContractPositionListModel.DataBean> listData) {
		mHoldPositionBinder.setNewData(listData);
		if (listData.size() > 0) {
			tvHoldPosition.setText(String.format("%s[%d]", getResources().getString(R.string.hold_position), listData.size()));
		} else {
			tvHoldPosition.setText(String.format("%s[%d]", getResources().getString(R.string.hold_position), 0));
		}


	}

//	public void setHoldPositionSize(List<ContractPositionListModel.DataBean> listData) {
//
//	}


	public void setNoUser() {
		tvHoldPosition.setText(String.format("%s[0]", getResources().getString(R.string.hold_position)));
		mHoldPositionBinder.setNewData(null);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void setRedRase(boolean isRedRise) {
		this.isRedRise = isRedRise;
		if (mHoldPositionBinder != null) {
			mHoldPositionBinder.setRedRaise(isRedRise);
		}
	}
}
