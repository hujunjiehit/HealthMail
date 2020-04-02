package com.coinbene.manbiwang.spot.otc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.widget.app.JustifyTextView;
import com.coinbene.common.zxing.QrCodeActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.OrderDetailModel;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.Unbinder;


public class OtcOrderDetailActivity extends CoinbeneBaseActivity {


    @BindView(R2.id.rl_order_info)
    RelativeLayout rlOrderInfo;
    @BindView(R2.id.ll_display_hide)
    LinearLayout llDisplayHide;

    @BindView(R2.id.ll_display)
    LinearLayout llDisplay;

    @BindView(R2.id.ll_bank_have)
    LinearLayout llBankHave;

    @BindView(R2.id.ll_pay_mark)
    LinearLayout llPayMark;


    @BindView(R2.id.iv_display_hide)
    ImageView ivDisplayHide;

    @BindView(R2.id.tv_order_direction)
    TextView tvOrderDirection;
    @BindView(R2.id.tv_order_asset)
    TextView tvOrderAsset;
    @BindView(R2.id.tv_order_status)
    TextView tvOrderStatus;
    @BindView(R2.id.tv_order_unit_price)
    TextView tvOrderUnitPrice;
    @BindView(R2.id.tv_order_unit_price_value)
    TextView tvOrderUnitPriceValue;
    @BindView(R2.id.tv_order_num)
    TextView tvOrderNum;
    @BindView(R2.id.tv_order_num_value)
    TextView tvOrderNumValue;
    @BindView(R2.id.tv_order_all_price)
    TextView tvOrderAllPrice;
    @BindView(R2.id.tv_order_all_price_value)
    TextView tvOrderAllPriceValue;
    @BindView(R2.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R2.id.tv_create_time_value)
    TextView tvCreateTimeValue;
    @BindView(R2.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R2.id.tv_order_id_value)
    TextView tvOrderIdValue;
    @BindView(R2.id.tv_order_status_click)
    TextView tvOrderStatusClick;
    @BindView(R2.id.tv_order_cancel)
    TextView tvOrderCancel;
    @BindView(R2.id.tv_remaining_time)
    TextView tvRemainingTime;
    @BindView(R2.id.tv_bank_address)
    TextView tvBankAddress;
    @BindView(R2.id.tv_sell_bank_name)
    TextView tvSellBankName;
    @BindView(R2.id.iv_order_id_copy)
    ImageView ivOrderOdCopy;
    @BindView(R2.id.iv_name_copy)
    ImageView ivNameCopy;

    @BindView(R2.id.iv_bank_address)
    ImageView ivBankAddress;
    @BindView(R2.id.iv_bank_code)
    ImageView ivBankCode;
    @BindView(R2.id.rl_buy_bank_info)
    LinearLayout rlBankInfo;
    @BindView(R2.id.tv_bank_address_value)
    TextView tvBankAddressValue;


    @BindView(R2.id.ll_bank_address)
    LinearLayout llBankAddress;
    @BindView(R2.id.tv_bank_code)
    TextView tvBankCode;
    @BindView(R2.id.tv_bank_code_value)
    TextView tvBankCodevValue;
    @BindView(R2.id.tv_order_status_bottom_dis)
    JustifyTextView tvOrderStatusBottomDis;
    @BindView(R2.id.rl_hander_click)
    LinearLayout rlHanderClick;
    @BindView(R2.id.tv_order_cancel_back)
    TextView tvOrderCancelBack;
    @BindView(R2.id.tv_bank_name_value)
    TextView tvBankNameValue;
    @BindView(R2.id.tv_fee_value)
    TextView tvFeeValue;

    @BindView(R2.id.tv_real_account)
    TextView tvRealAccount;
    @BindView(R2.id.tv_real_account_value)
    TextView tvRealAccountValue;
    @BindView(R2.id.tv_buy_pay_title)
    TextView tvBuyPayTitle;
    @BindView(R2.id.tv_bank_user_name_value)
    TextView tvBankUserNameValue;
    @BindView(R2.id.iv_bank_name)
    ImageView ivBankName;
    @BindView(R2.id.tv_pay_mark_value)
    TextView tvPayMarkValue;
    @BindView(R2.id.iv_bank_mark)
    ImageView ivBankMark;


    @BindView(R2.id.iv_all_price_copy)
    ImageView ivPriceCopy;
    @BindView(R2.id.tv_shops_remarks_value)
    TextView tvShopsRemarksValue;
    @BindView(R2.id.tv_shops_remarks)
    TextView tvShopsRemarks;

    @BindView(R2.id.rl_shops_remarks)
    RelativeLayout rlShopsRemarks;
    @BindView(R2.id.tv_pay_info_type)
    TextView tvPayInfoType;
    @BindView(R2.id.tv_pay_info_account)
    TextView tvPayInfoAccount;
    @BindView(R2.id.rl_sell_pay_info)
    RelativeLayout rlSellPayInfo;
    @BindView(R2.id.tv_others_name_value)
    TextView tvOthersNameValue;
    @BindView(R2.id.tv_others_register_time_value)
    TextView tvOthersRegisterTimeValue;
    @BindView(R2.id.tv_others_complete_orders_value)
    TextView tvOthersCompleteOrdersValue;
    @BindView(R2.id.rl_others_info)
    RelativeLayout rlOthersInfo;
    private Unbinder mUnbinder;

    @BindView(R2.id.menu_title_tv)
    TextView titleView;

    @BindView(R2.id.tv_currency)
    TextView tvCurrency;
    @BindView(R2.id.tv_currency1)
    TextView tvCurrency1;

    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    private String orderId;
    private CountDownTimer timer;
    private String url;
    //    private ChangePayTypeDialog dialog;
    private OrderDetailModel.DataBean data;
    private int payIndex = Constants.CODE_BANK;
    private boolean hasWecaht = false;
    private boolean hasAlipay = false;
    private boolean hasBank = false;
    private int orderType = 1;
    private AlertDialog.Builder orderHanderDialog = null;


    public static void startMe(Context context, String orderId, int orderType) {
        Intent intent = new Intent(context, OtcOrderDetailActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("orderType", orderType);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.spot_activity_order_detail;
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
        getOrderInfo();
    }

    @Override
    public boolean needLock() {
        return true;
    }

    private void init() {
        titleView.setText(R.string.order_detail_title);
        ivDisplayHide.setOnClickListener(this);
        llDisplay.setOnClickListener(this);
        backView.setOnClickListener(this);
        tvOrderStatusClick.setOnClickListener(this);
        tvOrderCancel.setOnClickListener(this);
//        ivOrderIdCopy.setOnClickListener(this);
        ivBankName.setOnClickListener(this);
        ivBankAddress.setOnClickListener(this);
        ivBankCode.setOnClickListener(this);
        ivBankMark.setOnClickListener(this);
        tvOrderCancelBack.setOnClickListener(this);
        ivOrderOdCopy.setOnClickListener(this);
        ivPriceCopy.setOnClickListener(this);
        ivNameCopy.setOnClickListener(this);
        orderId = getIntent().getStringExtra("orderId");
        orderType = getIntent().getIntExtra("orderType", 1);
        swipe_refresh_layout.setColorSchemeColors(getResources().getColor(R.color.res_blue));
        swipe_refresh_layout.setOnRefreshListener(this::getOrderInfo);
//        if (dialog == null)
//            dialog = new ChangePayTypeDialog(this);
//        dialog.setOnItemClickListener(dialogClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.menu_back) {
            finish();
        } else if (id == R.id.iv_all_price_copy) {
            if (!TextUtils.isEmpty(tvOrderAllPriceValue.getText().toString()))
                StringUtils.copyStrToClip(tvOrderAllPriceValue.getText().toString());
        } else if (id == R.id.iv_name_copy) {
            if (!TextUtils.isEmpty(tvBankUserNameValue.getText().toString()))
                StringUtils.copyStrToClip(tvBankUserNameValue.getText().toString());
        } else if (id == R.id.ll_display) {
            if (rlOrderInfo.getVisibility() == View.VISIBLE) {
                rlOrderInfo.setVisibility(View.GONE);
                ivDisplayHide.setImageResource(R.drawable.icon_bottom_arrow);
            } else {
                rlOrderInfo.setVisibility(View.VISIBLE);
                ivDisplayHide.setImageResource(R.drawable.icon_top_arrow);
            }
        } else if (id == R.id.iv_order_id_copy) {
            if (!TextUtils.isEmpty(tvOrderIdValue.getText().toString()))
                StringUtils.copyStrToClip(tvOrderIdValue.getText().toString());
        } else if (id == R.id.iv_bank_name) {
            if (!TextUtils.isEmpty(tvBankNameValue.getText().toString()))
                StringUtils.copyStrToClip(tvBankNameValue.getText().toString());
        } else if (id == R.id.iv_bank_mark) {
            if (!TextUtils.isEmpty(tvPayMarkValue.getText().toString()))
                StringUtils.copyStrToClip(tvPayMarkValue.getText().toString());
        } else if (id == R.id.iv_bank_address) {
            if (data.getPayment().getType() == Constants.CODE_BANK) {
                if (!TextUtils.isEmpty(tvBankAddressValue.getText().toString()))
                    StringUtils.copyStrToClip(tvBankAddressValue.getText().toString());
            } else {
                QrCodeActivity.startMe(this, data.getPayment().getType(), data.getPayment().getPayQrCode());
            }
        } else if (id == R.id.iv_bank_code) {
            if (!TextUtils.isEmpty(tvBankCodevValue.getText().toString()))
                StringUtils.copyStrToClip(tvBankCodevValue.getText().toString());
        } else if (id == R.id.iv_qr_code) {
        } else if (id == R.id.tv_order_status_click) {
            if (data != null && data.getOrder() != null && data.getOrder().getStatus() == 1 && data.getOrder().getType() == 1) {//买入待付款 点击已完成付款
                initTipDialog(getString(R.string.pay_confirm), getString(R.string.pay_comfirm_tip), getString(R.string.completed_transfer), 2);
            } else if (data != null && data.getOrder() != null && data.getOrder().getStatus() == 2 && data.getOrder().getType() == 2) {//卖出待确认 点击已收到付款
                initTipDialog(null, getString(R.string.pay_collection_tip), getString(R.string.get_pay_money), 3);
            }
        } else if (id == R.id.tv_order_cancel) {
            if (data != null && data.getOrder() != null && data.getOrder().getStatus() == 1 && data.getOrder().getType() == 1) {//买入待付款 点击取消订单
                initTipDialog(getString(R.string.comfirm_cancel_order), getString(R.string.comfirm_hander_order_tip), null, 4);
            } else if (data != null && data.getOrder() != null && data.getOrder().getStatus() == 2 && data.getOrder().getType() == 2) {//卖出待确认 点击未收到付款
                initTipDialog(null, getString(R.string.not_pay_collection_tip), getString(R.string.not_pay_money), 6);
            }
        } else if (id == R.id.tv_order_cancel_back) {
            if (data != null && data.getOrder() != null && data.getOrder().getStatus() == 2 && data.getOrder().getType() == 1) {//买入人工取消
                initTipDialog(getString(R.string.comfirm_cancel_order), getString(R.string.comfirm_hander_order_tip), null, 4);
            } else if (data != null && data.getOrder() != null && data.getOrder().getStatus() == 6 && data.getOrder().getType() == 1) {//买入被冻结人工取消
                initTipDialog(getString(R.string.comfirm_cancel_order), getString(R.string.comfirm_hander_order_tip), null, 4);
            } else if (data != null && data.getOrder() != null && data.getOrder().getStatus() == 6 && data.getOrder().getType() == 2) {//卖出被冻结 点击已收到付款
                initTipDialog(null, getString(R.string.pay_collection_tip), getString(R.string.get_pay_money), 3);
            }
        }
    }



    private void initTipDialog(String title, String content, String sureDis, int handerType) {

        if (orderHanderDialog == null) {
            orderHanderDialog = new AlertDialog.Builder(this);
        }
        if (!TextUtils.isEmpty(title))
            orderHanderDialog.setTitle(title);
        orderHanderDialog.setMessage(content);
        orderHanderDialog.setCancelable(true);
        if (!TextUtils.isEmpty(sureDis)) {
            orderHanderDialog.setPositiveButton(sureDis, (orderHanderDialog, which) -> {
                handleOrder(handerType);
                orderHanderDialog.dismiss();
            });
        } else {
            orderHanderDialog.setPositiveButton(getString(R.string.btn_ok), (orderHanderDialog, which) -> {
                handleOrder(handerType);
                orderHanderDialog.dismiss();
            });
        }
        orderHanderDialog.setNegativeButton(getString(R.string.btn_cancel), (orderHanderDialog, which) -> orderHanderDialog.dismiss());
        orderHanderDialog.show();
    }


    private void getOrderInfo() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("orderId", orderId);
        OkGo.<OrderDetailModel>post(Constants.OTC_GET_ORDER_INFO).tag(this).params(httpParams).execute(new NewJsonSubCallBack<OrderDetailModel>() {
            @Override
            public void onSuc(Response<OrderDetailModel> response) {
                if (OtcOrderDetailActivity.this.isFinishing()) {
                    return;
                }
                if (response.body() != null && response.body().getData() != null) {
                    data = response.body().getData();
                    setDataToView();
                }
            }

            @Override
            public void onE(Response<OrderDetailModel> response) {
            }

            @Override
            public void onFinish() {
                super.onFinish();
                setRefreshFalse();
            }
        });
    }

    private void handleOrder(int handleType) {//handleType: 2是确认付款 3是确认收款 4 是人工取消 6 是冻结订单
        HttpParams httpParams = new HttpParams();
        httpParams.put("orderId", orderId);
        httpParams.put("handleType", handleType);
        httpParams.put("accountType", 1);

        OkGo.<BaseRes>post(Constants.OTC_HANDLE_ORDER).tag(this).params(httpParams).execute(new NewJsonSubCallBack<BaseRes>() {
            @Override
            public void onSuc(Response<BaseRes> response) {
                getOrderInfo();
            }

            @Override
            public void onE(Response<BaseRes> response) {
            }

        });
    }


    private void initTimer(long time) {
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int second = (int) (millisUntilFinished / 1000);
                tvRemainingTime.setText(getString(R.string.time_remaining) + TimeUtils.secondToMinSecond(OtcOrderDetailActivity.this, second));
            }

            @Override
            public void onFinish() {
                tvRemainingTime.setText(String.format("%s0%s", getString(R.string.time_remaining), getString(R.string.seconds)));
            }
        };
        timer.start();
    }

    private void setDataToView() {
        boolean isRedRise = SwitchUtils.isRedRise();

        if (data != null && data.getOrder() != null) {
            tvFeeValue.setText(isUser() ? TextUtils.isEmpty(data.getOrder().getUserFee()) ? "" : data.getOrder().getUserFee() : TextUtils.isEmpty(data.getOrder().getMerchantFee()) ? "" : data.getOrder().getMerchantFee());

            tvOrderAsset.setText(TextUtils.isEmpty(data.getOrder().getPairName()) ? "" : data.getOrder().getPairName());
            tvCurrency.setText(TextUtils.isEmpty(data.getOrder().getMoneyTrade()) ? "" : data.getOrder().getMoneyTrade());
            tvCurrency1.setText(TextUtils.isEmpty(data.getOrder().getMoneyTrade()) ? "" : data.getOrder().getMoneyTrade());
            tvOrderAllPriceValue.setText(TextUtils.isEmpty(data.getOrder().getTotalMoney()) ? "" : data.getOrder().getTotalMoney());
            tvOrderUnitPriceValue.setText(TextUtils.isEmpty(data.getOrder().getUnitPrice()) ? "" : data.getOrder().getUnitPrice());
            tvOrderNumValue.setText(TextUtils.isEmpty(data.getOrder().getQuantity()) ? "" : data.getOrder().getQuantity());
            tvCreateTimeValue.setText(TextUtils.isEmpty(data.getOrder().getCreateTime()) ? "" : data.getOrder().getCreateTime());
            tvOrderIdValue.setText(data.getOrder().getOrderId());
            //买入
            if (data.getOrder().getType() == 1) {
                tvOrderDirection.setText(R.string.trade_buy);
                if (isRedRise)
                    tvOrderDirection.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_red));
                else
                    tvOrderDirection.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_green));
                tvRealAccount.setText(R.string.real_account);
                tvRealAccountValue.setText(BigDecimalUtils.subtract(data.getOrder().getQuantity(), isUser() ? data.getOrder().getUserFee() : data.getOrder().getMerchantFee()));
                setBuyData();

            } else if (data.getOrder().getType() == 2) {
                tvOrderDirection.setText(R.string.trade_sell);
                if (isRedRise)
                    tvOrderDirection.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_green));
                else
                    tvOrderDirection.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_red));

                tvRealAccount.setText(R.string.real_pay);
                tvRealAccountValue.setText(BigDecimalUtils.add(data.getOrder().getQuantity(), isUser() ? data.getOrder().getUserFee() : data.getOrder().getMerchantFee()));
                setSellData();


            }

//            initDialogVisible();
            setOrderStatus();
        }
    }

    private void setSellData() {
        rlShopsRemarks.setVisibility(View.GONE);
        rlBankInfo.setVisibility(View.GONE);
        llDisplay.setVisibility(View.GONE);
        rlOrderInfo.setVisibility(View.VISIBLE);
        if (data.getPayment() != null)
            setPaySellView(data.getPayment().getType());

    }


    private void setBuyData() {
        rlOthersInfo.setVisibility(View.GONE);
        rlSellPayInfo.setVisibility(View.GONE);
        if (data.getPayment() != null)
            setPayBuyView(data.getPayment().getType());
    }

    private void setOrderStatus() {
        if (data.getOrder().getType() == 1) {//买入
            if (timer != null) {
                timer.cancel();
            }
            if (data.getOrder().getStatus() == 1) {//待付款
                tvOrderStatus.setText(R.string.wait_payment);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_assistColor_21));
                tvOrderCancelBack.setVisibility(View.GONE);
                rlHanderClick.setVisibility(View.VISIBLE);
                tvOrderStatusClick.setVisibility(View.VISIBLE);
                tvOrderCancel.setVisibility(View.VISIBLE);
                tvOrderStatusBottomDis.setText(R.string.otc_order_status_tip);
                tvOrderStatusBottomDis.setVisibility(View.VISIBLE);
                tvRemainingTime.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(data.getTimeLeft())) {
                    long time = Long.valueOf(data.getTimeLeft());
                    initTimer(time);
                }
            } else if (data.getOrder().getStatus() == 2) {//已付款
                tvOrderStatus.setText(R.string.paid);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_assistColor_21));
                rlHanderClick.setVisibility(View.VISIBLE);
                tvOrderCancelBack.setVisibility(View.VISIBLE);
                tvOrderCancel.setVisibility(View.GONE);
                tvOrderStatusClick.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.VISIBLE);
                tvOrderStatusBottomDis.setVisibility(View.GONE);
                tvRemainingTime.setText(R.string.wait_release_coin);
            } else if (data.getOrder().getStatus() == 3) {//已完成
                tvOrderStatus.setText(R.string.order_completed);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_textColor_1));
                rlHanderClick.setVisibility(View.GONE);
                tvOrderStatusBottomDis.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.GONE);
                rlOrderInfo.setVisibility(View.VISIBLE);
                llDisplay.setVisibility(View.GONE);
                rlBankInfo.setVisibility(View.VISIBLE);
                rlShopsRemarks.setVisibility(View.GONE);
            } else if (data.getOrder().getStatus() == 4) {//已取消
                tvOrderStatus.setText(R.string.order_cancelled);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_textColor_3));
                rlHanderClick.setVisibility(View.GONE);
                tvOrderStatusBottomDis.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.GONE);
                rlOrderInfo.setVisibility(View.VISIBLE);
                llDisplay.setVisibility(View.GONE);
                rlBankInfo.setVisibility(View.GONE);
                rlShopsRemarks.setVisibility(View.GONE);
                tvFeeValue.setText("-");
                tvRealAccountValue.setText("-");
            } else if (data.getOrder().getStatus() == 5) {//超时取消
                tvOrderStatus.setText(R.string.time_out_pay);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_textColor_3));
                rlHanderClick.setVisibility(View.GONE);
                tvOrderStatusBottomDis.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.GONE);
                rlOrderInfo.setVisibility(View.VISIBLE);
                llDisplay.setVisibility(View.GONE);
                rlBankInfo.setVisibility(View.GONE);
                rlShopsRemarks.setVisibility(View.GONE);
                tvFeeValue.setText("-");
                tvRealAccountValue.setText("-");

            } else if (data.getOrder().getStatus() == 6) {//冻结
                tvOrderStatus.setText(R.string.order_freezing);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_red));
                rlHanderClick.setVisibility(View.VISIBLE);
                tvOrderCancelBack.setVisibility(View.VISIBLE);
                tvOrderCancel.setVisibility(View.GONE);
                tvOrderStatusClick.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.GONE);
                tvOrderStatusBottomDis.setVisibility(View.VISIBLE);
                tvOrderStatusBottomDis.setText(R.string.buy_order_freeze);
            }
        } else if (data.getOrder().getType() == 2) {//卖单


            //按钮状态
            if (data.getOrder().getStatus() == 1) {//代收款
                tvOrderStatus.setText(R.string.wait_receivables);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_assistColor_21));
                rlHanderClick.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.VISIBLE);
                tvRemainingTime.setText(R.string.wait_release_pay);
            } else if (data.getOrder().getStatus() == 2) {//代确认
                tvOrderStatus.setText(R.string.wait_sure);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_assistColor_21));
                tvRemainingTime.setText(R.string.comfirm_release_pay);
                tvRemainingTime.setVisibility(View.VISIBLE);
                rlHanderClick.setVisibility(View.VISIBLE);
                tvOrderStatusBottomDis.setVisibility(View.GONE);
                tvOrderStatusClick.setText(R.string.get_pay_money);
                tvOrderCancel.setText(R.string.not_pay_money);
            } else if (data.getOrder().getStatus() == 3) {//已完成
                tvOrderStatus.setText(R.string.order_completed);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_textColor_1));
                rlHanderClick.setVisibility(View.GONE);
                tvOrderStatusBottomDis.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.GONE);
            } else if (data.getOrder().getStatus() == 4) {//已取消
                tvOrderStatus.setText(R.string.order_cancelled);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_textColor_3));
                rlHanderClick.setVisibility(View.GONE);
                tvOrderStatusBottomDis.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.GONE);
                tvFeeValue.setText("-");
                tvRealAccountValue.setText("-");
            } else if (data.getOrder().getStatus() == 5) {//超时取消
                tvOrderStatus.setText(R.string.time_out_pay);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_textColor_3));
                rlHanderClick.setVisibility(View.GONE);
                tvOrderStatusBottomDis.setVisibility(View.GONE);
                tvRemainingTime.setVisibility(View.GONE);
                tvFeeValue.setText("-");
                tvRealAccountValue.setText("-");
            } else if (data.getOrder().getStatus() == 6) {//冻结中
                tvOrderStatus.setText(R.string.order_freezing);
                tvOrderStatus.setTextColor(getResources().getColor(R.color.res_red));
                tvOrderStatusBottomDis.setVisibility(View.VISIBLE);
                tvRemainingTime.setVisibility(View.GONE);
                tvOrderStatusBottomDis.setText(R.string.sell_order_freeze);
                rlHanderClick.setVisibility(View.VISIBLE);
                tvOrderCancelBack.setVisibility(View.VISIBLE);
                tvOrderCancel.setVisibility(View.GONE);
                tvOrderStatusClick.setVisibility(View.GONE);
                tvOrderCancelBack.setText(R.string.get_pay_money);

            }


        }
    }


    private void setPaySellView(int typeCode) {

        if (data != null && data.getOrder() != null && UserInfoController.getInstance().getUserInfo() != null) {
            if (!isUser()) {//如果我是商家  展示用户信息  否则展示商家信息
                tvOthersNameValue.setText(TextUtils.isEmpty(data.getOrder().getPlaceName()) ? "" : data.getOrder().getPlaceName());
                tvOthersRegisterTimeValue.setText(TextUtils.isEmpty(data.getOrder().getUserRegisterTime()) ? "" : data.getOrder().getUserRegisterTime());
                tvOthersCompleteOrdersValue.setText(TextUtils.isEmpty(data.getOrder().getUserFinishOrderNum()) ? "" : data.getOrder().getUserFinishOrderNum());
            } else {
                tvOthersNameValue.setText(TextUtils.isEmpty(data.getOrder().getSupplierName()) ? "" : data.getOrder().getSupplierName());
                tvOthersRegisterTimeValue.setText(TextUtils.isEmpty(data.getOrder().getMerchantRegisterTime()) ? "" : data.getOrder().getMerchantRegisterTime());
                tvOthersCompleteOrdersValue.setText(TextUtils.isEmpty(data.getOrder().getMerchantFinishOrderNum()) ? "" : data.getOrder().getMerchantFinishOrderNum());
            }
        }


        if (data != null && data.getPayment() != null) {
            if (typeCode == Constants.CODE_BANK) {
                tvPayInfoType.setText(TextUtils.isEmpty(data.getPayment().getBankName()) ? "" : data.getPayment().getBankName());
                tvPayInfoAccount.setText(TextUtils.isEmpty(data.getPayment().getBankAccount()) ? "" : data.getPayment().getBankAccount());

            } else if (typeCode == Constants.CODE_ALIPAY) {
                tvPayInfoType.setText(R.string.alipay);
                tvPayInfoAccount.setText(TextUtils.isEmpty(data.getPayment().getPayAccount()) ? "" : data.getPayment().getPayAccount());
            } else if (typeCode == Constants.CODE_WECHAT) {
                tvPayInfoType.setText(R.string.wechat);
                tvPayInfoAccount.setText(TextUtils.isEmpty(data.getPayment().getPayAccount()) ? "" : data.getPayment().getPayAccount());
            }

        }
    }

    private void setPayBuyView(int typeCode) {
        if (data != null && data.getPayment() != null) {
            rlBankInfo.setVisibility(View.VISIBLE);
            rlShopsRemarks.setVisibility(View.VISIBLE);
            tvBankUserNameValue.setText(TextUtils.isEmpty(data.getPayment().getUserName()) ? "" : data.getPayment().getUserName());
            if (data.getOrder() != null && data.getOrder().getShowReferenceNo() == 1 && data.getOrder().getOrderId().length() > 5) {
                tvPayMarkValue.setText(data.getOrder().getOrderId().substring(data.getOrder().getOrderId().length() - 6));
                llPayMark.setVisibility(View.VISIBLE);
            } else {
                llPayMark.setVisibility(View.GONE);
//                tvPayMarkValue.setText("-");
            }
            if (TextUtils.isEmpty(data.getOrder().getMerchantRemarks())) {
                tvShopsRemarksValue.setVisibility(View.GONE);
                tvShopsRemarks.setVisibility(View.GONE);
            } else {
                tvShopsRemarksValue.setVisibility(View.VISIBLE);
                tvShopsRemarks.setVisibility(View.VISIBLE);
                tvShopsRemarksValue.setText(data.getOrder().getMerchantRemarks());
            }
            if (typeCode == Constants.CODE_BANK) {
                tvSellBankName.setText(R.string.bank_name);
                tvBankAddress.setText(R.string.bank_address);
                tvBuyPayTitle.setText(R.string.bank_pay);
                llBankHave.setVisibility(View.VISIBLE);
                ivBankAddress.setImageResource(R.drawable.icon_copy);
                tvBankNameValue.setText(TextUtils.isEmpty(data.getPayment().getBankName()) ? "" : data.getPayment().getBankName());
//                llBankAddress.setVisibility(View.GONE);
                tvBankAddressValue.setText(TextUtils.isEmpty(data.getPayment().getBankAddress()) ? "" : data.getPayment().getBankAddress());
                tvBankCodevValue.setText(TextUtils.isEmpty(data.getPayment().getBankAccount()) ? "" : data.getPayment().getBankAccount());
            } else if (typeCode == Constants.CODE_ALIPAY) {
                tvSellBankName.setText(R.string.alipay_acount);
                tvBuyPayTitle.setText(R.string.alipay_pay);
//                llBankAddress.setVisibility(View.VISIBLE);
                llBankHave.setVisibility(View.GONE);
                tvBankAddress.setText(R.string.pay_qrcode);
                ivBankAddress.setImageResource(R.drawable.icon_qr_code);
                tvBankNameValue.setText(TextUtils.isEmpty(data.getPayment().getPayAccount()) ? "" : data.getPayment().getPayAccount());
                tvBankAddressValue.setText("");
            } else if (typeCode == Constants.CODE_WECHAT) {
                tvSellBankName.setText(R.string.wechat_acount);
                tvBuyPayTitle.setText(R.string.wechat_pay);
                tvBankAddress.setText(R.string.pay_qrcode);
//                llBankAddress.setVisibility(View.VISIBLE);
                llBankHave.setVisibility(View.GONE);
                tvBankAddressValue.setText("");
                ivBankAddress.setImageResource(R.drawable.icon_qr_code);
                tvBankNameValue.setText(TextUtils.isEmpty(data.getPayment().getPayAccount()) ? "" : data.getPayment().getPayAccount());
            }

        }
    }

    /**
     * 这个订单是否是用户
     */
    private boolean isUser() {
        if (data == null || UserInfoController.getInstance().getUserInfo() == null) {
            return true;
        }
        return UserInfoController.getInstance().getUserInfo().userId == data.getOrder().getPlaceId();

    }

    private void setRefreshFalse() {
        if (swipe_refresh_layout != null && swipe_refresh_layout.isRefreshing()) {
            swipe_refresh_layout.setRefreshing(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(Constants.ORDER_LSIT_REFRESH);
        intent.putExtra("orderStatu", orderType);
        LocalBroadcastManager.getInstance(CBRepository.getContext()).sendBroadcast(intent);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}