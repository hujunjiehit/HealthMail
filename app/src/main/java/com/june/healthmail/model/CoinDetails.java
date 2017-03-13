package com.june.healthmail.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/3/13.
 */

public class CoinDetails extends BmobObject {
    private String userNmae;
    private Integer status; //1.表示金币还未到账，需要增加到账户余额   0.表示积分已经到账，不需要增加到账户余额
    private Integer type;   //0: 首次注册赠送  1：邀请人注册赠送  2：邀请人升级月卡赠送  3：邀请人升级永久赠送 4.用户充值
    private Integer score;
    private String reasons;
    private String relatedUserName;

    public String getUserNmae() {
        return userNmae;
    }

    public void setUserNmae(String userNmae) {
        this.userNmae = userNmae;
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
