package com.june.healthmail.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/3/13.
 */

public class MessageDetails extends BmobObject {
    private String username;
    private Integer status; //1.表示金币还未到账，需要增加到账户余额   0.表示积分已经到账，不需要增加到账户余额
    private Integer type;   //0: 首次注册赠送金币  1：邀请人注册赠送金币  2：邀请人升级月卡赠送金币  3：邀请人升级永久赠送金币 4.用户充值 5.用户月卡授权 6.用户永久授权 7.用户升级高级永久 8.用户开通付款永久  9.用户开通辅助功能授权  99.增加最大约课人数
    private Integer score;  //金币数量 或者 授权天数
    private String reasons;
    private String notice;
    private String relatedUserName;

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userNmae) {
        this.username = userNmae;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public String getRelatedUserName() {
        return relatedUserName;
    }

    public void setRelatedUserName(String relatedUserName) {
        this.relatedUserName = relatedUserName;
    }
}
