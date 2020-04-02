package com.coinbene.manbiwang.contract.newcontract;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.model.http.ContractLeverModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContractTopLayout extends ConstraintLayout {
	@BindView(R2.id.iv_change_symbol)
	ImageView ivChangeSymbol;
	@BindView(R2.id.tv_symbol)
	TextView tvSymbol;
	@BindView(R2.id.tv_position_model)
	TextView tvPositionModel;
	@BindView(R2.id.iv_contract_tip)
	ImageView ivContractTip;
	@BindView(R2.id.iv_kline)
	ImageView ivKline;
	@BindView(R2.id.iv_more)
	ImageView ivMore;
	private Context mContext;
	private ContractTopClickLisenter contractTopClickLisenter;
	private String marginSetting = Constants.MODE_FIXED;
	private String marginMode;

	public void setContractTopClickLisenter(ContractTopClickLisenter contractTopClickLisenter) {
		this.contractTopClickLisenter = contractTopClickLisenter;
	}

	public ContractTopLayout(Context context) {
		super(context);
		initView(context);
	}


	public ContractTopLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ContractTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}


	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.layout_contract_top, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}


		initLisenter();
	}


	private void initLisenter() {
		tvPositionModel.setOnClickListener(v -> {
			if (contractTopClickLisenter != null) {
				contractTopClickLisenter.showChangeMarginMode();
			}
		});


		ivContractTip.setOnClickListener(v -> {
			if (contractTopClickLisenter != null) {
				contractTopClickLisenter.showContractPrams();
			}
		});


		tvSymbol.setOnClickListener(v -> {
			if (contractTopClickLisenter != null) {
				contractTopClickLisenter.onChangeSymbole();
			}
		});

		ivChangeSymbol.setOnClickListener(v -> {
			if (contractTopClickLisenter != null) {
				contractTopClickLisenter.onChangeSymbole();
			}
		});
		ivKline.setOnClickListener(v -> {
			if (contractTopClickLisenter != null) {
				contractTopClickLisenter.goKline();
			}
		});
		ivMore.setOnClickListener(v -> {
			if (contractTopClickLisenter != null) {
				contractTopClickLisenter.showMore(v);
			}
		});

	}

	public void setData(String currentSymbol) {
		tvSymbol.setText(String.format(getContext().getString(R.string.forever_no_delivery), TradeUtils.getContractBase(currentSymbol)));
		setMarginText(marginSetting);
	}


	public void setMarginSetting(ContractLeverModel.DataBean data) {
		this.marginMode = data.getMarginMode();
		if (!TextUtils.isEmpty(marginSetting) && marginSetting.equals(data.getMarginModeSetting())) {
			return;
		}
		if (!TextUtils.isEmpty(data.getMarginModeSetting())) {
			setMarginText(data.getMarginModeSetting());
		}
//		}
	}


	public void setMarginText(String marginMode) {
		this.marginSetting = marginMode.equals(Constants.MODE_CROSSED) ? Constants.MODE_CROSSED : Constants.MODE_FIXED;
		tvPositionModel.setText(marginMode.equals(Constants.MODE_CROSSED) ? getContext().getString(R.string.crossed_position) : getContext().getString(R.string.gradually_position));
	}


	public String getMarginSetting() {
		return marginSetting;
	}

	public String getMarginMode() {
		return marginMode;
	}

	public interface ContractTopClickLisenter {
		void onChangeSymbole();

		void goKline();

		void showMore(View v);

		void showContractPrams();

		void showChangeMarginMode();

	}
}
