package com.coinbene.common.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class UserInvitationTable {

	@Id
	public long id;
	public String invitationType;
	public String invitationCode;
	public String pageCode;
}
