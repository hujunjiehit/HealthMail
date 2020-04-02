package com.coinbene.manbiwang.spot.otc.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.database.OtcConfigController;
import com.coinbene.common.database.OtcCurrencyTable;
import com.coinbene.common.database.OtcPayTypeTable;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.widget.SupportPopupWindow;
import com.coinbene.common.widget.app.RecyclerItemDecoration;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.otc.OtcFilterAdapter;
import com.coinbene.manbiwang.spot.otc.OtcFilterCurrencyAdapter;
import com.coinbene.manbiwang.spot.otc.OtcFilterPayTypeAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * @author xiangdong.meng
 */
public class OtcFilterWindowNew implements View.OnClickListener {
    private View mAnchor;
    private SupportPopupWindow mPopupWindow;
    boolean mDismissed = false;
    private LinearLayout mPanel;
    private static final int BG_VIEW_ID = 10;
    private OtcSelectListener selectTypeListener;
    private View mBg;

    //    private TextView tv_all, tv_bank, tv_alipay, tv_wechat;
    private int accountType = Constants.PAY_TYPE_ALL;
    private RecyclerView rlAmount, rlPayType, rlCurrency;
    private Button btnReset, btnSure;
    private OtcFilterAdapter otcAmountAdapter;
    private OtcFilterCurrencyAdapter otcCurrencyAdapter;
    private OtcFilterPayTypeAdapter otcPayAdapter;

    private String curCurrency;
    private int curAmountPosition = 0;
    private int curPayTypePosition = 0;

    public OtcFilterWindowNew(View anchor, String curCurrency) {
        this.curCurrency = curCurrency;
        mAnchor = anchor;
        mPopupWindow = new SupportPopupWindow(anchor);
        mPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);// true，才能popupWindow消失

        LinearLayout mContentView = new LinearLayout(mAnchor.getContext());
        mContentView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mContentView.setBackgroundColor(mAnchor.getContext().getResources().getColor(
                android.R.color.transparent));

        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setContentView(mContentView);
        mPopupWindow.setOnDismissListener(() -> dismissQuickActionBar());
        initView();
    }

    private void initView() {
        InputMethodManager imm = (InputMethodManager) mAnchor.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (mAnchor != null) {
                imm.hideSoftInputFromWindow(mAnchor.getWindowToken(), 0);
            }
        }
        FrameLayout parent = new FrameLayout(mAnchor.getContext());
        parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        mBg = new View(mAnchor.getContext());
        mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mBg.setBackgroundColor(Color.argb(105, 0, 0, 0));
        mBg.setId(BG_VIEW_ID);

        mPanel = (LinearLayout) LayoutInflater.from(mAnchor.getContext()).inflate(
                R.layout.spot_otc_pay_type_popview, null);

//        tv_all = mPanel.findViewById(R.id.tv_all);
        rlAmount = mPanel.findViewById(R.id.rl_amount);
        rlCurrency = mPanel.findViewById(R.id.rl_currency);
        rlPayType = mPanel.findViewById(R.id.rl_pay_type);
        btnReset = mPanel.findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
        btnSure = mPanel.findViewById(R.id.btn_sure);
        btnSure.setOnClickListener(this);
        parent.addView(mBg);
        parent.addView(mPanel);

        mBg.setOnClickListener(v -> onDismiss());

        ((ViewGroup) mPopupWindow.getContentView()).addView(parent);


        initAmountData();
        initPayTypeData();
        initCurrencyData();

    }

    private void initCurrencyData() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(mAnchor.getContext(), 4);
        rlCurrency.addItemDecoration(new RecyclerItemDecoration(DensityUtil.dip2px( 5), 4));
        rlCurrency.setLayoutManager(linearLayoutManager);

        otcCurrencyAdapter = new OtcFilterCurrencyAdapter();
        rlCurrency.setAdapter(otcCurrencyAdapter);
        List<String> strings = new ArrayList<>();
        List<OtcCurrencyTable> otcCurrencyTables = OtcConfigController.getInstance().queryCurrencyList();
        if (otcCurrencyTables != null && otcCurrencyTables.size() > 0) {
            for (OtcCurrencyTable table : otcCurrencyTables) {
                strings.add(table.currency);
            }
        }
        otcCurrencyAdapter.setLists(strings, curCurrency);
        otcCurrencyAdapter.setOnItemClickLisenter((coinName, position) -> otcCurrencyAdapter.setSelect(coinName));
    }

    private void initPayTypeData() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(mAnchor.getContext(), 4);
        rlPayType.addItemDecoration(new RecyclerItemDecoration(DensityUtil.dip2px( 5), 4));
        rlPayType.setLayoutManager(linearLayoutManager);

        otcPayAdapter = new OtcFilterPayTypeAdapter();
        rlPayType.setAdapter(otcPayAdapter);
        List<OtcPayTypeTable> otcPayTypeTables = OtcConfigController.getInstance().queryPayTypeList();
        otcPayTypeTables.add(0,new OtcPayTypeTable(0, mAnchor.getResources().getString(R.string.all_acount)));
        otcPayAdapter.setLists(otcPayTypeTables, 0);
        otcPayAdapter.setOnItemClickLisenter((coinName, position) -> otcPayAdapter.setSelectPosition(position));
    }

    private void initAmountData() {

        GridLayoutManager linearLayoutManager = new GridLayoutManager(mAnchor.getContext(), 4);
        rlAmount.addItemDecoration(new RecyclerItemDecoration(DensityUtil.dip2px( 5), 4));
        rlAmount.setLayoutManager(linearLayoutManager);

        otcAmountAdapter = new OtcFilterAdapter();
        rlAmount.setAdapter(otcAmountAdapter);
        List<String> strings = new ArrayList<>();
        strings.add(mAnchor.getResources().getString(R.string.all_acount));
        strings.add("<100000");
        strings.add("≥100000");
        strings.add("≥200000");
        otcAmountAdapter.setLists(strings, 0);
        otcAmountAdapter.setOnItemClickLisenter((coinName, position) -> otcAmountAdapter.setSelectPosition(position));
    }


    public void notifyData(){
//        initCurrencyData();
//        initPayTypeData();
        if(otcPayAdapter!=null){
            otcPayAdapter.setSelectPosition(curPayTypePosition);
        }
        if(otcCurrencyAdapter!=null){
            otcCurrencyAdapter.setSelect(curCurrency);
        }
        if(otcAmountAdapter!=null){
            otcAmountAdapter.setSelectPosition(curAmountPosition);
        }
    }

    public void showBelowAnchor(Context context) {
        mDismissed = false;
        int yoff = DensityUtil.dip2px( 0);
        mPopupWindow.showAsDropDown(mAnchor, 0, yoff);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reset) {
            if (otcAmountAdapter != null) {
                otcAmountAdapter.setSelectPosition(0);
            }
            if (otcCurrencyAdapter != null) {
                otcCurrencyAdapter.setSelect(LanguageHelper.getCurrencyString());
            }
            if (otcPayAdapter != null) {
                otcPayAdapter.setSelectPosition(0);
            }

            accountType = Constants.PAY_TYPE_ALL;
        } else if (v.getId() == R.id.btn_sure) {
            accountType = Constants.PAY_TYPE_BANK;
            this.curAmountPosition = otcAmountAdapter.getSelectPosition();
            this.curPayTypePosition = otcPayAdapter.getSelectPosition();
            this.curCurrency = otcCurrencyAdapter.getSelect();
            selectTypeListener.selectFilter(getPriceRange(otcAmountAdapter.getSelectPosition()), otcPayAdapter.getSelectPayType(), otcCurrencyAdapter.getSelect());
            onDismiss();
        }

    }


    private int[] getPriceRange(int position) {
        if (position == 0) {
            return Constants.PRICE_TYPE_ALL;
        } else if (position == 1) {
            return Constants.PRICE_TYPE_UNDER_TEN;

        } else if (position == 2) {
            return Constants.PAY_TYPE_UP_ONE_HUNDRED_THOUSAND;

        } else if (position == 3) {
            return Constants.PAY_TYPE_UP_TWO_HUNDRED_THOUSAND;
        }
        return Constants.PRICE_TYPE_ALL;
    }

    public void setTypeChangeListener(OtcSelectListener selectTimeListener) {
        this.selectTypeListener = selectTimeListener;
    }

    public void onDismiss() {
        dismissQuickActionBar();
    }

    private void dismissQuickActionBar() {
        if (mDismissed) {
            return;
        }
        mDismissed = true;
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public SupportPopupWindow getPopupWindow() {
        return mPopupWindow;
    }
}