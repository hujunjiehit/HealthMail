package com.coinbene.common.router;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

public interface UIRouter {

    String COINBENE_SCHEME = "coinbene";

    String HOST_NOTICE = "notice";

    String HOST_HTML5 = "H5";

    //现货币币交易
    String HOST_TRADING = "trading";

    //现货的K线
    String HOST_SPOTKLINE = "spotkline";

    //合约K线
    String HOST_CONTRACT_KLINE = "contractkline";

    //OTC
    String HOST_OTC = "currency";

    //合约交易
    String HOST_CONTRACT = "contract";

    //资产
    String HOST_ASSET = "asset";

    //个人中心
    String HOST_MINE = "mine";

    //期权
    String HOST_OPTION = "options";

    //登录
    String HOST_LOGIN = "login";

    //分享
    String HOST_SHARE = "share";

    String HOST_INVITE_REBATE = "inviterebate";

    String HOST_RANKING = "ranking";

    String HOST_CUSTOMER = "customer"; //客服
    String HOST_NAVIGATOR = "navigator";//导航


    String HOST_RECORDS = "records";  //记录列表页
    String HOST_RECORDS_RECHARGE = "records/recharge";  //充币记录
    String HOST_RECORDS_WITHDRAW = "records/withdraw";  //提币记录
    String HOST_RECORDS_TRANSFER = "records/transfer";  //转账记录
    String HOST_RECORDS_TURN = "records/turn";  //划转记录
    String HOST_RECORDS_DISTRIBUTE = "records/distribute";  //其它记录

    String HOST_RECORDS_SPOT_OPENORDERS = "records/spotopenorders";  //币币当前委托
    String HOST_RECORDS_SPOT_HISTORYORDERS = "records/spothistoryorders";  //币币历史委托

    String HOST_RECORDS_CONTRACT_OPENORDERS = "records/contractopenorders";  //合约当前委托
    String HOST_RECORDS_CONTRACT_HISTORYORDERS = "records/contracthistoryorders";  //合约历史委托
    String HOST_RECORDS_CONTRACT_FEES = "records/contractfees";  //合约资金费用

    String HOST_RECORDS_OPTION_ORDERS = "records/optionorders";  //期权完整交易记录
    String HOST_RECORDS_MINING = "records/mining";  //挖矿明细

    String HOST_RECHARGE = "recharge"; //充币
    String HOST_TRANSFER = "transfer"; //划转
    String HOST_KYC = "kyc"; //实名认证
    String HOST_GAME_SHARE = "playgroundshare";//游戏分享

    String HOST_CHANGE_TAB = "changetab"; //切换Tab

    String HOST_YBBTRANSFER = "ybbtransfer"; //余币宝转入转出

    String HOST_FORTUNE_DETAIL = "fortunedetail"; //余币宝财富账户

    String HOST_HELP_CENTER = "helpcenter"; //帮助中心

    /**
     * 打开一个链接
     *
     * @param url 目标url可以是http 或者 自定义scheme
     * @param bundle 打开目标activity时要传入的参数。
     * @return 是否正常打开
     */
    boolean openUri(Context context, String url, Bundle bundle);
    boolean openUri(Context context, String url, Bundle bundle, int requestCode);
    boolean openUri(Context context, Uri uri, Bundle bundle);
    boolean openUri(Context context, Uri uri, Bundle bundle, int requestCode);
    boolean verifyUri(Uri uri);

}
