package com.coinbene.common.utils;


import com.coinbene.common.context.CBRepository;

/**
 * 开关utils
 */
public class SwitchUtils {


	/**
	 * 是否开启合约
	 *
	 * @return
	 */
	public static boolean isOpenContract() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.PRE_CONTRCT_SWITCH, false);
//        return false;
	}


	/**
	 * 资产，记录是否开启合约
	 *
	 * @return
	 */
	public static boolean isOpenContract_Asset() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.PRE_CONTRCT_SWITCH_ASSET, false);
//        return false;
	}

	/**
	 * 是否开启OTC
	 *
	 * @return
	 */
	public static boolean isOpenOTC() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.PRE_OTC_SWITCH, false);
//        return false;
	}

	/**
	 * 资产，记录是否开启OTC
	 *
	 * @return
	 */
	public static boolean isOpenOTC_Asset() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.PRE_OTC_SWITCH_ASSET, false);
//        return false;
	}


	public static boolean isOpenMargin() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.PRE_MARGIN_SWITCH, false);
	}

	public static boolean isOpenMarginAsset() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.PRE_MARGIN_SWITCH_ASSET, false);
	}

	public static boolean isOpenFortune() {
		return SpUtil.getFortuneSwitch() >= 1;
	}

	public static boolean isOpenFortuneAsset() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.KEY_FORTUNE_SWITCH_ASSET, false);
	}

	/**
	 * 是否是红涨绿跌
	 *
	 * @return
	 */
	public static boolean isRedRise() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.IS_RED_RISE_GREEN_FALL, false);
	}


	/**
	 * 是否是首次安装的用户
	 *
	 * @return
	 */
	public static boolean isFirstInstallApp() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.IS_APP_FIRST_INSTALL, true);
	}


	/**
	 * 是否开启ws压缩
	 *
	 * @return
	 */
	public static boolean getWsCompress() {
		return SpUtil.get(CBRepository.getContext(), SpUtil.KEY_WS_COMPRESS, true);
	}

}
