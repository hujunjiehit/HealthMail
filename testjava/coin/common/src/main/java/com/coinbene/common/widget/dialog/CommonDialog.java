package com.coinbene.common.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;

import com.coinbene.common.R;


public class CommonDialog extends Dialog {

    private DialogOnClickListener clickLisenter;

    public void setClickLisenter(DialogOnClickListener clickLisenter) {
        this.clickLisenter = clickLisenter;
    }

    public CommonDialog(Context context) {
        this(context, R.style.CoinBene_BottomSheet);
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.common_dialog_common);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dismiss();
            if (clickLisenter != null) {
                clickLisenter.onCancel();
            }
        });

        findViewById(R.id.btn_trade).setOnClickListener(v -> {
            dismiss();
            if (clickLisenter != null) {
                clickLisenter.onGoTrade();
            }
        });

        TextView tvNotTip = findViewById(R.id.tv_not_tip);
        tvNotTip.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvNotTip.setOnClickListener(v -> {
            dismiss();
            if (clickLisenter != null) {
                clickLisenter.onNotTip();
            }
        });
    }

    public interface DialogOnClickListener {
        void onCancel();

        void onGoTrade();

        void onNotTip();
    }
}
