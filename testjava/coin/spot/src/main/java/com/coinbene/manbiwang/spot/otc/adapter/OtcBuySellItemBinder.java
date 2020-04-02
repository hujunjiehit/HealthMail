package com.coinbene.manbiwang.spot.otc.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.OtcAdListModel;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.wrapper.LoadMoreWrapper;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.otc.OtcPayTypeAdapter;
import com.coinbene.manbiwang.spot.otc.dialog.OtcPayOrderListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtcBuySellItemBinder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OtcAdListModel.DataBean.ListBean> adItemList;
    private OtcAdListModel.DataBean.ListBean lastAdItem;
    private LoadMoreWrapper loadMoreWrapper;
    private String balance;
    private OtcPayOrderListener payOrderListener;
    private boolean listenerFlag = false;//EditText关联是否更新
    private boolean isOverSize = false;
    private boolean isRedRise;

    public void setItems(List<OtcAdListModel.DataBean.ListBean> items) {
        this.adItemList = items;
    }


    public void appendItems(List<OtcAdListModel.DataBean.ListBean> items) {
        if (adItemList == null) {
            adItemList = new ArrayList<>();
        }
        this.adItemList.addAll(items);
    }

    public boolean isRedRise() {
        return isRedRise;
    }

    public void setRedRise(boolean redRise) {
        isRedRise = redRise;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spot_otc_buy_item, parent, false);
        isRedRise = SwitchUtils.isRedRise();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
//        viewholder.setIsRecyclable(false);
        ViewHolder holder = (ViewHolder) viewholder;
        OtcAdListModel.DataBean.ListBean adItem = adItemList.get(position);
        holder.tv_name.setText(adItem.getName());
        holder.tv_number_balue.setText(String.format("%s  %s", adItem.getStock(), adItem.getAsset()));
        holder.tv_limit_value.setText(String.format("%s%s-%s", adItem.getCurrencySymbol(), adItem.getMinOrder(), adItem.getMaxOrder()));
        holder.tv_unit_price_value.setText(String.format("%s%s", adItem.getCurrencySymbol(), adItem.getPrice()));
        if (!TextUtils.isEmpty(adItem.getName())&&adItem.getName().length()>0)
            holder.merchantIcon.setText(adItem.getName().substring(0, 1));
        holder.tvOrderAmount.setText(adItem.getFinishOrderCount());
        holder.tvOrderRate.setText(String.format("%s%%", adItem.getFinishOrderRate()));


        if (adItem.getAdType() == 2) {
            if (isRedRise) {
                holder.btn_buy_or_sell.setBackground(CBRepository.getContext().getResources().getDrawable((R.drawable.shape_gradient_night)));

            } else {
                holder.btn_buy_or_sell.setBackground(CBRepository.getContext().getResources().getDrawable((R.drawable.shape_gradient_day)));

            }
            holder.btn_buy_or_sell.setText(R.string.trade_buy);
            holder.et_usdt.setHint(R.string.please_input_number);
            holder.et_cny.setHint(R.string.please_input_buy_cny);
        } else {

            if (isRedRise) {
                holder.btn_buy_or_sell.setBackground(CBRepository.getContext().getResources().getDrawable((R.drawable.shape_gradient_day)));

            } else {
                holder.btn_buy_or_sell.setBackground(CBRepository.getContext().getResources().getDrawable((R.drawable.shape_gradient_night)));

            }
            holder.btn_buy_or_sell.setText(R.string.trade_sell);
            holder.et_usdt.setHint(R.string.please_input_sell_number);
            holder.et_cny.setHint(R.string.please_input_sell_price);
        }

        if (adItem.isInputVisible()) {
            holder.rl_submit.setVisibility(View.VISIBLE);
            holder.rl_buy_or_sell.setVisibility(View.GONE);
            listenerFlag = true;
            holder.et_cny.setText(TextUtils.isEmpty(lastAdItem.getInputCny()) ? "" : lastAdItem.getInputCny());
            holder.et_usdt.setText(TextUtils.isEmpty(lastAdItem.getInputUsdt()) ? "" : lastAdItem.getInputUsdt());
            listenerFlag = false;
        } else {
            holder.rl_submit.setVisibility(View.GONE);
            holder.rl_buy_or_sell.setVisibility(View.VISIBLE);
            listenerFlag = false;
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.rl_pay_type.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.rl_pay_type.setLayoutManager(linearLayoutManager);

        OtcPayTypeAdapter adapter = new OtcPayTypeAdapter();
        holder.rl_pay_type.setAdapter(adapter);
        adapter.setLists(adItem.getPayTypes());

        holder.btn_buy_or_sell.setOnClickListener(v -> {
            payOrder(holder,adItem);
        });
        holder.tv_cancel.setOnClickListener(v -> {

            if (payOrderListener != null) {
                cancel(holder);
            }


        });
        holder.tv_all_cny.setOnClickListener(v -> {
        });
        holder.tv_all_usdt.setOnClickListener(v -> {
        });

        holder.btn_submit.setTag(adItem.getPrice());

        holder.btn_submit.setOnClickListener(v -> {

            sumitOrder(holder,adItem);
        });

        if (adItem.isInputVisible()) {

            if (holder.et_usdt.getTag() == null || (int) holder.et_usdt.getTag() != position)
                holder.et_usdt.addTextChangedListener(new TextWatcher() {
                    private String lastInput = "";

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().contains(".")) {
                            if (s.length() - 1 - s.toString().indexOf(".") > 6) {
                                s = s.toString().subSequence(0,
                                        s.toString().indexOf(".") + (6 + 1));
                                holder.et_usdt.setText(s);
                                holder.et_usdt.setSelection(s.toString().length());
                            }
                        }
                        if (s.toString().trim().equals(".")) {
                            s = "0" + s;
                            holder.et_usdt.setText(s);
                            holder.et_usdt.setSelection(2);
                        }

                        if (s.toString().startsWith("0")
                                && s.toString().trim().length() > 1) {
                            if (!s.toString().substring(1, 2).equals(".") && !s.toString().substring(1, 2).equals(",")) {
                                holder.et_usdt.setText(s.subSequence(0, 1));
                                holder.et_usdt.setSelection(1);
                                return;
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!listenerFlag) {
                            if (lastInput.equals(s.toString()) || s.toString().equals(".") || s.toString().equals(",")) {
                                return;
                            }
                            lastInput = s.toString();
                            String price;
                            if (holder.btn_submit.getTag() == null) {
                                price = "";
                            } else {
                                price = String.valueOf(holder.btn_submit.getTag());
                            }
                            if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(price)) {
                                listenerFlag = true;
                                String cny = PrecisionUtils.getRoundDown(BigDecimalUtils.multiplyToStr(s.toString(), price), Constants.cnyPrecision);
                                holder.et_cny.setText(cny);
                                listenerFlag = false;
                                adItem.setInputUsdt(s.toString());
                                adItem.setInputCny(cny);
                            } else if (TextUtils.isEmpty(s.toString())) {
                                listenerFlag = true;
                                holder.et_cny.setText("");
                                listenerFlag = false;
                                adItem.setInputUsdt("");
                                adItem.setInputCny("");
                            }
                        }
                    }
                });
            holder.et_usdt.setTag(position);

            if (holder.et_cny.getTag() == null || (int) holder.et_cny.getTag() != position)
                holder.et_cny.addTextChangedListener(new TextWatcher() {
                    private String lastInput = "";

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().contains(".")) {
                            if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                                s = s.toString().subSequence(0,
                                        s.toString().indexOf(".") + (2 + 1));
                                isOverSize = true;
                                holder.et_cny.setText(s);
                                holder.et_cny.setSelection(s.toString().length());
                            }
                        }
                        if (s.toString().trim().equals(".")) {
                            s = "0" + s;
                            holder.et_cny.setText(s);
                            holder.et_cny.setSelection(2);
                        }
                        if (s.toString().startsWith("0")
                                && s.toString().trim().length() > 1) {
                            if (!s.toString().substring(1, 2).equals(".") && !s.toString().substring(1, 2).equals(",")) {
                                holder.et_cny.setText(s.subSequence(0, 1));
                                holder.et_cny.setSelection(1);
                                return;
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!listenerFlag) {
                            if (lastInput.equals(s.toString()) || s.toString().equals(".") || s.toString().equals(",")) {
                                return;
                            }
                            lastInput = s.toString();

                            String price;
                            if (holder.btn_submit.getTag() == null) {
                                price = "";
                            } else {
                                price = String.valueOf(holder.btn_submit.getTag());
                            }

                            if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(price)) {
                                listenerFlag = true;
                                String usdt = PrecisionUtils.getRoundDown(BigDecimalUtils.divideToStr(s.toString(), price, 6), Constants.usdtPrecision);
                                if (s.toString().contains(".")) {
                                    if (s.length() - 1 - s.toString().indexOf(".") > 2 || isOverSize) {
                                    } else {
                                        holder.et_usdt.setText(usdt);
                                        adItem.setInputUsdt(usdt);
                                    }
                                } else {
                                    holder.et_usdt.setText(usdt);
                                    adItem.setInputUsdt(usdt);
                                }
                                listenerFlag = false;
                                isOverSize = false;
                                adItem.setInputCny(s.toString());
                            } else if (TextUtils.isEmpty(s.toString())) {
                                listenerFlag = true;
                                holder.et_usdt.setText("");
                                listenerFlag = false;
                                adItem.setInputCny("");
                                adItem.setInputUsdt("");
                            }

                        }
                    }
                });
            holder.et_cny.setTag(position);
        }
    }

    @NeedLogin(jump = true)
    private void sumitOrder(ViewHolder holder, OtcAdListModel.DataBean.ListBean adItem) {
        if (TextUtils.isEmpty(holder.et_usdt.getText().toString())) {
            ToastUtil.show(R.string.please_input_usdt);
            return;
        }
        if (TextUtils.isEmpty(holder.et_cny.getText().toString())) {
            ToastUtil.show(R.string.please_input_cny);
            return;
        }
        if (payOrderListener != null) {
            KeyboardUtils.hideKeyboard(holder.btn_submit);
            payOrderListener.payOrder(String.valueOf(adItem.getAdId()), holder.et_cny.getText().toString(), adItem.getAdType());
        }
    }

    @NeedLogin(jump = true)
    private void cancel(ViewHolder holder) {
        KeyboardUtils.hideKeyboard(holder.btn_submit);
    }

    @NeedLogin(jump = true)
    public void payOrder(ViewHolder holder, OtcAdListModel.DataBean.ListBean adItem) {
        if (payOrderListener != null) {
            KeyboardUtils.hideKeyboard(holder.btn_submit);
            payOrderListener.onClickBuyOrSell(adItem);
        }
    }


    private void clickAll(OtcAdListModel.DataBean.ListBean adItem, ViewHolder holder) {
        if (adItem.getAdType() == 2) {
            String[] strings = CalculationUtils.calculateOtcBuyAll(adItem.getStock(), adItem.getMinOrder(), adItem.getMaxOrder(), adItem.getPrice());
            if (strings != null && !TextUtils.isEmpty(strings[0]) && !TextUtils.isEmpty(strings[1])) {
                listenerFlag = true;
                holder.et_usdt.setText(strings[0]);
                holder.et_cny.setText(strings[1]);
                listenerFlag = false;
            }
        } else {
            String[] strings = CalculationUtils.calculateOtcSellAll(adItem.getStock(), adItem.getMinOrder(), adItem.getMaxOrder(), adItem.getPrice(), balance);
            if (strings != null && !TextUtils.isEmpty(strings[0]) && !TextUtils.isEmpty(strings[1])) {
                listenerFlag = true;
                holder.et_usdt.setText(strings[0]);
                holder.et_cny.setText(strings[1]);
                listenerFlag = false;
            } else if (strings != null && !TextUtils.isEmpty(strings[0]) && TextUtils.isEmpty(strings[1])) {
                ToastUtil.show(R.string.available_balance_Insufficient);
            }
        }
    }

    public OtcPayOrderListener getPayOrderListener() {
        return payOrderListener;
    }

    public void setPayOrderListener(OtcPayOrderListener payOrderListener) {
        this.payOrderListener = payOrderListener;
    }

    @Override
    public int getItemCount() {
        return adItemList == null || adItemList.size() == 0 ? 0 : adItemList.size();
    }

    public void setLoadAdapter(LoadMoreWrapper loadMoreWrapper) {
        this.loadMoreWrapper = loadMoreWrapper;
    }

    public void setAvlBalance(String balance) {
        this.balance = balance;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_name)
        TextView tv_name;

        @BindView(R2.id.item_merchant_icon)
        TextView merchantIcon;

        @BindView(R2.id.tv_number_balue)
        TextView tv_number_balue;
        @BindView(R2.id.tv_limit_value)
        TextView tv_limit_value;
        @BindView(R2.id.tv_all_usdt)
        TextView tv_all_usdt;
        @BindView(R2.id.tv_all_cny)
        TextView tv_all_cny;

        @BindView(R2.id.tv_unit_price_value)
        TextView tv_unit_price_value;
        @BindView(R2.id.iv_bank)
        ImageView iv_bank;
        @BindView(R2.id.iv_alipay)
        ImageView iv_alipay;
        @BindView(R2.id.iv_wechat)
        ImageView iv_wechat;


        @BindView(R2.id.btn_buy_or_sell)
        Button btn_buy_or_sell;
        @BindView(R2.id.et_usdt)
        EditText et_usdt;
        @BindView(R2.id.et_cny)
        EditText et_cny;


        @BindView(R2.id.tv_order_rate)
        TextView tvOrderRate;
        @BindView(R2.id.tv_order_amount)
        TextView tvOrderAmount;
        @BindView(R2.id.tv_cancel)
        TextView tv_cancel;
        @BindView(R2.id.btn_submit)
        Button btn_submit;
        @BindView(R2.id.rl_submit)
        RelativeLayout rl_submit;

        @BindView(R2.id.rl_buy_or_sell)
        RelativeLayout rl_buy_or_sell;
        @BindView(R2.id.rl_pay_type)
        RecyclerView rl_pay_type;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            tv_name.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.roboto_medium));

        }
    }
}
