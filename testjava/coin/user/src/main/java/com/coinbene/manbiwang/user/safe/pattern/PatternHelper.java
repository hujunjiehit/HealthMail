package com.coinbene.manbiwang.user.safe.pattern;

import android.content.Context;
import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.manbiwang.user.R;

import java.util.List;

/**
 */

public class PatternHelper {
    public static final int MAX_SIZE = 4;
    public static final int MAX_TIMES = 5;

    private String message;
    private String storagePwd;
    private String tmpPwd;
    private int times;
    private boolean isFinish;
    private boolean isOk;

    public PatternHelper() {
        this.times = getPwdErrorCount();
    }

    /**
     * 设置手势图校验
     *
     * @param hitList
     */
    public void validateForSetting(List<Integer> hitList,Context context) {
        this.isFinish = false;
        this.isOk = false;

        if ((hitList == null) || (hitList.size() < MAX_SIZE)) {
            this.message = getSizeErrorMsg(context);
            return;
        }

        //1. draw first time
        if (TextUtils.isEmpty(this.tmpPwd)) {
            this.tmpPwd = convert2String(hitList);
            this.message = getReDrawMsg(context);
            this.isOk = true;
            return;
        }

        //2. draw second times
        if (this.tmpPwd.equals(convert2String(hitList))) {
//            this.message = getSettingSuccessMsg();
            saveToStorage(this.tmpPwd);
            this.isOk = true;
            this.isFinish = true;
            resetCheckPwdErrorCount();
        } else {
            this.message = getDiffPreErrorMsg(context);
        }
    }

    /**
     * 关闭手势图校验
     *
     * @param hitList
     */
    public void validateForClose(List<Integer> hitList,Context context) {
        this.isOk = false;

        if ((hitList == null) || (hitList.size() < MAX_SIZE)) {
            this.times++;
            setCheckPwdErrorCount();
            this.isFinish = this.times >= MAX_TIMES;
            this.message = getPwdErrorMsg(context);
            return;
        }

        this.storagePwd = getFromStorage();
        if (!TextUtils.isEmpty(this.storagePwd) && this.storagePwd.equals(convert2String(hitList))) {
//            this.message = getCheckingSuccessMsg();
            this.isOk = true;
            this.isFinish = true;
            resetCheckPwdErrorCount();
        } else {
            this.times++;
            setCheckPwdErrorCount();
            this.isFinish = this.times >= MAX_TIMES;
            this.message = getPwdErrorMsg(context);
        }
        if (this.isFinish) {
            clearKeyStorage();
        }
    }

    /**
     * 关闭手势图校验
     *
     * @param hitList
     */
    public void validateForCheck(List<Integer> hitList,Context context) {
        this.isOk = false;
        if ((hitList == null) || (hitList.size() < MAX_SIZE)) {
            this.times++;
            setCheckPwdErrorCount();
            this.isFinish = this.times >= MAX_TIMES;
            this.message = getPwdErrorMsg(context);
            return;
        }

        this.storagePwd = getFromStorage();
        if (!TextUtils.isEmpty(this.storagePwd) && this.storagePwd.equals(convert2String(hitList))) {
            this.isOk = true;
            this.isFinish = true;
            resetCheckPwdErrorCount();
        } else {
            this.times++;
            setCheckPwdErrorCount();
            this.isFinish = this.times >= MAX_TIMES;
            this.message = getPwdErrorMsg(context);
        }
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public boolean isOk() {
        return isOk;
    }

    public int getErrorTimes() {
        return times;
    }

    private String getReDrawMsg(Context context) {
        return context.getResources().getString(R.string.gesture_set_again);
    }

    private String getSizeErrorMsg(Context context) {
        return context.getResources().getString(R.string.gesture_set_error_count);
    }

    private String getDiffPreErrorMsg(Context context) {
        return context.getResources().getString(R.string.gesture_set_error_diff);
    }

    public String getPwdErrorMsg(Context context) {
        String strRes = context.getResources().getString(R.string.gesture_check_error_count);
        return String.format(strRes, getRemainTimes());
    }

    private String convert2String(List<Integer> hitList) {
        return hitList.toString();
    }

    private void saveToStorage(String gesturePwd) {
        UserInfoController.getInstance().saveGesturePwd(gesturePwd);
    }

    private String getFromStorage() {
        return UserInfoController.getInstance().getGesturePwd();
    }

    private void clearKeyStorage() {
        UserInfoController.getInstance().clearGesturePwd();
    }

    private int getRemainTimes() {
        return (times < MAX_TIMES) ? (MAX_TIMES - times) : 0;
    }

    private void setCheckPwdErrorCount() {
        int timeCount = times;
        if (times > MAX_TIMES) {
            timeCount = MAX_TIMES;
        }
        UserInfoController.getInstance().setPwdErrorCount(timeCount);
    }

    private void resetCheckPwdErrorCount() {
        UserInfoController.getInstance().resetPwdErrorCount();
    }

    private int getPwdErrorCount() {
        return UserInfoController.getInstance().getPwdErrorCount();
    }

    public void resetPwd(){
        this.tmpPwd = "";
    }
}
