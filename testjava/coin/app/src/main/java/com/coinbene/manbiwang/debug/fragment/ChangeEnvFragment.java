package com.coinbene.manbiwang.debug.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.config.ProductConfig;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.ConfigHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChangeEnvFragment extends BaseFragment {

	@BindView(R2.id.tv_desc)
	TextView tvDesc;
	Unbinder unbinder;

	private String[] envDesc = {
			"dev环境",
			"test环境",
			"staging环境",
			"线上环境"
	};

	private Spinner mSpinner;
	private Button mBtnRestart;

	private int preIndex;
	private int curIndex;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.settings_fragment_change_environment, container, false);
		unbinder = ButterKnife.bind(this, mRootView);
		init();
		return mRootView;
	}

	private void init() {
		mBtnRestart = mRootView.findViewById(R.id.btn_restart);
		mSpinner = mRootView.findViewById(R.id.spinner);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, envDesc);
		mSpinner.setAdapter(spinnerAdapter);


		curIndex = SpUtil.get(mActivity, SpUtil.CURRENT_ENVIRONMENT, CBRepository.getDefaultEnvironment());
		preIndex = curIndex;
		mSpinner.setSelection(preIndex, true);

		refreshData(curIndex);

		mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				curIndex = position;
				refreshData(curIndex);
				if (preIndex != position) {
					mBtnRestart.setVisibility(View.VISIBLE);
				} else {
					mBtnRestart.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		mBtnRestart.setOnClickListener(v -> {
			SpUtil.put(mActivity, SpUtil.CURRENT_ENVIRONMENT, curIndex);

			CommonUtil.exitLoginClearData();

			SpUtil.setAppConfig(null);

			//清空交易对和hash值
			TradePairInfoController.getInstance().clearTradePairInfo();
			SpUtil.put(CBRepository.getContext(), SpUtil.TRADE_PAIR_HASH, "");

			TradePairOptionalController.getInstance().removeAll();
			//BroadcastUtils.sendFreshOptionalData();

//			Process.killProcess(Process.myPid());

			Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			System.exit(0);
		});
	}

	private void refreshData(int curIndex) {
		StringBuilder sb = new StringBuilder();

		ProductConfig.BaseUrlConfigBean urlConfig = ConfigHelper.getUrlConfig(curIndex);

		sb.append(urlConfig.getEnvName()).append(":\n");

		sb.append("1:").append(urlConfig.getBase_url(CBRepository.getCurrentEnvironment().environmentType)).append("\n");
		sb.append("2:").append(urlConfig.getBase_url_h5(CBRepository.getCurrentEnvironment().environmentType)).append("\n");
		sb.append("3:").append(urlConfig.getBase_url_res()).append("\n");
		sb.append("4:").append(urlConfig.getSpot_websocket()).append("\n");
		sb.append("5:").append(urlConfig.getBtc_contract_websocket()).append("\n");
		sb.append("6:").append(urlConfig.getUsdt_contract_websocket()).append("\n");
		sb.append("7:").append(urlConfig.getOption_broker_id()).append("\n");

		tvDesc.setText(sb.toString());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}
}
