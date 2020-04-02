package com.coinbene.manbiwang.user.safe;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.user.R;


/**
 * 认证界面选择证件类型dialog
 */

public class IdTypeDialog extends Dialog implements View.OnClickListener {
    public IdTypeDialog(@NonNull Context context) {
        this(context, R.style.CoinBene_Dialog);
    }

    public IdTypeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        // 在构造方法里, 传入主题
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_dialog_id_type);
        initView();
    }

    private TextView tv_cancel, tv_id_card, tv_passport;

    private void initView() {
        tv_cancel = this.findViewById(R.id.tv_cancel);
        tv_id_card = this.findViewById(R.id.tv_id_card);
        tv_passport = this.findViewById(R.id.tv_passport);

        tv_cancel.setOnClickListener(this);
        tv_id_card.setOnClickListener(this);
        tv_passport.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int index = -1;
        int id = v.getId();
        if (id == R.id.tv_id_card) {
            index = Constants.CODE_ID;
        } else if (id == R.id.tv_passport) {
            index = Constants.CODE_PASSPORT;
        } else if (id == R.id.tv_cancel) {
            index = -1;
        }
        if (dialogClickListener != null) {
            dialogClickListener.setOnItemClick(index);
        }
        dismiss();
    }

    private DialogClickListener dialogClickListener;

    public void setOnItemClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }

    private int currentItemIndex;

    public void setCurrentItemIndex(int currentItemIndex) {
        this.currentItemIndex = currentItemIndex;
    }

    public interface DialogClickListener {
        void setOnItemClick(int itemIndex);
    }

}
