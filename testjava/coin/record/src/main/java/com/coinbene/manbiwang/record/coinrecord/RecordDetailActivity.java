package com.coinbene.manbiwang.record.coinrecord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.utils.GlideUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.model.http.DepositListModel;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;

import butterknife.BindView;
import butterknife.Unbinder;

public class RecordDetailActivity extends CoinbeneBaseActivity {

    @BindView(R2.id.menu_back)
    RelativeLayout menuBack;
    @BindView(R2.id.menu_title_tv)
    TextView menuTitleTv;

    @BindView(R2.id.iv_coin_icon)
    ImageView coin_img;
    @BindView(R2.id.tv_coin_name)
    TextView tv_coin_name;
    @BindView(R2.id.tv_coin_local_name)
    TextView tv_coin_local_name;

    @BindView(R2.id.tv_num)
    TextView tv_num;

    @BindView(R2.id.tv_num_value)
    TextView tv_num_value;
    @BindView(R2.id.tv_status)
    TextView tv_status;
    @BindView(R2.id.tv_status_value)
    TextView tv_status_value;
    @BindView(R2.id.tv_address)
    TextView tv_address;
    @BindView(R2.id.tv_address_value)
    TextView tv_address_value;
    @BindView(R2.id.tv_txid)
    TextView tv_txid;
    @BindView(R2.id.tv_txid_value)
    TextView tv_txid_value;
    @BindView(R2.id.tv_remark)
    TextView tv_remark;
    @BindView(R2.id.tv_remark_value)
    TextView tv_remark_value;
    @BindView(R2.id.tv_time)
    TextView tv_time;
    @BindView(R2.id.tv_time_value)
    TextView tv_time_value;

    private Unbinder mUnbinder;
    private DepositListModel.DataBean.ListBean bean;
    private String type;//充值，或者提币

    public static void startMe(Context context, DepositListModel.DataBean.ListBean bean, String type) {
        Intent intent = new Intent(context, RecordDetailActivity.class);
        intent.putExtra("bean", bean);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int initLayout() {
        return R.layout.record_activity_record_detail;
    }

    @Override
    public void initView() {
        getData();
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

    private void getData() {
        menuBack.setOnClickListener(this);
        bean = (DepositListModel.DataBean.ListBean) getIntent().getSerializableExtra("bean");
        type = getIntent().getStringExtra("type");
        if (type.equals(WithDrawRechargeHisActivity.ORDER_TYPE_DEPOSIT)) {
            menuTitleTv.setText(R.string.deposit_detail_title);
        } else {
            menuTitleTv.setText(R.string.withdraw_detail_title);
        }
        if (bean == null) {
            ToastUtil.show(R.string.toast_data_transfer_error);
            finish();
            return;
        }
        String urlpath = Constants.BASE_IMG_URL + bean.getAsset() + ".png";
        GlideUtils.loadImageViewLoad(this, urlpath, coin_img, R.drawable.coin_default_icon, R.drawable.coin_default_icon);

        tv_coin_name.setText(bean.getAsset());
        BalanceInfoTable balanceTable = BalanceController.getInstance().findByAsset(bean.getAsset());
        if (balanceTable != null) {
            tv_coin_local_name.setText(balanceTable.localAssetName);
        }
        tv_num_value.setText(bean.getAmount());
        //充值
        if (type.equals(WithDrawRechargeHisActivity.ORDER_TYPE_DEPOSIT)) {
            if (bean.getStatus().equals("1")) {//正在处理
                tv_status_value.setText(getResources().getString(R.string.orderstatus_1_doing));
            } else if (bean.getStatus().equals("2")) {//已经入账
                String str = getResources().getString(R.string.orderstatus_2_in);
                tv_status_value.setText(str);
            }
        } else {
            //提现
            if (bean.getStatus().equals("1")) {//正在处理
                tv_status_value.setText(getResources().getString(R.string.orderstatus_1_doing));
            } else if (bean.getStatus().equals("2")) {//待审核
                tv_status_value.setText(getResources().getString(R.string.orderstatus_daishenhe));
            } else if (bean.getStatus().equals("3")) {
                tv_status_value.setText(getResources().getString(R.string.orderstatus_4_submit));
            } else if (bean.getStatus().equals("4")) {
                tv_status_value.setText(getResources().getString(R.string.orderstatus_2_out));
            } else if (bean.getStatus().equals("5")) {
                tv_status_value.setText(getResources().getString(R.string.orderstatus_5));
            } else if (bean.getStatus().equals("6")) {
                tv_status_value.setText(getResources().getString(R.string.orderstatus_6));
            } else if (bean.getStatus().equals("7")) {
                tv_status_value.setText(getResources().getString(R.string.orderstatus_7));
            }
        }
        if (!TextUtils.isEmpty(bean.getAddress())) {
            tv_address_value.setVisibility(View.VISIBLE);
            tv_address_value.setText(bean.getAddress());
            tv_address.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(bean.getAddrUrl())) {
                tv_address_value.setTextColor(getResources().getColor(R.color.res_blue));
                tv_address_value.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tv_address_value.setOnClickListener(this);
                tv_address_value.getPaint().setAntiAlias(true);
            }
        } else {
            tv_address_value.setVisibility(View.GONE);
            tv_address.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(bean.getTxHash())) {
            tv_txid_value.setText(bean.getTxHash());
            tv_txid_value.setVisibility(View.VISIBLE);
            tv_txid.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(bean.getTxUrl())) {
                tv_txid_value.setTextColor(getResources().getColor(R.color.res_blue));
                tv_txid_value.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                tv_txid_value.getPaint().setAntiAlias(true);
                tv_txid_value.setOnClickListener(this);
            }
        } else {
            tv_txid_value.setVisibility(View.GONE);
            tv_txid.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(bean.getRemark())) {
            tv_remark_value.setText(bean.getRemark());
            tv_remark_value.setVisibility(View.VISIBLE);
            tv_remark.setVisibility(View.VISIBLE);
        } else {
            tv_remark_value.setVisibility(View.GONE);
            tv_remark.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(bean.getOrderTime())) {
            tv_time_value.setText(bean.getOrderTime());
            tv_time_value.setVisibility(View.VISIBLE);
            tv_time.setVisibility(View.VISIBLE);
        } else {
            tv_time_value.setVisibility(View.GONE);
            tv_time.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_address_value) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getAddrUrl()));
            startActivity(intent);

        } else if (v.getId() == R.id.tv_txid_value) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getTxUrl()));
            startActivity(intent);
        }else if(v.getId() == R.id.menu_back){
            finish();
        }
    }
}
