package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.LeverSymbolListModel;
import com.coinbene.manbiwang.model.http.UserInvitationModel;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class UserInvitationController {

	private static UserInvitationController userInvitationController;

	private UserInvitationController() {

	}

	public static UserInvitationController getInstance() {
		if (userInvitationController == null) {
			synchronized (UserInvitationController.class) {
				if (userInvitationController == null) {
					userInvitationController = new UserInvitationController();
				}
			}
		}
		return userInvitationController;
	}


	/**
	 * @param invitationModels
	 * @return
	 */
	public synchronized void addInToDatabase(List<UserInvitationModel.DataBean> invitationModels) {
		if (invitationModels == null || invitationModels.size() == 0) {
			return;
		}
		Box<UserInvitationTable> tableBox = CBRepository.boxFor(UserInvitationTable.class);
		if (tableBox != null && tableBox.count() > 0) {
			tableBox.removeAll();
		}
		List<UserInvitationTable> userInvitationTables = new ArrayList<>();
		for (int i = 0; i < invitationModels.size(); i++) {
			UserInvitationTable userInvitationTable = new UserInvitationTable();
			userInvitationTable.invitationType = invitationModels.get(i).getInvitationType();
			userInvitationTable.invitationCode = invitationModels.get(i).getInvitationCode();
			userInvitationTable.pageCode = invitationModels.get(i).getPageCode();
			userInvitationTables.add(userInvitationTable);
		}
		tableBox.put(userInvitationTables);
	}

	/**
	 * 根据币对查询数据库
	 *
	 * @return
	 */
	public String findCodeByInvitation(String invitationType) {
		if (TextUtils.isEmpty(invitationType)) {
			return "";
		}
		Box<UserInvitationTable> tableBox = CBRepository.boxFor(UserInvitationTable.class);
		UserInvitationTable first = tableBox.query().equal(UserInvitationTable_.invitationType, invitationType).build().findFirst();
		if (first == null || TextUtils.isEmpty(first.pageCode)) {
			return "";
		}
		return first.pageCode;
	}

	public void clearData() {
		Box<UserInvitationTable> userBox = CBRepository.boxFor(UserInvitationTable.class);
		userBox.removeAll();
	}

}
