package com.coinbene.manbiwang.user.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.widget.dialog.CommuinityDialog;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.about.adapter.CommunityAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.coinbene.common.Constants.SITE_BR;
import static com.coinbene.common.Constants.SITE_KO;

public class CommunityActivity extends CoinbeneBaseActivity {

	//所有社群
	public static final String COMMUNITY_WECHAT = "wechat";
	public static final String COMMUNITY_SINA = "sina";
	public static final String COMMUNITY_TWITTER = "twitter";
	public static final String COMMUNITY_FACEBOOK = "facebook";
	public static final String COMMUNITY_LINKEDIN = "linkedin";
	public static final String COMMUNITY_YOUTUBE = "youtube";
	public static final String COMMUNITY_INSTAGRAM = "instagram";
	public static final String COMMUNITY_KAKAO = "kakao";
	public static final String COMMUNITY_TELEGRAM = "telegram";


	//    主站社群网址
	private static final String URL_SINA = "https://weibo.com/u/6448353599/";
	private static final String URL_TWITTER = "https://twitter.com/coinbene";
	private static final String URL_FACEBOOK = "https://www.facebook.com/CoinBeneOfficial/";
	private static final String URL_LINKEDIN = "https://www.linkedin.com/company/coinbene-official/";
	private static final String URL_YOUTUBE = "https://m.youtube.com/channel/UCHpwmOUzpRRU4v4L1QrOksA";

	//    韩国站社群网址
	private static final String URL_KO_TELEGRAM = "https://t.me/coinbene_korea";
	private static final String URL_KO_FACEBOOK = "http://facebook.com/coinbenekorea";
	private static final String URL_KO_INSTAGRAM = "http://instagram.com/coinbene_kr";
	private static final String URL_KO_KAKAO = "http://pf.kakao.com/_Hxorxjj";
	private static final String URL_KO_TWITTER = "https://twitter.com/coinbene_kr";

	//    巴西站社群网址
	private static final String URL_BR_TWITTER = "https://twitter.com/coinbenebrasil";
	private static final String URL_BR_FACEBOOK = "https://www.facebook.com/coinbene/";
	private static final String URL_BR_LINKEDIN = "https://www.linkedin.com/company/coinbenebrasil/";
	private static final String URL_BR_YOUTUBE = "https://www.youtube.com/channel/UCHFy0RIgJ5WSriXPtANQqMA";

	@BindView(R2.id.menu_back)
	RelativeLayout menuBack;

	@BindView(R2.id.menu_title_tv)
	TextView menuTitleTv;

	@BindView(R2.id.rv_community)
	RecyclerView mRecyclerView;


	private String site;
	private CommuinityDialog commuinityDialog;
	private CommunityAdapter adapter;

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, CommunityActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.settings_activity_community;
	}

	@Override
	public void initView() {
		menuTitleTv.setText(R.string.about_joinCommunity);
		commuinityDialog = new CommuinityDialog(this);
		commuinityDialog.setWechatNumber("Coinbene_support");

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(layoutManager);

		adapter = new CommunityAdapter();
		adapter.bindToRecyclerView(mRecyclerView);

	}

	@Override
	public void setListener() {
		menuBack.setOnClickListener(v -> finish());
		adapter.setOnItemClickListener((adapter, view, position) -> {
			CommunityModel model = (CommunityModel) adapter.getData().get(position);
			if (model.getCommunity().equals(COMMUNITY_WECHAT)) {
				commuinityDialog.show();
			} else {
				loadUrl(model.getUrl());
			}
		});
	}

	@Override
	public void initData() {
		site = SpUtil.get(this, SpUtil.PRE_SITE_SELECTED, "");
		adapter.setNewData(getCommunityData());
	}

	private List<CommunityModel> getCommunityData() {
		List<CommunityModel> communityList = new ArrayList<>();

		if (communityList.size() > 0) {
			communityList.clear();
		}

		if (SITE_KO.equals(site)) {
			communityList.add(new CommunityModel(COMMUNITY_TELEGRAM, ResourceProvider.getString(this,R.string.telegram), URL_KO_TELEGRAM, R.drawable.icon_community_telegram, "CoinBene Official(Korea)"));
			communityList.add(new CommunityModel(COMMUNITY_FACEBOOK, ResourceProvider.getString(this,R.string.facebook), URL_KO_FACEBOOK, R.drawable.icon_community_facebook, "코인베네 코리아"));
			communityList.add(new CommunityModel(COMMUNITY_TWITTER, ResourceProvider.getString(this,R.string.twitter), URL_KO_TWITTER, R.drawable.icon_community_twitter, "coinbene_kr"));
			communityList.add(new CommunityModel(COMMUNITY_INSTAGRAM, "Instagram", URL_KO_INSTAGRAM, R.drawable.community_icon_instagram, "coinbene_kr"));
			communityList.add(new CommunityModel(COMMUNITY_KAKAO, "KAKAO", URL_KO_KAKAO, R.drawable.icon_community_kakao, "코인베네 CoinBene"));
		} else if (SITE_BR.equals(site)) {
			communityList.add(new CommunityModel(COMMUNITY_TWITTER, ResourceProvider.getString(this,R.string.twitter), URL_BR_TWITTER, R.drawable.icon_community_twitter, "CoinBene Brasil"));
			communityList.add(new CommunityModel(COMMUNITY_FACEBOOK, ResourceProvider.getString(this,R.string.facebook), URL_BR_FACEBOOK, R.drawable.icon_community_facebook, "CoinBene Brasil"));
			communityList.add(new CommunityModel(COMMUNITY_LINKEDIN, ResourceProvider.getString(this,R.string.commuinity_linkein), URL_BR_LINKEDIN, R.drawable.icon_community_linkein, "CoinBene Brasil"));
			communityList.add(new CommunityModel(COMMUNITY_YOUTUBE, ResourceProvider.getString(this,R.string.youtube), URL_BR_YOUTUBE, R.drawable.icon_community_youtube, "CoinBene Brasil"));
		} else {
			communityList.add(new CommunityModel(COMMUNITY_WECHAT, ResourceProvider.getString(this,R.string.wechat), "", R.drawable.icon_community_wechart, ResourceProvider.getString(this,R.string.community_Separate_service)));
			communityList.add(new CommunityModel(COMMUNITY_SINA, ResourceProvider.getString(this,R.string.weibo), URL_SINA, R.drawable.icon_community_sina, "CoinBene"));
			communityList.add(new CommunityModel(COMMUNITY_TELEGRAM, ResourceProvider.getString(this,R.string.telegram), ResourceProvider.getString(this,R.string.community_telegram_website), R.drawable.icon_community_telegram, "CoinBene Official"));
			communityList.add(new CommunityModel(COMMUNITY_TWITTER, ResourceProvider.getString(this,R.string.twitter), URL_TWITTER, R.drawable.icon_community_twitter, ResourceProvider.getString(this,R.string.coinbene_global)));
			communityList.add(new CommunityModel(COMMUNITY_FACEBOOK, ResourceProvider.getString(this,R.string.facebook), URL_FACEBOOK, R.drawable.icon_community_facebook, ResourceProvider.getString(this,R.string.coinbene_global)));
			communityList.add(new CommunityModel(COMMUNITY_LINKEDIN, ResourceProvider.getString(this,R.string.commuinity_linkein), URL_LINKEDIN, R.drawable.icon_community_linkein, ResourceProvider.getString(this,R.string.coinbene_global)));
			communityList.add(new CommunityModel(COMMUNITY_YOUTUBE, ResourceProvider.getString(this,R.string.youtube), URL_YOUTUBE, R.drawable.icon_community_youtube, ResourceProvider.getString(this,R.string.coinbene_global)));
		}

		return communityList;
	}

	@Override
	public boolean needLock() {
		return false;
	}

	private void loadUrl(String url) {
		if (!TextUtils.isEmpty(url)) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
		}
	}


	public class CommunityModel {
		private String community;
		private String communityName;
		private String dec;
		private String url;
		private int iconID;

		public CommunityModel(String community, String communityName, String url, int iconID, String dec) {
			this.communityName = communityName;
			this.community = community;
			this.url = url;
			this.iconID = iconID;
			this.dec = dec;
		}

		public String getCommunity() {
			return community;
		}

		public String getUrl() {
			return url;
		}

		public String getDec() {
			return dec;
		}

		public int getIconID() {
			return iconID;
		}

		public String getCommunityName() {
			return communityName;
		}
	}
}
