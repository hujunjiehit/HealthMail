package com.coinbene.manbiwang.spot.otc.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.manbiwang.model.http.OtcAdListModel;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("SetTextI18n")
public class OtcUserNotPayDialog extends BottomSheetDialog {


    @BindView(R2.id.tv_buy_or_sell)
    TextView tvBuyOrSell;
    @BindView(R2.id.tv_unit_price)
    TextView tvUnitPrice;
    @BindView(R2.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R2.id.tv_please_pay_type)
    TextView tvPleasePayType;
    @BindView(R2.id.tv_pay_tips)
    TextView tvPayTips;
    @BindView(R2.id.tv_add)
    TextView tvAdd;
    @BindView(R2.id.view_line)
    TextView viewLine;
    @BindView(R2.id.tv_cancel)
    TextView tvCancel;
    @BindView(R2.id.ll_dialog_root)
    RelativeLayout llDialogRoot;

    public OtcUserNotPayDialog(@NonNull Context context) {
        super(context, R.style.CoinBene_BottomSheet);
        setContentView(R.layout.spot_common_dialog_otc_user_not_pay);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        tvCancel.setOnClickListener(v -> dismiss());

        tvAdd.setOnClickListener(v -> {

            ARouter.getInstance().build(RouteHub.User.userPayTypesActivity).navigation(v.getContext());
            dismiss();
        });
    }


    public void setData(OtcAdListModel.DataBean.ListBean data) {
        if (data.getAdType() == 2) {
            tvBuyOrSell.setText(String.format("%s  %s", getContext().getResources().getString(R.string.trade_buy), data.getAsset()));
        } else {
            tvBuyOrSell.setText(String.format("%s  %s", getContext().getResources().getString(R.string.trade_sell), data.getAsset()));

        }

        String payTypes = "";
        tvUnitPrice.setText(String.format("%s%s", data.getCurrencySymbol(), data.getPrice()));
        if (data.getPayTypes() != null && data.getPayTypes().size() > 0) {
            for (int i = 0; i < data.getPayTypes().size(); i++) {
                if (i == 0) {
                    payTypes += data.getPayTypes().get(i).getPayTypeName();
                } else {
                    payTypes += "、" + data.getPayTypes().get(i).getPayTypeName();
                }

            }

            tvPayTips.setText(String.format(getContext().getResources().getString(R.string.buy_pay_type_tips), payTypes));
        }

    }


}
