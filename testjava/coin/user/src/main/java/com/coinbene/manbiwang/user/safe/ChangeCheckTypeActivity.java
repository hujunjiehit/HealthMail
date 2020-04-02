package com.coinbene.manbiwang.user.safe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.utils.ToastUtil;

import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 验证码首选项
 */

public class ChangeCheckTypeActivity extends CoinbeneBaseActivity {
    public static final int CODE_RESULT = 10;

    private Unbinder mUnbinder;

    @BindView(R2.id.next_btn)
    TextView nextBtn;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.msg_layout)
    View msgLayout;
    @BindView(R2.id.google_layout)
    View googleLayout;
    @BindView(R2.id.google_check_img)
    ImageView googleCheckImg;
    @BindView(R2.id.msg_check_img)
    ImageView msgCheckImg;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, ChangeCheckTypeActivity.class);
        context.startActivity(intent);
    }

    public static void startMeForResult(Activity activity, int code) {
        Intent intent = new Intent(activity, ChangeCheckTypeActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public int initLayout() {
        return R.layout.code_check_type_layout;
    }

    @Override
    public void initView() {
        nextBtn.setOnClickListener(this);
        titleView.setText(getText(R.string.setting_check_selected));
        backView.setOnClickListener(this);

        msgLayout.setOnClickListener(this);
        googleLayout.setOnClickListener(this);

        UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
        if (userTable != null && !TextUtils.isEmpty(userTable.verifyWay)) {
            if (userTable.verifyWay.equals(Constants.CODE_GOOGLE_CHECK_TYPE)) {
                googleCheckImg.setVisibility(View.VISIBLE);
                msgCheckImg.setVisibility(View.GONE);
            } else {
                googleCheckImg.setVisibility(View.GONE);
                msgCheckImg.setVisibility(View.VISIBLE);
            }
        }
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
        } else if (v.getId() == R.id.msg_layout) {
            googleCheckImg.setVisibility(View.GONE);
            msgCheckImg.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.google_layout) {
            googleCheckImg.setVisibility(View.VISIBLE);
            msgCheckImg.setVisibility(View.GONE);
        } else if (v.getId() == R.id.next_btn) {
            int type = -1;
            if (googleCheckImg.getVisibility() == View.VISIBLE) {
                type = CheckCodeActivity.TYPE_GOOGLE;
            } else if (msgCheckImg.getVisibility() == View.VISIBLE) {
                type = CheckCodeActivity.TYPE_MSG;
            }
            if (type == -1) {
                ToastUtil.show(R.string.please_check_one);
                return;
            }
            CheckCodeActivity.startMe(v.getContext(), type);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
