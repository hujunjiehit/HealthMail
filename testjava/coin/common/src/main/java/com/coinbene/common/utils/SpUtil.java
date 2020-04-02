package com.coinbene.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.manbiwang.model.http.AppConfigModel;
import com.coinbene.manbiwang.model.http.BannerList;
import com.coinbene.manbiwang.model.http.HomeMarketModel;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.manbiwang.model.http.ZendeskArticlesResponse;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Preference
 * Created by mxd
 */
public class SpUtil {


	public static final String KEY_WS_COMPRESS = "ws_compress";
	private static final String NEW_PREF_NAME = "new_prefs";
	private static final String LOG_TAG = SpUtil.class.getName();


	/**
	 * 期货风险协议
	 */
	public static final String SP_CONTRACT_REMIND = "contract_remind";


	//常量标识
	public static final String TRADE_PAIR_HASH = "trade_pair_hash";
	public static final String PRE_RATE = "pre_rate";
	public static final String PRE_TRADE_PAIR_SELECTED = "trade_pair_cur_selected";
	public static final String PRE_ASSET_TYPE_SELECTED = "asset_type_selected";
	public static final String PRE_LOGIN_ACTIVITY_EXIST = "pre_login_activity_exist";
	public static final String PRE_QUIT_TIME = "pre_quit_time";
	public static final String PRE_HIDE_ASSET_ZERO = "pre_hide_asset_zero";//隐藏资产为0的资产
	public static final String PRE_HIDE_ASSET_ESTIMATION = "pre_hide_asset_estimation";//隐藏预估资产
	public static final String PRE_USER_NAME = "user_name";
	public static final String PRE_GESTURE_PWD_KEY = "gesture_pwd_key";
	public static final String PRE_PWD_ERROR_COUNT = "pwd_error_count";
	public static final String PRE_FINGER_KEY = "gesture_finger_key";
	public static final String PRE_IS_LOCKED = "app_is_locked";
	public static final String PRE_USER_LOGIN = "user_need_login";
	public static final String PRE_KYC_STATUS = "kyc_status";//实名认证的状态
	public static final String PRE_OTC_IS_BINDED = "otc_is_binded";//支付绑定，otc是否绑定,true绑定
	public static final String PRE_SITE_SELECTED = "pre_site_selected";//设置过的站点
	public static final String K_LINE_TIME_STATUS = "k_line_time_status";//k线时间状态   0 15分钟   1 1小时  2 4小时   3 1天   4  1分钟   5  5分钟    6   30分钟   7   1周    8   1月
	public static final String K_LINE_MA_BOLL_STATUS = "k_line_ma_boll_status";//k线ma boll状态   0 都隐藏   1 ma展示  2 boll展示
	public static final String K_LINE_ZHIBIAO_STATUS = "k_line_zhibiao_status";//k线幅图2 指标   SelectTimeListener.MACD    1KDJ   2  RSI

	public static final String PRE_OTC_SWITCH = "otc_switch";//0隐藏OTCtab 隐藏设置支付绑定   1展示OTCtab展开设置支付绑定
	public static final String PRE_OPT_SWITCH = "opt_switch";
	public static final String PRE_CONTRCT_SWITCH = "contract_switch";//合约开关
	public static final String PRE_OTC_SWITCH_ASSET = "otc_switch_asset";//OTC,资产和记录开关
	public static final String PRE_OPTION_SWITCH_ASSET = "opt_switch_asset";//期权 资产和记录开关
	public static final String PRE_CONTRCT_SWITCH_ASSET = "contract_switch_asset";//合约 资产和记录开关
	public static final String PRE_MARGIN_SWITCH = "margin_switch";//杠杆开关
	public static final String PRE_MARGIN_SWITCH_ASSET = "margin_switch_asset";//杠杆资产


	public static final String POST_CLIENT_DATA_TIME = "post_client_data_time";//上传客户端信息 时间

	public static final String SPOT_COIN = "spot_coin";//现货记住交易对
	public static final String CONTRACT_COIN = "contract_coin";//合约记住交易对
	public static final String CONTRACT_COIN_USDT = "contract_coin_usdt";//合约记住交易对


	public static final String CONTRACT_COIN_NEW = "contract_coin_new";//合约记住交易对

	public static final String MARGIN_COIN = "margin_coin";//杠杆记住交易对

	public static final String KEY_DEBUG_WHITE_LIST = "key_debug_whitelist";//debug白名单
	public static final String KEY_CONTRACT_MINING_SWITCH = "key_contract_switch";//合约挖矿开关
	public static final String KEY_FORTUNE_SWITCH = "key_fortune_switch";//财富开关
	public static final String KEY_FORTUNE_SWITCH_ASSET = "key_fortune_switch_asset";//财富资产开关
	public static final String KEY_NEW_USER_GUIDE = "key_new_user_guide";//财富开关

	//是否红涨绿跌
	public static final String IS_RED_RISE_GREEN_FALL = "isRedRiseGreenFall";


	public static final String CONTRACT_MATCH_STATUS = "contract_match_status";//合约首页体验争霸赛 提示弹窗状态   1  永远不提示  2 当天不提示
	public static final String CONTRACT_MATCH_STATUS_TODAY = "contract_match_status_today";//当天不提示 存当天日期

	public static final String CLIENT_DATA = "client_data";//用于保存上报信息的map

	public static final String IS_APP_FIRST_INSTALL = "is_first_install_app";//首次安装用户

	public static final String CURRENT_ENVIRONMENT = "current_environment";

	public static final String CURRENT_USER_RESPONSE = "current_user_response";

	public static final String CURRENT_LOCK_TIME = "current_lock_time";

	public static final String CONTRACT_SURE_DIALOG_IS_SHOW = "contract_is_show_trade_dialog";

	public static final String USER_IS_OPEN_MARGIN = "user_is_open_margin";

	public static final String KEY_POPUP_ACTIVITY_ID = "popup_activity_id"; //app弹窗活动id
	public static final String KEY_POPUP_ENABLE = "key_pop_enable";  //是否允许弹出app弹窗

	public static final String PRE_GAME_SWITCH = "game_switch";
	public static final String PRE_GAME_ASSET_SWITCH = "game_asset_switch";
	public static final String PRE_BATTLE_SWITCH = "battle_switch";

	public static final String IS_CLEAR_HASH = "is_clear_hash";//清除本地hash值 的判断字段

	public static final String USDT_CONTRACT_STATUS = "usdt_contract_status";//USDT合约协议状态
	public static final String BTC_CONTRACT_STATUS = "btc_contract_status";//BTC合约协议状态

	public static final String KEY_HAS_NEW_VERSION = "key_new_version";

	public static final String KEY_USDT_PROTOCOL = "usdt_protocol";//USDT协议状态
	public static final String KEY_BTC_PROTOCOL = "btc_protocol";//USDT协议状态

	public static final String TRANSFER_TO_YBB = "key_transfer_to_ybb"; //是否直接转入余币宝

	public static final String CONTRACT_USDT_UNIT_SWITCH = "contract_usdt_unit_switch";
	public static final String USER_SPOT_TAB_OPTIONS = "user_spot_trade_options";//用户现货交易选项
	public static final String USER_SPOT_TRADE_OPTIONS = "user_spot_coin_options";//用户现货币种选项
	public static final String USER_CONTRACT_OPTIONS = "user_contract_options";//用户OTC币种选项

	public static final String APP_IS_RECREATE = "app_is_recreate";
	public static final String APP_FRAGMENT_RECREATE = "fragment_is_recreate";

	public static final String MAIN_HOT_COIN = "main_hot_coin";//热门币种
	public static final String MAIN_BIG_COIN = "main_big_coin";//热门币种

	public static final String APP_CONFIG = "COINBENE_APP_CONFIG";//coinbene app配置信息

	public static final String APP_BANNER = "APP_BANNER";//banner
	public static final String APP_NOTICE = "APP_NOTICE";//notice


	public static final String ENABLE_NEW_KLINE = "enable_new_kline";
	public static final String UPS_AND_DOWNS_TYPE = "ups_and_downs_type";

	public static final String CURRENT_BASE_URL = "local_base_url";
	public static final String CURRENT_WS_URL = "local_ws_url";
	public static final String CURRENT_H5_URL = "local_h5_url";


	public static final String SHOW_CONTRACT_PLAN = "show_contract_plan";


	/**
	 * @param context
	 * @param name    移除
	 */
	public static void remove(Context context, String name) {
		SharedPreferences prefs = getInstance(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.remove(name);
		//edit.apply();
	}

	/**
	 * @param context
	 * @param name
	 * @param value   存放
	 */
	public static void put(Context context, String name, long value) {
		put(context, name, value, false);
	}

	public static void put(Context context, String name, long value, boolean sync) {
		SharedPreferences prefs = getInstance(context);
		if (prefs != null) {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putLong(name, value);
//            if (sync) {
//                edit.commit();
//            } else {
//                edit.apply();
//            }
		}
	}

	public static long get(Context context, String name, long defaultValue) {
		SharedPreferences prefs = getInstance(context);
		return prefs == null ? defaultValue : prefs.getLong(name, defaultValue);
	}

	public static void put(Context context, String name, int value) {
		SharedPreferences prefs = getInstance(context);
		if (prefs != null) {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt(name, value);
			//edit.commit();
		}
	}

	public static int get(Context context, String name, int defaultValue) {
		SharedPreferences prefs = getInstance(context);
		return prefs == null ? defaultValue : prefs.getInt(name, defaultValue);
	}

	public static boolean get(Context context, String name, boolean defaultValue) {
		if (context == null) {
			return defaultValue;
		}
		SharedPreferences prefs = getInstance(context);
		return prefs == null ? defaultValue : prefs.getBoolean(name, defaultValue);
	}

	private static SharedPreferences getInstance(final Context context) {
		if (MMKV.getRootDir() == null) {
			MMKV.initialize(context);
		}
		MMKV mmkv = MMKV.mmkvWithID(NEW_PREF_NAME);
		Set<String> set = mmkv.getStringSet("key_preference_set", null);
		if (set == null) {
			//需要数据迁移
			set = new HashSet<>();
			set.add(NEW_PREF_NAME);
			mmkv.putStringSet("key_preference_set", set);

			SharedPreferences old_man = context.getSharedPreferences(NEW_PREF_NAME, Context.MODE_MULTI_PROCESS);
			mmkv.importFromSharedPreferences(old_man);
			old_man.edit().clear().commit();
		}

		return mmkv;
	}

	public static void put(Context context, String name, boolean value) {
		SharedPreferences prefs = getInstance(context);
		if (prefs != null) {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putBoolean(name, value);
			//edit.commit();
		}
	}

	public static void put(Context context, String name, String value) {
		SharedPreferences prefs = getInstance(context);
		if (prefs != null && !TextUtils.isEmpty(name)) {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putString(name, value);
			//edit.commit();
		}
	}

	public static String get(Context context, String name, String defaultValue) {
		SharedPreferences prefs = getInstance(context);
		return prefs == null ? defaultValue : prefs.getString(name, defaultValue);
	}

	public static void removeFromExternalByUser(Context context, String name) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		remove(context, name);
	}

	public static void putForUser(Context context, String name, String value) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		put(context, name, value);
	}

	public static String getByUser(Context context, String name, String defaultValue) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		return get(context, name, defaultValue);
	}

	public static void putForUser(Context context, String name, boolean value) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		put(context, name, value);
	}

	public static boolean getByUser(Context context, String name, boolean defaultValue) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		return get(context, name, defaultValue);
	}

	public static void putForUser(Context context, String name, int value) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		put(context, name, value);
	}


	public static int getByUser(Context context, String name, int defaultValue) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		return get(context, name, defaultValue);
	}

	public static void putForUser(Context context, String name, long value) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		put(context, name, value);
	}

	public static void putForUser(Context context, String name, long value, boolean sync) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		put(context, name, value);
	}

	public static long getByUser(Context context, String name, long defaultValue) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null) {
			int userId = userInfo.userId;
			name = name + "_" + userId;
		}
		return get(context, name, defaultValue);
	}

	public static int readContractMatchStatus() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.CONTRACT_MATCH_STATUS, 0);
	}

	public static String readContractMatchDay() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.CONTRACT_MATCH_STATUS_TODAY, "a");
	}

	/**
	 * 获取Map集合
	 */
	public static HashMap<String, String> getMap(Context context, String key) {
		HashMap<String, String> map = new HashMap<>();
		String strJson = getInstance(context).getString(key, null);
		if (TextUtils.isEmpty(strJson)) {
			return map;
		}
		map = CBRepository.gson.fromJson(strJson, new TypeToken<HashMap<String, String>>() {
		}.getType());
		return map;
	}

	public static void setMap(Context context, String key, HashMap<String, String> map) {
		if (map == null || map.isEmpty()) {
			return;
		}

		String strJson = CBRepository.gson.toJson(map);
		SharedPreferences.Editor editor = getInstance(context).edit();
		editor.putString(key, strJson);
		// editor.commit();
	}


	public static void clearMap(Context context, String key) {
		SharedPreferences.Editor editor = getInstance(context).edit();
		editor.putString(key, "");
		//editor.commit();
	}

	public static void setUserResponse(UserInfoResponse.DataBean response) {
		if (response == null) {
			put(CBRepository.getContext(), CURRENT_USER_RESPONSE, "");
		} else {
			put(CBRepository.getContext(), CURRENT_USER_RESPONSE, CBRepository.gson.toJson(response));
		}
	}


	public static String getUserResponse() {
		return get(CBRepository.getContext(), CURRENT_USER_RESPONSE, "");
	}


	public static void setHotCoin(HomeMarketModel.DataBean dataBean) {
		if (dataBean == null || dataBean.getList().size() == 0) {
			put(CBRepository.getContext(), MAIN_HOT_COIN, "");
		} else {
			put(CBRepository.getContext(), MAIN_HOT_COIN, CBRepository.gson.toJson(dataBean));
		}
	}


	public static HomeMarketModel.DataBean getHotCoin() {
		String s = get(CBRepository.getContext(), MAIN_HOT_COIN, "");
		if (TextUtils.isEmpty(s)) {
			return null;
		}
		return CBRepository.fromJson(s, HomeMarketModel.DataBean.class);
	}

	public static ZendeskArticlesResponse getNotices() {
		String s = get(CBRepository.getContext(), APP_NOTICE, "");
		if (TextUtils.isEmpty(s)) {
			return null;
		}
		return CBRepository.fromJson(s, ZendeskArticlesResponse.class);
	}


	public static void setNotice(ZendeskArticlesResponse notices) {
		if (notices != null && notices.getArticles() != null && notices.getArticles().size() > 0) {
			put(CBRepository.getContext(), APP_NOTICE, CBRepository.gson.toJson(notices));
		} else {
			put(CBRepository.getContext(), APP_NOTICE, "");
		}
	}


	public static BannerList getBanners() {
		String s = get(CBRepository.getContext(), APP_BANNER, "");
		if (TextUtils.isEmpty(s)) {
			return null;
		}
		return CBRepository.fromJson(s, BannerList.class);
	}


	public static void setBanners(BannerList banners) {
		if (banners != null && banners.getData() != null && banners.getData().size() > 0) {
			put(CBRepository.getContext(), APP_BANNER, CBRepository.gson.toJson(banners));
		} else {
			put(CBRepository.getContext(), APP_BANNER, "");
		}
	}


	public static void setAppConfig(AppConfigModel config) {
		if (config != null) {
			put(CBRepository.getContext(), APP_CONFIG, CBRepository.gson.toJson(config));
			AppConfigModel.SwitchBean switchBean = config.getSwitchBean();
			AppConfigModel.SwitchBean.ContractBean contract = switchBean.getContract();
			AppConfigModel.SwitchBean.OtcBean otcData = switchBean.getOtc();
			AppConfigModel.SwitchBean.MarginBean marginData = switchBean.getMargin();
			AppConfigModel.SwitchBean.FortuneBean fortuneData = switchBean.getFortune();
			AppConfigModel.SwitchBean.WsCompress ws_compress = switchBean.getWs_compress();

			if (contract != null) {
				SpUtil.put(CBRepository.getContext(), SpUtil.PRE_CONTRCT_SWITCH, contract.getFunction() == 1);
				SpUtil.put(CBRepository.getContext(), SpUtil.PRE_CONTRCT_SWITCH_ASSET, contract.getAsset() == 1);
			}


			if (otcData != null) {
				SpUtil.put(CBRepository.getContext(), SpUtil.PRE_OTC_SWITCH, otcData.getFunction() == 1);
				SpUtil.put(CBRepository.getContext(), SpUtil.PRE_OTC_SWITCH_ASSET, otcData.getAsset() == 1);
			}

			if (marginData != null) {
				SpUtil.put(CBRepository.getContext(), SpUtil.PRE_MARGIN_SWITCH, marginData.getFunction() == 1);
				SpUtil.put(CBRepository.getContext(), SpUtil.PRE_MARGIN_SWITCH_ASSET, marginData.getAsset() == 1);
			}


			if (fortuneData != null) {
				SpUtil.setFortuneSwitch(fortuneData.getFunction());
				SpUtil.put(CBRepository.getContext(), SpUtil.KEY_FORTUNE_SWITCH_ASSET, fortuneData.getAsset() == 1);
			}
			if (ws_compress != null) {
				SpUtil.put(CBRepository.getContext(), SpUtil.KEY_WS_COMPRESS, ws_compress.getCompress() == 1);
			}
		} else {
			put(CBRepository.getContext(), APP_CONFIG, "");
		}



	}


	public static AppConfigModel getAppConfig() {
		String s = get(CBRepository.getContext(), APP_CONFIG, "");
		if (TextUtils.isEmpty(s)) {
			return null;
		}
		return CBRepository.fromJson(s, AppConfigModel.class);
	}


	public static void setBigCoin(HomeMarketModel.DataBean dataBean) {
		if (dataBean == null || dataBean.getList() == null || dataBean.getList().size() == 0) {
			put(CBRepository.getContext(), MAIN_BIG_COIN, "");
		} else {
			put(CBRepository.getContext(), MAIN_BIG_COIN, CBRepository.gson.toJson(dataBean));
		}
	}


	public static HomeMarketModel.DataBean getBigCoin() {
		String s = get(CBRepository.getContext(), MAIN_BIG_COIN, "");
		if (TextUtils.isEmpty(s)) {
			return null;
		}
		return CBRepository.fromJson(s, HomeMarketModel.DataBean.class);
	}

	public static void setContractSureDialogIsShow(boolean isSelect) {
		put(CBRepository.getContext(), CONTRACT_SURE_DIALOG_IS_SHOW, isSelect);
	}

	public static boolean isContractSureDialogNotShow() {
		return get(CBRepository.getContext(), CONTRACT_SURE_DIALOG_IS_SHOW, false);
	}

	public static void closeFOTAGuide() {
		SharedPreferences preferences = CBRepository.getContext().getSharedPreferences("spUtils", Context.MODE_PRIVATE);
		preferences.edit().putBoolean("hasLeading_gamma", true).commit();
	}

	public static void setDebugWhiteList(boolean isInWhiteList) {
		put(CBRepository.getContext(), KEY_DEBUG_WHITE_LIST, isInWhiteList);
	}

	public static boolean isInDebugWhiteList() {
		return get(CBRepository.getContext(), KEY_DEBUG_WHITE_LIST, false);
	}

	public static void setContractMiningSwitch(boolean value) {
		put(CBRepository.getContext(), KEY_CONTRACT_MINING_SWITCH, value);
	}

	public static boolean getContractMiningSwitch() {
		return get(CBRepository.getContext(), KEY_CONTRACT_MINING_SWITCH, false) && SiteHelper.isMainSite();
	}

	public static String getMarginSymbol() {
		if (MarginSymbolController.getInstance().queryFirstSymbol() == null) {
			return "BTC/USDT";
		}
		String symbol = get(CBRepository.getContext(), MARGIN_COIN, MarginSymbolController.getInstance().queryFirstSymbol().symbol);

		//判断symbol在数据库中是否存在
		if (MarginSymbolController.getInstance().querySymbolByName(symbol) == null) {
			symbol = MarginSymbolController.getInstance().queryFirstSymbol().symbol;
			setMarginSymbol(symbol);
		}
		return symbol;
	}

	public static void setMarginSymbol(String symbol) {
		put(CBRepository.getContext(), MARGIN_COIN, symbol);
	}


	public static void setHideAssetZero(boolean value) {
		put(CBRepository.getContext(), PRE_HIDE_ASSET_ZERO, value);
	}

	public static boolean getHideAssetZero() {
		return get(CBRepository.getContext(), PRE_HIDE_ASSET_ZERO, false);
	}

	public static boolean getPreHideAssetEstimation() {
		return get(CBRepository.getContext(), PRE_HIDE_ASSET_ESTIMATION, false);
	}

	public static void setPreHideAssetEstimation(boolean value) {
		put(CBRepository.getContext(), PRE_HIDE_ASSET_ESTIMATION, value);
	}

	/**
	 * 是否是红涨绿跌
	 *
	 * @return
	 */
	public static boolean isRedRise() {
		return get(CBRepository.getContext(), IS_RED_RISE_GREEN_FALL, false);
	}

	public static void setRedRise(boolean isRedRise) {
		put(CBRepository.getContext(), IS_RED_RISE_GREEN_FALL, isRedRise);
	}

	/**
	 * 设置是否开通了杠杆  拿到的时候存本地   退出登录后清除
	 */
	public static void setMarginUserConfig(boolean isOpenMargin) {
		put(CBRepository.getContext(), USER_IS_OPEN_MARGIN, isOpenMargin);
	}

	/**
	 * 获取是否开通了杠杆  拿到的时候存本地   退出登录后清除
	 */
	public static boolean isMarginUserConfig() {
		return get(CBRepository.getContext(), USER_IS_OPEN_MARGIN, false);
	}

	/**
	 * app弹窗开关
	 */
	public static void setPopupActivityId(String id) {
		put(CBRepository.getContext(), KEY_POPUP_ACTIVITY_ID, id);
	}

	/**
	 * app弹窗开关，返回true,可以弹出，false不再弹出
	 */
	public static String getPopupActivityId() {
		return get(CBRepository.getContext(), KEY_POPUP_ACTIVITY_ID, "");
	}


	/**
	 * app弹窗开关
	 */
	public static void setPopupEnable(boolean enable) {
		put(CBRepository.getContext(), KEY_POPUP_ENABLE, enable);
	}

	/**
	 * app弹窗开关，返回true,可以弹出，false不再弹出
	 */
	public static boolean getPopupEnable() {
		return get(CBRepository.getContext(), KEY_POPUP_ENABLE, true);
	}


	public static void setIsClearHash(boolean isClearHash) {
		put(CBRepository.getContext(), IS_CLEAR_HASH, isClearHash);
	}

	/**
	 * 是否清除hash
	 *
	 * @return
	 */
	public static boolean getIsClearHash() {
		return get(CBRepository.getContext(), IS_CLEAR_HASH, false);
	}

	/**
	 * 得到交易对hash值
	 *
	 * @return
	 */
	public static String getTradePairHash() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.TRADE_PAIR_HASH, "");
	}

	public static void setTradePairHash(String hash) {
		SpUtil.put(CBRepository.getContext(), SpUtil.TRADE_PAIR_HASH, hash);
	}


	public static String getContractCionUsdt() {
		String contactCoinUsdt = get(CBRepository.getContext(), SpUtil.CONTRACT_COIN_USDT, "");
		if (TextUtils.isEmpty(contactCoinUsdt)) {
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackListFirst();
			if (table != null) {
				contactCoinUsdt = table.name;
			} else {
				contactCoinUsdt = "BTC-SWAP";
			}
		}
		return contactCoinUsdt;
	}

	public static String getContractCionNew() {
		String contactCoin = get(CBRepository.getContext(), SpUtil.CONTRACT_COIN_NEW, "");
		if (TextUtils.isEmpty(contactCoin)) {
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackListFirst();
			if (table != null) {
				contactCoin = table.name;
			} else {
				contactCoin = "BTC-SWAP";
			}
		}else {
			if(TradeUtils.isUsdtContract(contactCoin)){
				ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(contactCoin);
				if (table != null) {
					contactCoin = table.name;
				} else {
					contactCoin = "BTC-SWAP";
				}
			}else {
				ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(contactCoin);
				if (table != null) {
					contactCoin = table.name;
				} else {
					contactCoin = "BTCUSDT";
				}
			}
		}
		return contactCoin;
	}

	public static String checkContractCion(String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackListFirst();
			if (table != null) {
				symbol = table.name;
			} else {
				symbol = "BTC-SWAP";
			}
		}else {
			if(TradeUtils.isUsdtContract(symbol)){
				ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
				if (table != null) {
					symbol = table.name;
				} else {
					symbol = "BTC-SWAP";
				}
			}else {
				ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(symbol);
				if (table != null) {
					symbol = table.name;
				} else {
					symbol = "BTCUSDT";
				}
			}
		}
		return symbol;
	}



	public static void putContractNew(String symbol){
		put(CBRepository.getContext(),CONTRACT_COIN_NEW,symbol);
	}





	public static String getSpotCoin() {
		String symbol = SpUtil.get(CBRepository.getContext(), SpUtil.SPOT_COIN, "BTCUSDT");
		TradePairInfoTable table = TradePairInfoController.getInstance().queryDataByTradePair(symbol);
		if (table == null || TextUtils.isEmpty(table.tradePair)) {
			symbol = "BTCUSDT";
		}
		return symbol;
	}

	/**
	 * @param context
	 * @param key
	 * @return 是否开启合约
	 */
	public static boolean isOpenContract(Context context, String key) {
		if (TextUtils.isEmpty(key)) return false;
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo == null) return false;
		key = key + userInfo.userId;
		return get(context, key, false);
	}


	/**
	 * 设置合约开启状态
	 */
	public static void setContractStatus(Context context, String key, boolean isOpen) {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo == null) return;
		key = key + userInfo.userId;
		put(context, key, isOpen);
	}

	public static boolean getHasNewVersion() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.KEY_HAS_NEW_VERSION, false);
	}

	public static void setHasNewVersion(boolean value) {
		SpUtil.put(CBRepository.getContext(), SpUtil.KEY_HAS_NEW_VERSION, value);
	}

	public static void setProtocolStatusForContract(@NonNull String contract, boolean enable) {

		if (TextUtils.isEmpty(contract)) {
			return;
		}

		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo == null) {
			return;
		}

		String key = null;
		switch (contract.toUpperCase()) {
			case "USDT":
				key = KEY_USDT_PROTOCOL + userInfo.userId;
				break;
			case "BTC":
				key = KEY_BTC_PROTOCOL + userInfo.userId;
				break;
		}

		if (!TextUtils.isEmpty(key)) {
			SpUtil.put(CBRepository.getContext(), key, enable);
		}
	}

	public static boolean getProtocolStatusOfContract(String contract) {
		if (TextUtils.isEmpty(contract)) {
			return false;
		}

		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo == null) {
			return false;
		}

		String key = null;
		switch (contract.toUpperCase()) {
			case "USDT":
				key = KEY_USDT_PROTOCOL + userInfo.userId;
				break;
			case "BTC":
				key = KEY_BTC_PROTOCOL + userInfo.userId;
				break;
		}

		return SpUtil.get(CBRepository.getContext(), key, false);
	}

	public static boolean isTransferToYbb() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.TRANSFER_TO_YBB, true);
	}

	public static void setTransferToYbb(boolean value) {
		SpUtil.put(CBRepository.getContext(), SpUtil.TRANSFER_TO_YBB, value);
	}

	public static void setFortuneSwitch(int value) {
		put(CBRepository.getContext(), KEY_FORTUNE_SWITCH, value);
	}

	public static int getFortuneSwitch() {
		return get(CBRepository.getContext(), KEY_FORTUNE_SWITCH, 0);
	}

	public static void setNeedNewUserGuide(boolean value) {
		put(CBRepository.getContext(), KEY_NEW_USER_GUIDE, value);
	}

	public static boolean isNeedNewUserGuide() {
		return get(CBRepository.getContext(), KEY_NEW_USER_GUIDE, false);
	}


	public static int getContractUsdtUnitSwitch() {
		return get(CBRepository.getContext(), CONTRACT_USDT_UNIT_SWITCH, 0);
	}

	public static void setContractUsdtUnitSwitch(int value) {
		put(CBRepository.getContext(), CONTRACT_USDT_UNIT_SWITCH, value);
	}

	public static void setSpotTradingOptions(String options) {
		put(CBRepository.getContext(), USER_SPOT_TAB_OPTIONS, options);
	}

	public static String getSpotTradingOptions() {
		return get(CBRepository.getContext(), USER_SPOT_TAB_OPTIONS, "coin");
	}


	public static void setContractOptions(String options) {
		put(CBRepository.getContext(), USER_CONTRACT_OPTIONS, options);
	}

	public static String getContractOptions() {
		return get(CBRepository.getContext(), USER_CONTRACT_OPTIONS, "usdt");
	}


	public static void putAppRecreate(boolean isReceate) {
		put(CBRepository.getContext(), APP_IS_RECREATE, isReceate);
	}


	public static boolean getAppIsRecreate() {
		boolean isRecreate = get(CBRepository.getContext(), APP_IS_RECREATE, false);
		SpUtil.putAppRecreate(false);
		return isRecreate;
	}
	public static void putFragmentRecreate(boolean isReceate) {
		put(CBRepository.getContext(),APP_FRAGMENT_RECREATE , isReceate);
	}

	public static boolean getFragmentIsRecreate() {
		boolean isRecreate = get(CBRepository.getContext(), APP_FRAGMENT_RECREATE, false);
		SpUtil.putFragmentRecreate(false);
		return isRecreate;
	}


	public static void setEnableNewKline(boolean enable) {
		put(CBRepository.getContext(), ENABLE_NEW_KLINE, enable);
	}

	public static boolean getEnableNewKline() {
		return get(CBRepository.getContext(), ENABLE_NEW_KLINE, false);
	}

	public static void setUpsAndDownsType(int type) {
		put(CBRepository.getContext(), UPS_AND_DOWNS_TYPE, type);
	}

	public static int getUpsAndDownsType() {
		return get(CBRepository.getContext(), UPS_AND_DOWNS_TYPE, Constants.UPS_AND_DOWNS_TYPE_ZERO);
	}

	public static void setCurrentBaseUrl(String baseUrl) {
		put(CBRepository.getContext(), CURRENT_BASE_URL, baseUrl);
	}

	/**
	 * 获取App正在使用的BaseUrl，也就是app启动时设置的base_url
	 *
	 * @return
	 */
	public static String getCurrentBaseUrl() {
		return get(CBRepository.getContext(), CURRENT_BASE_URL, "");
	}

	public static void setCurrentWsUrl(String baseUrl) {
		put(CBRepository.getContext(), CURRENT_WS_URL, baseUrl);
	}

	/**
	 * 获取App正在使用的WsUrl，也就是app启动时设置的ws_url
	 * 	 *
	 * @return
	 */
	public static String getCurrentWsUrl() {
		return get(CBRepository.getContext(), CURRENT_WS_URL, "");
	}

	public static void putShowContractPlan(boolean b) {
		put(CBRepository.getContext(), SHOW_CONTRACT_PLAN, b);
	}

	public static boolean getShowContractPlan() {
		return get(CBRepository.getContext(),SHOW_CONTRACT_PLAN,false);

	}


	public static void setCurrentH5Url(String baseUrl) {
		put(CBRepository.getContext(), CURRENT_H5_URL, baseUrl);
	}

	/**
	 * 获取App正在使用的BaseUrl，也就是app启动时设置的base_url
	 *
	 * @return
	 */
	public static String getCurrentH5Url() {
		return get(CBRepository.getContext(), CURRENT_H5_URL, "");
	}

}
