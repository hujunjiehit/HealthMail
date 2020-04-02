package com.coinbene.common;

/**
 * growing io手动埋点
 */
public class PostPointHandler {

	private static final String click = "app_element_click";
	private static final String brower = "app_page_brower";

	public static final String tab_home = "底导-首页";
	public static final String tab_market = "底导-行情";
	public static final String tab_spot = "底导-现货";
	public static final String tab_contract = "底导-合约";
	public static final String tab_option = "底导-猜涨跌";
	public static final String tab_balance = "底导-资产";


	public static final String market_tab_optional = "自选_tab";
	public static final String market_tab_contract = "合约_tab";

	public static final String market_tab_coin = "tab";//前面拼接交易组名称
	public static final String market_search = "搜索";
	public static final String market_item = "交易对行情";//前面拼接交易对名称
	public static final String market_banner = "轮播图";//后面拼接url
	public static final String market_notice = "公告栏";//后面拼接url
	public static final String market_sort_edit = "编辑自选";
	public static final String market_sort_coin = "按币种排序";
	public static final String market_sort_24 = "按24h量排序";
	public static final String market_sort_last_price = "按最新加排序";
	public static final String market_sort_p = "按涨跌幅排序";


	public static final String tab_coin = "币币tab";
	public static final String tab_otc = "法币tab";
	public static final String tab_margin = "杠杆tab";

	public static final String spot_coin_price_eit = "币币价格输入框";
	public static final String spot_coin_vol_eit = "币币数量输入框";


	public static final String spot_coin_kline = "币币k线图";
	public static final String spot_coin_buy = "币币买入按钮";
	public static final String spot_coin_buy_success = "买入按钮_挂单成功";
	public static final String spot_coin_sell = "币币卖出按钮";
	public static final String spot_coin_sell_success = "卖出按钮_挂单成功";
	public static final String spot_coin_tab_buy = "币币买入tab";
	public static final String spot_coin_tab_sell = "币币卖出tab";
	public static final String spot_coin_change = "币币切换交易对";
	public static final String spot_coin_optional = "币币自选";
	public static final String spot_coin_order_detail = "币币盘口_最新成交";
	public static final String spot_coin_order_list = "币币盘口_最新挂单";
	public static final String spot_order_liset_item = "币币盘口价格";
	public static final String spot_coin_cancel_order = "币币撤单";
	public static final String spot_coin_history = "币币历史委托tab";
	public static final String spot_coin_history_item = "币币历史委托条目";
	public static final String spot_coin_history_all = "币币历史委托查看全部";
	public static final String spot_coin_current_entrust = "币币当前委托tab";
	public static final String spot_coin_hide_other = "币币隐藏其他交易对";
	public static final String spot_coin_login_register = "币币登录/注册";
	public static final String margin_coin_login_register = "杠杆登录/注册";

	public static final String balance_deposit = "资产充值";
	public static final String balance_withdraw = "资产提币";
	public static final String balance_transfer = "资产划转";
	public static final String balance_balance_item = "资产任何币种";
	public static final String balance_user_center = "资产个人中心";
	public static final String balance_record = "资产记录";
	public static final String balance_hide_balance = "资产隐藏资产";
	public static final String balance_show_balance = "资产显示资产";
	public static final String balance_tab_contract = "资产合约tab";
	public static final String balance_tab_option = "资产期权tab";
	public static final String balance_tab_margin = "资产杠杆tab";
	public static final String balance_tab_coin = "资产币币tab";
	public static final String balance_hide_asset_of_0 = "资产隐藏资产为0的币种";
	public static final String balance_show_asset_of_0 = "资产显示资产为0的币种";


	public static final String login_login_btn = "登录点击登录按钮";
	public static final String login_login_success = "登录成功";
	public static final String login_login_register_btn = "登录点击注册";


	public static final String register_register_btn = "注册点击注册按钮";
	public static final String register_register_success = "注册成功";
	public static final String register_email_register = "注册点击邮箱注册";

	public static final String register_email_register_btn = "邮箱注册点击注册";
	public static final String register_email_register_success = "邮箱注册注册成功";
	public static final String invitation = "邀请返金";


	public static final String spot_coin_brower = "币币页";
	public static final String coin_kline_brower = "币种k线图";//前面拼接币种名称
	public static final String login_brower = "登录页";
	public static final String register_brower = "注册页";
	public static final String market_brower = "行情页";
	public static final String register_email_brower = "注册页_邮箱";


	public static final String option_brower = "猜涨跌";
	public static final String otc_brower = "法币页";
	public static final String contract_brower = "合约页";
	public static final String balance_brower = "资产页";


//	public static final String startup_brower = "开屏广告浏览";
//	public static final String startup_click = "开屏广告点击";
//
//
//	public static final String main_dialog_brower = "首页弹窗浏览";
//	public static final String main_dialog_click = "首页弹窗点击";

	/**
	 * 上传点击事件
	 *
	 * @param eventValue 点击content value
	 */
	public static void postClickData(String eventValue) {
//		GrowingIO.getInstance().track(click, NetUtil.getClickPostPointData(eventValue));
	}

	/**
	 * 上传浏览事件
	 *
	 * @param pageValue 点击page value
	 */
	public static void postBrowerData(String pageValue) {
//		GrowingIO.getInstance().track(brower, NetUtil.getBorwerPostPointData(pageValue));

	}

}
