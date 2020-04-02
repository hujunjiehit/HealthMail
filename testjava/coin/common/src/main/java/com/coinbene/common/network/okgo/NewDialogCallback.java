/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coinbene.common.network.okgo;

import android.app.Activity;

import com.coinbene.common.R;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.widget.CustomDialog;
import com.lzy.okgo.request.base.Request;

/**
 * ================================================
 * 新的弹窗对话框
 * ================================================
 */
public abstract class NewDialogCallback<T> extends NewJsonSubCallBack<T> {

    private CustomDialog progress;
    Activity activity;

    private void initDialog(Activity activity) {
        this.activity = activity;
        if (progress == null)
            progress = new CustomDialog(activity, R.style.CustomDialog);
    }

    public NewDialogCallback(Activity activity) {
        super();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        initDialog(activity);
        if (progress != null && !progress.isShowing()) {
            progress.show();
        }
    }


    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);

    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        super.onFinish();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
//            progress = null;
        }
    }
}
