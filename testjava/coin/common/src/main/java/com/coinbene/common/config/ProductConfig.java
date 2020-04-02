package com.coinbene.common.config;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.model.http.AppConfigModel;

import java.util.List;

/**
 * Created by june
 * on 2019-12-25
 */
public class ProductConfig {

	/**
	 * nightModeEnable : true
	 * multiLanguageConfig : {"enable":true,"supportLanguages":["zh_cn","en_us"]}
	 * multiSiteConfig : {"enable":true,"supportSite":["main",""]}
	 * pushConfig : {"enable":true,"appId":"","appSecret":""}
	 * buglyConfig : {"enable":true,"appKey":""}
	 * shareConfig : {"enable":false}
	 * baseUrlConfig : {"dev":{"base_url":"http://a.dev.atomchain.vip","base_url_h5":"http://dev.dev.atomchain.vip","base_url_res":"http://res.atomchain.vip/resource-upload/","spot_websocket":"ws://ws.dev.atomchain.vip/ws","btc_contract_websocket":"ws://ws-contract.dev.atomchain.vip/contract/ws","usdt_contract_websocket":"ws://ws-contract.dev.atomchain.vip/usdt/ws","option_broker_id":"14"},"test":{"base_url":"http://a.test.atomchain.vip","base_url_h5":"http://test.test.atomchain.vip","base_url_res":"http://res.atomchain.vip/resource-upload/","spot_websocket":"ws://ws.test.atomchain.vip/ws","btc_contract_websocket":"ws://ws-contract.test.atomchain.vip/contract/ws","usdt_contract_websocket":"ws://ws-contract.test.atomchain.vip/usdt/ws","option_broker_id":"14"},"staging":{"base_url":"http://a.staging.atomchain.vip","base_url_h5":"http://master.staging.atomchain.vip","base_url_res":"http://res.atomchain.vip/resource-upload/","spot_websocket":"ws://ws.staging.atomchain.vip/ws","btc_contract_websocket":"ws://ws-contract.staging.atomchain.vip/contract/ws","usdt_contract_websocket":"ws://ws-contract.staging.atomchain.vip/usdt/ws","option_broker_id":"14"},"online":{"base_url":"http://a.coinbene.mobi","base_url_h5":"https://s-m-s.coinbene.mobi","base_url_res":"http://res.coinbene.mobi/resource-upload/","spot_websocket":"ws://ws.coinbene.mobi/ws","btc_contract_websocket":"ws://ws-contract.coinbene.mobi/contract/ws","usdt_contract_websocket":"ws://ws-contract.coinbene.mobi/usdt/ws","option_broker_id":"7801250866987737088"}}
	 */

	private boolean communityEnable;
	private boolean nightModeEnable;
	private boolean shareEnable;
	private LanguageConfigBean languageConfig;
	private MultiSiteConfigBean multiSiteConfig;
	private BuglyConfigBean buglyConfig;
	private List<BaseUrlConfigBean> baseUrlConfig;
	private ZendeskConfigBean zendeskConfig;
	private SupportOnlineConfig supportOnlineConfig;

	public boolean isNightModeEnable() {
		return nightModeEnable;
	}

	public void setNightModeEnable(boolean nightModeEnable) {
		this.nightModeEnable = nightModeEnable;
	}

	public LanguageConfigBean getLanguageConfig() {
		return languageConfig;
	}

	public void setLanguageConfig(LanguageConfigBean languageConfig) {
		this.languageConfig = languageConfig;
	}

	public MultiSiteConfigBean getMultiSiteConfig() {
		return multiSiteConfig;
	}

	public void setMultiSiteConfig(MultiSiteConfigBean multiSiteConfig) {
		this.multiSiteConfig = multiSiteConfig;
	}


	public BuglyConfigBean getBuglyConfig() {
		return buglyConfig;
	}

	public void setBuglyConfig(BuglyConfigBean buglyConfig) {
		this.buglyConfig = buglyConfig;
	}


	public List<BaseUrlConfigBean> getBaseUrlConfig() {
		return baseUrlConfig;
	}

	public void setBaseUrlConfig(List<BaseUrlConfigBean> baseUrlConfig) {
		this.baseUrlConfig = baseUrlConfig;
	}

	public boolean isShareEnable() {
		return shareEnable;
	}

	public void setShareEnable(boolean shareEnable) {
		this.shareEnable = shareEnable;
	}

	public ZendeskConfigBean getZendeskConfig() {
		return zendeskConfig;
	}

	public void setZendeskConfig(ZendeskConfigBean zendeskConfig) {
		this.zendeskConfig = zendeskConfig;
	}

	public SupportOnlineConfig getSupportOnlineConfig() {
		return supportOnlineConfig;
	}

	public void setSupportOnlineConfig(SupportOnlineConfig supportOnlineConfig) {
		this.supportOnlineConfig = supportOnlineConfig;
	}

	public boolean isCommunityEnable() {
		return communityEnable;
	}

	public void setCommunityEnable(boolean communityEnable) {
		this.communityEnable = communityEnable;
	}

	public static class SupportOnlineConfig {
		private boolean enable;
		private String appID;
		private String appKey;
		private String domain;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public String getAppID() {
			return appID;
		}

		public void setAppID(String appID) {
			this.appID = appID;
		}

		public String getAppKey() {
			return appKey;
		}

		public void setAppKey(String appKey) {
			this.appKey = appKey;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}
	}

	public static class LanguageConfigBean {
		/**
		 * enable : true
		 */

		private boolean enable;
		private List<SupportLanguage> supportLanguage;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public List<SupportLanguage> getSupportLanguage() {
			return supportLanguage;
		}

		public void setSupportLanguage(List<SupportLanguage> supportLanguage) {
			this.supportLanguage = supportLanguage;
		}
	}

	public static class SupportLanguage {

		private String name;
		private String code;
		private boolean selected;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}


		public void setCode(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}


		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
	}

	public static class MultiSiteConfigBean {
		/**
		 * enable : true
		 * supportSite : ["main",""]
		 */

		private boolean enable;
		private List<String> supportSite;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public List<String> getSupportSite() {
			return supportSite;
		}

		public void setSupportSite(List<String> supportSite) {
			this.supportSite = supportSite;
		}
	}

	public static class BuglyConfigBean {
		/**
		 * enable : true
		 * appKey :
		 */

		private boolean enable;
		private String releaseAppKey;
		private String testAppKey;

		public boolean isEnable() {
			return enable;
		}


		public String getReleaseAppKey() {
			return releaseAppKey;
		}

		public void setReleaseAppKey(String releaseAppKey) {
			this.releaseAppKey = releaseAppKey;
		}

		public String getTestAppKey() {
			return testAppKey;
		}

		public void setTestAppKey(String testAppKey) {
			this.testAppKey = testAppKey;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}
	}


	public static class ZendeskConfigBean {
		/**
		 * noticeConfig : [{"siteName":"MAIN","base":"https://coinbenevip.zendesk.com","noticeList":"/api/v2/help_center/%s/categories/360002111153/articles.json","noticeCenter":"/hc/%s/categories/360002111153"},{"siteName":"KO","base":"https://coinbenevip-kr.zendesk.com","noticeList":"/api/v2/help_center/%s/categories/360002198353/articles.json","noticeCenter":"/hc/%s/categories/360002198373"},{"siteName":"BR","base":"https://coinbenevip-br.zendesk.com","noticeList":"/api/v2/help_center/%s/categories/360002174694/articles.json","noticeCenter":"/hc/%s/categories/360002174694"},{"siteName":"VN","base":"https://coinbenecomvn.zendesk.com","noticeList":"/api/v2/help_center/%s/categories/360002666094/articles.json","noticeCenter":"/hc/%s/categories/360002666094"}]
		 * aboutUs : https://coinbenevip.zendesk.com/hc/%s/articles/360033363154
		 * btcContractGuide : https://coinbenevip.zendesk.com/hc/%s/sections/360005713174
		 * usdtContractGuide : https://coinbenevip.zendesk.com/hc/%s/sections/360005713154
		 * ybbGuide : https://coinbenevip.zendesk.com/hc/%s/articles/360038204114
		 * marginGuide : https://coinbenevip.zendesk.com/hc/%s/articles/360035444613
		 * marginUserProtocol : https://coinbenevip.zendesk.com/hc/%s/articles/360035444233
		 * helpCenter : https://coinbenevip.zendesk.com/hc/%s/categories/360002096194
		 */

		private String aboutUs;
		private String btcContractGuide;
		private String usdtContractGuide;
		private String btcContractProtocol;
		private String usdtContractProtocol;
		private String ybbGuide;
		private String marginGuide;
		private String marginUserProtocol;
		private String helpCenter;
		private String spotStrategyOrder;
		private List<NoticeConfigBean> noticeConfig;

		public String getAboutUs() {
			return aboutUs;
		}

		public void setAboutUs(String aboutUs) {
			this.aboutUs = aboutUs;
		}

		public String getBtcContractGuide() {
			return btcContractGuide;
		}

		public void setBtcContractGuide(String btcContractGuide) {
			this.btcContractGuide = btcContractGuide;
		}

		public String getUsdtContractGuide() {
			return usdtContractGuide;
		}

		public void setUsdtContractGuide(String usdtContractGuide) {
			this.usdtContractGuide = usdtContractGuide;
		}

		public String getYbbGuide() {
			return ybbGuide;
		}

		public void setYbbGuide(String ybbGuide) {
			this.ybbGuide = ybbGuide;
		}

		public String getMarginGuide() {
			return marginGuide;
		}

		public void setMarginGuide(String marginGuide) {
			this.marginGuide = marginGuide;
		}

		public String getMarginUserProtocol() {
			return marginUserProtocol;
		}

		public void setMarginUserProtocol(String marginUserProtocol) {
			this.marginUserProtocol = marginUserProtocol;
		}

		public String getHelpCenter() {
			return helpCenter;
		}

		public void setHelpCenter(String helpCenter) {
			this.helpCenter = helpCenter;
		}

		public String getSpotStrategyOrder() {
			return spotStrategyOrder;
		}

		public void setSpotStrategyOrder(String spotStrategyOrder) {
			this.spotStrategyOrder = spotStrategyOrder;
		}

		public List<NoticeConfigBean> getNoticeConfig() {
			return noticeConfig;
		}

		public void setNoticeConfig(List<NoticeConfigBean> noticeConfig) {
			this.noticeConfig = noticeConfig;
		}

		public String getBtcContractProtocol() {
			return btcContractProtocol;
		}

		public void setBtcContractProtocol(String btcContractProtocol) {
			this.btcContractProtocol = btcContractProtocol;
		}

		public String getUsdtContractProtocol() {
			return usdtContractProtocol;
		}

		public void setUsdtContractProtocol(String usdtContractProtocol) {
			this.usdtContractProtocol = usdtContractProtocol;
		}

		public static class NoticeConfigBean {
			/**
			 * siteName : MAIN
			 * base : https://coinbenevip.zendesk.com
			 * noticeList : /api/v2/help_center/%s/categories/360002111153/articles.json
			 * noticeCenter : /hc/%s/categories/360002111153
			 */

			private String siteName;
			private String base;
			private String noticeList;
			private String noticeCenter;

			public String getSiteName() {
				return siteName;
			}

			public void setSiteName(String siteName) {
				this.siteName = siteName;
			}

			public String getBase() {
				return base;
			}

			public void setBase(String base) {
				this.base = base;
			}

			public String getNoticeList() {
				return noticeList;
			}

			public void setNoticeList(String noticeList) {
				this.noticeList = noticeList;
			}

			public String getNoticeCenter() {
				return noticeCenter;
			}

			public void setNoticeCenter(String noticeCenter) {
				this.noticeCenter = noticeCenter;
			}
		}
	}

	public static class BaseUrlConfigBean {
		/**
		 * envName : dev
		 * base_url : http://a.dev.atomchain.vip
		 * base_url_h5 : http://dev.dev.atomchain.vip
		 * base_url_res : http://res.atomchain.vip/resource-upload/
		 * spot_websocket : ws://ws.dev.atomchain.vip/ws
		 * btc_contract_websocket : ws://ws-contract.dev.atomchain.vip/contract/ws
		 * usdt_contract_websocket : ws://ws-contract.dev.atomchain.vip/usdt/ws
		 * websocket_url : "ws://ws.dev.atomchain.vip/stream/ws"
		 * option_broker_id : 14
		 */

		private String envName;
		private String base_url;
		private String base_url_h5;
		private String base_url_res;
		private String spot_websocket;
		private String btc_contract_websocket;
		private String usdt_contract_websocket;
		private String websocket_url;
		private String option_broker_id;

		public String getEnvName() {
			return envName;
		}

		public void setEnvName(String envName) {
			this.envName = envName;
		}

		public String getBase_url(int environmentType) {
			if (environmentType != Constants.ONLINE_ENVIROMENT) {
				//非线上环境直接用ProductConfig配置的base_url
				SpUtil.setCurrentBaseUrl(base_url);
				return base_url;
			}
			//读取本地保存的base_url, 如果本地保存的为空，则取配置的Product配置的URL
			AppConfigModel.UrlConfigBean urlConfigBean = SpUtil.getAppConfig() == null ? null : SpUtil.getAppConfig().getUrl_config();
			if (urlConfigBean == null || urlConfigBean.getBase_url() == null || urlConfigBean.getBase_url().size() == 0) {
				SpUtil.setCurrentBaseUrl(base_url);
				return base_url;
			}

			String configBaseUrl = urlConfigBean.getBase_url().get(0);
			if (TextUtils.isEmpty(configBaseUrl)) {
				configBaseUrl = base_url;
			}
			SpUtil.setCurrentBaseUrl(configBaseUrl);
			return configBaseUrl;
		}

		public void setBase_url(String base_url) {
			this.base_url = base_url;
		}

		public String getBase_url_h5(int environmentType) {
			if (environmentType != Constants.ONLINE_ENVIROMENT) {
				//非线上环境直接用ProductConfig配置的h5_url
				SpUtil.setCurrentH5Url(base_url_h5);
				return base_url_h5;
			}
			//读取本地保存的base_url, 如果本地保存的为空，则取配置的Product配置的URL
			AppConfigModel.UrlConfigBean urlConfigBean = SpUtil.getAppConfig() == null ? null : SpUtil.getAppConfig().getUrl_config();
			if (urlConfigBean == null || TextUtils.isEmpty(urlConfigBean.getH5_url())) {
				SpUtil.setCurrentH5Url(base_url_h5);
				return base_url_h5;
			}

			String configH5Url = urlConfigBean.getH5_url();
			if (TextUtils.isEmpty(configH5Url)) {
				configH5Url = base_url_h5;
			}
			SpUtil.setCurrentH5Url(configH5Url);
			return configH5Url;
		}

		public void setBase_url_h5(String base_url_h5) {
			this.base_url_h5 = base_url_h5;
		}

		public String getBase_url_res() {
			return base_url_res;
		}

		public void setBase_url_res(String base_url_res) {
			this.base_url_res = base_url_res;
		}

		public String getSpot_websocket() {
			return spot_websocket;
		}

		public void setSpot_websocket(String spot_websocket) {
			this.spot_websocket = spot_websocket;
		}

		public String getBtc_contract_websocket() {
			return btc_contract_websocket;
		}

		public void setBtc_contract_websocket(String btc_contract_websocket) {
			this.btc_contract_websocket = btc_contract_websocket;
		}

		public String getUsdt_contract_websocket() {
			return usdt_contract_websocket;
		}

		public void setUsdt_contract_websocket(String usdt_contract_websocket) {
			this.usdt_contract_websocket = usdt_contract_websocket;
		}

		public String getWebsocket_url(int environmentType) {
			if (environmentType != Constants.ONLINE_ENVIROMENT) {
				//非线上环境直接用ProductConfig配置的base_url
				SpUtil.setCurrentWsUrl(wrapperWsUrl(websocket_url));
				return wrapperWsUrl(websocket_url);
			}

			//读取本地保存的base_url, 如果本地保存的为空，则取配置的Product配置的URL
			AppConfigModel.UrlConfigBean urlConfigBean = SpUtil.getAppConfig() == null ? null : SpUtil.getAppConfig().getUrl_config();
			if (urlConfigBean == null || urlConfigBean.getWs_url() == null || urlConfigBean.getWs_url().size() == 0) {
				SpUtil.setCurrentWsUrl(wrapperWsUrl(websocket_url));
				return wrapperWsUrl(websocket_url);
			}

			String configWsUrl = urlConfigBean.getWs_url().get(0);
			if (TextUtils.isEmpty(configWsUrl)) {
				configWsUrl = websocket_url;
			}

			SpUtil.setCurrentWsUrl(wrapperWsUrl(configWsUrl));
			return wrapperWsUrl(configWsUrl);
		}


		/**
		 * 根据是否压缩，返回对应的url
		 * @param websocket_url
		 * @return
		 */
		private String wrapperWsUrl(String websocket_url) {
			if (SwitchUtils.getWsCompress()) {
				return UrlUtil.appendParams(websocket_url, "compress", "true");
			} else {
				return websocket_url;
			}
		}

		public void setWebsocket_url(String websocket_url) {
			this.websocket_url = websocket_url;
		}

		public String getOption_broker_id() {
			return option_broker_id;
		}

		public void setOption_broker_id(String option_broker_id) {
			this.option_broker_id = option_broker_id;
		}
	}


}
