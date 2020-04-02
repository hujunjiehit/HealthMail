package com.coinbene.common.widget.app;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.R;
import com.coinbene.common.utils.DensityUtil;

public class TradeMarketPopWindow extends PopupWindow implements View.OnClickListener {
    private ImageView ivDault, ivBuyMarket, ivSellMarket;
    private TextView tvDefault, tvBuy, tvSell;
    private onClickPopLisener onClickLisener;
    private String string = null;
    private int position = 0;

    public TradeMarketPopWindow(Context context) {
        initPopupWindow();
        View view = View.inflate(context, R.layout.common_trade_market_popview, null);
        setContentView(view);
        //设置popwindow的宽高，这个数字是多少就设置多少dp，注意单位是dp
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(DensityUtil.dip2px( 140));

        initView(view);
    }

    private void initView(View view) {
        View llDefault = view.findViewById(R.id.rl_default);
        View llBuyMarket = view.findViewById(R.id.rl_buy);
        View llSellMarket = view.findViewById(R.id.rl_sell);
        ivDault = view.findViewById(R.id.iv_default);
        ivBuyMarket = view.findViewById(R.id.iv_buy);
        ivSellMarket = view.findViewById(R.id.iv_sell);

        tvDefault = view.findViewById(R.id.tv_default);
        tvBuy = view.findViewById(R.id.tv_buy);
        tvSell = view.findViewById(R.id.tv_sell);
        llDefault.setOnClickListener(this);
        llBuyMarket.setOnClickListener(this);
        llSellMarket.setOnClickListener(this);
        ivDault.setOnClickListener(this);
        ivBuyMarket.setOnClickListener(this);
        ivSellMarket.setOnClickListener(this);
    }

    private void initPopupWindow() {
//        setAnimationStyle(R.style.popwindowAnim);//设置动画
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 2);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        //自己设置点击事件。。。

        int id = v.getId();
        if (id == R.id.rl_default) {
            ivDault.setVisibility(View.VISIBLE);
            ivBuyMarket.setVisibility(View.GONE);
            ivSellMarket.setVisibility(View.GONE);
            string = tvDefault.getText().toString();
            position = Constants.TRADE_MARKET_POP_DEFAULT;
        } else if (id == R.id.rl_buy) {
            ivDault.setVisibility(View.GONE);
            ivBuyMarket.setVisibility(View.VISIBLE);
            ivSellMarket.setVisibility(View.GONE);
            string = tvBuy.getText().toString();
            position = Constants.TRADE_MARKET_POP_BUY;
        } else if (id == R.id.rl_sell) {
            ivDault.setVisibility(View.GONE);
            ivBuyMarket.setVisibility(View.GONE);
            ivSellMarket.setVisibility(View.VISIBLE);
            string = tvSell.getText().toString();
            position = Constants.TRADE_MARKET_POP_SELL;
        }
        if (onClickLisener != null) {
            onClickLisener.onPopClick(string, position);
        }
        dismiss();
    }

    public TradeMarketPopWindow.onClickPopLisener getOnClickLisener() {
        return onClickLisener;
    }

    public void setOnClickPopLisener(TradeMarketPopWindow.onClickPopLisener onClickLisener) {
        this.onClickLisener = onClickLisener;
    }

    public interface onClickPopLisener {
        public void onPopClick(String string, int position);
    }
}