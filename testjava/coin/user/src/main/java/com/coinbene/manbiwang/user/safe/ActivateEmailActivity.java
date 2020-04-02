package com.coinbene.manbiwang.user.safe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.coinbene.common.Constants;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 激活邮箱,邮箱已经存在，去认证
 */

public class ActivateEmailActivity extends CoinbeneBaseActivity {

    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.email_label)
    TextView emailTv;
    @BindView(R2.id.ok_btn)
    TextView okBtn;

    private String email;
    private String userID;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, ActivateEmailActivity.class);
        context.startActivity(intent);
    }


    @Override
    public int initLayout() {
        return R.layout.settings_user_activate_email;
    }

    @Override
    public void initView() {
        okBtn.setOnClickListener(this);
        titleView.setText(getText(R.string.activate_send_mail_title));
        UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
        if (userTable != null) {
            userID = String.valueOf(userTable.userId);
            email = userTable.email;
            emailTv.setText(TextUtils.isEmpty(email) ? "" : email);
        }
        backView.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_back) {
            finish();
        } else if (v.getId() == R.id.ok_btn) {
            sendEmail();
        }
    }

    /*
    * 发送邮箱认证
     */

    private void sendEmail() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("mailType", Constants.MAIL_ONE_VERI);
        OkGo.<BaseRes>post(Constants.USER_SEND_MAIL).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                BaseRes entity = response.body();
                if (entity.isSuccess()) {
                    ToastUtil.show(R.string.send_email_success);
                    SendEmailActivity.startMe(ActivateEmailActivity.this, "", SendEmailActivity.TYPE_MAIL_ACTIVATE);
                    finish();
                }
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }
        });
    }

}
