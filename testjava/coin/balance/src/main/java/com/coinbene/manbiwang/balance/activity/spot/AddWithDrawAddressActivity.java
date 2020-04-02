package com.coinbene.manbiwang.balance.activity.spot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.balance.ChainsAdapter;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.SpaceItemDecoration;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.common.zxing.ScannerActivity;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.mylhyl.zxing.scanner.common.Scanner;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 添加提现地址
 */

public class AddWithDrawAddressActivity extends CoinbeneBaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int CODE_ADD_ADDRESS_REQUEST = 101;

    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.text_tv2)
    TextView coinNameTv;

    @BindView(R2.id.zxing_layout)
    View zxingView;
    @BindView(R2.id.address_input)
    EditText addressValueEText;
    @BindView(R2.id.type_layout)
    View typeLayout;

    @BindView(R2.id.xrp_layout)
    View xrpLayout;
    @BindView(R2.id.xrp_line_bottom)
    View xrpLine_bottom;

    @BindView(R2.id.msg_code_layout)
    RelativeLayout msgCodeLayout;
    @BindView(R2.id.xrp_line)
    View xrp_line;
    @BindView(R2.id.msg_code_line)
    View msgLineView;
    @BindView(R2.id.google_layout)
    RelativeLayout googleCodeLayout;
    @BindView(R2.id.goole_line)
    View googleLineView;
    @BindView(R2.id.submmit_btn)
    TextView submmitBtn;
    @BindView(R2.id.withDraw_tips)
    EditText withDrawName;
    @BindView(R2.id.cap_pwd_value)
    EditTextTwoIcon capPwdInput;
    @BindView(R2.id.google_input_value)
    EditText googleInput;
    @BindView(R2.id.msg_code_input)
    EditText phoneCodeInput;
    @BindView(R2.id.destination_tg_value)
    EditText destiTagInput;
    @BindView(R2.id.send_msgcode_tv)
    TextView sendMsgCode;
    @BindView(R2.id.destination_tg_tv)
    TextView destination_tg_tv;
    private String sendMsgWait, sendMsgAgain;
    @BindView(R2.id.close_view)
    ImageView closeView;
    @BindView(R2.id.arrow_right)
    View arrowRight;
    @BindView(R2.id.rl_chains)
    RecyclerView rlChains;
    @BindView(R2.id.ll_chains)
    LinearLayout llChains;

    private String xrpTag;
    private String coinName;
    private String chain;
    private int selectChainPosition;
    private ChainsAdapter chainsAdapter;

    public static void startMeForResult(Activity activity, int requestCode, String coinName, int selectChainPosition) {
        Intent intent = new Intent(activity, AddWithDrawAddressActivity.class);
        if (!TextUtils.isEmpty(coinName)) {
            intent.putExtra("coin", coinName);
        }
        intent.putExtra("chain", selectChainPosition);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public int initLayout() {
        return R.layout.add_coin_withdraw_address;
    }

    @Override
    public void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        if (intent != null) {
            coinName = intent.getStringExtra("coin");
            selectChainPosition = intent.getIntExtra("chain", 0);
        }

        if (!TextUtils.isEmpty(coinName)) {
            currentModel = BalanceController.getInstance().findByAsset(coinName);

        }

        capPwdInput.setSecondRightEye_Num();
        titleView.setText(getText(R.string.address_coin_add_title));
        capPwdInput.getInputText().setHint(R.string.cap_pwd_hint);
        sendMsgWait = this.getResources().getString(R.string.send_msg_code_wait);
        sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);

        setData(currentModel);
    }


    @Override
    public void setListener() {
        backView.setOnClickListener(this);
        zxingView.setOnClickListener(this);
        typeLayout.setOnClickListener(this);
        submmitBtn.setOnClickListener(this);
        closeView.setOnClickListener(this);
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
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return true;
    }


    private void initChainsData(BalanceInfoTable currentModel) {
        if (currentModel.balanceChains != null && currentModel.balanceChains.size() > 0) {
            llChains.setVisibility(View.VISIBLE);
            GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
            rlChains.addItemDecoration(new SpaceItemDecoration(3, DensityUtil.dip2px(15)));
            rlChains.setLayoutManager(linearLayoutManager);
            chainsAdapter = new ChainsAdapter(ChainsAdapter.WITHDARW_TYPE);
            rlChains.setAdapter(chainsAdapter);
            chainsAdapter.setLists(currentModel.balanceChains, currentModel.balanceChains.get(selectChainPosition).protocolName);
            String pleaceInput = getString(R.string.please_input_chain_name);
            addressValueEText.setHint(String.format(pleaceInput, currentModel.asset, currentModel.balanceChains.get(selectChainPosition).protocolName));
            chain = currentModel.balanceChains.get(selectChainPosition).chain;
            chainsAdapter.setOnItemClickLisenter((selectStr, position) -> {
                addressValueEText.setHint(String.format(pleaceInput, currentModel.asset, currentModel.balanceChains.get(position).protocolName));
                chainsAdapter.setSelect(selectStr);
                chain = currentModel.balanceChains.get(position).chain;
            });
        } else {
            addressValueEText.setHint(R.string.with_draw_address_hint_new);
            llChains.setVisibility(View.GONE);
            chain = currentModel.chain;
        }


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_back) {
            KeyboardUtils.hideKeyboard(v);
            finish();
        } else if (v.getId() == R.id.zxing_layout) {
            AndPermission.with(this).runtime().permission(Permission.Group.CAMERA)
                    .onGranted(data ->
                        new InlineActivityResult(this).startForResult(new Intent(this, ScannerActivity.class))
                                .onSuccess(result -> {
                                    if (result.getResultCode() == RESULT_OK) {
                                        String value = result.getData().getStringExtra(Scanner.Scan.RESULT);
                                        addressValueEText.setText(value);
                                    }
                                })
                    )
                    .onDenied(data -> ToastUtil.show(R.string.please_give_permission_grant))
                    .start();
        } else if (v.getId() == R.id.type_layout) {
            Intent intent = new Intent(this, SelectCoinActivity.class);
            intent.putExtra(SelectCoinActivity.KEY_RETURN, true);
            new InlineActivityResult(this).startForResult(intent)
                    .onSuccess(result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            String asset = result.getData().getStringExtra("item");
                            currentModel = BalanceController.getInstance().findByAsset(asset);
                            setData(currentModel);
                        }
                    });
        } else if (v.getId() == R.id.submmit_btn) {
            doSubmmit();
        } else if (v.getId() == R.id.send_msgcode_tv) {
            sendMsgCode();
        } else if (v.getId() == R.id.close_view) {
            addressValueEText.setText("");
        }
    }

    private void doSubmmit() {
        if (currentModel == null) {
            ToastUtil.show(R.string.address_select_coin);
            return;
        }
        String name = withDrawName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show(R.string.address_other_name_is_empty);
            return;
        }
        String addressStr = addressValueEText.getText().toString();
        if (TextUtils.isEmpty(addressStr)) {
            ToastUtil.show(R.string.with_draw_address_is_empty);
            return;
        }
        String capPwdStr = capPwdInput.getInputText().getText().toString();
        if (TextUtils.isEmpty(capPwdStr)) {
            ToastUtil.show(R.string.capital_pwd_is_empty);
            return;
        }

//        if (googleCodeLayout.getVisibility() == View.VISIBLE) {
//            String googleCode = googleInput.getText().toString();
//            if (TextUtils.isEmpty(googleCode)) {
//                ToastUtil.show(R.string.please_input_google_code);
//                return;
//            }
//        } else if (msgCodeLayout.getVisibility() == View.VISIBLE) {
//            String phoneCode = phoneCodeInput.getText().toString();
//            if (TextUtils.isEmpty(phoneCode)) {
//                ToastUtil.show(R.string.msg_code_is_empty);
//                return;
//            }
//        }
        if (xrpLayout.getVisibility() == View.VISIBLE) {
            xrpTag = destiTagInput.getText().toString();
            if (TextUtils.isEmpty(xrpTag) && currentModel.withdrawTag) {
                ToastUtil.show(String.format(getString(R.string.please_input), currentModel.tagLabel));
                return;
            }
//            addressStr = addressStr + ":" + xrpTag;//xrp币 在地址添加:tag
        }
        submmit(name, addressStr, capPwdStr);
    }

    private void submmit(String name, String address, String moneyPasswd) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("asset", currentModel.asset);
        httpParams.put("label", name);
        httpParams.put("address", address);
        httpParams.put("pin", MD5Util.MD5(moneyPasswd));
//        if (currentModel != null && currentModel.balanceChains != null && currentModel.balanceChains.size() > 0) {
//            httpParams.put("chain", currentModel.balanceChains.get(selectChainPosition).chain);
//        } else {
        httpParams.put("chain", chain);
//        }

        if (!TextUtils.isEmpty(xrpTag))
            httpParams.put("tag", xrpTag);
        if (googleCodeLayout.getVisibility() == View.VISIBLE) {
            String googleCode = googleInput.getText().toString();
            httpParams.put("verifyCode", googleCode);
        } else {
            String msgCode = phoneCodeInput.getText().toString();
            httpParams.put("verifyCode", msgCode);
        }
        OkGo.<BaseRes>post(Constants.ACCOUNT_ADDRESS_ADD_WITHDARW).params(httpParams).tag(this).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.add_withdraw_addr_success);
                finishThis();
            }

            @Override
            public void onE(Response<BaseRes> response) {
            }
        });

    }

    private void finishThis() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private BalanceInfoTable currentModel;

    private void setData(BalanceInfoTable currentModel) {

        if (currentModel == null) {
            llChains.setVisibility(View.GONE);
            return;
        }
        coinNameTv.setText(currentModel.asset);
        int blackColor = getResources().getColor(R.color.res_textColor_1);
        coinNameTv.setTextColor(blackColor);
        if (currentModel.useTag) {
            if (!TextUtils.isEmpty(currentModel.tagLabel)) {
                if (currentModel.withdrawTag)
                    destination_tg_tv.setText(currentModel.tagLabel);
                else {
                    destination_tg_tv.setText(String.format(getString(R.string.optional), currentModel.tagLabel));
                }
            }
            xrp_line.setVisibility(View.VISIBLE);
            xrpLayout.setVisibility(View.VISIBLE);
            xrpLine_bottom.setVisibility(View.VISIBLE);
        } else {
            xrp_line.setVisibility(View.GONE);
            xrpLayout.setVisibility(View.GONE);
            xrpLine_bottom.setVisibility(View.GONE);
        }

        initChainsData(currentModel);
    }

    private void sendMsgCode() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("type", Constants.CODE_EIGHT_ADD_ADDRESS);

        OkGo.<BaseRes>post(Constants.USER_SENDSMS).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                BaseRes t = response.body();
                if (t.isSuccess()) {
                    ToastUtil.show(R.string.send_msg_code_success);
                    count = 59;
                    handler.sendEmptyMessage(send_msg_what);
                }
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }
        });

    }

    private int count = 59;
    private int send_msg_what = 10;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == send_msg_what) {
                sendMsgCode.setText(String.format(sendMsgWait, count));
                sendMsgCode.setTextColor(getResources().getColor(R.color.res_textColor_2));
                sendMsgCode.setOnClickListener(null);
                count--;
                if (count > 0) {
                    handler.sendEmptyMessageDelayed(send_msg_what, 1000);
                } else {
                    sendMsgCode.setText(sendMsgAgain);
                    sendMsgCode.setTextColor(getResources().getColor(R.color.res_blue));
                    sendMsgCode.setOnClickListener(AddWithDrawAddressActivity.this);
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
