package com.coinbene.manbiwang.contract.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.contract.R;

public class PositionModelDialog extends Dialog implements View.OnClickListener {


    private TextView tvCrossedPosition, tvGraduallyPosition;
    private ImageView ivCrossedPosition, ivGraduallyPosition;
    private Button btnCancel, btnSure;
    private String marginMode;
    private BailChangeListener listener;


    public PositionModelDialog(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        // 在构造方法里, 传入主题
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new BitmapDrawable());
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.gravity = Gravity.CENTER;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_position_model);
        initView();
        initListener();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initListener() {
        tvCrossedPosition.setOnClickListener(this);
        tvGraduallyPosition.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }

    private void initView() {
        tvCrossedPosition = findViewById(R.id.tv_crossed_position);
        tvGraduallyPosition = findViewById(R.id.tv_gradually_position);
        ivCrossedPosition = findViewById(R.id.iv_crossed_position);
        ivGraduallyPosition = findViewById(R.id.iv_gradually_position);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSure = findViewById(R.id.btn_sure);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_crossed_position) {
            selectTab(Constants.MODE_CROSSED);
        } else if (id == R.id.tv_gradually_position) {
            selectTab(Constants.MODE_FIXED);
        } else if (id == R.id.btn_cancel) {
            dismiss();
        } else if (id == R.id.btn_sure) {
            if (listener != null) {
                listener.bailChange(marginMode);
            }
            dismiss();
        }
    }

    public void setChangeListener(BailChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void show() {
        super.show();
        selectTab(marginMode);
    }

    /**
     * @param mode 设置默认选中模式
     */
    public void setDefaultMode(String mode) {
       marginMode = mode;
    }

    /**
     * @param mode 设置默认选中tab
     */
    private void selectTab(String mode) {

        if (TextUtils.isEmpty(mode)||mode.equals(Constants.MODE_FIXED)) {
            tvCrossedPosition.setBackgroundResource(R.drawable.bg_trade_defoat);
            ivCrossedPosition.setVisibility(View.GONE);
            tvCrossedPosition.setTextColor(getContext().getResources().getColor(R.color.res_textColor_1));
            tvGraduallyPosition.setBackgroundResource(R.drawable.bg_trade_cancel);
            ivGraduallyPosition.setVisibility(View.VISIBLE);
            tvGraduallyPosition.setTextColor(getContext().getResources().getColor(R.color.res_blue));
            marginMode = Constants.MODE_FIXED;
        } else {
            tvCrossedPosition.setBackgroundResource(R.drawable.bg_trade_cancel);
            ivCrossedPosition.setVisibility(View.VISIBLE);
            tvCrossedPosition.setTextColor(getContext().getResources().getColor(R.color.res_blue));
            tvGraduallyPosition.setBackgroundResource(R.drawable.bg_trade_defoat);
            ivGraduallyPosition.setVisibility(View.GONE);
            tvGraduallyPosition.setTextColor(getContext().getResources().getColor(R.color.res_textColor_1));
            marginMode = Constants.MODE_CROSSED;
        }
    }

    public interface BailChangeListener {
        void bailChange(String mode);
    }
}
