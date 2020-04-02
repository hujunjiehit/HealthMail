package com.coinbene.manbiwang.balance.products.spot.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.common.widget.app.ClearEditText;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.CoinTotalInfoModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by june
 * on 2019-08-16
 */
public class CoinHeaderViewBinder extends ItemViewBinder<CoinTotalInfoModel.DataBean, CoinHeaderViewBinder.ViewHolder> {

	private boolean lastConiSwitchChecked = false;
	private AssetHeaderListener assetHeaderListener;
	ViewHolder mViewHolder;

	private boolean clearFocus = false;

	@NonNull
	@Override
	protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
		View root = inflater.inflate(R.layout.item_coin_header, parent, false);
		return new ViewHolder(root);
	}

	public void setAssetHeaderListener(AssetHeaderListener assetHeaderListener) {
		this.assetHeaderListener = assetHeaderListener;
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull CoinTotalInfoModel.DataBean accountDataBean) {
		mViewHolder = holder;

		//设置资产头部数据
		if (AssetManager.getInstance().isHideValue()) {
			holder.tvAccountBalance.setText("*****");
			holder.tvAccountBalanceLocal.setText("*****");
		} else {
			holder.tvAccountBalance.setText(String.format("%s BTC", accountDataBean.getBtcTotalPreestimate()));
			holder.tvAccountBalanceLocal.setText(String.format("≈ %s %s", StringUtils.getCnyReplace(accountDataBean.getCurrencySymbol()), accountDataBean.getLocalTotalPreestimate()));
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

		holder.mSwitchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (lastConiSwitchChecked == isChecked) {
				return;
			}
			lastConiSwitchChecked = isChecked;
			submmitConiSwitchInfo(isChecked);
		});

		holder.checkBox.setChecked(SpUtil.getHideAssetZero());

		holder.llCheckbox.setOnClickListener(v -> {
			holder.checkBox.setChecked(!holder.checkBox.isChecked());
			if (assetHeaderListener != null) {
				assetHeaderListener.onAssetHideChanged(holder.checkBox.isChecked());
			}
		});

		holder.etSearchCoin.clearTextChangedListeners();
		BaseTextWatcher textWatcher = new BaseTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				if (assetHeaderListener != null) {
					assetHeaderListener.doFilter(s.toString());
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

	private void updateConiView(boolean isChecked) {
		UserInfoController.getInstance().updateDiscountSwitchStatus(isChecked ? "on" : "off");
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R2.id.tv_account_balance)
		TextView tvAccountBalance;
		@BindView(R2.id.tv_account_balance_local)
		TextView tvAccountBalanceLocal;
		@BindView(R2.id.et_search_coin)
		ClearEditText etSearchCoin;
		@BindView(R2.id.check_box)
		CheckBox checkBox;
		@BindView(R2.id.hide_asset_tv)
		TextView hideAssetTv;
		@BindView(R2.id.ll_checkbox)
		LinearLayout llCheckbox;
		@BindView(R2.id.balance_coin_header)
		RelativeLayout balanceCoinHeader;

		@BindView(R2.id.coni_layout)
		View coniLayout;
		@BindView(R2.id.tb_zhekou)
		Switch mSwitchBtn;
		@BindView(R2.id.coni_txt)
		TextView coniTxtView;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public interface AssetHeaderListener {
		void onAssetHideChanged(boolean hide);

		void doFilter(String s);
	}
}
