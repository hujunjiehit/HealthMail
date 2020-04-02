package com.coinbene.manbiwang.modules.common.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.datacollection.SchemeFrom;
import com.coinbene.common.datacollection.SchemeTo;
import com.coinbene.common.model.http.DataCollectionModel;
import com.coinbene.common.router.Params;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.MainActivity;
import com.coinbene.manbiwang.user.MySelfActivity;

import java.util.HashMap;

//import com.coinbene.manbiwang.modules.common.ReactNative.InvatReactActivity;

/**
 * http://59.110.163.209:8090/pages/viewpage.action?pageId=7342114
 * 解析schema逻辑，控制跳转到原生页面的逻辑
 */

public class SchemaParser {
    public static final String SCHEMA_COINBENE = "coinbene";
    public static final String NO_ACTION = "no_action";
    public static final String WEB_VIEW = "web_view";
    public static final String RN = "rn";
    public static final int LOCAL_TYPE_RN = 1;//跳转到RN页面
    public static final String TYPE_URL = "type_url";
    public static final String HOST_TAB = "tab";
    //现货的K线
    public static final String HOST_SPOTKLINE = "spotkline";
    //合约K线
    public static final String HOST_CONTRACT_KLINE = "contractkline";
    //现货币币交易
    public static final String HOST_TRADING = "trading";
    //杠杆交易
    public static final String HOST_MARGIN = "margin";
    //OTC
    public static final String HOST_OTC = "currency";
    //合约交易
    public static final String HOST_CONTRACT = "contract";
    //资产
    public static final String HOST_ASSET = "asset";
    //个人中心
    public static final String HOST_MINE = "mine";

    //期权
    public static final String HOST_OPTION = "options";

    public static final String HOST_INVITE = "invite";

    public static final String HOST_INVITE_REBATE = "inviterebate";

    /**
     * 登录
     */
    public static final String HOST_LOGIN = "login";

    public static final String HOST_SHARE = "share";

    /**
     * 合约排行赛活动
     */
    public static final String HOST_RANKING = "ranking";

    public static final String HOST_GAME = "playground";//游戏首页
    public static final String HOST_GAME_BATTLE = "playground///battle";//大作战游戏


    public static void handleSchema(Context context, String url, HashMap<String, String> map) {
        handleSchema(context, buildUri(url), map);
    }

    public static void handleSchema(Context context, Uri uri) {
        HashMap<String, String> map = new HashMap<>();
        handleSchema(context, uri, map);
    }

    public static void handleSchema(Context context, Uri uri, HashMap<String, String> map) {
        if (uri == null) {
            return;
        }
        if (uri.toString().contains("?")) {
            //uri 中有参数需要解析
            UrlUtil.parseParams(uri, map);
        }

        //webview,
        String schema = uri.getScheme();
        if (schema.toLowerCase().equals(SCHEMA_COINBENE)) {
            String host = uri.getHost().toLowerCase();
            if (host.equals(HOST_TRADING)) {
                //现货--币币交易

                //coinbene://changetab?tab=spot&subTab=coin&symbol=ETH/USDT&type=2
                String symbol = !TextUtils.isEmpty(map.get(Params.KEY_TRADE_PAIR)) ? map.get(Params.KEY_TRADE_PAIR) : map.get(Params.KEY_PAIR_NAME);
                Bundle bundle = new Bundle();
                bundle.putString("tab","spot");
                bundle.putString("subTab","coin");
                bundle.putString("symbol",symbol);
                bundle.putString("type", map.get(Params.KEY_TYPE));

                if (!TextUtils.isEmpty(map.get("SchemeFrom"))) {
                    DataCollectionHanlder.getInstance().putClientData(
                            DataCollectionHanlder.appScheme,
                            new DataCollectionModel().from(SchemeFrom.valueOf(map.get("SchemeFrom"))).to(SchemeTo.SPOT_COIN)
                    );
                }

                UIBusService.getInstance().openUri(context, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);

            }else if (host.equals(HOST_OTC)) {
                //现货--OTC

                //coinbene://changetab?tab=spot&subTab=otc
                Bundle bundle = new Bundle();
                bundle.putString("tab","spot");
                bundle.putString("subTab","otc");

                if (!TextUtils.isEmpty(map.get("SchemeFrom"))) {
                    DataCollectionHanlder.getInstance().putClientData(
                            DataCollectionHanlder.appScheme,
                            new DataCollectionModel().from(SchemeFrom.valueOf(map.get("SchemeFrom"))).to(SchemeTo.SPOT_OTC)
                    );
                }

                UIBusService.getInstance().openUri(context, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);

            } else if(host.equals(HOST_MARGIN)){
                //现货--杠杆交易

                String symbol = map.get(Params.KEY_SYMBOL);
                Bundle bundle = new Bundle();
                bundle.putString("tab","spot");
                bundle.putString("subTab","margin");
                bundle.putString("symbol",symbol);
                bundle.putString("type", map.get(Params.KEY_TYPE));

                if (!TextUtils.isEmpty(map.get("SchemeFrom"))) {
                    DataCollectionHanlder.getInstance().putClientData(
                            DataCollectionHanlder.appScheme,
                            new DataCollectionModel().from(SchemeFrom.valueOf(map.get("SchemeFrom"))).to(SchemeTo.SPOT_MARGIN)
                    );
                }

                UIBusService.getInstance().openUri(context, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);

            }else if (host.equals(HOST_CONTRACT)) {
                //合约--BTC or USDT合约交易

                //coinbene://Contract?symbol=ETHUSDT&type=1
                String symbol = map.get(Params.KEY_SYMBOL);
                Bundle bundle = new Bundle();
                bundle.putString("tab","contract");
                if (TextUtils.isEmpty(symbol)) {
                    bundle.putString("subTab","btc");
                } else {
                    bundle.putString("subTab", symbol.contains("-") ? "usdt" : "btc");
                    bundle.putString("symbol",symbol);
                }

                if (!TextUtils.isEmpty(map.get("SchemeFrom"))) {
                    DataCollectionHanlder.getInstance().putClientData(
                            DataCollectionHanlder.appScheme,
                            new DataCollectionModel()
                                    .from(SchemeFrom.valueOf(map.get("SchemeFrom")))
                                    .to("btc".equals(bundle.get("subTab")) ? SchemeTo.CONTRACT_BTC : SchemeTo.CONTRACT_USDT)
                    );
                }

                UIBusService.getInstance().openUri(context, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);

            } else if (host.equals(HOST_ASSET)) {
                //跳转到资产页面

                Bundle bundle = new Bundle();
                bundle.putString("tab","balance");
                UIBusService.getInstance().openUri(context, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);

            } else if (host.equals(HOST_MINE)) {
                //跳转（个人中心）原生规则
                gotoMine(context, uri);
            }
            else if (host.equals(HOST_OPTION)) {
//                if (SwitchUtils.isOpenGame() && SwitchUtils.isOpenFortune()) {
//                    gotoOption(context, uri, map);
//                }
            }
            else if (host.equals(HOST_GAME)) {
//                if (SwitchUtils.isOpenGame() && SwitchUtils.isOpenBattle()) {
//                    gotoGame(context, uri, map);
//                }
            }
        } else {
            //webview的scheme全部移植到UIRouter里面
        }
    }

    @NeedLogin(jump = true)
    private static void gotoGame(Context context, Uri uri, HashMap<String, String> map) {
        Bundle bundle = new Bundle();
        bundle.putString("tab","game");
        bundle.putString("subTab","battle");

        if (!TextUtils.isEmpty(map.get("SchemeFrom"))) {
            DataCollectionHanlder.getInstance().putClientData(
                    DataCollectionHanlder.appScheme,
                    new DataCollectionModel().from(SchemeFrom.valueOf(map.get("SchemeFrom"))).to(SchemeTo.GAME_BATTLE)
            );
        }

        UIBusService.getInstance().openUri(context, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
    }

    @NeedLogin(jump = true)
    private static void gotoOption(Context context, Uri uri, HashMap<String, String> map) {
        Bundle bundle = new Bundle();
        bundle.putString("tab","game");
        bundle.putString("subTab","options");

        if (!TextUtils.isEmpty(map.get("SchemeFrom"))) {
            DataCollectionHanlder.getInstance().putClientData(
                    DataCollectionHanlder.appScheme,
                    new DataCollectionModel().from(SchemeFrom.valueOf(map.get("SchemeFrom"))).to(SchemeTo.GAME_OTIONS)
            );
        }

        UIBusService.getInstance().openUri(context, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
    }

    @NeedLogin(jump = true)
    private static void gotoMine(Context context, Uri uri) {
        MySelfActivity.startMe(context, new Bundle());
    }

    public static void needMainActivityForeground(Context context) {
        //判断当前activity，如果不是MainActivity需要start MainActivity
        if ( !(CBRepository.getLifeCallback().getCurrentAcitivty() instanceof MainActivity)) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * 将url字符串，转化成为uri
     *
     * @param url
     * @return
     */
    private static Uri buildUri(String url) {
        String finalTarget = "";
        if (TextUtils.isEmpty(url)) {
            finalTarget = SCHEMA_COINBENE + "://" + NO_ACTION;
        } else if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")) {
            finalTarget = url;
        } else if (url.toLowerCase().startsWith(SCHEMA_COINBENE)) {
            finalTarget = url;
        } else {
            finalTarget = CommonUtil.pathAppendUrl(Constants.BASE_URL_H5, url);
            finalTarget = UrlUtil.replaceH5Url(finalTarget);
        }
        return Uri.parse(finalTarget);
    }
}
