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
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.activities.adapter.TextSelectAdapter;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractbtc.activity.view.ChooseLevelView;
import com.coinbene.manbiwang.model.http.CalculateTPModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ding
 * 2019-05-14
 * com.coinbene.manbiwang.modules.trade.contract
 *
 *  目标价计算
 */
public class CalculateBtcTPFragment extends BaseFragment {
    @BindView(R2.id.contarct_Spinner)
    TextView contarctSpinner;
    @BindView(R2.id.contract_spinner_img)
    ImageView contractSpinnerImg;
    @BindView(R2.id.direction_More)
    TextView directionMore;
    @BindView(R2.id.mroe_select)
    ImageView mroeSelect;
    @BindView(R2.id.direction_null)
    TextView directionNull;
    @BindView(R2.id.null_Select)
    ImageView nullSelect;
    @BindView(R2.id.open_EditText)
    EditText openEdit;
    @BindView(R2.id.amount_EditText)
    EditText amountEdit;
    @BindView(R2.id.target_EditText)
    EditText targetEdit;
    @BindView(R2.id.start_calculate)
    Button calculate;
    @BindView(R2.id.target_Result)
    TextView targetResult;
    Unbinder unbinder;
    @BindView(R2.id.profitGroup)
    Group profitGroup;
    @BindView(R2.id.rateGroup)
    Group rateGroup;
    @BindView(R2.id.rate_open_EditText)
    EditText editOpenRate;
    @BindView(R2.id.rate_EditText)
    EditText editPercent;
    @BindView(R2.id.expectedRate)
    TextView expectedRate;
    @BindView(R2.id.rate_indicator)
    View rateIndicator;
    @BindView(R2.id.expectedProfit)
    TextView expectedProfit;
    @BindView(R2.id.profit_indicator)
    View profitIndicator;
    @BindView(R2.id.choose_level_view)
    ChooseLevelView mChooseLevelView;

    private Context mContext;


    /**
     * 目标类型
     */
    public final int TARGET_RATE = 0;
    public final int TARGET_PROFIT = 1;

    /**
     * 当前选中方向
     */
    private String mDirection = Constants.DIRECTION_LONG;
    private int mTarget = TARGET_RATE;

    private String[] symbols;

    //合约Dialog
    private TextSelectAdapter symbolAdapter;
    private BottomSheetDialog symbolDialog;
    private RecyclerView symbolRecycler;
    private View symbolCancel;
    private boolean isCalculate = false;
    /**
     * 根据当前合约类型选择精度默认为1（BTC）
     */
    private int precision = 1;
    private String minPriceChange;

    private String currentLevel;
    private String currentSymbol;

    public static CalculateBtcTPFragment newInstance(String symbol, String lever, String[] symbols) {
        CalculateBtcTPFragment fragment = new CalculateBtcTPFragment();
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
        View view = inflater.inflate(R.layout.fragment_calculate_tp, container, false);
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
    public void onDestroyView() {
        super.onDestroyView();
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

        setInputPrecision(contarctSpinner.getText().toString());
        setDirection(mDirection);
        setTargetType(mTarget);

        setCalculateView(editOpenRate, editPercent);
    }

    private void listener() {
        directionMore.setOnClickListener(v -> setDirection(Constants.DIRECTION_LONG));
        directionNull.setOnClickListener(v -> setDirection(Constants.DIRECTION_SHORT));
        contarctSpinner.setOnClickListener(v -> showSymbolDialog());
        contractSpinnerImg.setOnClickListener(v -> showSymbolDialog());
        symbolCancel.setOnClickListener(v -> symbolDialog.dismiss());
        expectedProfit.setOnClickListener(v -> setTargetType(TARGET_PROFIT));
        expectedRate.setOnClickListener(v -> setTargetType(TARGET_RATE));


        symbolAdapter.setListener((tag, content, positon) -> {
            if (tag.equals(symbolAdapter.getTag())) {
                contarctSpinner.setText(content);
                setInputPrecision(content);
                symbolDialog.dismiss();
                clearInput();
                resetResult();

                currentSymbol = content;
                currentLevel = String.valueOf(ContractInfoController.getInstance().getCurContractLever(content));
                mChooseLevelView.initParams(currentSymbol, currentLevel);

                symbolAdapter.setData(symbols);
            }
        });

        //按预期收益配置
        openEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                openEdit.removeTextChangedListener(this);
                PrecisionUtils.setPrecisionMinPriceChange(openEdit, s, precision,minPriceChange);
                openEdit.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculateView(openEdit, amountEdit, targetEdit);
            }
        });

        amountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculateView(openEdit, amountEdit, targetEdit);
            }
        });

        targetEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PrecisionUtils.setPrecision(targetEdit, s, 4);
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculateView(openEdit, amountEdit, targetEdit);
            }
        });

        //按收益率配置
        editOpenRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editOpenRate.removeTextChangedListener(this);
                PrecisionUtils.setPrecisionMinPriceChange(editOpenRate, s, precision,minPriceChange);
                editOpenRate.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculateView(editOpenRate, editPercent);
            }
        });

        editPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setCalculateView(editOpenRate, editPercent);
            }
        });

    }

    //展示选择合约Dialog
    private void showSymbolDialog() {
        if (!symbolDialog.isShowing() && symbolDialog != null) {
            symbolDialog.show();
        }
    }

    /**
     * @param direction 设置选中方向
     */
    private void setDirection(String direction) {

        mroeSelect.setVisibility(View.GONE);
        nullSelect.setVisibility(View.GONE);
        directionMore.setBackgroundResource(R.drawable.shape_rim);
        directionNull.setBackgroundResource(R.drawable.shape_rim);
        directionMore.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));
        directionNull.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));

        if (direction.equals(Constants.DIRECTION_LONG)) {
            directionMore.setTextColor(Color.parseColor("#3b7bfd"));
            directionMore.setBackgroundResource(R.drawable.shape_rim_blue);
            mroeSelect.setVisibility(View.VISIBLE);
        } else {
            directionNull.setTextColor(Color.parseColor("#3b7bfd"));
            directionNull.setBackgroundResource(R.drawable.shape_rim_blue);
            nullSelect.setVisibility(View.VISIBLE);
        }

        mDirection = direction;

    }

    /**
     * @param target 设置目标类型
     */
    private void setTargetType(int target) {

        if (mTarget == target) {
            return;
        }

        clearInput();
        isCalculate = false;
        rateGroup.setVisibility(View.GONE);
        profitGroup.setVisibility(View.GONE);
        rateIndicator.setVisibility(View.GONE);
        profitIndicator.setVisibility(View.GONE);
        expectedRate.setTextColor(Color.parseColor("#6b7d8c"));
        expectedProfit.setTextColor(Color.parseColor("#6b7d8c"));

        if (target == TARGET_RATE) {
            expectedRate.setTextColor(Color.parseColor("#3b7bfd"));
            rateIndicator.setVisibility(View.VISIBLE);
            rateGroup.setVisibility(View.VISIBLE);
            setCalculateView(editOpenRate, editPercent);
        } else {
            expectedProfit.setTextColor(Color.parseColor("#3b7bfd"));
            profitIndicator.setVisibility(View.VISIBLE);
            profitGroup.setVisibility(View.VISIBLE);
            setCalculateView(openEdit, amountEdit, targetEdit);
        }

        mTarget = target;
    }

    /**
     * 重置计算结果
     */
    private void resetResult() {
        targetResult.setText("0 USDT");
    }

    /**
     * 执行计算
     */
    private void executeCalculate() {
        if (mTarget == TARGET_RATE) {
            getResultByRate();
        } else {
            getResultByProfit();
        }
    }

    /**
     * @param type 根据合约类型控制输入精度
     */
    private void setInputPrecision(String type) {
        ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(type);
        precision = table.precision;
        minPriceChange = table.minPriceChange;

    }

    /**
     * 按收益率计算
     */
    private void getResultByRate() {

        if (TextUtils.isEmpty(editOpenRate.getText().toString())) {
            return;
        }

        if (TextUtils.isEmpty(editPercent.getText().toString())) {
            return;
        }

        HttpParams params = new HttpParams();
        params.put("symbol", contarctSpinner.getText().toString());
        params.put("direction", mDirection);
        params.put("leverage", currentLevel.replace("X",""));
        params.put("openPrice", editOpenRate.getText().toString());
        params.put("roe", editPercent.getText().toString());

        OkGo.<CalculateTPModel>get(Constants.CALCULATOR_FC_RATE).tag(this).params(params).execute(new NewJsonSubCallBack<CalculateTPModel>() {
            @Override
            public void onSuc(Response<CalculateTPModel> response) {

                CalculateTPModel.DataBean data = response.body().getData();

                if (data == null && TextUtils.isEmpty(data.getTargetPrice())) {
                    return;
                }

                if (BigDecimalUtils.isLessThan(data.getTargetPrice(), "0")) {
                    targetResult.setText("--");
                    return;
                }

                targetResult.setText(String.format("%s USDT", data.getTargetPrice()));
            }

            @Override
            public void onE(Response<CalculateTPModel> response) {

            }
        });

    }

    /**
     * 清空输入
     */
    private void clearInput() {
        openEdit.setText("");
        editPercent.setText("");
        amountEdit.setText("");
        targetEdit.setText("");
        editOpenRate.setText("");
        resetResult();
    }

    /**
     * 按预计收益计算
     */
    private void getResultByProfit() {

        HttpParams params = new HttpParams();
        params.put("symbol", contarctSpinner.getText().toString());
        params.put("direction", mDirection);
        params.put("quantity", amountEdit.getText().toString());
        params.put("openPrice", openEdit.getText().toString());
        params.put("profit", targetEdit.getText().toString());

        OkGo.<CalculateTPModel>get(Constants.CALCULATOR_FC_PROFIT).tag(this).params(params).execute(new NewJsonSubCallBack<CalculateTPModel>() {
            @Override
            public void onSuc(Response<CalculateTPModel> response) {

                CalculateTPModel.DataBean data = response.body().getData();

                if (data == null && TextUtils.isEmpty(data.getTargetPrice())) {
                    return;
                }

                if (BigDecimalUtils.isLessThan(data.getTargetPrice(), "0")) {
                    targetResult.setText("--");
                    return;
                }

                targetResult.setText(String.format("%s USDT", data.getTargetPrice()));
            }

            @Override
            public void onE(Response<CalculateTPModel> response) {

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
            calculate.setOnClickListener(v -> executeCalculate());
        } else {
            calculate.setBackgroundColor(ResourceProvider.getColor(getContext(),R.color.color_button_disable));
            calculate.setOnClickListener(null);
        }

    }


}
