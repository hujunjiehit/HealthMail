package com.june.healthmail.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/3/20.
 */

public class DeviceInfo extends BmobObject{

    private String username;
    private String deviceId;
    private String deviceMac;
    private String deviceDesc;
    private Integer unbindTimes;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public Integer getUnbindTimes() {
        return unbindTimes;
    }

    public void setUnbindTimes(Integer unbindTimes) {
        this.unbindTimes = unbindTimes;
    }

    @Override
    public String toString() {
        return "objectId:" + this.getObjectId() + "  userName:" + this.getUsername() +
                "  deviceId:" + this.getDeviceId() +
                "  deviceDesc:" + this.getDeviceDesc();
    }
}
