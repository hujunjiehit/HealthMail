package com.coinbene.common;


import com.coinbene.common.context.CBRepository;

/**
 * 常量
 *
 * @author huyong
 */
public class Constants {
	public static final int ENVIRONMENT_COUNT = 4;

	public static final int DEV_ENVIROMENT = 0; //dev环境
	public static final int TEST_ENVIROMENT = 1; //test环境
	public static final int STAGING_ENVIROMENT = 2; //staging预生产环境
	public static final int ONLINE_ENVIROMENT = 3;  //线上环境

	public static final int WEBSOCKET_PING_INTERVAL = 10;  //10秒

	public static final String BASE_URL = CBRepository.getCurrentEnvironment().BASE_URL;
	public static final String BASE_URL_H5 = CBRepository.getCurrentEnvironment().BASE_URL_H5;
	public static final String BASE_IMG_URL = CBRepository.getCurrentEnvironment().BASE_URL_RES;
	public static final String BASE_WEBSOCKET = CBRepository.getCurrentEnvironment().BASE_WEBSOCKET;
	public static final String BASE_WEBSOCKET_CONTRACT_BTC = CBRepository.getCurrentEnvironment().BASE_WEBSOCKET_CONTRACT;
	public static final String BASE_WEBSOCKET_CONTRACT_USDT = CBRepository.getCurrentEnvironment().BASE_WEBSOCKET_USDT;

	public static final String BASE_WEBSOCKET_URL = CBRepository.getCurrentEnvironment().BASE_WEBSOCKET_URL;

	public static final String OPTIONS_BROKER_ID = CBRepository.getCurrentEnvironment().OPTIONS_BROKER_ID;

	public static final String DOANLOAD_APK_QR_CODE_URL = CBRepository.getCurrentEnvironment().BASE_URL_H5 + "/appDownload.html#/appDownload";
	//websocket开关
	public static final String WEBSOCKET_OFF_ON = BASE_WEBSOCKET + "/gated-launch";

	//巴西的服务条款
	public static final String BASE_SERVICE_LINK_URL_PT = "https://www.coinbene.com.br/Termos-de-Uso";
	//2.0接口
	public static final String APP_CHECK_UPDATE = BASE_URL + "/content/checkVersion";//update
	//    public static final String USER_LOGIN = BASE_URL + "/user/login";//登录
	public static final String USER_LOGIN_V3 = BASE_URL + "/user/public/auth/loginV3";//登录   ok

	//    public static final String USER_REGISTER = BASE_URL + "/user/register";//注册
	public static final String USER_REGISTER_V3 = BASE_URL + "/user/public/auth/registerV3";//注册

	//    public static final String USER_SENDSMS_NOLOGIN = BASE_URL + "/user/public/sendSms";//发送验证码(无需登录)注册 找回密码用此接口
	public static final String USER_SENDSMS_NOLOGIN_V2 = BASE_URL + "/user/public/sendSmsV2";//发送验证码(无需登录)注册 找回密码用此接口


	//    public static final String USER_SEND_MAIL_NOLOGIN = BASE_URL + "/user/public/sendMail";//未登录发送邮件
	public static final String USER_SEND_MAIL_NOLOGIN_V2 = BASE_URL + "/user/public/sendMailV2";//未登录发送邮件

	public static final String USER_SENDSMS = BASE_URL + "/user/sendSms";//发送验证码登录需要发送
	public static final String USER_BIND_EMAIL_CONFIRM = BASE_URL + "/user/newemail/bindEmailConfirm";//绑定邮箱确认 //user/bindEmailConfirm
	public static final String USER_RESET_LOGIN_PASSWD = BASE_URL + "/user/public/resetLoginPasswdV4";//重置登录密码


	public static final String USER_SEND_MAIL = BASE_URL + "/user/sendMail";//登录发送邮件
	public static final String USER_GET_USERINFO = BASE_URL + "/user/userInfo";//获取用户信息
	public static final String USER_RESET_PIN = BASE_URL + "/user/resetPinPasswd";//重置资金密码
	public static final String USER_SET_PIN = BASE_URL + "/user/setPinPasswd";//设置资金密码
	public static final String USER_TOGGLE_VERIFYWAY = BASE_URL + "/user/toggleVerifyWay";//验证码首选项

	public static final String USER_CURORDER_LIST = BASE_URL + "/trade/curorder/list";//当前委托查询
	public static final String TRADE_HISORDER_LIST = BASE_URL + "/trade/hisorder/list";//历史委托查询
	public static final String TRADE_HIGH_lEVER_LIST = BASE_URL + "/trade/advanced/order/list";//高级委托列表

	public static final String TRADE_ORDER = BASE_URL + "/trade/order";//委托下单
	public static final String TRADE_PLAN_ORDER = BASE_URL + "/trade/plan/order";//止盈止损
	public static final String TRADE_OCO_ORDER = BASE_URL + "/trade/oco/order";//oco


	public static final String TRADE_ORDER_CANCEL = BASE_URL + "/trade/order/cancel";//取消委托
	public static final String TRADE_ORDER_CANCEL_ALL = BASE_URL + "/trade/order/cancelAll";//取消全部委托
	public static final String TRADE_HIGH_LEVER_ORDER_CANCEL = BASE_URL + "/trade/cancel/advanced/order";//撤销高级委托


	public static final String ACCOUNT_ADDRESS_GET_DEPOSIT = BASE_URL + "/account/address/getDeposit";//获取充值地址
	public static final String ACCOUNT_ADDRESS_NEW_DEPOSIT = BASE_URL + "/account/address/newDeposit";//创建充值地址
	public static final String ACCOUNT_DEPOSIT_LIST = BASE_URL + "/account/deposit/list";//查询充值记录
	public static final String ACCOUNT_WITHDRAW_LIST = BASE_URL + "/account/withdraw/list";//查询提现记录
	public static final String ACCOUNT_BALANCE_ONE = BASE_URL + "/account/account/one";//查询某一资产
	public static final String ACCOUNT_ADDRESS_WITHDRAW_LIST = BASE_URL + "/account/address/withdrawList";//查询提现地址
	public static final String ACCOUNT_ADDRESS_DELETE_LIST = BASE_URL + "/account/address/delWithdraw";//查询提现体质
	public static final String ACCOUNT_ADDRESS_ADD_WITHDARW = BASE_URL + "/account/address/newWithdraw";//添加充值地址
	public static final String ACCOUNT_WITHDARW_APPLY = BASE_URL + "/account/withdraw/apply";//提现申请
	public static final String ACCOUNT_TRANSFER = BASE_URL + "/account/transfer/transfer";//平台内转账
	public static final String ACCOUNT_ADD_TRANSFER = BASE_URL + "/account/transfer/addAddress";//添加转账账号
	public static final String ACCOUNT_GET_TRANSFER_LIST = BASE_URL + "/account/transfer/getAddressList";//添加转账账号
	public static final String ACCOUNT_GET_30DAY = BASE_URL + "/account/account/assetlist/30days";//30天之内的资产  走势


	//    public static final String MARKET_ASSETS = BASE_URL + "/market/site/assets";//获取指定站点的所有资产
	public static final String MARKET_TRADEPAIR_GROUP = BASE_URL + "/market/site/tradepair/group";//获取指定站点的交易对分组详情
	public static final String MARKET_TRADEPAIR_GROUP_HASH = BASE_URL + "/market/v3/site/tradepair/appgroup";//获取指定站点的交易对分组详情  带hash


	public static final String MARKET_TRADEPAIR_DATA = BASE_URL + "/market/site/tradepair/group/quote";//分组查询交易对行情
	public static final String MARKET_EXCHANGE_RATES = BASE_URL + "/market/exchange-rates";//获取当前汇率


	public static final String MARKET_ORDER_LIST = BASE_URL + "/market/tradepair/latest/orderlist";//最新挂单查询
	public static final String MARKET_TRADEPAIR_KLINE = BASE_URL + "/market/tradepair/kline";//K线数据
	public static final String MARKET_TRADEPAIR_LATEST_TRADEDETAIL = BASE_URL + "/market/tradepair/latest/tradedetail";//最新成交明细查询
	public static final String USER_REFRESH_TOKEN = BASE_URL + "/user/refresh_token";//获取新的token
	public static final String REFRESH_TOKEN = "/user/refresh_token";//获取新的token
	public static final String ACCOUNT_TRANSFER_LIST = BASE_URL + "/account/transfer/list";//查询转账记录
	//KYC实名认证接口
	public static final String KYC_UPLOAD = BASE_URL + "/zuul/kyc/upload";//kyc上传图片
	public static final String KYC_ADD_USERINFO = BASE_URL + "/kyc/addkyc";//kyc上传用户信息
	public static final String KYC_GET_STATUS = BASE_URL + "/kyc/getstatus";//kyc查询状态
	public static final String KYC_GET_INFO = BASE_URL + "/kyc/getkyc";//kyc查询信息
	//我的页面-->公告
	public static final String KYC_NOTICE_INFO = BASE_URL + "/user/activity/notice";//我的页面的公告
	//自选
	public static final String GET_FAVORITE_LIST = BASE_URL + "/user/favorite/list";//获取自选交易对
	public static final String GET_FAVORITE_TOGGLE = BASE_URL + "/user/favorite/toggle";//删除或者添加交易对  type  1添加2删除
	public static final String GET_MARKET_TRADEPAIR_QUOTE = BASE_URL + "/market/site/tradepair/multi/quote";//查询指定交易对信息
	public static final String FAVORITE_EDIT = BASE_URL + "/user/favorite/toggleBatch";//批量编辑自选

	//otc
	public static final String GET_OTC_AD_LIST = BASE_URL + "/otc/public/list";//查询广告列表
	public static final String OTC_UPLOAD_FILE = BASE_URL + "/otc/oss/upload";//otc上传图片
	public static final String OTC_BIND_BANK = BASE_URL + "/otc/payment/bandBank";//otc绑定银行卡
	public static final String OTC_BIND_OTHER = BASE_URL + "/otc/payment/bandOther";//otc绑定支付宝或者微信
	public static final String OTC_BIND_INFO = BASE_URL + "/otc/payment/getBank";//查看绑定信息
	public static final String OTC_PAY_ORDER = BASE_URL + "/otc/orders/placeOrder";//下单
	public static final String OTC_GET_ORDER_INFO = BASE_URL + "/otc/orders/orderInfo";//查看订单详情
	public static final String OTC_GET_ORDER_List = BASE_URL + "/otc/orders/appOrderPage";//查寻订单列表
	public static final String OTC_GET_SHOPS_ORDER_List = BASE_URL + "/otc/orders/ordersPage";//查寻订单列表
	public static final String OTC_GET_CONFIG = BASE_URL + "/otc/public/getAdConfig";//查寻OTC配置信息
	public static final String OTC_GET_USER_PAY_TYPE = BASE_URL + "/otc/payment/v2/getPayWays";//获取用户的支付方式
	public static final String OTC_ONLINE_PAYWAY = BASE_URL + "/otc/payment/onlinePayWay";//修改支付绑定状态
	public static final String OTC_UNBIND_PAYWAY = BASE_URL + "/otc/payment/unbindPayWay";//解绑收款方式

	//OTC商家相关
	public static final String OTC_GET_RELEASE_AD_INFO = BASE_URL + "/otc/supplier/ad/getReleaseAdInfo";//请求发布广告信息
	public static final String OTC_MAX_TRADE_ACCPUNT = BASE_URL + "/otc/supplier/ad/getPriceAndNum";//获取用户最大可交易数量
	public static final String OTC_QUERY_PSW_STATE = BASE_URL + "/otc/supplier/ad/queryPswState";//请求是否需要输入密码状态
	public static final String OTC_GET_PRICE_DATA = BASE_URL + "/otc/supplier/ad/getPriceData";//请求最大下单量以及币种估值


	/**
	 * 广告保存
	 */
	public static final String AD_REALEASE = BASE_URL + "/otc/supplier/ad/release";
	/**
	 * otc获取市场参考价
	 */
	public static final String GET_MARKET_PRICE = BASE_URL + "/otc/supplier/ad/getMarketPriceData";


	/**
	 * 广告下架
	 */
	public static final String AD_CANCEL = BASE_URL + "/otc/supplier/ad/withdraw";

	/**
	 * 广告删除
	 */
	public static final String AD_DELETE = BASE_URL + "/otc/supplier/ad/delete";

	public static final String OTC_HANDLE_ORDER = BASE_URL + "/otc/orders/handleOrder";//用户操作订单
	public static final String SWITCH_CONFIG = BASE_URL + "/market/site/public/switch";//app 内开关  包含OTC  和  合约
//    public static final String SWITCH_CONFIG = BASE_URL + "/api/contract-market-api/site/globalconfig";//app 内开关  包含OTC  和  合约
	/**
	 * 活动开关
	 */
	public static final String ACTIVITY_SWITCH = BASE_URL + "/activities/public/switch";


	public static final String TRADE_ITEM_DETAIL_LIST = BASE_URL + "/trade/trade/record/list";//历史委托详情-->成交明细查询接口
	public static final String USER_CONI_SWITHC = BASE_URL + "/user/coniDiscountSwitch";//切换coni的开关控制
	public static final String GET_AD_INFO = BASE_URL + "/content/getAdvertInfo";//获取广告的信息
	public static final String POPUP_BANNER = BASE_URL + "/content/indexPopupBanner";//APP首页弹窗
	public static final String K_COIN_INFO = BASE_URL + "/content/coinInfo";//币种介绍


	//内容
	public static final String CONTENT_GET_BANNER_LIST = BASE_URL + "/content/getbanner";//首页banner图
	public static final String CONTENT_GET_BANNER_ARTICLES_LIST = BASE_URL + "/content/getSlideArticles";//轮播文章
	public static final String CONTENT_GET_ARTICLES_LIST = BASE_URL + "/content/articletitle";//公告中心
	public static final String CONTENT_GET_COUNTRYAREA_LIST = BASE_URL + "/content/getCountryAreaCode";//获取国家区号

	//商家广告
	public static final String SELLER_ADVERTISEMENT = BASE_URL + "/otc/supplier/ad/mylist";

	//交易排名赛活动图片链接
	public static final String img_url = "http://res.coinbene.mobi/coinbene-activity/88b45e832676e8f4.png";

	/**
	 * 期货成交详情
	 */
	public static final String CONTRACT_TRANSACTION_DETAIL = BASE_URL + "/api/contract-order-api/order/tradedetail";
	public static final String CONTRACT_TRANSACTION_DETAIL_USDT = BASE_URL + "/api/usdt-order-api/order/tradedetail";

	/**
	 * 仓位模式更改
	 */
	public static final String CONTRACT_POSTIONMODE_CHANGE = BASE_URL + "/api/contract-trade-api/trade/changesetting";
	public static final String CONTRACT_POSTIONMODE_CHANGE_USDT = BASE_URL + "/api/usdt-trade-api/trade/changesetting";

	/**
	 * 计算器盈亏计算
	 */
	public static final String CALCULATOR_CALCULATE_PL = BASE_URL + "/api/contract-order-api/public/calculator/profit";
	public static final String CALCULATOR_CALCULATE_PL_USDT = BASE_URL + "/api/usdt-order-api/public/calculator/profit";

	/**
	 * 计算器强平价计算
	 */
	public static final String CALCULATOR_CALCULATE_FC = BASE_URL + "/api/contract-order-api/public/calculator/liquidationPrice";
	public static final String CALCULATOR_CALCULATE_FC_USDT = BASE_URL + "/api/usdt-order-api/public/calculator/liquidationPrice";


	/**
	 * 按预期收益率计算目标价格
	 */
	public static final String CALCULATOR_FC_RATE = BASE_URL + "/api/contract-order-api/public/calculator/targetPriceRoe";
	public static final String CALCULATOR_FC_RATE_USDT = BASE_URL + "/api/usdt-order-api/public/calculator/targetPriceRoe";

	/**
	 * 按预期收益计算目标价格
	 */
	public static final String CALCULATOR_FC_PROFIT = BASE_URL + "/api/contract-order-api/public/calculator/targetPriceProfit";
	public static final String CALCULATOR_FC_PROFIT_USDT = BASE_URL + "/api/usdt-order-api/public/calculator/targetPriceProfit";


	/**
	 * 保证金信息
	 */
	public static final String CONTRACT_MARGIN_INFO = BASE_URL + "/api/contract-account-api/account/margininfo";
	public static final String CONTRACT_MARGIN_INFO_USDT = BASE_URL + "/api/usdt-account-api/account/margininfo";


	/**
	 * 计算强平价格
	 */
	public static final String CONTRACT_CALCULATE_FROCE = BASE_URL + "/api/contract-account-api/account/pricecalculate";
	public static final String CONTRACT_CALCULATE_FROCE_USDT = BASE_URL + "/api/usdt-account-api/account/pricecalculate";


	/**
	 * 更新保证金
	 */
	public static final String CONTRACT_UPDATE_MARGIN = BASE_URL + "/api/contract-account-api/account/adjustmargin";
	public static final String CONTRACT_UPDATE_MARGIN_USDT = BASE_URL + "/api/usdt-account-api/account/adjustmargin";

	/**
	 * 用户挖矿总数据
	 */
	public static final String CONTRACT_MINING_SUMMARY = BASE_URL + "/api/contract-account-api/mine/user/summary";

	/**
	 * 用户挖矿每日明细
	 */
	public static final String CONTRACT_MINING_DETAIL = BASE_URL + "/api/contract-account-api/mine/user/detail";


	/**
	 * 期权登录接口
	 */
	public static final String OPTIONS_LOGIN = BASE_URL + "/api/options-account-api/user/login";


	/**
	 * 期权收益率
	 */
	public static final String OPTIONS_PROFIT_RATE = BASE_URL + "/api/options-account-api/order/public/maxprofitrate";


	/**
	 * 期权划转记录
	 */
	public static final String OPTIONS_TRANSFER_RECORD = BASE_URL + "/api/options-account-api/fund/search";

	/**
	 * 期权下单请求
	 */
	public static final String OPTIONS_PLACE_ORDER = BASE_URL + "/api/options-account-api/order/public/placeorder";


	/**
	 * 期权下单成功
	 */
	public static final String OPTIONS_PLACE_ORDER_SUC = BASE_URL + "/api/options-account-api/order/public/placeordersucc";


	/**
	 * 期权结算结果
	 */
	public static final String OPTIONS_SETTLE_ORDER = BASE_URL + "/api/options-account-api/order/public/settleorder";

	/**
	 * 期权转账
	 */
	public static final String OPTIONS_TRANSFER = BASE_URL + "/api/options-account-api/fund/transfer";


	/**
	 * 期权可用资产
	 */
	public static final String OPTIONS_VALID_BALANCE = BASE_URL + "/api/options-account-api/fund/balancebythird";

	/**
	 * 期权转账流水号请求
	 */
	public static final String OPTIONS_SERIALNUM = BASE_URL + "/api/options-account-api/fund/getserial";


	/**
	 * 期权交易记录
	 */
	public static final String OPTIONS_RECORD = BASE_URL + "/api/options-account-api/order/query";


	/**
	 * 期权账户退出登录
	 */
	public static final String OPTIONS_EXIT_LOGIN = BASE_URL + "/api/options-account-api/user/logout";

	/**
	 * 旧手机号发送验证码
	 */
	public static final String OLD_VERIFY_CODE = BASE_URL + "/user/sendSms";

	/**
	 * 更换手机号
	 */
	public static final String UPDATE_PHONE_NUM = BASE_URL + "/user/changePhone";

	/**
	 * 首页热门数据
	 */
	public static final String HOME_HOT_COIN = BASE_URL + "/market/site/banner/tradePair";


	/**
	 * PC同步功能
	 */
	public static final String UPDATE_LOGIN_PASSWORD = BASE_URL + "/user/updatePasswd/login";
	public static final String UPDATE_BALANCE_PASSWORD = BASE_URL + "/user/updatePinPasswd";
	public static final String LOGIN_VERIFY_OPEN = BASE_URL + "/user/loginVerify/open";
	public static final String LOGIN_VERIFY_CLOSE = BASE_URL + "/user/loginVerify/close";


	/**
	 * 人工上账记录接口
	 */
	public static final String RECORD_DISPATCH_INTERFACE = BASE_URL + "/account/transfer/distributionList";

	/**
	 * 邀请反金查询
	 */
	public static final String INVITE_QUERY_ASSET = BASE_URL + "/activities/inviteFriend/query";

	/**
	 * 合约指南路径
	 */
//	public static final String CONTRACT_GUIDE_URL = BASE_URL_H5 + "/contract/h5/guide.html";

	/**
	 * 查询可计划平仓数量
	 */
	public static final String CONTRACT_PLAN_QUANTITY = BASE_URL + "/api/contract-trade-api/trade/availableplanquantity";
	public static final String CONTRACT_PLAN_QUANTITY_USDT = BASE_URL + "/api/usdt-trade-api/trade/availableplanquantity";

	/**
	 * 止盈止损平仓计划委托
	 */
	public static final String CONTRACT_PLAN_CLOSE_ORDER = BASE_URL + "/api/contract-trade-api/trade/plancloseorder";
	public static final String CONTRACT_PLAN_CLOSE_ORDER_USDT = BASE_URL + "/api/usdt-trade-api/trade/plancloseorder";

	/**
	 * 止盈止损平仓计划委托
	 */
	public static final String CONTRACT_PLAN_ORDER_LIST = BASE_URL + "/api/contract-order-api/order/planorder";
	public static final String CONTRACT_PLAN_ORDER_LIST_USDT = BASE_URL + "/api/usdt-order-api/order/planorder";

	/**
	 * 取消计划委托
	 */
	public static final String CONTRACT_CANCEL_PLAN_ORDER = BASE_URL + "/api/contract-trade-api/trade/cancelplanorder";
	public static final String CONTRACT_CANCEL_PLAN_ORDER_USDT = BASE_URL + "/api/usdt-trade-api/trade/cancelplanorder";

	/**
	 * 合约
	 */
	public static final String MARKET_FUTURE_KLINE = BASE_URL + "/api/contract-market-api/appkline/history";//期货的K线数据
	public static final String MARKET_FUTURE_KLINE_USDT = BASE_URL + "/api/usdt-market-api/appkline/history";//USDT期货的K线数据
//    public static final String MARKET_FUTURE_KLINE_MARKLINE = BASE_URL + "/market/tradepair/kline";//期货的K线数据,标记线的数据

	/**
	 * 资金划转
	 */
	public static final String FUND_TRANSFER = BASE_URL + "/api/contract-account-api/account/transfer";

	/**
	 * 划转记录
	 */
	public static final String FUND_TRANSFER_RECORD = BASE_URL + "/api/contract-account-api/account/transferhistory";

	/**
	 * 资金费用记录
	 */
	public static final String FUND_COST = BASE_URL + "/api/contract-account-api/account/feehistory";
	public static final String FUND_COST_USDT = BASE_URL + "/api/usdt-account-api/account/feehistory";

	/**
	 * 合约当前委托
	 */
	public static final String CONTRACT_CURRENT_DELEGATION = BASE_URL + "/api/contract-order-api/order/curorder";
	public static final String CONTRACT_CURRENT_DELEGATION_USDT = BASE_URL + "/api/usdt-order-api/order/curorder";

	/**
	 * 合约历史委托
	 */
	public static final String CONTRACT_HISTORY_DELEGATION = BASE_URL + "/api/contract-order-api/order/hisorder";
	public static final String CONTRACT_HISTORY_DELEGATION_USDT = BASE_URL + "/api/usdt-order-api/order/hisorder";

	/**
	 * 合约列表
	 */
	public static final String MARKET_CONTRACT_LIST = BASE_URL + "/api/contract-market-api/site/contractlist";
	public static final String MARKET_CONTRACT_USDT_LIST = BASE_URL + "/api/usdt-market-api/site/contractlist";
	/**
	 * 持有仓位
	 */
	public static final String ACCOUNT_POSITION_LIST = BASE_URL + "/api/contract-account-api/account/positionlist";
	public static final String ACCOUNT_POSITION_LIST_USDT = BASE_URL + "/api/usdt-account-api/account/positionlist";

	/**
	 * 查询用户等级及费率等信息
	 */
	public static final String ACCOUNT_FEE_RATEINFO = BASE_URL + "/api/contract-account-api/account/feeRateInfo";

	/**
	 * 合约账户信息
	 */
	public static final String CONTRACT_ACCOUNT_INFO = BASE_URL + "/api/contract-account-api/account/accountinfo";
	public static final String CONTRACT_ACCOUNT_INFO_USDT = BASE_URL + "/api/usdt-account-api/account/accountinfo";


	/**
	 * 用户正在使用的杠杆接口
	 */
	public static final String TRADE_CUR_LEVERAGE = BASE_URL + "/api/contract-trade-api/trade/curleverage";
	public static final String TRADE_CUR_LEVERAGE_USDT = BASE_URL + "/api/usdt-trade-api/trade/curleverage";

	public static final String TRADE_LEVERAGE_LIMIT = BASE_URL + "/api/contract-trade-api/public/trade/leveragelimit";
	public static final String TRADE_USDT_LEVERAGE_LIMIT = BASE_URL + "/api/usdt-trade-api/public/trade/leveragelimit";

	/**
	 * 距离费用结算时间
	 */
	public static final String MARKET_CONTRACT_FUNDING_TIME = BASE_URL + "/api/contract-market-api/contract/fundingtime";
	public static final String MARKET_CONTRACT_FUNDING_TIME_USDT = BASE_URL + "/api/usdt-market-api/contract/fundingtime";


	/**
	 * 开仓
	 */
	public static final String TRADE_PLACE_ORDER = BASE_URL + "/api/contract-trade-api/trade/placeorder";
	public static final String TRADE_PLACE_ORDER_USDT = BASE_URL + "/api/usdt-trade-api/trade/placeorder";

	/**
	 * 平仓
	 */
	public static final String TRADE_CLOSE_ORDER = BASE_URL + "/api/contract-trade-api/trade/closeorder";
	public static final String TRADE_CLOSE_ORDER_USDT = BASE_URL + "/api/usdt-trade-api/trade/closeorder";


	/**
	 * 撤单
	 */
	public static final String TRADE_CANCEL_ORDER = BASE_URL + "/api/contract-trade-api/trade/cancelorder";
	public static final String TRADE_CANCEL_ORDER_USDT = BASE_URL + "/api/usdt-trade-api/trade/cancelorder";


	/**
	 * 资产,包括币币和合约的资产
	 */
	public static final String USER_ACCOUNT_TOTAL_ACCOUNT = BASE_URL + "/account/account/totalaccount";

	/**
	 * 资产相关接口 totalaccount改造之后的接口
	 */
	public static final String BALANCE_TOTAL_ACCOUNT = BASE_URL + "/api/coinbene-account-center-api/asset/totalPreestimate";
	public static final String BALANCE_SPOT_COIN = BASE_URL + "/account/account/totalInfo";
	public static final String BALANCE_MARGIN = BASE_URL + "/api/margin-rest-api/account/preestimate";
	public static final String BALANCE_BTC_CONTRACT = BASE_URL + "/api/contract-account-api/account/totalInfo";
	public static final String BALANCE_USDT_CONTRACT = BASE_URL + "/api/usdt-account-api/account/totalInfo";
	public static final String BALANCE_FORTUNE = BASE_URL + "/api/financial-ybb-api/account/preestimate";
	public static final String BALANCE_GAME = BASE_URL + "/api/game-common-api/account/totalInfo";
	public static final String BALANCE_OPTIONS = BASE_URL + "/api/options-account-api/account/totalInfo";


	/**
	 * POST /user/changeLanguage
	 * 用户登录后记录切换语言
	 */
	public static final String USER_CHANGELANGUAGE = BASE_URL + "/user/changeLanguage";

	/**
	 * 上传后端统计
	 */
	public static final String POST_CLIENT_INFO = BASE_URL + "/api/risk-gather-api/public/gather/clientInfo";

	/**
	 * 杠杆交易（当前借币）
	 */
	public static final String LEVERAGE_CURRENT_LOAN = BASE_URL + "/api/margin-rest-api/borrowed/openOrderList";
	/**
	 * 杠杆交易（历史借币）
	 */
	public static final String LEVERAGE_HISTORY_LOAN = BASE_URL + "/api/margin-rest-api/borrowed/finishOrderList";

	/**
	 * 杠杆账户可用余额
	 */
	public static final String LEVERAGE_CAN_USE = BASE_URL + "/api/margin-rest-api/transfer/max";

	/**
	 * 杠杆账户划转
	 */
	public static final String LEVERAGE_TRANSFER = BASE_URL + "/api/margin-rest-api/transfer";


	/**
	 * 杠杆账户划转记录
	 */
	public static final String LEVERAGE_TRANSFER_RECORD = BASE_URL + "/api/margin-rest-api/transfer/transferRecords";


	/***
	 * 获取杠杆币对配置相关接口
	 */
	public static final String MARGIN_SYMBOL_LIST = BASE_URL + "/api/margin-rest-api/public/symbol/list";
	/**
	 * 获取用户是否开通杠杆
	 */
	public static final String MARGIN_USER_CONFIG = BASE_URL + "/api/margin-rest-api/userConfig/single";
	/**
	 * 用户开通杠杆
	 */
	public static final String CREATE_MARGIN_USER_CONFIG = BASE_URL + "/api/margin-rest-api/userConfig/create";

	/**
	 * 获取杠杆单个币对账户信息
	 */
	public static final String GET_MARGIN_SINGLE_ACCOUNT = BASE_URL + "/api/margin-rest-api/account/single";


	/**
	 * 杠杆账单明细
	 */
	public static final String MARGIN_BILLING_DETAILS = BASE_URL + "/api/margin-rest-api/accountRecord/list";

	/**
	 * 杠杆账单类型
	 */
	public static final String MARGIN_BILL_TYPES = BASE_URL + "/api/margin-rest-api/public/accountRecord/bizType/list";

	/**
	 * 获取用户VIP信息
	 */
	public static final String USER_VIP_INFO = BASE_URL + "/user/allVipData";

	/**
	 * 杠杆账户 查询  有缓存 接口
	 */
//	/api/margin-rest-api/account/forCache

	/**
	 * 杠杆下单接口
	 */
	public static final String MARGIN_PLACE_ORDER = BASE_URL + "/trade/margin/order";


	public static final String lever_symbol_list = BASE_URL + "/api/margin-rest-api/public/symbol/list";
	public static final String MARGIN_ACCOUNT_SINGLE = BASE_URL + "/api/margin-rest-api/account/single";    //指定交易对账户列表
	public static final String MARGIN_OPEN_ORDERLIST = BASE_URL + "/api/margin-rest-api/borrowed/openOrderList";    //未还清借币订单列表
	public static final String MARGIN_INIT_BORROWED = BASE_URL + "/api/margin-rest-api/borrowed/initBorrowed";  //借币页面初始化接口
	public static final String MARGIN_BORROW_COIN = BASE_URL + "/api/margin-rest-api/borrowed/borrow";  //借币接口
	public static final String MARGIN_INIT_REPAYE = BASE_URL + "/api/margin-rest-api/borrowed/initRepay";  //还币页面初始化接口
	public static final String MARGIN_REPAY_COIN = BASE_URL + "/api/margin-rest-api/account/repay";  //还币接口


	/**
	 * 新划转相关接口
	 */
	public static final String TRANSFER_ASSET_LIST = BASE_URL + "/api/coinbene-account-center-api/asset";  //获取划转支持的资产列表
	public static final String TRANSFER_INFO = BASE_URL + "/api/coinbene-account-center-api/asset/transfer/info";  //转账前查询
	public static final String TRANSFER_ACTION = BASE_URL + "/api/coinbene-account-center-api/asset/transfer";  //转账操作
	public static final String TRANSFER_RECORD = BASE_URL + "/api/coinbene-account-center-api/asset/transfer/record";  //转账记录查询

	/**
	 * 余币宝相关接口
	 */
	public static final String YBB_ACCOUNT_TOTAL = BASE_URL + "/api/financial-ybb-api/current/account/total"; //获取余币宝活期资产详情
	public static final String YBB_ASSET_CONFIG_LIST = BASE_URL + "/api/financial-ybb-api/public/asset/config/list"; //获取余币宝配置的资产列表
	public static final String YBB_TRANSFER_IN_PAGEINFO = BASE_URL + "/api/financial-ybb-api/financial/current/transferInPageInfo"; //初始化余币宝转入接口
	public static final String YBB_TRANSFER_OUT_PAGEINFO = BASE_URL + "/api/financial-ybb-api/financial/current/transferOutPageInfo"; //初始化余币宝转出接口
	public static final String YBB_TRANSFER_ACTION = BASE_URL + "/api/financial-ybb-api/financial/current/transfer"; //余币宝转入转出接口
	public static final String YBB_ASSET_LIST = BASE_URL + "/api/financial-ybb-api/asset/list"; //余币宝支持的资产列表
	public static final String YBB_ACCOUNT_LIST = BASE_URL + "/api/financial-ybb-api/account/preestimate"; //余币宝账户列表
	public static final String YBB_RECORD_LIST = BASE_URL + "/api/financial-ybb-api/current/record/list"; //余币宝记录列表

	//开通合约
	public static final String OPEN_USDT_CONTRACT = BASE_URL + "/api/usdt-account-api/userConfig/create";
	public static final String OPEN_BTC_CONTRACT = BASE_URL + "/api/contract-account-api/userConfig/create";

	public static final String VERIFY_SLIDE_URL = BASE_URL_H5 + "/apps/smartcaptcha.html";

	//查询用户状态
	public static final String QUERY_USER_CONFIG = BASE_URL + "/api/exchange-account-center-api/userConfig/query";
	//更新用户状态
	public static final String UPDATE_USER_CONFIG = BASE_URL + "/api/exchange-account-center-api/userConfig/create";

	//安全加固
	public static final String GET_USER_AUTH = BASE_URL + "/user/public/getUserAuth";


	//代理商id
	public static final String USER_INVITATION = BASE_URL + "/activities/inviteFriend/getInvitation";
	//代理商二维码地址
	public static final String USER_INVITATION_QRCODE = BASE_URL_H5 + "/auth/register/phone?hash=%s";


	public static final String MD5_APPEND_KEY = "#@!!@#";
	public static final String URL_KEY = "url_key";
	public static final String TYPE_PHONE = "2";
	public static final String TYPE_MAIL = "1";


	/**
	 * 下拉充值提现记录--》类型 选择
	 */
	public static final int CODE_RECORD_ALL_TYPE = 0;//全部类型
	public static final int CODE_RECORD_RECHARGE_TYPE = 1;//充值
	public static final int CODE_RECORD_WITHDRAW_TYPE = 2;//提现
	public static final int CODE_RECORD_TRANSFER_TYPE = 3;//转账
	public static final int CODE_RECORD_DISPATCH_TYPE = 4;//人工上账

	/**
	 * 实名认证拍照 选择照片
	 */
	public static final int CODE_AUTH_TAKE_PHOTO = 0;//拍照
	public static final int CODE_AUTH_SELECT_PIC = 1;//选择照片

	/**
	 * 订单详情切换支付方式
	 */
	public static final int CODE_BANK = 1;//银行卡
	public static final int CODE_ALIPAY = 2;//支付宝
	public static final int CODE_WECHAT = 3;//微信

	/**
	 * 实名选择证件类型
	 */
	public static final int CODE_ID = 0;//身份证
	public static final int CODE_PASSPORT = 1;//护照
	/**
	 * intent filer broadcastmanager
	 */
	public static final String BROAD_SEND_REGISTER_EMAIL = "BROAD.SEND.REGISTER.EMAIL";
	public static final String BROAD_SENDMAIL_RIGHTBTN_CLICK = "BROAD.SENDMAIL.RIGHTBTN.CLICK";
	//订单详情 返回刷新订单列表
	public static final String ORDER_LSIT_REFRESH = "BROAD.ORDER.REFRESH";
	public static final String DOWNLOAD_IMAGE_SUCCESS = "BROAD.DOWNLOAD.IMAGE.SUCCESS";

	public static final String BROAD_SELECT_PRICE_CLICK = "BROAD.SELECT.PRICE.CLICK";
	//    public static final String BROAD_SELECT_PRICE_SELL_CLICK = "BROAD.SELECT.PRICE.SELL.CLICK";
	public static final String BROAD_K_CHART_BUY = "BROAD.K_CHART_BUY";
	public static final String BROAD_K_CHART_SELL = "BROAD.K_CHART_SELL";
	//15分钟，在用户登录状态才检测；如果设置了手势密码，弹出手势密码，否则弹出登录页面,并清除用户信息
	public static final String BROAD_MORE_THAN_15MIN = "BROAD.MORE.THAN.15MIN";
	//退出登录,清空用户信息，结束二级页面，主页切换到tab 行情，弹出登录页面
	public static final String BROAD_QUIT_LOGIN = "BROAD.QUIT.APP.TO.LOGIN";
	//15分钟点击返回，用户还是保持登录状态，但是不解锁；清除二级页面，回到主页tab 行情
	public static final String BROAD_MORE_THAN_15MIN_BACK = "BROAD.MORE.THAN.15MIN.BACK";
	public static final String LOGIN_SUCC_QUERY_BALANCE = "LOGIN.SUCC.QUERY.BALANCE";
	public static final String LOGIN_SUCC = "LOGIN.SUCC";
	public static final String SECOND_LOGIN_SUCC = "SECOND.LOGIN.SUCC";
	//自选登录成功发送广播，首页接收广播刷新自选数据
	public static final String FRESH_OPTIONAL_DATA = "BROAD.LOGIN.SUCC.FRESHOPTIONALDATA";
	public static final String FRESH_BALANCE_DATA = "BROAD.LOGIN.SUCC.FRESH.BALANCE.DATA";

	public static final String BROAD_RECREATE_ACTIVITY = "BROAD.RECREATE.ACTIVITY";

	//切换到不同的tab
	public static final String BROAD_TAB_CHANGE = "BROAD.TAB.CHANGE";
	//启动合约的k线
	public static final String BROAD_START_CONTRACT_KLINE = "BROAD.START.CONTRACT.KLINE";
	//我的页面，退出登录
	public static final String BROAD_BTN_QUIT_LOGOUT = "BROAD.BTN.QUIT.LOGOUT";

	//期权分享广播
	public static final String BROAD_OPTION_SHARE = "BROAD.OPTION_SHARE";


	public static final String K_FROM = "K.FROM";
	public static final String K_FROM_MARKET = "K.FROM.MARKET";
	public static final String K_FROM_TRADE = "K.FROM.TRADE";

	//    夜间模式广播
	public static final String BROAD_REPLACE_MODE = "BROAD.REPLACE.MODE";

	//    回调期权登录成功
	public static final String BROAD_OPTION_LOGIN_SUCCESS = "BROAD.OPTION.SUCCESS";


	/**
	 * 设置相关的页面
	 */
	public static final String CODE_PHONE_MSG_CHECK_TYPE = "1";//短信验证
	public static final String CODE_GOOGLE_CHECK_TYPE = "2";//google验证

	/**
	 * 发送短信验证的分类
	 */
	public static final String CODE_ONE_REGISTER = "1";//注册
	public static final String CODE_TWO_MODIFY_PHONE = "2";//修改手机号
	public static final String CODE_THREE_MODIFY_PWD = "3";//修改密码
	//    public static final String CODE_FOUR_BIND_PHONE = "4";//绑定手机号
	public static final String CODE_FIVE_CHANGE_SELECT = "5";//修改验证码首选项
	public static final String CODE_SIX_WITH_DRAW = "6";//提现申请
	public static final String CODE_SEVEN_SET_CAP_PWD = "16";//设置资金密码
	public static final String CODE_EIGHT_ADD_ADDRESS = "8";//添加提现地址
	public static final String CODE_NINE_SENCD_CHECK = "9";//二次登陆验证
	//    public static final String CODE_OPEN_CLOSE_CHECK = "10";//开启或关闭二次登陆验证
	public static final String CODE_BIND_MAIL = "11";//绑定邮箱
	public static final String CODE_TRANFER_INNER = "12";//站内转账
	public static final String CODE_MODIFY_NEW_PHONE = "26";//站内转账
	//*
	public static final String CODE_CHINA_PHONE = "86";


	//更多新增短信类型参考文档：http://59.110.163.209:8090/pages/viewpage.action?pageId=20644309
	public static final String CODE_TYPE_RESET_LOGIN_PWD = "30";//重置登陆密码验证码
	public static final String CODE_TYPE_RESET_ASSET_PWD = "32";//重置资金密码验证码
	public static final String CODE_TYPE_WITHDRAW = "33";//提币验证码
	public static final String CODE_TYPE_PLATFORM_TRANSFER= "34";//平台内转账验证码

	/**
	 * 发送邮箱验证码，邮箱类型
	 */
	public static final String MAIL_ONE_VERI = "13";//邮箱认证,由1变成13
	public static final String MAIL_TWO_RESET_LOGIN_PWD = "16";//找回登录密码，由2变成16
	public static final String MAIL_THRESS_RESET_CAP_PWD = "3";//重置资金密码
	public static final String MAIL_FOUR_BING_GOOGLE = "4";//Google绑定
	public static final String MAIL_FIVE_RESET_GOOGLE = "5";//Google重置
	public static final String MAIL_EIGHT_BING_MAIL = "6";//绑定邮箱
	//    public static final String MAIL_NINE_BING_PHONE = "9";//绑定手机号
	public static final String MAIL_NINE_BING_PHONE = "15";//绑定手机号, 由7变成15

	public static final String MAIL_WITHDRAW= "23";//提币验证码
	public static final String MAIL_PLATFORM_TRANSFER = "24";//转账验证码

	/**
	 * 邮箱的状态，1未绑定2未认证3已认证
	 */
	public static final String STATUS_MAIL_UNBIND = "1";//1未绑定
	public static final String STATUS_MAIL_UNVERIFY = "2";//2未认证
	public static final String STATUS_MAIL_VERIFYED = "3";//3已认证


	//实名认证状态
	public static final int AUTH_UNVERIFIED = -1;//未认证
	public static final int AUTH_PROCESSING = 1;//审核中
	public static final int AUTH_VERIFIED = 2;//已通过
	public static final int AUTH_FAILED = 9;//认证失败
	public static final int AUTH_UNFINISHED = 8;//待完善

	public static final int OTC_NOT_AUTH = 8035;//请先实名认证
	public static final int OTC_NOT_BIND_PAY_INFO = 8023;//请先绑定银行卡
	public static final int OTC_NOT_BIND_PHONE = 8040;//请先绑定手机

	//ActivityResult结果
	public static final int SELECT_PAIR_RESULT_CODE = 101;
	public static final int KLINE_BUY_SELL_RESULT_CODE = 102;//从交易页面进入k 线图，点击买入或者卖出后返回
	public static final int CONI_TIP_DIALOG_CODE = 105;

	public static final int TAB_INDEX_DEFAULT = -1;

	//常量精度固定
	public static final String PRECISION = "0.01";


	//币币界面挂单选择档位 位置 定义
	public static final int TRADE_MARKET_POP_DEFAULT = 0;//默认
	public static final int TRADE_MARKET_POP_BUY = 1;//展示买单
	public static final int TRADE_MARKET_POP_SELL = 2;//展示卖单
	//usdt cny精度
	public static String usdtPrecision = "0.000001";
	public static String cnyPrecision = "0.01";
	public static String SITE_BR = "BR";//巴西站点
	public static String SITE_MAIN = "MAIN";//主站
	public static String SITE_KO = "KO";//韩国站
	public static String SITE_VN = "VN";//越南站
	public static String LANGUAGE_JAPAN = "ja_JP";//日本语
	public static String LANGUAGE_BR = "pt_BR";//葡萄牙语
	public static String LANGUAGE_ENGLISH = "en_US";

	/**
	 * 保存全部的二级页面名字，和用户相关的页面
	 */
	public static int secondPageRelatedUserCount = 0;


	//买入 卖出广告支付方式过滤
	public static int PAY_TYPE_ALL = 0;
	public static int PAY_TYPE_BANK = 1;
	public static int PAY_TYPE_ALIPAY = 2;
	public static int PAY_TYPE_WECHAT = 3;
	//买入 卖出广告金额过滤
	public static int[] PRICE_TYPE_ALL = {0, 999999999};
	public static int[] PRICE_TYPE_UNDER_TEN = {0, 100000};//小于10万
	public static int[] PRICE_TYPE_UP_FIFTY = {50000, 999999999};//大于等于50000
	public static int[] PAY_TYPE_UP_ONE_HUNDRED_THOUSAND = {100000, 999999999};//大于等于10万
	public static int[] PAY_TYPE_UP_TWO_HUNDRED_THOUSAND = {200000, 999999999};//大于等于20万

	//行情排序字段接口  sortField传值
	public static String SORT_COIN_NAME = "s";//币种排序sortField 值
	public static String SORT_V24_VOL = "amt24";//24小时排序sortField 值
	public static String SORT_LAST_PRICE = "n";//最新价排序sortField 值
	public static String SORT_FALL_REISE = "p";//涨跌幅排序sortField 值


	//长度必须是8的倍数
	public static String Test = "67c34b2a29df4ab38bf393b663e2887b";


	public static String SORT_ASC = "ASC";//升序
	public static String SORT_DESC = "DESC";//降序
	public static String SORT_BY_SORT = "SORT";//默认排序


	//巴西法币
	public static String BRL = "BRL";

	//换绑手机号 user返回的 禁止提现原因 如果是以下  就提示用户24小时限制
	public static String CHANGE_PHONE = "changePhone";


	/**
	 * 挂单档位
	 */
	public static int MARKET_NUMBER_FORETEEN = 14;
	public static int MARKET_NUMBER_SIXTEEN = 16;
	public static int MARKET_NUMBER_EIGHTTEEN = 18;
	public static int MARKET_NUMBER_SEVENTEN = 17;
	public static int MARKET_NUMBER_SVEEN = 7;
	public static int MARKET_NUMBER_EIGHT = 8;
	public static int MARKET_NUMBER_TEN = 10;
	public static int MARKET_NUMBER_FIVE = 5;
	public static int MARKET_NUMBER_SIX = 6;
	public static int MARKET_NUMBER_TWELVE = 12;

	public static boolean COINBENE_IS_DEBUG = false;


	public static String accurasyStr = "0.00";
	public static int newScale = 0;

	public static String ACTION_AWAKE = "android.action.awake";

	/**
	 * 合约类型，BTC合约或者USDT合约
	 */
	public static int CONTRACT_TYPE_BTC = 0;
	public static int CONTRACT_TYPE_USDT = 1;

	/**
	 * Tab名字
	 */
	public static final String TAB_HOME = "home";
	public static final String TAB_MARKET = "market";
	public static final String TAB_SPOT = "spot";
	public static final String TAB_CONTRACT = "contract";
	public static final String TAB_GAME = "game";
	public static final String TAB_BALANCE = "balance";

	public static final String BUNDLE_KEY_GROUPNAME = "groupname";


	public final static String TYPE_LIMIT = "limit";
	public final static String TYPE_MARKET = "market";
	public final static String TYPE_LIQUIDATION = "liquidation";
	public final static String TYPE_ADL = "adl";
	public final static String TYPE_PLAN_LIMIT = "planLimit";
	public final static String TYPE_PLAN_MARKET = "planMarket";
	public final static String TYPE_POST_ONLY = "postOnly";
	public final static String TYPE_FOK = "fok";
	public final static String TYPE_IOC = "ioc";


	public static int TRADE_OPEN_LONG = 1;//买入开多
	public static int TRADE_OPEN_SHORT = 2;//卖出开空
	public static int TRADE_CLOSE_SHORT = 3;//买入平空
	public static int TRADE_CLOSE_LONG = 4;//卖出平多
	public static int FIXED_PRICE = 0;//限价
	public static int MARKET_PRICE = 1;//市价
	public static int FIXED_PRICE_HIGH_LEVER = 2;//高级限价委托
	public static int ONLY_MAKER = 3;//只做maker
	public static int ALL_DEAL_OR_ALL_CANCEL = 4;//全部成交或立即取消
	public static int DEAL_CANCEL_SURPLUS = 5;//立即成交并取消剩余

	public static int FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS = 2;//限价止盈止损
	public static int MARKET_PRICE_STOP_LOSS_AND_STOP_LOSS = 3;//市价止盈止损
	public static int FIXED_PRICE_OCO = 4;//限价otc
	public static int MARKET_PRICE_OCO = 5;//市价otc

	/**
	 * 逐仓
	 */
	public static final String MODE_FIXED = "fixed";
	/**
	 * 全仓
	 */
	public static final String MODE_CROSSED = "crossed";

	/**
	 * 方向：多
	 */
	public static final String DIRECTION_LONG = "openLong";

	/**
	 * 方向：空
	 */
	public static final String DIRECTION_SHORT = "openShort";

	public static int INDEX_OPEN = 0;//开仓
	public static int INDEX_CLOSE = 1;//平仓
	public static int DISPLAY_ORDER_LIST = 0;
	public static int DISPLAY_ORDER_DETAIL = 1;


	public static final String WEB_EXTRA_URL = "webview_load_url";
	public static final String WEB_EXTRA_TITLE = "title";       //actionbar 标题
	public static final String WEB_EXTRA_RIGHT_TEXT = "right_text";     //actionbar 右侧文本
	public static final String WEB_EXTRA_RIGHT_IMAGE = "right_image";   //actionbar 右侧图片
	public static final String WEB_EXTRA_RIGHT_URL = "right_url";       //actionbar 右侧跳转链接

	public static final int CoinAddressActivity_FROM_NORMAL = 0;
	public static final int CoinAddressActivity_FROM_WITHDRAW = 1;
	public static final int CoinAddressActivity_CODE_REQUEST = 100;


	public static int RISE_DEFAULT = 0;//默认  不涨不跌
	public static int RISE_UP = 1;//默认  涨
	public static int RISE_DOWN = -1;//默认  跌

	public static int TAB_BUY = 0;
	public static int TAB_SELL = 1;

	public static int UPS_AND_DOWNS_TYPE_ZERO = 0;
	public static int UPS_AND_DOWNS_TYPE_24H = 1;

	public static final String USER_CUSTOMER = "customer";//代理商  key
	public static final String USER_INVATATION = "invitation";//邀请返金key
}
