package com.coinbene.manbiwang.user.serviceimpl;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.user.UserService;
import com.coinbene.manbiwang.service.user.UserStatus;

/**
 * Created by june
 * on 2019-11-14
 */
@Route(path = RouteHub.User.userService)
public class UserServiceImpl implements UserService {

	@Override
	public void logOut() {
//		OptionManager.logOut();

		//清空用户信息
		UserInfoController.getInstance().clearUseInfo();

		BalanceController.getInstance().clearBalanceDataBase();

		SpUtil.setMarginUserConfig(false);
	}

	@Override
	public void lockUserInfo() {
		if (UserInfoController.getInstance().isSetFingerPrint() || UserInfoController.getInstance().isGesturePwdSet()) {
			UserInfoController.getInstance().setLock(true);
		}
	}

	@Override
	public UserStatus getUserStatus() {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null || TextUtils.isEmpty(userTable.token)) {
			return UserStatus.UN_LOGIN;
		}

		if (UserInfoController.getInstance().isLocked()) {
			return UserStatus.LOCKED;
		}

		return UserStatus.LOGIN;
	}

	@Override
	public boolean isLogin() {
		return getUserStatus() == UserStatus.LOGIN;
	}

	@Override
	public void init(Context context) {

	}
}
