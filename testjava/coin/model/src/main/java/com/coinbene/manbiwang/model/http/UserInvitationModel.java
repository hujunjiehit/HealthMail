package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class UserInvitationModel extends BaseRes {


	private List<DataBean> data;

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * invitationType : invitation
		 * invitationCode : 65243406
		 * pageCode : 65243406
		 */

		private String invitationType;
		private String invitationCode;
		private String pageCode;

		public String getInvitationType() {
			return invitationType;
		}

		public void setInvitationType(String invitationType) {
			this.invitationType = invitationType;
		}

		public String getInvitationCode() {
			return invitationCode;
		}

		public void setInvitationCode(String invitationCode) {
			this.invitationCode = invitationCode;
		}

		public String getPageCode() {
			return pageCode;
		}

		public void setPageCode(String pageCode) {
			this.pageCode = pageCode;
		}
	}
}
