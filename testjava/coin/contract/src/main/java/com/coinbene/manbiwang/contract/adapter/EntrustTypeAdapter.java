package com.coinbene.manbiwang.contract.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.BaseViewHolder;
import com.coinbene.manbiwang.model.http.BottomSelectModel;
import com.coinbene.manbiwang.contract.R;

import java.util.List;


/**
 * 委托类型选择
 */
public class EntrustTypeAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<BottomSelectModel> list;
    private OnItemClickListener listener;
    private String currentSelect;
    private int selectColor, defaultColor;

    public EntrustTypeAdapter(String currentSelect) {
        this.currentSelect = currentSelect;
    }

    public void setCurrentSelect(String currentSelect) {
        this.currentSelect = currentSelect;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        selectColor = R.color.res_blue;
        defaultColor = R.color.res_textColor_3;
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrust_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        BottomSelectModel item = list.get(position);
        if (item == null || TextUtils.isEmpty(item.getTypeName())) {
            return;
        }
        if (!TextUtils.isEmpty(currentSelect) && currentSelect.equals(item.getTypeName())) {
            holder.setTextColor(R.id.tv_type, selectColor);
        } else {
            holder.setTextColor(R.id.tv_type, defaultColor);
        }
        holder.setText(R.id.tv_type, item.getTypeName());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.itemClick(item);
            }

        });

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setItem(List<BottomSelectModel> list) {
        if (list != null) {
            this.list = list;
        }
        notifyDataSetChanged();
    }


    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void itemClick(BottomSelectModel item);
    }

}
