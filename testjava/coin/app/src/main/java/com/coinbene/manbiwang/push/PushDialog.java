package com.coinbene.manbiwang.push;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.R;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

public class PushDialog extends QMUIDialog implements View.OnClickListener {

    private Button agree;
    private Button cancel;

    public PushDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_push);
        initView();
    }

    private void initView() {
        agree = findViewById(R.id.dialog_push_ok);
        cancel = findViewById(R.id.dialog_push_no);
        initListener();
    }

    public void initListener() {
        agree.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_push_ok:
                SpUtil.put(getContext(), "IsPush", true);
                PushSelectDialog a = new PushSelectDialog(getContext(), PushSelectDialog.TYPE_OPEN_PUSH);
                a.setCancelable(false);
                a.show();
                dismiss();
                break;
            case R.id.dialog_push_no:
                SpUtil.put(getContext(), "IsPush", false);
                PushSelectDialog b = new PushSelectDialog(getContext(), PushSelectDialog.TYPE_CLOSE_PUSH);
                b.setCancelable(false);
                b.show();
                dismiss();
                break;

            default:
        }

    }
}
