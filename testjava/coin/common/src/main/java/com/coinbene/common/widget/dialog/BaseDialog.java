package com.coinbene.common.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

public abstract class BaseDialog extends QMUIDialog implements View.OnClickListener {

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        initView();
        listener();
    }

    public abstract int getLayoutRes();

    public abstract void initView();

    public abstract void listener();

    @Override
    public void onClick(View v) {

    }
}
