package com.coinbene.common.widget.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.R;


public class QrCodeDialog extends Dialog implements View.OnClickListener {

    private String title;

    private Bitmap bitmap;
    private String url;
    private String account;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        tv_deposit_title.setText(title);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (bitmap != null) {
            iv_qr_code.setImageBitmap(bitmap);
        }
    }

    public void setImageUrl(String url) {
        this.url = url;
    }

    public QrCodeDialog(@NonNull Context context) {
        this(context, R.style.CoinBene_Dialog);
    }

    public QrCodeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
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
        attributes.gravity = Gravity.CENTER;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_deposit_code);
        initView();
    }

    private TextView tv_deposit_title, tv_save, tv_qr_account;
    ImageView iv_qr_code;
    LinearLayout ll_close;

    private void initView() {
        tv_deposit_title = this.findViewById(R.id.tv_deposit_title);
        tv_qr_account = this.findViewById(R.id.tv_qr_account);
        iv_qr_code = this.findViewById(R.id.iv_qr_code);
        tv_save = this.findViewById(R.id.tv_save);
        ll_close = this.findViewById(R.id.ll_close);
        if (!TextUtils.isEmpty(account)) {
            tv_qr_account.setText(account);
        }

        tv_save.setOnClickListener(this);
        ll_close.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_save) {
            if (dialogClickListener != null) {
                dialogClickListener.setOnClick();
            }
        } else if (id == R.id.ll_close) {
            dismiss();
        }

//        dismiss();
    }

    private DialogClickListener dialogClickListener;

    public void setOnClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }

    public void setQrAccount(String payAccount) {
        this.account = payAccount;
    }


    public interface DialogClickListener {
        void setOnClick();
    }

}
