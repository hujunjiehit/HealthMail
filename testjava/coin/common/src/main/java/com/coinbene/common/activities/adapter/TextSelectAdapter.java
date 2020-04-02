package com.coinbene.common.activities.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.R;
import com.coinbene.common.base.BaseViewHolder;

public class TextSelectAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    static final String TAG = "TextSelectAdapter";

    private String[] data;
    private SelectedListener listener;
    private Object tag;
    private int selectPosition = 0;


    public TextSelectAdapter(@NonNull Object tag) {
        this.tag = tag;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new BaseViewHolder(inflater.inflate(R.layout.common_item_text_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        if (this.selectPosition == position) {
            holder.setTextColor(R.id.select_textView, "#3b7bfd");
        } else {
            holder.setTextColor(R.id.select_textView, "#7c88a0");
        }

        holder.setText(R.id.select_textView, data[position]);

        holder.itemView.setOnClickListener(v -> {

            this.selectPosition = position;

            notifyDataSetChanged();

            if (listener != null) {
                this.listener.selected( tag, holder.getText(R.id.select_textView), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.length;
    }

    /**
     * @param data 设置选择数据
     */
    public void setData(String[] data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setListener(SelectedListener listener) {
        this.listener = listener;
    }

    public interface SelectedListener {
        void selected(Object tag, String content, int position);
    }

    /**
     * @param position 根据选中位置设置选中颜色
     */
    public void setSelectByPosition(@NonNull int position) {

        this.selectPosition = position;

        notifyDataSetChanged();


    }


    /**
     * @return 获得当前Adapter标签
     */
    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
