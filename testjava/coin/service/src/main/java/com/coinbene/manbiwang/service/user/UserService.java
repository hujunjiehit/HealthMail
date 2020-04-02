package com.coinbene.manbiwang.service.user;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by june
 * on 2019-11-14
 */
public interface UserService extends IProvider {

	void logOut();

	void lockUserInfo();

	UserStatus getUserStatus();

	boolean isLogin();
}
