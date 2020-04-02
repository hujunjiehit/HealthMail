package com.coinbene.manbiwang.user.preference;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.ConfigHelper;
import com.coinbene.common.utils.DayNightHelper;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2017/11/28.
 */

public class SettingDefinedActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.defined_push_switch)
	ToggleButton pushSwitch;

	@BindView(R2.id.site_main_layout)
	View siteMainLayout;
	@BindView(R2.id.launague_layout)
	View launageLayout;
	@BindView(R2.id.lanuage_name_tv)
	TextView languageTv;
	@BindView(R2.id.site_name_tv)
	TextView siteNameTv;
	@BindView(R2.id.tv_red_green)
	TextView tv_red_green;
	@BindView(R2.id.auth_line_view)
	View authLineView;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.Klien_color_layout)
	View klineConfig;
	@BindView(R2.id.defined_nigthMode)
	ToggleButton nightMode;
	@BindView(R2.id.kline_img)
	ImageView kline_img;
	@BindView(R2.id.push_switch_line)
	View pushLine;
	@BindView(R2.id.push_switch_layout)
	View switchLayout;
	@BindView(R2.id.layout_DayNight)
	View layoutDayNight;

	private boolean isRedRiseGreenFall;
	public static final int CODE_SITE_SELECT = 14;
	public static final int CODE_LANGUAGE_SELECT = 15;
	private boolean isFirstLoad;

	public static void startMe(Activity activity) {
		Intent intent = new Intent(activity, SettingDefinedActivity.class);
		activity.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.settings_setting_defined;
	}


	@Override
	public void initView() {
		//setSwipeBackEnable(false);//需要监听返回事件，滑动返回的时候，无法监听

		if (!ConfigHelper.nightModeEnable()) {
			layoutDayNight.setVisibility(View.GONE);
		}

		if (!ConfigHelper.getSiteConfig().isEnable()){
			siteMainLayout.setVisibility(View.GONE);
		}

		if (!ConfigHelper.multiLanguageEnable()) {
			launageLayout.setVisibility(View.GONE);
		}

		if (ServiceRepo.getAppService() != null) {
			ServiceRepo.getAppService().updateUserInfo();
		}
		isFirstLoad = true;
		if (DayNightHelper.isNight(this)) {
			nightMode.setChecked(true);
		} else {
			nightMode.setChecked(false);
		}
		isFirstLoad = false;
		if (!LanguageHelper.isKorean(this)) {
			switchLayout.setVisibility(View.GONE);
			pushLine.setVisibility(View.GONE);
		} else {
			boolean isPush = SpUtil.get(this, "IsPush", false);
			pushSwitch.setChecked(isPush);
			pushSwitch.setOnClickListener(this);
			//  监听状态改变存取是否接受推送的状态
			pushSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
				ServiceRepo.getAppService().showPushSwitchDialog(this, isChecked);
			});
		}
		klineConfig.setOnClickListener(this);
		siteMainLayout.setOnClickListener(this);
		launageLayout.setOnClickListener(this);
		backView.setOnClickListener(this);
		languageTv.setText(LanguageHelper.getDisplayName());
		setSiteText();
	}


	@Override
	public void setListener() {
		nightMode.setOnCheckedChangeListener((buttonView, isChecked) -> switchDayNight(isChecked));
	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		isRedRiseGreenFall = SwitchUtils.isRedRise();
		if (isRedRiseGreenFall) {
			setRedRise();
		} else {
			setGreenRise();
		}
	}

	private void setRedRise() {
		tv_red_green.setText(R.string.red_rise_green_fall);
		kline_img.setBackgroundResource(R.drawable.kline_config_red_rise);
	}

	private void setGreenRise() {
		tv_red_green.setText(R.string.green_rise_red_fall);
		kline_img.setBackgroundResource(R.drawable.kline_config_green_rise);
	}

	private void setSiteText() {
		String site_str = SiteController.getInstance().getSiteName();

		String[] siteArray = this.getResources().getStringArray(R.array.site_list);

		if (siteArray.length == 0) {
			return;
		}
		String tempSiteNameStr = "";
		for (int i = 0; i < siteArray.length; i++) {
			String[] siteSplitStr = siteArray[i].split(",");
			if (siteSplitStr.length != 2) {
				continue;
			}

			if (TextUtils.isEmpty(site_str) && i == 0) {
				tempSiteNameStr = siteSplitStr[0];
				break;
			}
			if (site_str.equals(siteSplitStr[1])) {
				tempSiteNameStr = siteSplitStr[0];
				break;
			}
		}
		siteNameTv.setText(tempSiteNameStr);
	}


//	@AddFlowControl
	private void switchDayNight(boolean isChecked) {
		if(isFirstLoad){
			return;
		}

		//夜间模式
		if (isChecked) {
			DayNightHelper.setMode(this, DayNightHelper.DARK);
		} else {
			DayNightHelper.setMode(this, DayNightHelper.LIGHT);
		}
		CBRepository.getLifeCallback().recreateAll();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.site_main_layout) {
			SiteSelectorActivity.startMe(SettingDefinedActivity.this, CODE_SITE_SELECT);
		} else if (v.getId() == R.id.launague_layout) {
			LanguageActivity.startActivity(this);
		} else if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.Klien_color_layout) {
			KLineConfigActivity.startMe(this);
		}
	}
}
