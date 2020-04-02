package com.coinbene.common.widget.app;

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
import com.coinbene.common.R;

/**
 * 认证界面选择相机或者拍照dialog
 */

public class TakePhonePicDialog extends Dialog implements View.OnClickListener {
    public TakePhonePicDialog(@NonNull Context context) {
        this(context, R.style.CoinBene_Dialog);
    }

    public TakePhonePicDialog(@NonNull Context context, int themeResId) {
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
        setContentView(R.layout.common_take_photo_pic_dialog_type);
        initView();
    }

    private TextView tv_cancel, tv_select_pic, tv_take_photo;

    private void initView() {
        tv_cancel = this.findViewById(R.id.tv_cancel);
        tv_select_pic = this.findViewById(R.id.tv_select_pic);
        tv_take_photo = this.findViewById(R.id.tv_take_photo);

        tv_cancel.setOnClickListener(this);
        tv_select_pic.setOnClickListener(this);
        tv_take_photo.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int index = -1;
        int id = v.getId();
        if (id == R.id.tv_select_pic) {
            index = Constants.CODE_AUTH_SELECT_PIC;
        } else if (id == R.id.tv_take_photo) {
            index = Constants.CODE_AUTH_TAKE_PHOTO;
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
