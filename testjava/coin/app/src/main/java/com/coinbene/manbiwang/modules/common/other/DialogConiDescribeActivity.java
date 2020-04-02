package com.coinbene.manbiwang.modules.common.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.datacollection.SchemeFrom;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 首页弹窗
 */
public class DialogConiDescribeActivity extends CoinbeneBaseActivity implements View.OnClickListener {

    @BindView(R.id.coni_switch_title)
    TextView coniSwitchTitleTv;
    @BindView(R.id.goto_set_btn)
    TextView goToSetBtn;
    @BindView(R.id.tv_not_mind)
    TextView mTvNotMind;
    @BindView(R.id.coni_layout)
    LinearLayout coniLayout;
    @BindView(R.id.url_img)
    ImageView urlImgView;
    String img_url;
    String link_url;
    private boolean isCoinHalf = false;

    public static void startMeForResult(Activity activity, int code) {
        Intent intent = new Intent(activity, DialogConiDescribeActivity.class);
        intent.putExtra("isCoinHalf", true);
        activity.startActivityForResult(intent, code);
    }

    public static void startMe(Activity activity, String img_url, String link_url) {
        Intent intent = new Intent(activity, DialogConiDescribeActivity.class);
        intent.putExtra("img_url", img_url);
        intent.putExtra("link_url", link_url);
        intent.putExtra("isCoinHalf", false);
        activity.startActivity(intent);
    }


    @Override
    public int initLayout() {
        return R.layout.dialog_coni_switch;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();

        if (intent != null) {
            isCoinHalf = intent.getBooleanExtra("isCoinHalf", true);
            if (isCoinHalf) {
                String messageStr = coniSwitchTitleTv.getResources().getString(R.string.coni_switch_title);
                SpannableString builder = new SpannableString(messageStr);
                String coniStr = "CONI";
                String percentStr = "50%";
                int index_coni = messageStr.indexOf(coniStr);
                int index_percent = messageStr.indexOf(percentStr);
                if (index_coni < index_percent) {
                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.white)), 0, index_coni, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.res_assistColor_22)), index_coni, index_coni + coniStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.white)), index_coni + coniStr.length(), index_percent, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //默认认为 coni 后面是 50%
                    if (index_coni < index_percent) {
                        builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.res_assistColor_22)), index_percent, index_percent + percentStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.white)), index_percent + percentStr.length(), messageStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    //Getting the transaction fee 50% off with the CONI payment
                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.white)), 0, index_percent, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.res_assistColor_22)), index_percent, index_percent + percentStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.white)), index_percent + percentStr.length(), index_coni, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.res_assistColor_22)), index_coni, index_coni + coniStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    builder.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.white)), index_coni + coniStr.length(), messageStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }


                coniSwitchTitleTv.setText(builder);
                goToSetBtn.setOnClickListener(this);

                coniLayout.setVisibility(View.VISIBLE);
                urlImgView.setVisibility(View.GONE);

            } else {
                img_url = intent.getStringExtra("img_url");
                link_url = intent.getStringExtra("link_url");
                coniLayout.setVisibility(View.GONE);
                urlImgView.setVisibility(View.VISIBLE);

                urlImgView.setOnClickListener(this);
                Glide.with(CBRepository.getContext())
                        .load(img_url)
                        .apply(new RequestOptions().placeholder(R.drawable.dialog_coni_loading).error(R.drawable.dialog_coni_loading))
                        .into(urlImgView);
            }
        }
        this.setFinishOnTouchOutside(true);//false, 禁用点击空白处自动关闭; true, 允许点击空白处关闭
        mTvNotMind.setOnClickListener(this);

        mTvNotMind.setText(R.string.not_hint);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.goto_set_btn) {
            setResult(Activity.RESULT_OK);
            finish();
        } else if (v.getId() == R.id.tv_not_mind) {
            SpUtil.setPopupEnable(false);
            finish();
        } else if (v.getId() == R.id.url_img) {

            if (TextUtils.isEmpty(link_url)) {
                return;
            }
//            BrowserItem browserItem = new BrowserItem();
////            browserItem.title = getString(R.string.set_about_service_link);
////            browserItem.url = Constants.BASE_URL_H5 + link_url.trim();
//            browserItem.url = CommonUtil.pathAppendUrl(Constants.BASE_URL_H5, link_url.trim());
//            BrowserActivity.startMe(DialogConiDescribeActivity.this, browserItem, true);

            String finalUrl = UrlUtil.parseUrl(link_url);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isNotice", true);
            bundle.putString("SchemeFrom", SchemeFrom.APP_DIALOG.name());
            UIBusService.getInstance().openUri(DialogConiDescribeActivity.this, finalUrl, bundle);

            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
