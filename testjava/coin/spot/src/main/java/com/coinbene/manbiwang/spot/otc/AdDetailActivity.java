package com.coinbene.manbiwang.spot.otc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.SellerAdModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.widget.app.FixedRecyclerView;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.otc.adapter.AdDetailPayBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.drakeet.multitype.MultiTypeAdapter;

public class AdDetailActivity extends CoinbeneBaseActivity {


    @BindView(R2.id.iv_close)
    ImageView ivClose;
    @BindView(R2.id.menu_back)
    RelativeLayout menuBack;
    @BindView(R2.id.menu_title_tv)
    TextView menuTitleTv;
    @BindView(R2.id.menu_right_tv)
    TextView menuRightTv;
    @BindView(R2.id.search_img)
    ImageView searchImg;
    @BindView(R2.id.menu_line_view)
    View menuLineView;
    @BindView(R2.id.tv_ad_status)
    TextView tvAdStatus;
    @BindView(R2.id.tv_ad_type)
    TextView tvAdType;
    @BindView(R2.id.tv_asset_name)
    TextView tvAssetName;
    @BindView(R2.id.tv_mark)
    TextView tvMark;
    @BindView(R2.id.tv_amount)
    TextView tvAmount;
    @BindView(R2.id.tv_pay_type)
    TextView tvPayType;

    @BindView(R2.id.tv_currency)
    TextView tvCurrency;
    @BindView(R2.id.tv_unit_price)
    TextView tvUnitPrice;
    @BindView(R2.id.tv_limit_price)
    TextView tvLimitPrice;
    @BindView(R2.id.btn_handle)
    Button btnHandle;

    @BindView(R2.id.tv_price_type)
    TextView tvPriceType;
    @BindView(R2.id.rlv_pay_types)
    FixedRecyclerView rlvPayTypes;
    @BindView(R2.id.tv_unit)
    TextView tvUnit;
    private String status = "1";
    private AlertDialog.Builder handerDialog;
    private int adId;

    public static void startMe(Context context, SellerAdModel.DataBean.ListBean listBean) {
        Intent intent = new Intent(context, AdDetailActivity.class);
        intent.putExtra("item", listBean);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.spot_activity_ad_detail;
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
        getData();
    }

    @Override
    public boolean needLock() {
        return true;
    }

    private void getData() {
        menuTitleTv.setText(R.string.ad_detail);
        SellerAdModel.DataBean.ListBean listBean = (SellerAdModel.DataBean.ListBean) getIntent().getSerializableExtra("item");
        if (listBean != null) {
            status = listBean.getReleaseType();
            adId = listBean.getAdId();
            if (!TextUtils.isEmpty(status)) {
                if (status.equals("1")) {
                    btnHandle.setText(R.string.under_order);
                    tvAdStatus.setText(R.string.ad_status_running);
                    tvAdStatus.setTextColor(getResources().getColor(R.color.res_blue));
                } else {
                    btnHandle.setText(R.string.delete_order);
                    tvAdStatus.setText(R.string.ad_status_invalid);
                    tvAdStatus.setTextColor(getResources().getColor(R.color.res_textColor_1));
                }
            }
            if (listBean.getAdType() == 1) {
                tvAdType.setText(R.string.histr_filter_direction_buy);
                tvPayType.setText(R.string.pay_type);
                tvAdType.setTextColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
            } else {
                tvPayType.setText(R.string.ad_payment_method);
                tvAdType.setText(R.string.histr_filter_direction_sell);
                tvAdType.setTextColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
            }
            tvAssetName.setText(TextUtils.isEmpty(listBean.getAsset()) ? "" : listBean.getAsset());
            tvAmount.setText((TextUtils.isEmpty(listBean.getStock()) ? "" : listBean.getStock()));
            tvCurrency.setText((TextUtils.isEmpty(listBean.getCurrency()) ? "" : listBean.getCurrency()));

            if (listBean.getAdPriceMode() == 0) {//固定金额
                tvPriceType.setText(R.string.fixed_amount);
                tvUnitPrice.setText((TextUtils.isEmpty(listBean.getPrice()) ? "" : listBean.getPrice()));
                tvUnit.setText(R.string.order_unit_price);
            } else {//浮动比例
                tvPriceType.setText(R.string.floating_range);
                tvUnit.setText(R.string.floating_range);
                tvUnitPrice.setText((TextUtils.isEmpty(listBean.getFloatingRatio()) ? "" : BigDecimalUtils.toPercentage(BigDecimalUtils.divideToStr(listBean.getFloatingRatio(), "100", 4))));
            }


            String minLimit = listBean.getMinOrder();
            String maxLimit = listBean.getMaxOrder();
            String currency = listBean.getCurrency();
            if (!TextUtils.isEmpty(minLimit) && !TextUtils.isEmpty(maxLimit) && !TextUtils.isEmpty(currency)) {
                tvLimitPrice.setText(String.format("%s  %s    -    %s  %s", minLimit, currency, maxLimit, currency));

            }
            tvMark.setText(TextUtils.isEmpty(listBean.getRemark()) ? "" : listBean.getRemark());
            initPayType(listBean);
        }

    }

    private void initPayType(SellerAdModel.DataBean.ListBean listBean) {
        MultiTypeAdapter mContentAdapter = new MultiTypeAdapter();
        AdDetailPayBinder adDetailPayBinder = new AdDetailPayBinder();
        mContentAdapter.register(SellerAdModel.DataBean.ListBean.PayWayListBean.class, adDetailPayBinder);
        rlvPayTypes.setLayoutManager(new LinearLayoutManager(AdDetailActivity.this));
        rlvPayTypes.setAdapter(mContentAdapter);
        mContentAdapter.setItems(listBean.getPayWayList());
    }

    private void init() {
        btnHandle.setOnClickListener(v -> initDialog(status));
        menuBack.setOnClickListener(v -> finish());
    }


    private void initDialog(String status) { //status.equals("1")//上架中   去下架   //已下架   去删除
        if (TextUtils.isEmpty(status)) {
            return;
        }

        if (handerDialog == null) {
            handerDialog = new AlertDialog.Builder(this);
        }
        if (status.equals("1")) {
            handerDialog.setTitle(R.string.under_order_sure);
            handerDialog.setMessage(R.string.under_order_tip);
        } else {
            handerDialog.setTitle(R.string.delete_order_sure);
        }
        handerDialog.setCancelable(true);
        if (status.equals("1")) {
            handerDialog.setPositiveButton(getString(R.string.btn_ok), (orderHanderDialog, which) -> {
                adCancel();
                orderHanderDialog.dismiss();
            });
        } else {
            handerDialog.setPositiveButton(getString(R.string.btn_ok), (orderHanderDialog, which) -> {
                adDelete();
                orderHanderDialog.dismiss();
            });
        }
        handerDialog.setNegativeButton(getString(R.string.btn_cancel), (orderHanderDialog, which) -> orderHanderDialog.dismiss());
        handerDialog.show();
    }


    /**
     * 下架广告
     */
    private void adCancel() {


        HttpParams httpParams = new HttpParams();

        httpParams.put("adId", adId);

        OkGo.<BaseRes>post(Constants.AD_CANCEL).params(httpParams).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {

            @Override
            public void onSuc(Response<BaseRes> response) {
                finish();
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }
        });

    }


    /**
     * 下架广告
     */
    private void adDelete() {


        HttpParams httpParams = new HttpParams();

        httpParams.put("adId", adId);

        OkGo.<BaseRes>post(Constants.AD_DELETE).params(httpParams).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {

            @Override
            public void onSuc(Response<BaseRes> response) {
                finish();
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }
        });

    }
}
