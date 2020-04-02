package com.coinbene.manbiwang.kline.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.listener.SelectTimeListener;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;

/**
 * Created by mxd
 */
public class ZhibiaoPopWindow implements View.OnClickListener {
    private PopupWindow mPopupWindow;
    private View mAnchor;
    private View mContentView;
    private SelectTimeListener selectTimeListener;
    private String kType;
    private int kMasterType = 1;//0  都隐藏  1 MA显示  2Boll显示

    private CheckBox mCbHideMain;
    private CheckBox mCbHideSub2;

    public ZhibiaoPopWindow(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.zhibiao_popview, null);
        mPopupWindow = new PopupWindow(mContentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
    }

    public void setType(String kType) {
        this.kType = kType;
    }

    public void setkMasterType(int kMasterType) {
        this.kMasterType = kMasterType;
    }

    public void show(View anchor) {
        mAnchor = anchor;
        int xoff = DensityUtil.dip2px( 0);
        int yoff = DensityUtil.dip2px( 0);

        mPopupWindow.showAsDropDown(mAnchor, xoff, yoff);
        mCbHideMain = mContentView.findViewById(R.id.cb_hide_main);
        mCbHideSub2 = mContentView.findViewById(R.id.cb_hide_sub2);

        mCbHideMain.setOnClickListener(this);
        mCbHideSub2.setOnClickListener(this);

        TextView macdTv = mContentView.findViewById(R.id.macd_tv);
        TextView kdjTv = mContentView.findViewById(R.id.kdi_tv);
        TextView rsiTv = mContentView.findViewById(R.id.rsi_tv);

        if (kType.equals(ZhibiaoListener.MACD)) {
            macdTv.setSelected(true);
            kdjTv.setSelected(false);
            rsiTv.setSelected(false);
            mCbHideSub2.setChecked(false);
        } else if (kType.equals(ZhibiaoListener.KDJ)) {
            macdTv.setSelected(false);
            kdjTv.setSelected(true);
            rsiTv.setSelected(false);
            mCbHideSub2.setChecked(false);
        } else if (kType.equals(ZhibiaoListener.RSI)) {
            macdTv.setSelected(false);
            kdjTv.setSelected(false);
            rsiTv.setSelected(true);
            mCbHideSub2.setChecked(false);
        } else if (kType.equals(ZhibiaoListener.SUB2_HIDE)) {
            macdTv.setSelected(false);
            kdjTv.setSelected(false);
            rsiTv.setSelected(false);
            mCbHideSub2.setChecked(true);
        }


        macdTv.setOnClickListener(this);
        kdjTv.setOnClickListener(this);
        rsiTv.setOnClickListener(this);

        TextView ma_tv = mContentView.findViewById(R.id.ma_tv);
        TextView boll_tv = mContentView.findViewById(R.id.boll_tv);
        if (kMasterType == ZhibiaoListener.MASTER_TYPE_HIDE) {
            ma_tv.setSelected(false);
            boll_tv.setSelected(false);
            mCbHideMain.setChecked(true);
        } else if (kMasterType == ZhibiaoListener.MASTER_TYPE_MA) {
            ma_tv.setSelected(true);
            boll_tv.setSelected(false);
            mCbHideMain.setChecked(false);
        } else if (kMasterType == ZhibiaoListener.MASTER_TYPE_BOLL) {
            ma_tv.setSelected(false);
            boll_tv.setSelected(true);
            mCbHideMain.setChecked(false);
        }
        ma_tv.setOnClickListener(this);
        boll_tv.setOnClickListener(this);
    }

    public PopupWindow getmPopupWindow() {
        return mPopupWindow;
    }

    public void setmPopupWindow(PopupWindow mPopupWindow) {
        this.mPopupWindow = mPopupWindow;
    }

    @Override
    public void onClick(View v) {
        String timeType = ZhibiaoListener.MACD;
        TextView textView = (TextView) v;
        if (v.getId() == R.id.cb_hide_main) {
            mCbHideMain.setChecked(false);
            SpUtil.put(v.getContext(), SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_HIDE);
            if (selectTimeListener != null) {
                selectTimeListener.selectMasterType(ZhibiaoListener.MASTER_TYPE_HIDE, textView.getText().toString());
            }
        } else if (v.getId() == R.id.cb_hide_sub2) {
            timeType = ZhibiaoListener.SUB2_HIDE;
            mCbHideSub2.setChecked(false);
            SpUtil.put(v.getContext(), SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.SUB2_HIDE);
            if (selectTimeListener != null) {
                selectTimeListener.selectZhibiaoType(timeType, textView.getText().toString());
            }
        } else if (v.getId() == R.id.macd_tv) {
            timeType = ZhibiaoListener.MACD;
            mCbHideSub2.setChecked(true);
            SpUtil.put(v.getContext(), SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.MACD);
            if (selectTimeListener != null) {
                selectTimeListener.selectZhibiaoType(timeType, textView.getText().toString());
            }
        } else if (v.getId() == R.id.kdi_tv) {
            timeType = ZhibiaoListener.KDJ;
            mCbHideSub2.setChecked(true);
            SpUtil.put(v.getContext(), SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.KDJ);
            if (selectTimeListener != null) {
                selectTimeListener.selectZhibiaoType(timeType, textView.getText().toString());
            }
        } else if (v.getId() == R.id.rsi_tv) {
            timeType = ZhibiaoListener.RSI;
            mCbHideSub2.setChecked(true);
            SpUtil.put(v.getContext(), SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.RSI);
            if (selectTimeListener != null) {
                selectTimeListener.selectZhibiaoType(timeType, textView.getText().toString());
            }
        } else if (v.getId() == R.id.ma_tv) {
            mCbHideMain.setChecked(true);
            SpUtil.put(v.getContext(), SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
            if (selectTimeListener != null) {
                selectTimeListener.selectMasterType(ZhibiaoListener.MASTER_TYPE_MA, textView.getText().toString());
            }
        } else if (v.getId() == R.id.boll_tv) {
            mCbHideMain.setChecked(true);
            SpUtil.put(v.getContext(), SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_BOLL);
            if (selectTimeListener != null) {
                selectTimeListener.selectMasterType(ZhibiaoListener.MASTER_TYPE_BOLL, textView.getText().toString());
            }
        }

        mPopupWindow.dismiss();
    }

    public void setTypeChangeListener(SelectTimeListener selectTimeListener) {
        this.selectTimeListener = selectTimeListener;
    }
}
