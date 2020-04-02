package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.common.balance.AssetManager;
import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.common.utils.NetUtil;
import com.coinbene.common.utils.SecurityUtil;
import com.coinbene.common.utils.SpUtil;

import io.objectbox.Box;
import io.objectbox.exception.DbException;

/**
 * Created by zhangle on 2018/4/6.
 */

public class UserInfoController {

	private static UserInfoController userManager;

	private UserInfoController() {

	}

	public static UserInfoController getInstance() {
		if (userManager == null) {
			synchronized (UserInfoController.class) {
				if (userManager == null) {
					userManager = new UserInfoController();
				}
			}
		}
		return userManager;
	}

	public void regisiterUser(UserInfoResponse userLoginResponse) {
		//登陆成功
		AssetManager.getInstance().setLogin(true);

		//保存userLoginResponse到sp， 提供给jsbridge使用
		userLoginResponse.getData().setClientData(NetUtil.getSystemParam().toString());
		SpUtil.setUserResponse(userLoginResponse.getData());

		Box<UserInfoTable> tableBox = CBRepository.boxFor(UserInfoTable.class);
		tableBox.removeAll();

		UserInfoTable user = new UserInfoTable();
		user.token = userLoginResponse.data.token;
		user.auth = userLoginResponse.data.auth;
		user.bank = userLoginResponse.data.bank;
		user.email = userLoginResponse.data.email;
		user.emailStatus = userLoginResponse.data.emailStatus;

		user.googleBind = userLoginResponse.data.googleBind;
		user.loginId = userLoginResponse.data.loginId;
		user.loginVerify = userLoginResponse.data.loginVerify;
		user.phone = userLoginResponse.data.phone;
		user.pinSetting = userLoginResponse.data.pinSetting;
		user.areaCode = userLoginResponse.data.areaCode;
		user.site = userLoginResponse.data.site;
		user.userId = userLoginResponse.data.userId;
		user.verifyWay = userLoginResponse.data.verifyWay;
		user.kyc = userLoginResponse.data.kyc;
		user.payment = userLoginResponse.data.payment;
		user.supplier = userLoginResponse.data.supplier;
		user.refreshToken = userLoginResponse.data.refreshToken;
		user.coniDiscountSwitch = userLoginResponse.data.coniDiscountSwitch;
		user.discountSwitchDes = userLoginResponse.data.discountSwitchDes;
		user.withdrawBanReason = userLoginResponse.data.withdrawBanReason;
		if (userLoginResponse.data.permissions != null) {
			user.deposit = userLoginResponse.data.permissions.deposit;
			user.withdraw = userLoginResponse.data.permissions.withdraw;
			user.fundTrans = userLoginResponse.data.permissions.fundTrans;
		}
		user.displayUserId = userLoginResponse.data.displayUserId;
		tableBox.put(user);
		userTable = tableBox.query().build().findFirst();//更新user

		//更新是否是白名单用户
		CBRepository.setIsInDebugWhiteList();
	}

	private UserInfoTable userTable;

	public UserInfoTable getUserInfo() {
		if (userTable == null) {
			try {
				Box<UserInfoTable> userBox = CBRepository.boxFor(UserInfoTable.class);
				userTable = userBox.query().build().findFirst();
			} catch (DbException e) {
				e.printStackTrace();
				return null;
			}
		}
		return userTable;
	}

	public void clearUseInfo() {
		//退出登陆
		AssetManager.getInstance().setLogin(false);

		Box<UserInfoTable> userBox = CBRepository.boxFor(UserInfoTable.class);
		userBox.removeAll();
		userTable = null;
		SpUtil.setUserResponse(null);
	}

	public synchronized void updateUserInfo(UserInfoResponse.DataBean user) {//这里返回的数据没有token，so token不更新
		if (user == null) {
			return;
		}

		Box<UserInfoTable> tableBox = CBRepository.boxFor(UserInfoTable.class);
		UserInfoTable table = tableBox.query().equal(UserInfoTable_.userId, user.userId).build().findFirst();

		//保存userLoginResponse到sp， 提供给jsbridge使用
		user.setClientData(NetUtil.getSystemParam().toString());
		if (TextUtils.isEmpty(user.token) && table != null) {
			user.token = table.token;
		}
		if (TextUtils.isEmpty(user.refreshToken) && table != null) {
			user.refreshToken = table.refreshToken;
		}
		SpUtil.setUserResponse(user);

		if (table != null) {
			table.auth = user.auth;
			table.bank = user.bank;
			table.email = user.email;
			table.emailStatus = user.emailStatus;
			table.googleBind = user.googleBind;
			table.loginId = user.loginId;
			table.loginVerify = user.loginVerify;
			table.phone = user.phone;
			table.pinSetting = user.pinSetting;
			table.site = user.site;
			table.areaCode = user.areaCode;
			table.userId = user.userId;
			table.verifyWay = user.verifyWay;
			table.withdrawBanReason = user.withdrawBanReason;
			table.kyc = user.kyc;
			table.payment = user.payment;
			table.supplier = user.supplier;
			table.level = user.level;
			table.transfer = user.transfer;
			table.transferBanReason = user.transferBanReason;
			if (user.permissions != null) {
				table.deposit = user.permissions.deposit;
				table.withdraw = user.permissions.withdraw;
				table.fundTrans = user.permissions.fundTrans;
			}
			if (!TextUtils.isEmpty(user.token)) {
				table.token = user.token;
			}
			if (!TextUtils.isEmpty(user.refreshToken)) {
				table.refreshToken = user.refreshToken;
			}
			table.coniDiscountSwitch = user.coniDiscountSwitch;
			table.discountSwitchDes = user.discountSwitchDes;
			table.displayUserId = user.displayUserId;
			tableBox.put(table);
		}
		userTable = tableBox.query().build().findFirst();//更新user
	}


	public void updateDiscountSwitchStatus(String onOroff) {
		if (userTable == null) {
			return;
		}
		Box<UserInfoTable> tableBox = CBRepository.boxFor(UserInfoTable.class);
		UserInfoTable table = tableBox.query().equal(UserInfoTable_.userId, userTable.userId).build().findFirst();
		if (table != null) {
			table.coniDiscountSwitch = onOroff;
			tableBox.put(table);
		}
		userTable = tableBox.query().build().findFirst();//更新user
	}

	/**
	 * 判断手势密码是否存在
	 *
	 * @return
	 */
	public boolean isGesturePwdSet() {
		String key = SpUtil.getByUser(CBRepository.getContext(), SpUtil.PRE_GESTURE_PWD_KEY, "");
		return !TextUtils.isEmpty(key);
	}

	/**
	 * 保存手势密码
	 *
	 * @param gesturePwd
	 */
	public void saveGesturePwd(String gesturePwd) {
		final String encryptPwd = SecurityUtil.encrypt(gesturePwd);
		SpUtil.putForUser(CBRepository.getContext(), SpUtil.PRE_GESTURE_PWD_KEY, encryptPwd);
	}

	/**
	 * 获取手势密码
	 *
	 * @return
	 */
	public String getGesturePwd() {
		final String result = SpUtil.getByUser(CBRepository.getContext(), SpUtil.PRE_GESTURE_PWD_KEY, "");
		return SecurityUtil.decrypt(result);
	}

	/**
	 * 清空手势密码
	 */
	public void clearGesturePwd() {
		SpUtil.putForUser(CBRepository.getContext(), SpUtil.PRE_GESTURE_PWD_KEY, "");
	}

	/**
	 * 设置手势输入错误的次数，全局范围
	 *
	 * @param count
	 */
	public void setPwdErrorCount(int count) {
		SpUtil.putForUser(CBRepository.getContext(), SpUtil.PRE_PWD_ERROR_COUNT, count);
	}

	/**
	 * 获取手势输入错误的次数,默认为0
	 *
	 * @return
	 */
	public int getPwdErrorCount() {
		return SpUtil.getByUser(CBRepository.getContext(), SpUtil.PRE_PWD_ERROR_COUNT, 0);
	}

	/**
	 * 重置错误的次数
	 */
	public void resetPwdErrorCount() {
		setPwdErrorCount(0);
	}

	/**
	 * 重置指纹信息
	 */
	public void resetFingerPrint() {
		SpUtil.putForUser(CBRepository.getContext(), SpUtil.PRE_FINGER_KEY, false);
	}

	/**
	 * 设置指纹信息
	 */
	public void setFingerPrint(boolean isSetFinger) {
		SpUtil.putForUser(CBRepository.getContext(), SpUtil.PRE_FINGER_KEY, isSetFinger);
	}

	/**
	 * 是否设置指纹信息
	 */
	public boolean isSetFingerPrint() {
		return SpUtil.getByUser(CBRepository.getContext(), SpUtil.PRE_FINGER_KEY, false);
	}

	/**
	 * 是否已经开锁，true:当前是锁的状态
	 * false: 当前是没有锁的状态，即 登录状态
	 *
	 * @return
	 */
	public boolean isLocked() {
		if (getUserInfo() == null) {
			setLock(false);
			return false;
		}
		return SpUtil.getByUser(CBRepository.getContext(), SpUtil.PRE_IS_LOCKED, false);
	}

	/**
	 * true: 锁住
	 * false: 解锁状态
	 *
	 * @param isLocked
	 */
	public void setLock(boolean isLocked) {
		SpUtil.putForUser(CBRepository.getContext(), SpUtil.PRE_IS_LOCKED, isLocked);
	}

	//检查KYC状态
	public boolean checkKycStatus() {
		UserInfoTable userTable = getInstance().getUserInfo();
		if (userTable == null) {
			return false;
		}
		if (!userTable.kyc) {
			return false;
		}
		return true;
	}

	//判断是否是商家的用户
	public boolean checkUserIsSeller() {
		UserInfoTable userTable = getInstance().getUserInfo();
		if (userTable == null) {
			return false;
		}
		if (userTable.supplier) {
			return true;
		}
		return false;
	}
}
