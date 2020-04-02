package com.coinbene.manbiwang.push;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.R;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PushSelectDialog extends QMUIDialog implements View.OnClickListener {


    public static final int TYPE_OPEN_PUSH = 0;
    public static final int TYPE_CLOSE_PUSH = 1;


    private int type;
    private TextView date;
    private TextView tip;
    private Button agree;

    public PushSelectDialog(@NonNull Context context, int type) {
        super(context);
        this.type = type;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_push_select);
        date = findViewById(R.id.push_select_date);
        tip = findViewById(R.id.push_select_tip);
        agree = findViewById(R.id.push_select_agree);
        agree.setOnClickListener(this);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        date.setText(dateFormat.format(new Date()));

        if (type == TYPE_OPEN_PUSH) {
            tip.setText("코인베네 광고 알림 수신에 동의하셨습니다.");
        } else {
            tip.setText("코인베네 광고 알림 수신에 동의하지 않으셨습니다.");
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.push_select_agree) {

            if (type == TYPE_OPEN_PUSH) {

                SpUtil.put(getContext(), "IsPush", true);

                int userId = 0;

                if (UserInfoController.getInstance().getUserInfo() != null) {
                    userId = UserInfoController.getInstance().getUserInfo().userId;
                }

                XGPushManager.registerPush(getContext(), String.valueOf(userId), new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        //token在设备卸载重装的时候有可能会变
                        Log.d("TPush", "注册成功，设备token为：" + data);
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                    }
                });

            } else {
                SpUtil.put(getContext(), "IsPush", false);

                XGPushManager.unregisterPush(getContext(), new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object o, int i) {

                    }

                    @Override
                    public void onFail(Object o, int i, String s) {

                    }
                });
            }

            dismiss();

        }
    }


}
