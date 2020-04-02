package com.coinbene.manbiwang.contract.dialog;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.widget.CustomSeekBar;
import com.coinbene.common.widget.input.InputView;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.LeverageLimitRes;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by june
 * on 2019-12-25
 * <p>
 * 选择杠杆倍数的弹窗
 */
public class ChooseLevelDialog extends QMUIDialog {

	@BindView(R2.id.tv_title)
	TextView mTvTitle;
	@BindView(R2.id.input_view)
	InputView mInputView;
	@BindView(R2.id.seek_bar)
	CustomSeekBar mSeekBar;
	@BindView(R2.id.tv_max_open)
	TextView mTvMaxOpen;
	@BindView(R2.id.tv_risk_tips)
	TextView mTvRiskTips;
	@BindView(R2.id.tv_cancel)
	TextView mTvCancel;
	@BindView(R2.id.tv_sure)
	TextView mTvSure;

	String[] tickTextArray;

	private String symbol;
	private int currentLevel;
	private int maxLevel = 0;

	private SparseArray<Integer> leverageMap;

	private int contractType;

	private CallBack mCallBack;

	private ContractUsdtInfoTable usdtInfoTable;

	public ChooseLevelDialog(Context context) {
		super(context);

		setContentView(R.layout.dialog_choose_level_layout);

		ButterKnife.bind(this);

		initView();

		setListener();
	}


	private void initView() {
		//通过反射修改tick_text文字高度
		try {
			Field fieldTickTextsHeight = IndicatorSeekBar.class.getDeclaredField("mTickTextsHeight");
			fieldTickTextsHeight.setAccessible(true);
			int mTickTextsHeight = (int) fieldTickTextsHeight.get(mSeekBar);
			mTickTextsHeight = mTickTextsHeight + QMUIDisplayHelper.dp2px(getContext(), CustomSeekBar.tick_text_top_margin);
			fieldTickTextsHeight.set(mSeekBar, mTickTextsHeight);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	private void setListener() {

		mSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
			@Override
			public void onSeeking(SeekParams seekParams) {

				if(seekParams.progress == mInputView.getValue()) {
					return;
				}
				if (seekParams.progress <= 1) {
					mInputView.setValue(1);
				} else {
					mInputView.setValue(seekParams.progress);
				}
				updateView(mInputView.getValue());
			}

			@Override
			public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
				if (mInputView != null) {
					mInputView.clearFocus();
				}
			}

			@Override
			public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

			}
		});

		mInputView.setValueChangeListener(value -> {
//			if (mSeekBar.getProgress() == value) {
//				return;
//			}
			mSeekBar.setProgress(value <= 1 ? 0 : value);
			updateView(value);
		});

		mTvCancel.setOnClickListener(v -> dismiss());

		mTvSure.setOnClickListener(v -> changeLeverage());
	}

	private void updateView(int value) {
		currentLevel = value;

		mTvRiskTips.setVisibility(value >= 20 ? View.VISIBLE : View.GONE);

		//最大可开
		if (contractType == Constants.CONTRACT_TYPE_BTC) {
			String key = getContext().getResources().getString(R.string.res_max_open_number);
			SpannableString spannableString = new SpannableString(key + " " +  leverageMap.get(value) + getContext().getResources().getString(R.string.number));
			spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.res_textColor_2)), 0 ,key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.res_assistColor_22)), key.length() + 1 ,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			mTvMaxOpen.setText(spannableString);
		} else {
			String key = getContext().getResources().getString(R.string.res_max_open_number);
			if (usdtInfoTable == null) {
				usdtInfoTable = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
			}
			SpannableString spannableString = new SpannableString(key + " " +  TradeUtils.getContractUsdtUnitValue(String.valueOf(leverageMap.get(value)), usdtInfoTable) + TradeUtils.getContractUsdtUnit(usdtInfoTable));
			spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.res_textColor_2)), 0 ,key.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.res_assistColor_22)), key.length() + 1 ,spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			mTvMaxOpen.setText(spannableString);
		}
	}


	@Override
	public void show() {
		//初始化inputView
		mInputView.initInputView(1, maxLevel);
		mInputView.setValue(currentLevel);

		//初始化滑动条
		mSeekBar.setMax(maxLevel);
		if (tickTextArray == null) {
			tickTextArray = new String[6];
		}
		for(int i = 0; i < tickTextArray.length; i++) {
			//滑动条下方的文案
			tickTextArray[i] = (i == 0 ? "1X" : maxLevel/5 * i + "X");
		}
		mSeekBar.customTickTexts(tickTextArray);

		mSeekBar.setProgress(currentLevel);


		super.show();


//		mSeekBar.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mSeekBar.setProgress(currentLevel);
//			}
//		},500);
	}

	public void initDialog(String symbol, String lever) {
		this.contractType = symbol.contains("-") ? Constants.CONTRACT_TYPE_USDT :Constants.CONTRACT_TYPE_BTC;
		this.symbol = symbol;
		this.currentLevel = Tools.parseInt(lever.replace("X",""), 1);

		this.usdtInfoTable = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
	}

	public void showWithCallBack(CallBack callBack) {
		this.mCallBack = callBack;

		String url = contractType == Constants.CONTRACT_TYPE_USDT ? Constants.TRADE_USDT_LEVERAGE_LIMIT : Constants.TRADE_LEVERAGE_LIMIT;
		OkGo.<LeverageLimitRes>get(url)
				.params("symbol", symbol)
				.tag(this)
				.execute(new DialogCallback<LeverageLimitRes>(getOwnerActivity()) {
					@Override
					public void onSuc(Response<LeverageLimitRes> response) {
						if (leverageMap == null) {
							leverageMap = new SparseArray();
						}
						leverageMap.clear();
						maxLevel = 0;
						if (response.body() != null && response.body().getData() != null) {
							for( int i =0 ;i < response.body().getData().size(); i++) {
								LeverageLimitRes.DataBean dataBean = response.body().getData().get(i);
								for (int j = dataBean.getLevelMin(); j <= dataBean.getLevelMax(); j++) {
									leverageMap.put(j, dataBean.getQuantityLimit());
									maxLevel = j > maxLevel ? j : maxLevel;
								}
							}
							if (leverageMap.size() > 0) {
								show();
							}
						}
					}

					@Override
					public void onE(Response<LeverageLimitRes> response) {

					}
				});
	}


	private void changeLeverage() {
		//如果用户未登陆，杠杆倍数保存在本地
		if (!ServiceRepo.getUserService().isLogin()) {
			if (mCallBack != null) {
				mCallBack.onLeverageSelected(currentLevel);
			}
			dismiss();
			return;
		}

		// 如果已经登陆调用后端接口，改变杠杆倍数
		String url = contractType == Constants.CONTRACT_TYPE_USDT ? Constants.CONTRACT_POSTIONMODE_CHANGE_USDT : Constants.CONTRACT_POSTIONMODE_CHANGE;
		HttpParams params = new HttpParams();
		params.put("symbol", symbol);
		params.put("leverageLong", currentLevel);
		params.put("leverageShort", currentLevel);
		OkGo.<BaseRes>post(url).tag(this).params(params).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				dismiss();
				if (mCallBack != null) {
					mCallBack.onLeverageSelected(currentLevel);
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	public interface CallBack {
		void onLeverageSelected(int leverage);
	}
}
