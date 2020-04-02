package com.coinbene.manbiwang.user.balance;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.google.android.material.textfield.TextInputLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;


/**
 * @author ding
 *
 * 修改资金密码页面
 */
public class AmendmentFundPasswordActivity extends CoinbeneBaseActivity {

    @BindView(R2.id.menu_back)
    RelativeLayout menuBack;
    @BindView(R2.id.amendment_oldPwd_input)
    TextInputLayout oldInput;
    @BindView(R2.id.amendment_oldPwd_close)
    ImageView oldClose;
    @BindView(R2.id.amendment_newPwd_input)
    TextInputLayout newInput;
    @BindView(R2.id.google_code_input)
    TextInputLayout googleCodeInput;
    @BindView(R2.id.amendment_newPwd_close)
    ImageView newClose;
    @BindView(R2.id.amendment_forget_pwd)
    TextView forgetPwd;
    @BindView(R2.id.amendment_fundPwd_btn)
    Button submit;

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,AmendmentFundPasswordActivity.class));
    }

    @Override
    public int initLayout() {
        return R.layout.activity_amendment_fund_password;
    }

    @Override
    public void initView() {
        init();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return true;
    }

    public void init() {
        oldInput.getEditText().setHint(R.string.please_enter_old_balance_password);
        newInput.getEditText().setHint(R.string.set_cap_num_hint);
        googleCodeInput.getEditText().setHint(R.string.please_input_google_code);
        initListener();
    }


    public void initListener() {
        menuBack.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        submit.setOnClickListener(this);
        oldClose.setOnClickListener(this);
        newClose.setOnClickListener(this);

        oldInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showCloseImg(true, s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showCloseImg(false, s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    /**
     * 清空数据icon展示隐藏
     */
    private void showCloseImg(boolean type, CharSequence s) {

        if (type) {
            //                旧密码清空icon
            if (!TextUtils.isEmpty(s)) {
                oldInput.setPasswordVisibilityToggleEnabled(true);
                oldClose.setVisibility(View.VISIBLE);
            } else {
                oldClose.setVisibility(View.GONE);
                oldInput.setPasswordVisibilityToggleEnabled(false);
            }
        } else {
            //                旧密码清空icon
            if (!TextUtils.isEmpty(s)) {
                newClose.setVisibility(View.VISIBLE);
                newInput.setPasswordVisibilityToggleEnabled(true);
            } else {
                newClose.setVisibility(View.GONE);
                newInput.setPasswordVisibilityToggleEnabled(false);
            }
        }
    }

    /**
     * 校验
     */
    public Boolean verificationPassword(String oldPassword, String newPassword, String googleCode) {

        if (TextUtils.isEmpty(oldPassword)) {
            ToastUtil.show(R.string.please_enter_old_balance_password);
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            ToastUtil.show(R.string.please_enter_new_balance_password);
            return false;
        }

        if (CheckMatcherUtils.checkChiness(newPassword)) {
            ToastUtil.show(R.string.contains_chinese_chat);
            return false;
        }

        if (!CheckMatcherUtils.checkSixDigits(newPassword)) {
            ToastUtil.show(R.string.set_cap_num_hint);
            return false;
        }

        if (TextUtils.isEmpty(googleCode)) {
            ToastUtil.show(R.string.please_input_google_code);
            return false;
        }

        return true;
    }


    /**
     * 更改密码
     */
    private void updatePassword() {
        String oldPwd = oldInput.getEditText().getText().toString();
        String newPwd = newInput.getEditText().getText().toString();
        String googleCode = googleCodeInput.getEditText().getText().toString();

        if (verificationPassword(oldPwd, newPwd, googleCode)) {
            HttpParams httpParams = new HttpParams();
            httpParams.put("orginPasswd", MD5Util.MD5(oldPwd));
            httpParams.put("newPasswd", MD5Util.MD5(newPwd));
            httpParams.put("googleCode", googleCode);
            OkGo.<BaseRes>post(Constants.UPDATE_BALANCE_PASSWORD).params(httpParams).tag(this).execute(new NewDialogJsonCallback<BaseRes>(this) {
                @Override
                public void onSuc(Response<BaseRes> response) {
                    ToastUtil.show(R.string.submit_success);
                    finish();
                }

                @Override
                public void onE(Response<BaseRes> response) {
                    ToastUtil.show(response.body().message);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.menu_back) {
            finish();
        } else if (id == R.id.amendment_fundPwd_btn) {
            updatePassword();
        } else if (id == R.id.amendment_forget_pwd) {
            ForgetFundPasswordActivity.startActivity(this);
            finish();
        } else if (id == R.id.amendment_oldPwd_close) {
            oldInput.getEditText().setText("");
        } else if (id == R.id.amendment_newPwd_close) {
            newInput.getEditText().setText("");
        }
    }
}
