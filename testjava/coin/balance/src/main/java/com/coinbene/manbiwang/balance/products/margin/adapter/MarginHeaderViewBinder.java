package com.coinbene.manbiwang.balance.products.margin.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.common.widget.app.ClearEditText;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.manbiwang.model.http.MarginTotalInfoModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.margin.BillingDetailsActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by june
 * on 2019-08-17
 */
public class MarginHeaderViewBinder extends ItemViewBinder<MarginTotalInfoModel.DataBean, MarginHeaderViewBinder.ViewHolder> {

	private boolean lastConiSwitchChecked = false;
	private MarginHeaderListener marginHeaderListener;
	ViewHolder mViewHolder;
	private boolean clearFocus = false;


	public void setMarginHeaderListener(MarginHeaderListener marginHeaderListener) {
		this.marginHeaderListener = marginHeaderListener;
	}

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.item_margin_header, parent, false);
		return new ViewHolder(root);
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MarginTotalInfoModel.DataBean accountDataBean) {
		mViewHolder = holder;
		//设置资产头部数据
		if (AssetManager.getInstance().isHideValue()) {
			holder.mTvAccountBalance.setText("*****");
			holder.mTvAccountBalanceLocal.setText("*****");
		} else {
			holder.mTvAccountBalance.setText(String.format("%s BTC", accountDataBean.getBtcTotalPreestimate()));
			holder.mTvAccountBalanceLocal.setText(String.format("≈ %s %s", StringUtils.getCnyReplace(accountDataBean.getCurrencySymbol()), accountDataBean.getLocalTotalPreestimate()));
		}

		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo == null) {
			return;
		}
		String discountSwitchDes = userInfo.discountSwitchDes;
		if (TextUtils.isEmpty(discountSwitchDes)) {
			holder.coniLayout.setVisibility(View.GONE);
		} else {
			holder.coniLayout.setVisibility(View.VISIBLE);
			holder.coniTxtView.setText(discountSwitchDes);
			if (TextUtils.isEmpty(userInfo.coniDiscountSwitch)) {
				lastConiSwitchChecked = false;
			} else {
				lastConiSwitchChecked = userInfo.coniDiscountSwitch.toLowerCase().equals("on");
			}
			holder.mSwitchBtn.setChecked(lastConiSwitchChecked);
		}

		////coni支付手续付费开关点击事件
		holder.mSwitchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (lastConiSwitchChecked == isChecked) {
				return;
			}
			lastConiSwitchChecked = isChecked;
			submmitConiSwitchInfo(isChecked);
		});

		//账单点击事件
		holder.mTvMarginBill.setOnClickListener(v -> BillingDetailsActivity.startActivity(holder.mTvMarginBill.getContext()));


		holder.etSearchCoin.clearTextChangedListeners();
		BaseTextWatcher textWatcher = new BaseTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				if(marginHeaderListener!=null){
					marginHeaderListener.doFilter(s.toString());
				}
			}
		};
		holder.etSearchCoin.addTextChangedListener(textWatcher);

		holder.etSearchCoin.setOnClickListener(v -> {
			if (holder.etSearchCoin.hasFocus()) {
				holder.etSearchCoin.setCursorVisible(true);
			}
		});

		if (clearFocus) {
			holder.etSearchCoin.clearFocus();
			holder.etSearchCoin.setCursorVisible(false);
			clearFocus = false;
			mViewHolder.balanceCoinHeader.setFocusable(false);
			mViewHolder.balanceCoinHeader.setFocusableInTouchMode(false);
		}
	}

	public void clearFocus() {
		if (mViewHolder != null && mViewHolder.etSearchCoin != null) {
			mViewHolder.etSearchCoin.clearFocus();
			clearFocus = true;
			mViewHolder.balanceCoinHeader.setFocusable(true);
			mViewHolder.balanceCoinHeader.setFocusableInTouchMode(true);
		}
	}

	public void submmitConiSwitchInfo(boolean isChecked) {
		HttpParams httpParams = new HttpParams();
		String url = Constants.USER_CONI_SWITHC + "/" + (isChecked ? "on" : "off");
		OkGo.<BaseRes>post(url).tag(this).params(httpParams).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				if (response.body().isSuccess()) {
					updateConiView(isChecked);
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {
				lastConiSwitchChecked = !isChecked;
				updateConiView(!isChecked);
			}

		});
	}

	public interface MarginHeaderListener {
		void doFilter(String s);
	}

	private void updateConiView(boolean isChecked) {
		UserInfoController.getInstance().updateDiscountSwitchStatus(isChecked ? "on" : "off");
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_account_balance)
		TextView mTvAccountBalance;
		@BindView(R2.id.tv_account_balance_local)
		TextView mTvAccountBalanceLocal;
		@BindView(R2.id.et_search_coin)
		ClearEditText etSearchCoin;
		@BindView(R2.id.layout_coin_account)
		LinearLayout mLayoutCoinAccount;
		@BindView(R2.id.coni_txt)
		TextView coniTxtView;
		@BindView(R2.id.balance_coin_header)
		RelativeLayout balanceCoinHeader;

		@BindView(R2.id.tb_zhekou)
		Switch mSwitchBtn;
		@BindView(R2.id.coni_layout)
		View coniLayout;
		@BindView(R2.id.tv_margin_bill)
		TextView mTvMarginBill;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
