package com.coinbene.manbiwang.kline.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.widget.MoreTextView;
import com.coinbene.common.widget.dialog.CopyPopWindow;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.fragment.klineinterface.ActivityInterface;
import com.coinbene.manbiwang.model.http.CoinInfoModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;


/**
 * 提供基础内容和生命周期控制
 */
public class CoinInfoFragment extends CoinbeneBaseFragment {


    @BindView(R2.id.tv_coin_name)
    TextView tvCoinName;
    @BindView(R2.id.tv_disc)
    MoreTextView tvDisc;
    @BindView(R2.id.tv_send_time)
    TextView tvSendTime;
    @BindView(R2.id.tv_send_time_value)
    TextView tvSendTimeValue;
    @BindView(R2.id.tv_send_price)
    TextView tvSendPrice;
    @BindView(R2.id.tv_send_price_value)
    TextView tvSendPriceValue;
    @BindView(R2.id.tv_send_vol)
    TextView tvSendVol;
    @BindView(R2.id.tv_send_vol_value)
    TextView tvSendVolValue;
    @BindView(R2.id.tv_white_paper)
    TextView tvWhitePaper;
    @BindView(R2.id.tv_white_paper_value)
    TextView tvWhitePaperValue;
    @BindView(R2.id.tv_coin_query)
    TextView tvCoinQuery;
    @BindView(R2.id.tv_coin_query_value)
    TextView tvCoinQueryValue;
    @BindView(R2.id.tv_link)
    TextView tvLink;
    @BindView(R2.id.tv_link_value)
    TextView tvLinkValue;
    @BindView(R2.id.tv_total_vol)
    TextView tvTotalVol;
    @BindView(R2.id.tv_total_vol_value)
    TextView tvTotalVolValue;
    private String strDefoult = "--";
    private CopyPopWindow popWindow;

    private String[] coinPairArray;
    private String coinName;

    ActivityInterface.IActivityListener mActivityListener;

    public static CoinInfoFragment newInstance() {
        Bundle args = new Bundle();
        CoinInfoFragment fragment = new CoinInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int initLayout() {
        return R.layout.fr_kline_coin_info;
    }

    @Override
    public void initView(View rootView) {
        if (popWindow == null) {
            popWindow = new CopyPopWindow(getContext());
        }
    }

    @Override
    public void setListener() {
        popWindow.setOnClickPopLisener(string -> {
            if (!TextUtils.isEmpty(string)) {
                StringUtils.copyStrToClip(string);
            }
        });
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tvLinkValue.setBackgroundColor(getResources().getColor(R.color.transparent));
                tvWhitePaperValue.setBackgroundColor(getResources().getColor(R.color.transparent));
                tvCoinQueryValue.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });
        tvWhitePaperValue.setOnClickListener(v -> {
            if (TextUtils.isEmpty(tvWhitePaperValue.getText().toString()) || tvWhitePaperValue.getText().toString().equals(strDefoult)) {
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tvWhitePaperValue.getText().toString()));
            startActivity(intent);
        });
        tvCoinQueryValue.setOnClickListener(v -> {
            if (TextUtils.isEmpty(tvCoinQueryValue.getText().toString()) || tvCoinQueryValue.getText().toString().equals(strDefoult)) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tvCoinQueryValue.getText().toString()));
            startActivity(intent);
        });
        tvLinkValue.setOnClickListener(v -> {
            if (TextUtils.isEmpty(tvLinkValue.getText().toString()) || tvLinkValue.getText().toString().equals(strDefoult)) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tvLinkValue.getText().toString()));
            startActivity(intent);
        });

        tvLinkValue.setOnLongClickListener(v -> {
            if (popWindow == null) {
                popWindow = new CopyPopWindow(getContext());
            }
            popWindow.setCopyStr(tvLinkValue.getText().toString());
            popWindow.showPopupWindow(v);
            tvLinkValue.setBackgroundColor(getResources().getColor(R.color.res_green));
            return false;
        });
        tvWhitePaperValue.setOnLongClickListener(v -> {
            if (popWindow == null) {
                popWindow = new CopyPopWindow(getContext());
            }
            popWindow.setCopyStr(tvWhitePaperValue.getText().toString());
            popWindow.showPopupWindow(v);
            tvWhitePaperValue.setBackgroundColor(getResources().getColor(R.color.res_green));
            return false;
        });
        tvCoinQueryValue.setOnLongClickListener(v -> {
            if (popWindow == null) {
                popWindow = new CopyPopWindow(getContext());
            }
            popWindow.setCopyStr(tvCoinQueryValue.getText().toString());
            popWindow.showPopupWindow(v);
            tvCoinQueryValue.setBackgroundColor(getResources().getColor(R.color.res_green));
            return false;
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof ActivityInterface) {
            if (mActivityListener == null) {
                mActivityListener = tradePairName -> {
                    coinPairArray = parseCoinName(tradePairName);
                    if (coinPairArray != null) {
                        coinName = coinPairArray[0];
                    }
                    getCoinInfo(coinName);
                };
            }
            ((ActivityInterface) getActivity()).registerActivityListener(mActivityListener);
        }
    }

    private String[] parseCoinName(String coinPairName) {
        if (TextUtils.isEmpty(coinPairName)) {
            return null;
        }
        if (!coinPairName.contains("/")) {
            return null;
        }
        String[] arr = new String[2];
        String fenzi = coinPairName.substring(0, coinPairName.indexOf('/'));
        String fenmu = coinPairName.substring(coinPairName.indexOf('/') + 1);
        arr[0] = fenzi;
        arr[1] = fenmu;
        return arr;
    }

    @Override
    public void onFragmentShow() {

    }

    @Override
    public void onFragmentHide() {

    }



    /**
     * /币种介绍
     */

    private void getCoinInfo(String treadPair) {
        OkGo.<CoinInfoModel>get(Constants.K_COIN_INFO).params("asset", treadPair).tag(this).execute(new NewJsonSubCallBack<CoinInfoModel>() {

            @Override
            public void onSuc(Response<CoinInfoModel> response) {
                if (response != null && response.body() != null) {
                    CoinInfoModel coinInfoModel = response.body();
                    setData(coinInfoModel);
                }
            }

            @Override
            public void onE(Response<CoinInfoModel> response) {

            }

        });
    }



    private void setData(CoinInfoModel entity) {
        if (entity != null && null != entity.getData()) {
            if (tvCoinName != null) {
                tvCoinName.setText(TextUtils.isEmpty(entity.getData().getAsset()) ? strDefoult : entity.getData().getAsset());
            }
            if (tvDisc != null) {
                tvDisc.setText(TextUtils.isEmpty(entity.getData().getIntroduction()) ? strDefoult : entity.getData().getIntroduction());
            }
            if (tvSendTimeValue != null) {
                tvSendTimeValue.setText(TextUtils.isEmpty(entity.getData().getIssueTime()) ? strDefoult : entity.getData().getIssueTime());
            }
            if (tvSendPriceValue != null) {
                tvSendPriceValue.setText(TextUtils.isEmpty(entity.getData().getInitialPrice()) ? strDefoult : entity.getData().getInitialPrice());
            }
            if (tvSendVolValue != null) {
                tvSendVolValue.setText(TextUtils.isEmpty(entity.getData().getCirculation()) ? strDefoult : entity.getData().getCirculation());
            }
            if (tvTotalVolValue != null) {
                tvTotalVolValue.setText(TextUtils.isEmpty(entity.getData().getTotalAmount()) ? strDefoult : entity.getData().getTotalAmount());
            }
            if (tvWhitePaperValue != null) {
                tvWhitePaperValue.setText(TextUtils.isEmpty(entity.getData().getWhitePaper()) ? strDefoult : entity.getData().getWhitePaper());
            }
            if (tvCoinQueryValue != null) {
                tvCoinQueryValue.setText(TextUtils.isEmpty(entity.getData().getBlockExplorer()) ? strDefoult : entity.getData().getBlockExplorer());
            }
            if (tvLinkValue != null) {
                tvLinkValue.setText(TextUtils.isEmpty(entity.getData().getOfficialWebsite()) ? strDefoult : entity.getData().getOfficialWebsite());
            }

        }
    }


}
