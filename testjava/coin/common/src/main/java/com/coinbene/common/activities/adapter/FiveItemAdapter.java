package com.coinbene.common.activities.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.coinbene.common.R;
import com.coinbene.common.base.BaseAdapter;
import com.coinbene.common.base.BaseViewHolder;

/**
 * @author ding
 */
public abstract class FiveItemAdapter<T> extends BaseAdapter<T> {



    public FiveItemAdapter(int item) {
        super(item);
    }

    @Override
    public int getItemCount() {

        if (list != null && list.size() > 5) {
            return 6;
        }

        return list == null || list.size() == 0 ? 1 : list.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (list == null || list.size() == 0) {
            return EMPTY_VIEW;
        }

        if (position == 5) {
            return LOOK_FOOTER_VIEW;
        }

        return ITEM_VIEW;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mContext = parent.getContext();

        BaseViewHolder holder = null;

        switch (viewType) {

            case EMPTY_VIEW:
                holder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.common_base_empty, parent, false));
                return holder;

            case LOOK_FOOTER_VIEW:
                holder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.commom_footer_look_all, parent, false));
                return holder;

            default:
                holder = new BaseViewHolder(LayoutInflater.from(mContext).inflate(item, parent, false));
                return holder;

        }
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        if (getItemViewType(position) == EMPTY_VIEW) {
            return;
        }

        //最后一条无数据，传null预防下标越界
        if (getItemViewType(position) == LOOK_FOOTER_VIEW) {
            convert(holder, position, null);
            return;
        }

        convert(holder, position, list.get(position));

    }


}
