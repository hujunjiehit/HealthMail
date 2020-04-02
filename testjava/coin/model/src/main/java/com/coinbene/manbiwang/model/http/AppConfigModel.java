package com.coinbene.manbiwang.model.http;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;

public class AppConfigModel {


	private List<MainNavigationBean> main_navigation;
	private UrlConfigBean url_config;
	private List<String> base_url_configs;
	/**
	 * switch : {"otc":{"function":0,"asset":0},"margin":{"function":0,"asset":0},"contract":{"function":0,"asset":0},"fortune":{"function":0,"asset":0}}
	 */

	private SwitchBean switch_config;


	public List<MainNavigationBean> getMain_navigation() {
		return main_navigation;
	}

	public void setMain_navigation(List<MainNavigationBean> main_navigation) {
		this.main_navigation = main_navigation;
	}

	public UrlConfigBean getUrl_config() {
		return url_config;
	}

	public void setUrl_config(UrlConfigBean url_config) {
		this.url_config = url_config;
	}

	public List<String> getBase_url_config() {
		return base_url_configs;
	}

	public void setBase_url_config(List base_url_config) {
		this.base_url_configs = base_url_config;
	}

	public SwitchBean getSwitchBean() {
		if (switch_config == null) {
			switch_config = new SwitchBean();
		}
		return switch_config;
	}

	public void setSwitchBean(SwitchBean switchBean) {
		this.switch_config = switchBean;
	}

	public static class MainNavigationBean {
		/**
		 * imgUrl : http://
		 * schema : coinbene://Spot
		 * sort : 1
		 * lang : {"zh_CN":"法币交易","en_US":"OTC trading","ko_KR":"OTC거래","pt_BR":"OTC trading","ja_JP":"円か取引","vi_VN":"Giao dịch OTC"}
		 */

		private String imgUrl;
		private String schema;
		private int sort;
		private HashMap<String, String> lang;

		public HashMap<String, String> getLang() {
			return lang;
		}

		public void setLang(HashMap<String, String> lang) {
			this.lang = lang;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public String getSchema() {
			return schema;
		}

		public void setSchema(String schema) {
			this.schema = schema;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		@NonNull
		@Override
		public String toString() {
			return "imgUrl=" + imgUrl + " schema=" + schema + " sort" + sort + " lang=" + lang.toString();
		}
	}

	public static class SwitchBean {
		/**
		 * otc : {"function":0,"asset":0}
		 * margin : {"function":0,"asset":0}
		 * contract : {"function":0,"asset":0}
		 * fortune : {"function":0,"asset":0}
		 */

		private OtcBean otc;
		private MarginBean margin;
		private ContractBean contract;
		private FortuneBean fortune;
		private WsCompress ws_compress;

		public WsCompress getWs_compress() {
			return ws_compress;
		}

		public void setWs_compress(WsCompress ws_compress) {
			this.ws_compress = ws_compress;
		}

		public OtcBean getOtc() {
			if(otc == null){
				otc = new OtcBean();
			}
			return otc;
		}

		public void setOtc(OtcBean otc) {
			this.otc = otc;
		}

		public MarginBean getMargin() {
			if(margin==null){
				margin =new MarginBean();
			}
			return margin;
		}

		public void setMargin(MarginBean margin) {
			this.margin = margin;
		}

		public ContractBean getContract() {
			if(contract ==null){
				contract= new ContractBean();
			}
			return contract;
		}

		public void setContract(ContractBean contract) {
			this.contract = contract;
		}

		public FortuneBean getFortune() {
			if(fortune == null){
				fortune = new FortuneBean();
			}
			return fortune;
		}

		public void setFortune(FortuneBean fortune) {
			this.fortune = fortune;
		}

		public static class OtcBean {
			/**
			 * function : 0
			 * asset : 0
			 */

			private int function;
			private int asset;

			public int getFunction() {
				return function;
			}

			public void setFunction(int function) {
				this.function = function;
			}

			public int getAsset() {
				return asset;
			}

			public void setAsset(int asset) {
				this.asset = asset;
			}
		}

		public static class MarginBean {
			/**
			 * function : 0
			 * asset : 0
			 */

			private int function;
			private int asset;

			public int getFunction() {
				return function;
			}

			public void setFunction(int function) {
				this.function = function;
			}

			public int getAsset() {
				return asset;
			}

			public void setAsset(int asset) {
				this.asset = asset;
			}
		}

		public static class ContractBean {
			/**
			 * function : 0
			 * asset : 0
			 */

			private int function;
			private int asset;

			public int getFunction() {
				return function;
			}

			public void setFunction(int function) {
				this.function = function;
			}

			public int getAsset() {
				return asset;
			}

			public void setAsset(int asset) {
				this.asset = asset;
			}
		}

		public static class FortuneBean {
			/**
			 * function : 0
			 * asset : 0
			 */

			private int function;
			private int asset;

			public int getFunction() {
				return function;
			}

			public void setFunction(int function) {
				this.function = function;
			}

			public int getAsset() {
				return asset;
			}

			public void setAsset(int asset) {
				this.asset = asset;
			}
		}

		public static class WsCompress {
			/**
			 * function : 0
			 * asset : 0
			 */

			private int compress;

			public int getCompress() {
				return compress;
			}

			public void setCompress(int compress) {
				this.compress = compress;
			}
		}
	}

	public static class UrlConfigBean {
		private List<String> base_url;
		private List<String> ws_url;
		private String h5_url;
		private String user_invitation_url;

		public String getUser_invitation_url() {
			return user_invitation_url;
		}

		public void setUser_invitation_url(String user_invitation_url) {
			this.user_invitation_url = user_invitation_url;
		}

		public List<String> getBase_url() {
			return base_url;
		}

		public void setBase_url(List<String> base_url) {
			this.base_url = base_url;
		}

		public List<String> getWs_url() {
			return ws_url;
		}

		public void setWs_url(List<String> ws_url) {
			this.ws_url = ws_url;
		}

		public String getH5_url() {
			return h5_url;
		}

		public void setH5_url(String h5_url) {
			this.h5_url = h5_url;
		}
	}
}
