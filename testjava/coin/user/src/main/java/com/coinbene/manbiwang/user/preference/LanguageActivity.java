package com.coinbene.manbiwang.user.preference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.config.ProductConfig;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.TradePairGroupController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.ConfigHelper;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.preference.adapter.LanguageAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;

public class LanguageActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.recyclerView)
	RecyclerView mRecyclerView;
	private static final String TAG = "LanguageActivity";
	private LanguageAdapter adapter;
	private String sLanguageCode;
	private Button btnApply;
	private List<ProductConfig.SupportLanguage> languageList;

	public static void startActivity(Context context) {
		context.startActivity(new Intent(context, LanguageActivity.class));
	}

	@Override
	public int initLayout() {
		return R.layout.activity_language;
	}

	@Override
	public void initView() {
		setTitle(R.string.setting_language_label);
		btnApply = setRightTextForTitleBar(getString(R.string.menu_name_done));
		adapter = new LanguageAdapter();
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		adapter.bindToRecyclerView(mRecyclerView);
	}

	@Override
	public void setListener() {
		adapter.setOnItemClickListener((adapter, view, position) -> {
			ProductConfig.SupportLanguage item = (ProductConfig.SupportLanguage) adapter.getData().get(position);
			if (item.isSelected()) {
				return;
			}

			for (int i = 0; i < adapter.getData().size(); i++) {
				ProductConfig.SupportLanguage model = (ProductConfig.SupportLanguage) adapter.getData().get(i);
				if (i == position) {
					model.setSelected(true);
					sLanguageCode = model.getCode();
				} else {
					model.setSelected(false);
				}
			}

			adapter.notifyDataSetChanged();
		});

		btnApply.setOnClickListener(v -> {
			//选择的语言没有变不用走接口
			if (sLanguageCode.equals(LanguageHelper.getLocaleCode(this))) {
				finish();
				return;
			}

			updateSwitch();
		});
	}

	@Override
	public void initData() {
		getLanguageData();
	}

	@Override
	public boolean needLock() {
		return false;
	}

	/**
	 * 获取语言配置list
	 */
	private void getLanguageData() {
		languageList = ConfigHelper.getSupportLanguage();
		sLanguageCode = LanguageHelper.getLocaleCode(this);

		//配置信息没有拿到
		if (languageList == null || languageList.size() == 0) {
			finish();
			return;
		}

		//全部加入
		for (ProductConfig.SupportLanguage language : languageList) {
			//取语言部分做比对，不取国家
			if (language.getCode().contains(sLanguageCode)) {
				language.setSelected(true);
				sLanguageCode = language.getCode();
			}else {
				language.setSelected(false);
			}
		}

		//不受支持的语言设置为默认选中英语
		if (!LanguageHelper.isSupport()) {
			languageList.get(1).setSelected(true);
		}



		adapter.setNewData(languageList);
	}

	/**
	 * 切换语言后更新开关信息
	 */
	private void updateSwitch() {
		if (ServiceRepo.getAppService() != null) {
			ServiceRepo.getAppService().getAppConfig(this, result -> {
				if (result) {
					TradePairGroupController.getInstance().clearTradePairGroupTable();
					SpUtil.put(CBRepository.getContext(), SpUtil.TRADE_PAIR_HASH, "");

					LanguageHelper.setLocale(CBRepository.getContext(), sLanguageCode);
					LanguageHelper.updateAppConfig(CBRepository.getContext(), sLanguageCode);

					//如果是登录状态通知服务端改变语言，否则关闭页面更新语言
					if (ServiceRepo.getUserService().isLogin()) {
						notifyServerOfLanguageChange();
					} else {
						closePage();
					}

				} else {
					finish();
				}
			});
		}
	}


	private void notifyServerOfLanguageChange() {
		HttpParams httpParams = new HttpParams();

		//日语，韩语单独处理
		String codeParam = LanguageHelper.getProcessedCode(sLanguageCode);

		httpParams.put("lang", codeParam);
		OkGo.<BaseRes>post(Constants.USER_CHANGELANGUAGE).tag(this).params(httpParams).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				closePage();
			}

			@Override
			public BaseRes dealJSONConvertedResult(BaseRes balance30DayModel) {
				return super.dealJSONConvertedResult(balance30DayModel);
			}

			@Override
			public void onE(Response<BaseRes> response) {
				finish();
			}
		});
	}


	/**
	 * 关闭当前页面并更新语言
	 */
	private void closePage() {
		//WebSocketManager.getInstance().sendChangeSiteLang();

		//切换语言回到首页
		Bundle bundle = new Bundle();
		bundle.putString("tab", Constants.TAB_HOME);
		UIBusService.getInstance().openUri(this, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
		CBRepository.getLifeCallback().recreateAll();
		finish();
	}
}
