package com.coinbene.game;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.datacollection.SchemeFrom;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.banner.BannerAdapter;
import com.coinbene.game.battle.BattleGameManager;
import com.coinbene.game.options.OptionsDelegate;
import com.coinbene.game.view.JoinCommunityDialog;
import com.coinbene.game.view.ThumbnailDialog;
import com.coinbene.manbiwang.model.http.BannerList;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIAppBarLayout;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.List;

import butterknife.BindView;
import me.erwa.sourceset.view.banner.BannerView;

/**
 * ding
 * 2019-09-09
 * com.coinbene.manbiwang.modules.game
 */
public class GameFragment extends CoinbeneBaseFragment {
	@BindView(R2.id.btn_StartOptions)
	QMUIRoundButton btnStartOptions;
	@BindView(R2.id.btn_JoinCommunity)
	QMUIRoundButton btnJoinCommunity;
	@BindView(R2.id.layout_option)
	ConstraintLayout layoutOption;
	@BindView(R2.id.img_OptionGif)
	ImageView imgOptionGif;
	@BindView(R2.id.img_BattleGif)
	ImageView imgBattleGif;
	@BindView(R2.id.btn_StartBattle)
	QMUIRoundButton btnStartBattle;
	@BindView(R2.id.tv_Battle)
	TextView tvBattle;
	@BindView(R2.id.layout_Battle)
	ConstraintLayout layoutBattle;
	@BindView(R2.id.layout_GameCooperation)
	LinearLayout layoutGameCooperation;
	@BindView(R2.id.layout_JoinCommunity)
	LinearLayout layoutJoinCommunity;
	@BindView(R2.id.new_banner)
	BannerView mBanner;
	@BindView(R2.id.layout_AppBar)
	QMUIAppBarLayout layoutAppBar;
	@BindView(R2.id.toolbar)
	QMUITopBar toolbar;
	private BannerAdapter bannerAdapter;

	public static GameFragment init() {
		return new GameFragment();
	}

	@Override
	public int initLayout() {
		return R.layout.fragment_game;
	}

	@Override
	public void initView(View rootView) {
		toolbar.setTitle(R.string.tab_game);

		//Glide加圆角加载Gif图
		int corners = QMUIDisplayHelper.dp2px(rootView.getContext(), 3);
		RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(corners));
		GlideUtils.asGif(imgOptionGif, R.drawable.res_gif_options, options);
		GlideUtils.asGif(imgBattleGif, R.drawable.res_gif_battle, options);

		bannerAdapter = new BannerAdapter();
		mBanner.setBannerViewImpl(bannerAdapter);

		//猜涨跌关闭
		if (!SwitchUtils.isOpenOPT()) {
			layoutOption.setVisibility(View.GONE);
		}

		//大作战关闭
		if (!SwitchUtils.isOpenBattle()) {
			layoutBattle.setVisibility(View.GONE);
		}
	}

	@Override
	public void setListener() {
		btnStartOptions.setOnClickListener(v -> startOptions());
		btnStartBattle.setOnClickListener(v -> startBattle());

		btnJoinCommunity.setOnClickListener(v -> {
			JoinCommunityDialog dialog = new JoinCommunityDialog(btnJoinCommunity.getContext());
			dialog.show();
		});

		imgOptionGif.setOnClickListener(v -> {
			ThumbnailDialog dialog = ThumbnailDialog.init();
			dialog.setThumbnailRes(R.drawable.res_gif_options);
			dialog.show(getChildFragmentManager(), "options");
		});

		imgBattleGif.setOnClickListener(v -> {
			ThumbnailDialog dialog = ThumbnailDialog.init();
			dialog.setThumbnailRes(R.drawable.res_gif_battle);
			dialog.show(getChildFragmentManager(), "battle");
		});

		bannerAdapter.setBannerListener(url -> {
			Bundle bundle = new Bundle();
			bundle.putString("SchemeFrom", SchemeFrom.APP_BANNER_GAME.name());
			UIBusService.getInstance().openUri(getContext(), UrlUtil.parseUrl(url), bundle);
		});

		int diffChangeStatusBar = QMUIDisplayHelper.dp2px(getContext(), 184 - 90) - QMUIStatusBarHelper.getStatusbarHeight(getContext());
		layoutAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
			if (Math.abs(verticalOffset) - diffChangeStatusBar >= 0) {
				//设置状态栏黑色字体与图标
				toolbar.setVisibility(View.VISIBLE);
				setMerBarBlack();
			} else {
				//设置状态栏白色字体与图标
				toolbar.setVisibility(View.GONE);
				setMerBarWhite();
			}
		});
	}

	@Override
	public void initData() {
		getGameBanner();
	}

	@Override
	public void onFragmentHide() {
		if (mBanner != null) {
			mBanner.stopAutoScroll();
		}
	}

	@Override
	public void onFragmentShow() {
		if (mBanner != null) {
			mBanner.startAutoScroll();
		}
	}

	/**
	 * 开启期权
	 */
	@NeedLogin(jump = true)
	private void startOptions() {
		OptionsDelegate.login();
		OptionsDelegate.startOptions(btnStartOptions.getContext());
	}

	/**
	 * 开启大作战
	 */
	@NeedLogin(jump = true)
	private void startBattle() {
		BattleGameManager.play(btnStartBattle.getContext());
	}


	/**
	 * 获取游戏Banner
	 */
	private void getGameBanner() {
		OkGo.<BannerList>get(Constants.CONTENT_GET_BANNER_LIST).params("position", "APP-GAME").tag(this).execute(new NewJsonSubCallBack<BannerList>() {
			@Override
			public void onSuc(Response<BannerList> response) {
				List<BannerList.DataBean> data = response.body().getData();
				if (data == null || data.size() == 0) {
					return;
				}

				bannerAdapter.setData(data);
				mBanner.doRecreate();
			}

			@Override
			public void onE(Response<BannerList> response) {

			}
		});
	}
}
