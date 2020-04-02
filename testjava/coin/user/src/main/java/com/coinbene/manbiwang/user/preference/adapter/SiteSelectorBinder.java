package com.coinbene.manbiwang.user.preference.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.SelectorSiteItem;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author mxd on 2018/9/2.
 */

public class SiteSelectorBinder extends ItemViewBinder<SelectorSiteItem, SiteSelectorBinder.ViewHolder> {

    public SiteSelectorBinder() {

    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.site_selector_item, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull SiteSelectorBinder.ViewHolder holder, @NonNull SelectorSiteItem item) {
        holder.launageTv.setText(item.title);
        holder.checkImgView.setVisibility(item.isSelected ? View.VISIBLE : View.INVISIBLE);

        int position = getPosition(holder);
        holder.lineView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isSelected) {
                    return;
                }
                List<?> adapterItems = getAdapter().getItems();
                for (int i = 0; i < adapterItems.size(); i++) {
                    SelectorSiteItem item1 = (SelectorSiteItem) adapterItems.get(i);
                    if (item.code.equals(item1.code)) {
                        item1.isSelected = true;
                    } else {
                        item1.isSelected = false;
                    }
                }
                getAdapter().notifyDataSetChanged();
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.line_view)
        View lineView;
        @BindView(R2.id.root_layout)
        View rootView;
        @BindView(R2.id.launague_tv)
        TextView launageTv;
        @BindView(R2.id.check_imgview)
        ImageView checkImgView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
