package com.coinbene.common;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.manbiwang.model.http.CountryAreaCodeResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author mxd on 2018/3/11.
 */

public class SelectCountryItemBinder extends ItemViewBinder<CountryAreaCodeResponse.Country, SelectCountryItemBinder.ViewHolder> {
    private Activity activity;

    public SelectCountryItemBinder(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.select_country_item, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull CountryAreaCodeResponse.Country country) {

        holder.tv_country_name.setText(new StringBuilder().append(country.name).append("（+").append(country.areaCode).append("）").toString());

        holder.rootLayout.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("countryName", country.name);
            intent.putExtra("countryArea", country.areaCode);
            intent.putExtra("country_ISO", country.code);
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.root_layout)
        View rootLayout;
        @BindView(R2.id.tv_country_name)
        TextView tv_country_name;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
