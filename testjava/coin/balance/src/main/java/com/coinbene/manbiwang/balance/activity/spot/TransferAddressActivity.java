package com.coinbene.manbiwang.balance.activity.spot;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.balance.activity.spot.adapter.TransferAddressAdapter;
import com.coinbene.manbiwang.model.http.TransferAccountListModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;


public class TransferAddressActivity extends CoinbeneBaseActivity {

    public static void startMe(Activity activity,int requestCode) {
        Intent intent = new Intent(activity, TransferAddressActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }

    @BindView(R2.id.list_category)
    RecyclerView recyclerView;
    TransferAddressAdapter addressViewAdapter;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.empty_layout)
    LinearLayout emptyLayout;

    @BindView(R2.id.add_address_btn)
    TextView addBtn;
    public static final int CODE_REQUEST = 102;


    @Override
    public int initLayout() {
        return R.layout.transfer_account_list;
    }

    @Override
    public void initView() {
        titleView.setText(R.string.transfer_account_data);
        addressViewAdapter = new TransferAddressAdapter();
        addressViewAdapter.setListener(new TransferAddressAdapter.ItemListener() {
            @Override
            public void itemClick(TransferAccountListModel.DataBean item) {
                Intent intent = new Intent();
                intent.putExtra("targetId", item.getTargetId());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addressViewAdapter);

        backView.setOnClickListener(this);
        addBtn.setOnClickListener(this);

        gettTransferListRequest();
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

    public void gettTransferListRequest() {
        OkGo.<TransferAccountListModel>get(Constants.ACCOUNT_GET_TRANSFER_LIST).tag(this).execute(new DialogCallback<TransferAccountListModel>(this) {

            @Override
            public void onSuc(Response<TransferAccountListModel> response) {
                if (response.body() == null || response.body().getData() == null || response.body().getData() == null || response.body().getData().size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                    addressViewAdapter.setItems(response.body().getData());
                    addressViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onE(Response<TransferAccountListModel> response) {
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
            AddTransferAccountActivity.startMeForResult(TransferAddressActivity.this, AddTransferAccountActivity.CODE_ADD_TRANSFER_REQUEST);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }
    public void back_click_unlogin_min_15() {
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == AddTransferAccountActivity.CODE_ADD_TRANSFER_REQUEST) {
            gettTransferListRequest();
        }
    }

}
