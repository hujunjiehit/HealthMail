/*
 *    Copyright (C) 2016 Tamic
 *
 *    link :https://github.com/Tamicer/Novate
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.coinbene.common.network.okgo;

/**
 * Created by Tamic on 2016-11-03.
 */

public class AppThrowable extends Exception {

    private int code;
    private String message;
    private boolean isChinese;
    private String url;
    private int userId;

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isChinese() {
        return isChinese;
    }

    public void setChinese(boolean chinese) {
        isChinese = chinese;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public AppThrowable(java.lang.Throwable throwable, int code, boolean isChinese) {
        super(throwable);
        this.code = code;
        this.isChinese = isChinese;
    }

    public AppThrowable(java.lang.Throwable throwable, int code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String chMsg, String engMsg) {
        if (isChinese)
            this.message = chMsg;
        else{
            this.message = engMsg;
        }
    }
}
