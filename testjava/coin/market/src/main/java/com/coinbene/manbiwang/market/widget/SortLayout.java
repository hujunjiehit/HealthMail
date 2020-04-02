package com.coinbene.manbiwang.market.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.market.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by huyong
 * on 2018/7/30
 */
public class SortLayout extends LinearLayout {

    @BindView(R2.id.ll_edit_self)
    LinearLayout llEditSelf;
    @BindView(R2.id.tv_coin_name)
    AscDescTextView tvCoinName;
    @BindView(R2.id.tv_24_limit)
    AscDescTextView tv24Limit;
    @BindView(R2.id.tv_new_price)
    AscDescTextView tvNewPrice;
    @BindView(R2.id.tv_rise_and_fall)
    AscDescTextView tvRiseAndFall;
    private ClickType clickType;
    private boolean isOption = false;

    public SortLayout(Context context) {
        super(context);
        initView();
    }

    public SortLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SortLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.sort_layout, null);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        SortLayout.this.addView(view, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        setViewData();
    }

    public void setOption(boolean option) {
        isOption = option;
        if (isOption) {
            llEditSelf.setVisibility(VISIBLE);
            tv24Limit.setVisibility(GONE);
        } else {
            llEditSelf.setVisibility(GONE);
            tv24Limit.setVisibility(VISIBLE);
        }
    }

    private void setViewData() {
        tvCoinName.getTvSortName().setText(R.string.token);
        tvCoinName.setFirstDown(false);
        tvCoinName.setSortType(0);
        tv24Limit.getTvSortName().setText(R.string.v24);
        tv24Limit.setSortType(1);
        tvNewPrice.getTvSortName().setText(R.string.last_price);
        tvNewPrice.setSortType(2);
        tvRiseAndFall.getTvSortName().setText(R.string.fall_rese_change);
        tvRiseAndFall.setSortType(3);
        llEditSelf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickType.onClickType(4, null);
            }
        });


        tvCoinName.setOnClickSortType(sortType -> {
            tv24Limit.clearStutes();
            tvNewPrice.clearStutes();
            tvRiseAndFall.clearStutes();
            clickType.onClickType(0, tvCoinName.getType());
        });
        tv24Limit.setOnClickSortType(sortType -> {
            tvCoinName.clearStutes();
            tvNewPrice.clearStutes();
            tvRiseAndFall.clearStutes();
            clickType.onClickType(1, tv24Limit.getType());
        });
        tvNewPrice.setOnClickSortType(sortType -> {
            tv24Limit.clearStutes();
            tvCoinName.clearStutes();
            tvRiseAndFall.clearStutes();
            clickType.onClickType(2, tvNewPrice.getType());
        });
        tvRiseAndFall.setOnClickSortType(sortType -> {
            tv24Limit.clearStutes();
            tvNewPrice.clearStutes();
            tvCoinName.clearStutes();
            clickType.onClickType(3, tvRiseAndFall.getType());
        });

    }

    public void setClickType(ClickType clickType) {
        this.clickType = clickType;
    }

    public interface ClickType {
        void onClickType(int type, String sortType);
    }
}
