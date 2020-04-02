package com.coinbene.manbiwang.user.preference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.TradePairGroupController;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.SiteHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.model.http.SelectorSiteItem;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.preference.adapter.SiteSelectorBinder;

import java.util.List;

import butterknife.BindView;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

public class SiteSelectorActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.site_recycler)
	RecyclerView site_recycler;
	private MultiTypeAdapter mContentAdapter;
	@BindView(R2.id.menu_title_tv)
	TextView titleView;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.menu_right_tv)
	TextView menuRightTv;
	private String lastSite, newSiteCode;

	public static void startMe(Activity activity, int code) {
		Intent intent = new Intent(activity, SiteSelectorActivity.class);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public int initLayout() {
		return R.layout.settings_activity_site_selector;
	}

	@Override
	public void initView() {
		backView.setOnClickListener(v -> finish());
		titleView.setText(R.string.setting_site_label);
		menuRightTv.setText(R.string.menu_name_done);
		menuRightTv.setTextColor(getResources().getColor(R.color.res_blue));
		menuRightTv.setVisibility(View.VISIBLE);

		mContentAdapter = new MultiTypeAdapter();
		SiteSelectorBinder siteBinder = new SiteSelectorBinder();
		mContentAdapter.register(SelectorSiteItem.class, siteBinder);

		menuRightTv.setOnClickListener(v -> {
			mContentAdapter.getItems();
			List<SelectorSiteItem> items = (List<SelectorSiteItem>) mContentAdapter.getItems();
			SelectorSiteItem siteItem = null;
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).isSelected) {
					siteItem = items.get(i);
					break;
				}
			}
			if (siteItem == null) {
				finish();
				return;
			}

			lastSite = SpUtil.get(v.getContext(), SpUtil.PRE_SITE_SELECTED, "");
			if (siteItem.code.equals(lastSite)) {
				finish();
				return;
			}
			newSiteCode = siteItem.code;
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setMessage(R.string.site_change_message);
			builder.setPositiveButton(getString(R.string.btn_ok), (dialog16, which) -> {
				updateConfig();
			});
			builder.setNegativeButton(getString(R.string.btn_cancel), (dialog13, which) -> dialog13.dismiss());
			AlertDialog dialog = builder.create();
			dialog.show();
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ResourceProvider.getColor(this,R.color.res_blue));
			dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ResourceProvider.getColor(this,R.color.res_blue));
		});

		site_recycler.setLayoutManager(new LinearLayoutManager(this));
		site_recycler.setAdapter(mContentAdapter);

		getData();
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

	private void getData() {
		String site_str = SpUtil.get(this, SpUtil.PRE_SITE_SELECTED, "");

		List<String> siteList = SiteHelper.getSupportSiteList(this);

		if (siteList.size() == 0) {
			return;
		}

		Items itemslist = new Items();

		for (int i = 0; i < siteList.size(); i++) {

			String[] siteSplitStr = siteList.get(i).split(",");
			if (siteSplitStr.length != 2) {
				continue;
			}
			SelectorSiteItem item = new SelectorSiteItem();
			item.title = siteSplitStr[0];
			item.code = siteSplitStr[1];


			if (TextUtils.isEmpty(site_str) && i == 0) {
				item.isSelected = true;
			} else if (site_str.equals(item.code)) {
				item.isSelected = true;
			}

			itemslist.add(item);
		}
		mContentAdapter.setItems(itemslist);
		mContentAdapter.notifyDataSetChanged();
	}


	/**
	 * 获取当前语言环境下，otc功能是否可以使用
	 */
	private void updateConfig() {
		SiteController.getInstance().updateSiteName(newSiteCode);

		if (ServiceRepo.getAppService() != null) {
			ServiceRepo.getAppService().getAppConfig(CBRepository.getLifeCallback().getCurrentAcitivty(), result -> {
				if (result) {
					TradePairGroupController.getInstance().clearTradePairGroupTable();
					SiteController.getInstance().updateSiteName(newSiteCode);
					SpUtil.setNotice(null);
					SpUtil.setBanners(null);
					SpUtil.setAppConfig(null);

					//WebSocketManager.getInstance().sendChangeSiteLang();

					SpUtil.put(CBRepository.getContext(), SpUtil.TRADE_PAIR_HASH, "");
					TradePairOptionalController.getInstance().removeAll();

					//切换完站点回到行情页面
					Bundle bundle = new Bundle();
					bundle.putString("tab", Constants.TAB_HOME);
					UIBusService.getInstance().openUri(SiteSelectorActivity.this, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);

					//清空用户信息，同时拉起登陆界面
					CommonUtil.exitLoginClearData();
					ARouter.getInstance().build(RouteHub.User.loginActivity).navigation(SiteSelectorActivity.this);

					//recreate MainActivity
					CBRepository.getLifeCallback().recreateAll();

				} else {
					SiteController.getInstance().updateSiteName(lastSite);
					List<SelectorSiteItem> items = (List<SelectorSiteItem>) mContentAdapter.getItems();
					for (int i = 0; i < items.size(); i++) {
						if (items.get(i).code.equals(lastSite)) {
							items.get(i).isSelected = true;
						} else {
							items.get(i).isSelected = false;
						}
					}
					mContentAdapter.notifyDataSetChanged();
				}
			});
			ServiceRepo.getAppService().getOtcConfig();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
