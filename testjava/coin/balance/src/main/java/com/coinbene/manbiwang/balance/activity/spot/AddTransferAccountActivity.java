package com.coinbene.manbiwang.balance.activity.spot;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.common.zxing.ScannerActivity;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class AddTransferAccountActivity extends CoinbeneBaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Unbinder mUnbinder;

    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;

    @BindView(R2.id.address_input)
    EditText addressValueEText;
    @BindView(R2.id.submmit_btn)
    TextView submmitBtn;
    @BindView(R2.id.withDraw_tips)
    EditText withDrawName;
    @BindView(R2.id.cap_pwd_value)
    EditTextTwoIcon capPwdInput;
    @BindView(R2.id.close_view)
    ImageView closeView;
    @BindView(R2.id.close_view1)
    ImageView closeView1;
    public static final int CODE_ADD_TRANSFER_REQUEST = 101;

    public static void startMeForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, AddTransferAccountActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int initLayout() {
        return R.layout.add_transfer_account;
    }

    @Override
    public void initView() {
        titleView.setText(R.string.add_account);
        capPwdInput.setSecondRightEye_Num();
        capPwdInput.getInputText().setHint(R.string.cap_pwd_hint);
        init();
    }

    @Override
    public void setListener() {
        backView.setOnClickListener(this);
        submmitBtn.setOnClickListener(this);
        closeView.setOnClickListener(this);
        closeView1.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return true;
    }

    private void init() {
        addressValueEText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    return;
                }
                if (s.length() > 0) {
                    closeView.setVisibility(View.VISIBLE);
                } else {
                    closeView.setVisibility(View.GONE);
                }
            }
        });
        withDrawName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    return;
                }
                if (s.length() > 0) {
                    closeView1.setVisibility(View.VISIBLE);
                } else {
                    closeView1.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_back) {
            KeyboardUtils.hideKeyboard(v);
            finish();
        } else if (v.getId() == R.id.submmit_btn) {
            doSubmmit();
        } else if (v.getId() == R.id.close_view) {
            addressValueEText.setText("");
        }else if (v.getId() == R.id.close_view1) {
            withDrawName.setText("");
        }
    }

    private void doSubmmit() {
        String name = withDrawName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(getString(R.string.please_input_tag));
            return;
        }
        String addressStr = addressValueEText.getText().toString();
        if (TextUtils.isEmpty(addressStr)) {
            ToastUtil.show(R.string.please_input_account);
            return;
        }
        String capPwdStr = capPwdInput.getInputText().getText().toString();
        if (TextUtils.isEmpty(capPwdStr)) {
            ToastUtil.show(R.string.capital_pwd_is_empty);
            return;
        }
        submmit(name, addressStr, capPwdStr);
    }

    private void submmit(String name, String address, String moneyPasswd) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("label", name);
        httpParams.put("targetId", address);
        httpParams.put("pin", MD5Util.MD5(moneyPasswd));
        OkGo.<BaseRes>post(Constants.ACCOUNT_ADD_TRANSFER).params(httpParams).tag(this).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.add_transfor_account);
                finishThis();
            }

            @Override
            public void onE(Response<BaseRes> response) {
            }

            @Override
            public void onFail(String msg) {

            }

        });

    }

    private void finishThis() {
        setResult(Activity.RESULT_OK);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ScannerActivity.startMeForResult(AddTransferAccountActivity.this, ScannerActivity.CODE_RESULT_ADDRESS);
        } else {
            ToastUtil.show(R.string.please_give_permission_grant);
        }
    }

}
