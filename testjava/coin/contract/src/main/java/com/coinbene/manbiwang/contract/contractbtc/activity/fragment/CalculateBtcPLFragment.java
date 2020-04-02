package com.coinbene.manbiwang.contract.contractbtc.activity.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.activities.adapter.TextSelectAdapter;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.widget.CustomSeekBar;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractbtc.activity.view.ChooseLevelView;
import com.coinbene.manbiwang.model.http.CalculatePLModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.warkiz.widget.IndicatorSeekBar;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ding
 * 2019-05-14
 * com.coinbene.manbiwang.modules.trade.contract
 * 盈亏计算
 */
public class CalculateBtcPLFragment extends BaseFragment {
    @BindView(R2.id.contract_Spinner)
    TextView contractSpinner;
    @BindView(R2.id.contract_spinner_img)
    ImageView contractSpinnerImg;
    @BindView(R2.id.direction_More)
    TextView directionMore;
    @BindView(R2.id.mroe_select)
    ImageView mroeSelectImg;
    @BindView(R2.id.direction_null)
    TextView directionNull;
    @BindView(R2.id.null_Select)
    ImageView nullSelectImg;
    @BindView(R2.id.open_EditText)
    EditText openPrice;
    @BindView(R2.id.close_EditText)
    EditText closePrice;
    @BindView(R2.id.open_count_EditText)
    EditText openCount;
    @BindView(R2.id.start_calculate)
    Button calculate;
    @BindView(R2.id.bail_Result)
    TextView bailResult;
    @BindView(R2.id.PL_Result)
    TextView PLResult;
    @BindView(R2.id.rate_Result)
    TextView rateResult;
    @BindView(R2.id.choose_level_view)
    ChooseLevelView mChooseLevelView;

    Unbinder unbinder;

    static final String TAG = "CalculateUsdtPLFragment";

    /**
     * 默认选中方向
     */
    private String mDirection = Constants.DIRECTION_LONG;

    private String[] symbols;
    private TextSelectAdapter symbolAdapter;
    private BottomSheetDialog symbolDialog;
    private RecyclerView symbolRecycler;
    private View symbolCancel;
    private Context mContext;
    private int precision = 1;
    private boolean isCalculate = false;
    private String minPriceChange;

    private String currentLevel;
    private String currentSymbol;

    public static CalculateBtcPLFragment newInstance(String symbol, String lever, String[] symbols) {
        CalculateBtcPLFragment fragment = new CalculateBtcPLFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        bundle.putString("symbol", symbol);
        bundle.putString("lever", lever);
        bundle.putStringArray("symbols", symbols);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculate_pl, container, false);
        mContext = view.getContext();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        listener();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            KeyboardUtils.hideKeyboard(openCount);
            KeyboardUtils.hideKeyboard(openPrice);
            KeyboardUtils.hideKeyboard(closePrice);

            if (symbolDialog.isShowing()) {
                symbolDialog.dismiss();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkGo.getInstance().cancelTag(this);
        unbinder.unbind();
    }

    private void init() {
        //合约Dialog配置
        symbolAdapter = new TextSelectAdapter("合约");
        symbolDialog = new BottomSheetDialog(mContext, R.style.CoinBene_BottomSheet);
        symbolDialog.setContentView(R.layout.common_dialog_text_select);
        symbolRecycler = symbolDialog.findViewById(R.id.text_select_RecyclerView);
        symbolCancel = symbolDialog.findViewById(R.id.text_select_cancel);
        symbolRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        symbolRecycler.setAdapter(symbolAdapter);

        //设置默认显示
        if (getArguments() != null) {
            symbols = getArguments().getStringArray("symbols");
            symbolAdapter.setData(symbols);

            currentSymbol = getArguments().getString("symbol");
            currentLevel = getArguments().getString("lever");

            mChooseLevelView.initParams(currentSymbol, currentLevel);

            mChooseLevelView.setLevelChangeListener(level -> currentLevel = String.valueOf(level));
        }


        setInputPrecision(contractSpinner.getText().toString());

        setDirection(mDirection);

        setCalculateView(closePrice, openPrice, openCount);

    }


    private void listener() {
        symbolCancel.setOnClickListener(v -> symbolDialog.dismiss());
        contractSpinner.setOnClickListener(v -> showContractDialog());
        contractSpinnerImg.setOnClickListener(v -> showContractDialog());
        directionMore.setOnClickListener(v -> setDirection(Constants.DIRECTION_LONG));
        directionNull.setOnClickListener(v -> setDirection(Constants.DIRECTION_SHORT));

        symbolAdapter.setListener((tag, content, positon) -> {
            if (tag.equals(symbolAdapter.getTag())) {
                contractSpinner.setText(content);
                symbolDialog.dismiss();
                openPrice.setText("");
                closePrice.setText("");
                openCount.setText("");
                setInputPrecision(content);
                resetResult();

                currentSymbol = content;
                currentLevel = String.valueOf(ContractInfoController.getInstance().getCurContractLever(content));
                mChooseLevelView.initParams(currentSymbol, currentLevel);

                symbolAdapter.setData(symbols);
            }
        });

        openCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculateView(closePrice, openPrice, openCount);
            }
        });

        openPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                openPrice.removeTextChangedListener(this);
                PrecisionUtils.setPrecisionMinPriceChange(openPrice, s, precision,minPriceChange);
                openPrice.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculateView(closePrice, openPrice, openCount);
            }
        });

        closePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                closePrice.removeTextChangedListener(this);
                PrecisionUtils.setPrecisionMinPriceChange(closePrice, s, precision,minPriceChange);
                closePrice.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculateView(closePrice, openPrice, openCount);
            }
        });

    }

    /**
     * @param type 根据合约类型控制输入精度
     */
    private void setInputPrecision(String type) {
        ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(type);
        precision = table.precision;
        minPriceChange = table.minPriceChange;

    }

    private void showContractDialog() {
        if (!symbolDialog.isShowing()) {
            symbolDialog.show();
        }
    }

    /**
     * @param direction 设置选中方向
     */
    private void setDirection(String direction) {

        mroeSelectImg.setVisibility(View.GONE);
        nullSelectImg.setVisibility(View.GONE);
        directionMore.setBackgroundResource(R.drawable.shape_rim);
        directionNull.setBackgroundResource(R.drawable.shape_rim);
        directionMore.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));
        directionNull.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));

        if (direction.equals(Constants.DIRECTION_LONG)) {
            //选中多
            directionMore.setTextColor(Color.parseColor("#3b7bfd"));
            directionMore.setBackgroundResource(R.drawable.shape_rim_blue);
            mroeSelectImg.setVisibility(View.VISIBLE);
        } else {
            //选中
            directionNull.setTextColor(Color.parseColor("#3b7bfd"));
            directionNull.setBackgroundResource(R.drawable.shape_rim_blue);
            nullSelectImg.setVisibility(View.VISIBLE);
        }

        mDirection = direction;

    }

    private void resetResult() {
        bailResult.setText("0.0000 BTC");
        PLResult.setText("0.0000 BTC");
        rateResult.setText("0.00 %");
    }


    private void calculate() {

        HttpParams params = new HttpParams();
        params.put("direction", mDirection);
        params.put("symbol", contractSpinner.getText().toString());
        params.put("leverage", currentLevel.replace("X",""));
        params.put("openPrice", openPrice.getText().toString());
        params.put("closePrice", closePrice.getText().toString());
        params.put("quantity", openCount.getText().toString());

        OkGo.<CalculatePLModel>get(Constants.CALCULATOR_CALCULATE_PL).params(params).tag(this).execute(new NewJsonSubCallBack<CalculatePLModel>() {
            @Override
            public void onSuc(Response<CalculatePLModel> response) {
                CalculatePLModel.DataBean data = response.body().getData();
                if (data != null) {
                    bailResult.setText(String.format("%s BTC", data.getMargin()));
                    PLResult.setText(String.format("%s BTC", data.getProfit()));
                    rateResult.setText(BigDecimalUtils.toPercentage(data.getRoe(), "0.00%"));
                }
            }

            @Override
            public void onE(Response<CalculatePLModel> response) {

            }
        });
    }

    private void setCalculateView(EditText... edit) {
        int flag = 0;
        for (EditText editText : edit) {
            if (!TextUtils.isEmpty(editText.getText().toString())) {
                flag++;
            }
        }

        if (flag == edit.length) {
            isCalculate = true;
        } else {
            isCalculate = false;
        }

        if (isCalculate) {
            calculate.setBackgroundResource(R.drawable.confirm_btn_bg);
            calculate.setOnClickListener(v -> calculate());
        } else {
            calculate.setBackgroundColor(ResourceProvider.getColor(getContext(),R.color.color_button_disable));
            calculate.setOnClickListener(null);
        }

    }
}
