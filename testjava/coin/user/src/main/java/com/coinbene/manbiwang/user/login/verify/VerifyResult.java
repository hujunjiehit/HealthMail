package com.coinbene.manbiwang.user.login.verify;

import java.io.Serializable;

/**
 * Created by june
 * on 2020-01-07
 */
public class VerifyResult implements Serializable {


	/**
	 * nc_token : FFFF0N1N00000000509E:1578386414443:0.7718458005846711
	 * sessionid : 01_yD0t4NqS49Zwnhm-mTBdqkhtIux4ENGTdBMJ0MF20YC2KEzv57RjV2DRdFmV0Z8oyUTNmT4RpXkrDsaGu9aItILksAsi_jOiYiArUQCcwdB868MvOl8tcUwL1pP4CneQ59XIzF9whaBdkMFqhiUIQGxWQHU8Qn6Kqo8LkXOIukDsvO-i8m7hb8eTDsieNGJRlin0SxEZNbPva4gFyeQ9Q
	 * sig : 05URXVU-kjM0vFgTcbsPNsSlklcho1dGCSMYmf9zxhDRm4Zfo-p5N14vD3lqmcuZgZ43jVuV1GRY2DNWrh2U-9igP12Q3GL1VLwDhe-OT4aRQytynIYONT6a-R0mIsjolzqQ7GbCh-9GT_2nVFSmCTLi_SJC5QXw4RV5rVFU-tQuDs8bkDC7THr0v0_-1jRiQeWZNd9HCWoXoW0DMSbIMUAzvbJpusgKvt89P2M62sr1aJ5qhY_MrcYaNOV0HVaB0v01Ar3YahFiLVfGqfBS59zpygaUaZNpuELUyxeHbp1CuRObZtpD7hzGTzaLnC1Ui3GnLeozyOpSQmH2cD19_h-rxKPekNgh9vz5AusYTyZIgSvv4nl_FvOq1uG0pfULkhWGoAyehvG3a7hq2MEflMqz0ZFS1mz4z4HP5D9PkHdQQaTG-7PSzP1Fd5DyLddrC_u70knitkK-G9votrDS_GB-Yfa1rxuQxO9nqDUl7-5Oc
	 */

	private String nc_token;
	private String sessionid;
	private String sig;
	private String scene;
	private String appkey;

	public String getNc_token() {
		return nc_token;
	}

	public void setNc_token(String nc_token) {
		this.nc_token = nc_token;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getSig() {
		return sig;
	}

	public void setSig(String sig) {
		this.sig = sig;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}
}
