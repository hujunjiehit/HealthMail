package com.coinbene.common.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by zhangle on 2018/4/6.
 */
@Entity
public class UserInfoTable {
	@Id
	public long id;
	public boolean auth;//是否认证
	public String bank;
	public String email;
	public int emailStatus;//邮箱状态1未绑定2未认证3已认证 ,
	public boolean googleBind;
	public String loginId;
	public boolean loginVerify;
	public String phone;
	public boolean pinSetting;
	public String site;
	public int userId;
	public String verifyWay;
	public String token;
	public String refreshToken;

	public boolean kyc;
	public boolean payment;
	public boolean supplier;
	public String coniDiscountSwitch;//ON 打开，OFF 关闭
	public String discountSwitchDes;
	public boolean deposit;//1是true  0 是false
	public boolean withdraw;//1是true  0 是false
	public boolean fundTrans;//true 允许资金划转  false 暂停资金划转
	public String areaCode;//区号
	public String transferBanReason;
	public String withdrawBanReason;
	public String displayUserId;//userid的展示id
	public String level;
	public boolean transfer;//平台内转账(好像无效，利用资产表的transfer)
}
