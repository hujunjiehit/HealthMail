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
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.contractbtc.activity.view.ChooseLevelView;
import com.coinbene.manbiwang.model.http.CalculateFCModel;
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
 * 强评价计算
 */
public class CalculateBtcFCFragment extends BaseFragment {

    @BindView(R2.id.contract_Spinner)
    TextView contractSpinner;
    @BindView(R2.id.contract_spinner_img)
    ImageView contractSpinnerImg;
    @BindView(R2.id.type_crossed)
    TextView typeCrossed;
    @BindView(R2.id.crossed_select_img)
    ImageView crossedSelectImg;
    @BindView(R2.id.type_fixed)
    TextView typeFixed;
    @BindView(R2.id.fixed_select_img)
    ImageView fixedSelectImg;
    @BindView(R2.id.direction_More)
    TextView directionMore;
    @BindView(R2.id.direction_Mroe_img)
    ImageView directionMroeImg;
    @BindView(R2.id.direction_null)
    TextView directionNull;
    @BindView(R2.id.direction_Null_img)
    ImageView directionNullImg;
    @BindView(R2.id.open_EditText)
    EditText openPrice;
    @BindView(R2.id.open_count_EditText)
    EditText openCount;
    @BindView(R2.id.over_EditText)
    EditText availableBalance;
    @BindView(R2.id.start_calculate)
    Button calculate;
    @BindView(R2.id.FC_Result)
    TextView FCResult;
    Unbinder unbinder;
    @BindView(R2.id.group_available)
    Group groupAvailable;
    @BindView(R2.id.choose_level_view)
    ChooseLevelView mChooseLevelView;

    private Context mContext;

    static final String TAG = "CalculateUsdtFCFragment";

    /**
     * 合约数据
     */
    private String[] symbols;

    /**
     * 当前方向
     */
    private String mDirection = Constants.DIRECTION_LONG;

    /**
     * 当前保证金模式
     */
    private String mMode = Constants.MODE_CROSSED;

    private TextSelectAdapter contractAdapter;
    private BottomSheetDialog symbolDialog;
    private RecyclerView contractRecycler;
    private View symbolCancel;
    private boolean isCalculate = false;

    /**
     * 根据当前合约类型选择精度默认为1（BTC）
     */
    private int precision = 1;
    private String minPriceChange;

    private String currentLevel;
    private String currentSymbol;

    public static CalculateBtcFCFragment newInstance(String symbol, String lever, String[] symbols) {
        CalculateBtcFCFragment fragment = new CalculateBtcFCFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        bundle.putString("symbol", symbol);
        bundle.putString("lever", lever);
        bundle.putStringArray("symbols", symbols);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculate_fc, container, false);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardUtils.hideKeyboard(openPrice);
        KeyboardUtils.hideKeyboard(openCount);
        KeyboardUtils.hideKeyboard(availableBalance);
        if (symbolDialog.isShowing()) {
            symbolDialog.dismiss();
        }
    }

    private void init() {
        //合约Dialog配置
        contractAdapter = new TextSelectAdapter("合约");
        symbolDialog = new BottomSheetDialog(mContext, R.style.CoinBene_BottomSheet);
        symbolDialog.setContentView(R.layout.common_dialog_text_select);
        contractRecycler = symbolDialog.findViewById(R.id.text_select_RecyclerView);
        symbolCancel = symbolDialog.findViewById(R.id.text_select_cancel);
        contractRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        contractRecycler.setAdapter(contractAdapter);


        //设置默认显示
        if (getArguments() != null) {
            symbols = getArguments().getStringArray("symbols");
            contractAdapter.setData(symbols);

            currentSymbol = getArguments().getString("symbol");
            currentLevel = getArguments().getString("lever");

            mChooseLevelView.initParams(currentSymbol, currentLevel);

            mChooseLevelView.setLevelChangeListener(level -> currentLevel = String.valueOf(level));
        }


        setCalculateView(openCount, openPrice, availableBalance);
        setInputPrecision(contractSpinner.getText().toString());
        setDirection(mDirection);
        setBailMode(mMode);
    }

    private void listener() {

        calculate.setOnClickListener(v -> calculate());
        symbolCancel.setOnClickListener(v -> symbolDialog.dismiss());
        contractSpinner.setOnClickListener(v -> showContractDialog());
        contractSpinnerImg.setOnClickListener(v -> showContractDialog());
        directionMore.setOnClickListener(v -> setDirection(Constants.DIRECTION_LONG));
        directionNull.setOnClickListener(v -> setDirection(Constants.DIRECTION_SHORT));
        typeFixed.setOnClickListener(v -> setBailMode(Constants.MODE_FIXED));
        typeCrossed.setOnClickListener(v -> setBailMode(Constants.MODE_CROSSED));

        contractAdapter.setListener((tag, content, positon) -> {
            if (tag.equals(contractAdapter.getTag())) {
                setInputPrecision(content);
                contractSpinner.setText(content);
                symbolDialog.dismiss();
                clearInput();
                resetResult();

                currentSymbol = content;
                currentLevel = String.valueOf(ContractInfoController.getInstance().getCurContractLever(content));
                mChooseLevelView.initParams(currentSymbol, currentLevel);
            }
        });

        openPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                openPrice.removeTextChangedListener(this);
                PrecisionUtils.setPrecisionMinPriceChange(openPrice, s, precision, minPriceChange);
                openPrice.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Constants.MODE_CROSSED.equals(mMode)) {
                    setCalculateView(openCount, openPrice, availableBalance);
                } else {
                    setCalculateView(openCount, openPrice);
                }
            }
        });

        availableBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                availableBalance.removeTextChangedListener(this);
                PrecisionUtils.setPrecision(availableBalance, s, 4);
                availableBalance.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Constants.MODE_CROSSED.equals(mMode)) {
                    setCalculateView(openCount, openPrice, availableBalance);
                } else {
                    setCalculateView(openCount, openPrice);
                }
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
                if (Constants.MODE_CROSSED.equals(mMode)) {
                    setCalculateView(openCount, openPrice, availableBalance);
                } else {
                    setCalculateView(openCount, openPrice);
                }
            }
        });
    }

    /**
     * 清空输入
     */
    private void clearInput() {
        openPrice.setText("");
        openCount.setText("");
        availableBalance.setText("");
    }


    /**
     * 开始计算了
     */
    private void calculate() {

        HttpParams params = new HttpParams();
        params.put("symbol", contractSpinner.getText().toString());
        params.put("direction", mDirection);
        params.put("leverage", currentLevel.replace("X",""));
        params.put("openPrice", openPrice.getText().toString());
        params.put("marginMode", mMode);
        params.put("quantity", openCount.getText().toString());
        params.put("balance", availableBalance.getText().toString());

        OkGo.<CalculateFCModel>get(Constants.CALCULATOR_CALCULATE_FC).params(params).tag(this).execute(new NewJsonSubCallBack<CalculateFCModel>() {
            @Override
            public void onSuc(Response<CalculateFCModel> response) {
                CalculateFCModel.DataBean data = response.body().getData();
                if (data != null) {

                    if (BigDecimalUtils.isLessThan(data.getLiquidationPrice(), "0")) {
                        FCResult.setText("--");
                        return;
                    }

                    FCResult.setText(String.format("%s USDT", data.getLiquidationPrice()));
                }
            }

            @Override
            public void onE(Response<CalculateFCModel> response) {

            }
        });

    }

    public void setDirection(String direction) {

        directionMroeImg.setVisibility(View.GONE);
        directionNullImg.setVisibility(View.GONE);
        directionMore.setBackgroundResource(R.drawable.shape_rim);
        directionNull.setBackgroundResource(R.drawable.shape_rim);
        directionMore.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));
        directionNull.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));

        if (Constants.DIRECTION_LONG.equals(direction)) {
            //选中多方向
            directionMroeImg.setVisibility(View.VISIBLE);
            directionMore.setBackgroundResource(R.drawable.shape_rim_blue);
            directionMore.setTextColor(Color.parseColor("#3b7bfd"));
        } else {
            //选中空方向
            directionNullImg.setVisibility(View.VISIBLE);
            directionNull.setBackgroundResource(R.drawable.shape_rim_blue);
            directionNull.setTextColor(Color.parseColor("#3b7bfd"));
        }

        mDirection = direction;
    }

    public void setBailMode(String mode) {

        typeCrossed.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));
        typeFixed.setTextColor(mContext.getResources().getColor(R.color.res_textColor_1));
        typeCrossed.setBackgroundResource(R.drawable.shape_rim);
        typeFixed.setBackgroundResource(R.drawable.shape_rim);
        crossedSelectImg.setVisibility(View.GONE);
        fixedSelectImg.setVisibility(View.GONE);

        if (Constants.MODE_CROSSED.equals(mode)) {
            //全仓
            typeCrossed.setTextColor(Color.parseColor("#3b7bfd"));
            typeCrossed.setBackgroundResource(R.drawable.shape_rim_blue);
            crossedSelectImg.setVisibility(View.VISIBLE);
            groupAvailable.setVisibility(View.VISIBLE);
        } else {
            //逐仓
            typeFixed.setTextColor(Color.parseColor("#3b7bfd"));
            typeFixed.setBackgroundResource(R.drawable.shape_rim_blue);
            fixedSelectImg.setVisibility(View.VISIBLE);
            groupAvailable.setVisibility(View.GONE);
        }

        clearInput();

        mMode = mode;

    }

    private void showContractDialog() {
        if (!symbolDialog.isShowing()) {
            symbolDialog.show();
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


    public void resetResult() {
        FCResult.setText("0 USDT");
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
