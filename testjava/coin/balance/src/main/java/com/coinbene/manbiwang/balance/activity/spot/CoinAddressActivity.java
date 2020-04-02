package com.coinbene.manbiwang.balance.activity.spot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.spot.adapter.CoinAddressViewAdapter;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.WithDrawAddressModel;
import com.coinbene.manbiwang.service.RouteHub;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/12/3.
 * 选择提现地址，提现地址管理
 */
@Route(path = RouteHub.Balance.coinAddressActivity)
public class CoinAddressActivity extends CoinbeneBaseActivity {
    public static final int FROM_NORMAL = 0;//正常情况
    public static final int FROM_WITHDRAW = 1;//提现
    public static final int CODE_REQUEST = 100;
    private String code;
    private int selectChainPosition;
    private String chain;

    public static void startMe(Activity activity, int from_type, int requestCode, Bundle bundle) {
        Intent intent = new Intent(activity, CoinAddressActivity.class);
        if (from_type == FROM_WITHDRAW) {
            intent.putExtra("needReturn", true);
            intent.putExtra("bundle", bundle);
            activity.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivity(intent);
        }
    }

    Unbinder mUnbinder;
    @BindView(R2.id.list_category)
    RecyclerView recyclerView;
    CoinAddressViewAdapter addressViewAdapter;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.empty_layout)
    LinearLayout emptyLayout;

    @BindView(R2.id.add_address_btn)
    TextView addBtn;
    private boolean needReturn;
    private boolean hideDelete;
    private String assetId;


    @Override
    public int initLayout() {
        return R.layout.coin_address_list;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            hideDelete = needReturn = intent.getBooleanExtra("needReturn", false);
        }
        if (needReturn) {
            Bundle bundle = intent.getBundleExtra("bundle");
            if (bundle != null) {
                assetId = bundle.getString("assetId");
                code = bundle.getString("code");
                selectChainPosition = bundle.getInt("chain");
                chain = bundle.getString("chainName");
                String coin_str = getResources().getString(R.string.select_withdraw_address_title);
                titleView.setText(String.format(coin_str, code));
                addBtn.setText(String.format(getString(R.string.add_coin_address), code));
            }

//            addBtn.setVisibility(View.GONE);
        } else {
            titleView.setText(R.string.withdraw_address_title);
//            addBtn.setVisibility(View.VISIBLE);
        }
        addressViewAdapter = new CoinAddressViewAdapter(hideDelete);
        addressViewAdapter.setListener(itemListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addressViewAdapter);

        backView.setOnClickListener(this);
        addBtn.setOnClickListener(this);

        getCoinAddressRequest();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean needLock() {
        return true;
    }

    public void getCoinAddressRequest() {
        HttpParams httpParams = new HttpParams();
        if (!TextUtils.isEmpty(assetId)) {//如果存在，则查询指定的币
            httpParams.put("asset", assetId);
        }
        httpParams.put("pageNum", 1);
        httpParams.put("pageSize", 200);
        httpParams.put("chain", chain);

        OkGo.<WithDrawAddressModel>get(Constants.ACCOUNT_ADDRESS_WITHDRAW_LIST).tag(this).params(httpParams).execute(new DialogCallback<WithDrawAddressModel>(this) {

            @Override
            public void onSuc(Response<WithDrawAddressModel> response) {
                if (response.body() == null || response.body().getData() == null || response.body().getData().getList() == null || response.body().getData().getList().size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                    addressViewAdapter.setItems(response.body().getData().getList());
                    addressViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onE(Response<WithDrawAddressModel> response) {
                recyclerView.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFail(String msg) {
                recyclerView.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_back) {
            finish();
        } else if (v.getId() == R.id.add_address_btn) {
            AddWithDrawAddressActivity.startMeForResult(CoinAddressActivity.this, AddWithDrawAddressActivity.CODE_ADD_ADDRESS_REQUEST, code, selectChainPosition);
        }
    }

    private ItemListener itemListener = new ItemListener() {
        @Override
        public void delete(WithDrawAddressModel.DataBean.ListBean item) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CoinAddressActivity.this);
            dialog.setMessage(getString(R.string.dialog_delete_content));
            dialog.setPositiveButton(getString(R.string.btn_ok), (dialog1, which) -> {
                deleteCoinAddress(item.getId());
                dialog1.dismiss();
            });
            dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog12, which) -> dialog12.dismiss());
            dialog.show();
        }

        @Override
        public void itemClick(WithDrawAddressModel.DataBean.ListBean item) {
            if (needReturn) {
                Intent intent = new Intent();
                intent.putExtra("item", item);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

    private void deleteCoinAddress(int id) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("addressId", id);
        OkGo.<BaseRes>post(Constants.ACCOUNT_ADDRESS_DELETE_LIST).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(this) {


            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.delete_withdraw_addr_success);
                addressViewAdapter.removeId(id);
                addressViewAdapter.notifyDataSetChanged();
                if (addressViewAdapter.getItemCount() == 0) {
                    emptyLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    if (recyclerView.getVisibility() == View.GONE) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }

            @Override
            public void onFail(String msg) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        OkGo.getInstance().cancelTag(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == AddWithDrawAddressActivity.CODE_ADD_ADDRESS_REQUEST) {
            getCoinAddressRequest();
        }
    }

    public interface ItemListener {
        void delete(WithDrawAddressModel.DataBean.ListBean item);

        void itemClick(WithDrawAddressModel.DataBean.ListBean item);
    }
}
