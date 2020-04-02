package com.coinbene.manbiwang.contract.newcontract.orderlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.coinbene.common.Constants;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.newcontract.ContractViewModel;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContractUnitChange extends RadioGroup {
	@BindView(R2.id.rb_coin)
	RadioButton rbCoin;
	@BindView(R2.id.rb_number)
	RadioButton rbNumber;
	@BindView(R2.id.rg_parent)
	RadioGroup rgParent;
	private Context mContext;

	public ContractUnitChange(Context context) {
		super(context);
		initView(context);
	}

	public ContractUnitChange(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {

		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.layout_change_unit, this, true);
		ButterKnife.bind(this);
		if (isInEditMode()) {
			return;
		}
		initLisenter();

	}

	private void initLisenter() {

		ContractViewModel contractViewModel = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(ContractViewModel.class);
		rgParent.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.rb_coin) {
				if (ServiceRepo.getUserService().isLogin())
					updateUnit("usdtContract_tradeUnit", String.valueOf(1));
				SpUtil.setContractUsdtUnitSwitch(1);
				contractViewModel.getUnitType().postValue(1);
			} else if (checkedId == R.id.rb_number) {
				if (ServiceRepo.getUserService().isLogin())
					updateUnit("usdtContract_tradeUnit", String.valueOf(0));
				SpUtil.setContractUsdtUnitSwitch(0);
				contractViewModel.getUnitType().postValue(0);
			}
		});


		contractViewModel.getUnitType().observe((LifecycleOwner) getContext(), integer -> {
			if (integer == 0) {
				rbNumber.setChecked(true);
			} else {
				rbCoin.setChecked(true);
			}
		});

	}

	private void updateUnit(String key, String value) {
		HttpParams params = new HttpParams();
		params.put("settingKey", key);
		params.put("settingValue", value);

		OkGo.<BaseRes>post(Constants.UPDATE_USER_CONFIG).params(params).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				SpUtil.setContractUsdtUnitSwitch(Integer.valueOf(value));
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	public void setContractBase(String symbol) {
		rbCoin.setText(TradeUtils.getUsdtContractBase(symbol));
	}
}
