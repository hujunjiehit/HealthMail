package com.june.healthmail.model;

/**
 * Created by june on 2017/3/4.
 */

public class TokenData {
    private long birthTimeMills;
    private long expireTimeMills;
    private HmMemberUserVo hmMemberUserVo;
    private String token;
    private String accessToken;
    private String aesKey;
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getBirthTimeMills() {
        return birthTimeMills;
    }

    public void setBirthTimeMills(int birthTimeMills) {
        this.birthTimeMills = birthTimeMills;
    }

    public long getExpireTimeMills() {
        return expireTimeMills;
    }

    public void setExpireTimeMills(int expireTimeMills) {
        this.expireTimeMills = expireTimeMills;
    }

    public HmMemberUserVo getHmMemberUserVo() {
        return hmMemberUserVo;
    }

    public void setHmMemberUserVo(HmMemberUserVo hmMemberUserVo) {
        this.hmMemberUserVo = hmMemberUserVo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
}
