package com.coinbene.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.manbiwang.model.http.CountryAreaCodeResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 选择国籍，区分国籍
 */

public class SelectCountryActivity extends CoinbeneBaseActivity {

    public static final int REQUEST_CODE = 100;
    Unbinder mUnbinder;
    @BindView(R2.id.menu_back)
    View backView;

    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.rl_country)
    RecyclerView rl_country;

    @BindView(R2.id.search_input)
    EditText searchInput;
    @BindView(R2.id.close_view)
    ImageView closeView;
    @BindView(R2.id.empty_layout)
    View emptyLayout;

    private MultiTypeAdapter mContentAdapter;
    private List<CountryAreaCodeResponse.Country> allCountry;
    private String lastInputStr;

    public static void startMe(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SelectCountryActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int initLayout() {
        return R.layout.select_country;
    }

    @Override
    public void initView() {
        mContentAdapter = new MultiTypeAdapter();
        SelectCountryItemBinder itemBinder = new SelectCountryItemBinder(this);
        mContentAdapter.register(CountryAreaCodeResponse.Country.class, itemBinder);

        rl_country.setLayoutManager(new LinearLayoutManager(this));
        rl_country.setAdapter(mContentAdapter);

        backView.setOnClickListener(this);

        titleView.setText(R.string.country_select_title);
        closeView.setOnClickListener(this);
        closeView.setVisibility(View.GONE);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeInputStr(s.toString());
            }
        });
        rl_country.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    KeyboardUtils.hideKeyboard(recyclerView);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        getData();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return false;
    }

    private void getData() {
        OkGo.<CountryAreaCodeResponse>get(Constants.CONTENT_GET_COUNTRYAREA_LIST).tag(this).execute(new NewDialogJsonCallback<CountryAreaCodeResponse>(SelectCountryActivity.this) {
            @Override
            public void onSuc(Response<CountryAreaCodeResponse> response) {
                CountryAreaCodeResponse baseResponse = response.body();
                if (baseResponse.isSuccess()) {
                    List<CountryAreaCodeResponse.DataBean> dataArray = baseResponse.data;
                    //返回的数据，包括两部分，一是常用的国家；二是全部的国家.这里用的第二部分
                    if (dataArray == null || dataArray.size() < 2) {
                        emptyLayout.setVisibility(View.VISIBLE);
                        rl_country.setVisibility(View.GONE);
                        return;
                    }
                    if (dataArray.get(1) == null || dataArray.get(1).country == null) {
                        emptyLayout.setVisibility(View.VISIBLE);
                        rl_country.setVisibility(View.GONE);
                        return;
                    }

                    allCountry = dataArray.get(1).country;
                    mContentAdapter.setItems(allCountry);
                    mContentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onE(Response<CountryAreaCodeResponse> response) {
                emptyLayout.setVisibility(View.VISIBLE);
                rl_country.setVisibility(View.GONE);
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }

    private void changeInputStr(String inputStr) {
        if (allCountry == null || allCountry.size() == 0) {
            return;
        }
        inputStr = inputStr.trim();
        if (TextUtils.isEmpty(inputStr)) {
            lastInputStr = "";
            closeView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
            rl_country.setVisibility(View.VISIBLE);
            mContentAdapter.setItems(allCountry);
            mContentAdapter.notifyDataSetChanged();
            return;
        }
        closeView.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(lastInputStr) && lastInputStr.equalsIgnoreCase(inputStr)) {
            return;
        }
        lastInputStr = inputStr;
        List<CountryAreaCodeResponse.Country> searchResult = searchCountry(inputStr);
        if (searchResult == null || searchResult.size() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
            rl_country.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            rl_country.setVisibility(View.VISIBLE);
        }
        mContentAdapter.setItems(searchResult);
        mContentAdapter.notifyDataSetChanged();
    }

    private List<CountryAreaCodeResponse.Country> searchCountry(String country_area) {
        if (allCountry == null || allCountry.size() == 0) {
            return null;
        }
        List<CountryAreaCodeResponse.Country> searchResult = new ArrayList<>();
        for (int i = 0; i < allCountry.size(); i++) {
            CountryAreaCodeResponse.Country tempCountry = allCountry.get(i);
            country_area = country_area.toLowerCase();
            if (tempCountry.name.toLowerCase().contains(country_area) || tempCountry.areaCode.contains(country_area)) {
                searchResult.add(tempCountry);
            }
        }
        return searchResult;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_back) {
            finish();
        } else if (v.getId() == R.id.close_view) {
            searchInput.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
}
