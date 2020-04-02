package com.coinbene.manbiwang.spot.otc.adapter;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.manbiwang.model.http.OrderListModel;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OtcOrderListBinder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Unbinder unbind;
    private List<OrderListModel.DataBean.ResultBean> adItemList;
    private ItemClickListener itemClickListener;
    private CountDownTimer timer;
    //    private String leftTime;
    private boolean isRedRise;

    public void setItems(List<OrderListModel.DataBean.ResultBean> items) {
        this.adItemList = items;
    }


    public void appendItems(List<OrderListModel.DataBean.ResultBean> items) {
        if (adItemList == null) {
            adItemList = new ArrayList<>();
        }
        this.adItemList.addAll(items);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spot_otc_order_list_item, parent, false);
        isRedRise = SwitchUtils.isRedRise();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        ViewHolder holder = (ViewHolder) viewholder;
        OrderListModel.DataBean.ResultBean resultBean = adItemList.get(position);
        if (resultBean != null) {
            if (resultBean.getType() == 1) {
                holder.tv_order_direction.setText(R.string.trade_buy);
                if (isRedRise)
                    holder.tv_order_direction.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_red));
                else
                    holder.tv_order_direction.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_green));

            } else if (resultBean.getType() == 2) {
                holder.tv_order_direction.setText(R.string.trade_sell);
                if (isRedRise)
                    holder.tv_order_direction.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_green));
                else
                    holder.tv_order_direction.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_red));

            }
            holder.tv_order_asset.setText(resultBean.getPairName());
            holder.tv_order_create_time.setText(resultBean.getCreateTime());
            if (resultBean.getType() == 1) {//买单
                if (resultBean.getStatus() == 1) {
                    holder.tv_order_status.setText(R.string.wait_payment);
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_assistColor_21));
                } else if (resultBean.getStatus() == 2) {
                    holder.tv_order_status.setText(R.string.paid);
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_assistColor_21));
                } else if (resultBean.getStatus() == 3) {
                    holder.tv_order_status.setText(R.string.order_completed);
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_textColor_3));
                } else if (resultBean.getStatus() == 4) {
                    holder.tv_order_status.setText(R.string.order_cancelled);
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_textColor_3));
                } else if (resultBean.getStatus() == 5) {
                    holder.tv_order_status.setText(R.string.time_out_pay);
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_textColor_3));
                }else if (resultBean.getStatus() == 6) {
                    holder.tv_order_status.setText(R.string.order_freezing);
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_red));
                }

            } else {//卖单
                if (resultBean.getStatus() == 1) {
                    holder.tv_order_status.setText(CBRepository.getContext().getText(R.string.wait_receivables));
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_assistColor_21));
                }else  if (resultBean.getStatus() == 2) {
                    holder.tv_order_status.setText(CBRepository.getContext().getText(R.string.wait_sure));
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_assistColor_21));
                }else  if (resultBean.getStatus() == 3) {
                    holder.tv_order_status.setText(CBRepository.getContext().getText(R.string.order_completed));
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_textColor_3));
                }else  if (resultBean.getStatus() == 4) {
                    holder.tv_order_status.setText(CBRepository.getContext().getText(R.string.order_cancelled));
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_textColor_3));
                }else  if (resultBean.getStatus() == 5) {
                    holder.tv_order_status.setText(CBRepository.getContext().getText(R.string.time_out_pay));
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_textColor_3));
                }else  if (resultBean.getStatus() == 6) {
                    holder.tv_order_status.setText(CBRepository.getContext().getText(R.string.order_freezing));
                    holder.tv_order_status.setTextColor(CBRepository.getContext().getResources().getColor(R.color.res_red));
                }

            }
            holder.tv_order_unit_price.setText(String.format("%s/%s", CBRepository.getContext().getResources().getString(R.string.order_unit_price), resultBean.getMoneyTrade()));
            holder.tv_order_unit_price_value.setText(resultBean.getUnitPrice());
            holder.tv_order_num_value.setText(resultBean.getQuantity());
            holder.tv_order_all_price_value.setText(resultBean.getTotalMoney());
            holder.tv_order_all_price.setText(String.format("%s/%s", CBRepository.getContext().getResources().getString(R.string.totle_price), resultBean.getMoneyTrade()));
        }
        holder.root_layout.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.click(resultBean.getOrderId());
            }
        });


    }

    private void initTimer(long time, ViewHolder holder) {
        if (timer == null) {
            timer = new CountDownTimer(time, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int second = (int) (millisUntilFinished / 1000);
                    holder.tv_timer.setText(CBRepository.getContext().getString(R.string.time_remaining) + TimeUtils.secondToMinSecond(CBRepository.getContext(), second));
                }

                @Override
                public void onFinish() {
                    holder.tv_timer.setText(String.format("%s0%s", CBRepository.getContext().getString(R.string.time_remaining), CBRepository.getContext().getString(R.string.seconds)));
                }
            };
            timer.start();
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        if (adItemList == null || adItemList.size() == 0) {
            return 0;
        } else
            return adItemList.size();
    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_order_direction)
        TextView tv_order_direction;
        @BindView(R2.id.tv_order_asset)
        TextView tv_order_asset;
        @BindView(R2.id.tv_order_create_time)
        TextView tv_order_create_time;
        @BindView(R2.id.tv_order_status)
        TextView tv_order_status;
        @BindView(R2.id.tv_order_unit_price_value)
        TextView tv_order_unit_price_value;
        @BindView(R2.id.tv_order_unit_price)
        TextView tv_order_unit_price;
        @BindView(R2.id.tv_order_num_value)
        TextView tv_order_num_value;
        @BindView(R2.id.tv_order_all_price_value)
        TextView tv_order_all_price_value;
        @BindView(R2.id.tv_order_all_price)
        TextView tv_order_all_price;
        @BindView(R2.id.tv_order_status_disc)
        TextView tv_order_status_disc;
        @BindView(R2.id.tv_timer)
        TextView tv_timer;
        @BindView(R2.id.root_layout)
        LinearLayout root_layout;


        ViewHolder(View view) {
            super(view);
            unbind = ButterKnife.bind(this, view);
        }
    }

    public interface ItemClickListener {
        void click(String adID);
    }
}
