package com.coinbene.manbiwang.user.balance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.UserPayTypeModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.balance.adapter.UserPayBindAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


@Route(path = RouteHub.User.userPayTypesActivity)
public class UserPayTypesActivity extends CoinbeneBaseActivity {
    @BindView(R2.id.menu_back)
    View backBtn;
    private Unbinder mUnbinder;
    @BindView(R2.id.menu_title_tv)
    TextView menu_title_tv;
    @BindView(R2.id.rlv_pay_types)
    RecyclerView rlvPayTypes;
    @BindView(R2.id.tv_add)
    TextView tv_add;
    private String realName = "";
    private UserPayBindAdapter adapter;
    private AlertDialog.Builder unBindDialog;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, UserPayTypesActivity.class);
        context.startActivity(intent);
    }


    @Override
    public int initLayout() {
        return R.layout.activity_pay_types;
    }

    @Override
    public void initView() {
        init();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        getData();
    }

    @Override
    public boolean needLock() {
        return true;
    }

    private void getData() {

    }

    private void init() {
        tv_add.setText(String.format("+%s", getString(R.string.add_pay_type)));
        menu_title_tv.setText(R.string.pay_binding);
        backBtn.setOnClickListener(this);
        adapter = new UserPayBindAdapter();
        rlvPayTypes.setLayoutManager(new LinearLayoutManager(this));
        rlvPayTypes.setAdapter(adapter);
        tv_add.setOnClickListener(this);
        adapter.setOnLineChangeListener(new UserPayBindAdapter.OnLineChangeListener() {
            @Override
            public void onLineChange(UserPayTypeModel.DataBean.PaymentWayListBean item) {
                changePayStutes(item);
            }

            @Override
            public void unBind(UserPayTypeModel.DataBean.PaymentWayListBean item) {

                initDialog(item);
            }
        });
    }

    private void initDialog(UserPayTypeModel.DataBean.PaymentWayListBean item) { //status.equals("1")//上架中   去下架   //已下架   去删除
        if (unBindDialog == null) {
            unBindDialog = new AlertDialog.Builder(this);
        }
        if (item.getType() == 1) {
            unBindDialog.setTitle(R.string.unbind_bank);
            unBindDialog.setMessage(getString(R.string.user_name) + " : " + item.getUserName() + "\n" + getString(R.string.account) + " : " + item.getBankAccount());
        } else if (item.getType() == 2) {
            unBindDialog.setTitle(R.string.unbind_alipay);
            unBindDialog.setMessage(getString(R.string.user_name) + " : " + item.getUserName() + "\n" + getString(R.string.account) + " : " + item.getPayAccount());
        } else if (item.getType() == 3) {
            unBindDialog.setTitle(R.string.unbind_wechat);
            unBindDialog.setMessage(getString(R.string.user_name) + " : " + item.getUserName() + "\n" + getString(R.string.account) + " : " + item.getPayAccount());

        }

        unBindDialog.setCancelable(true);
        unBindDialog.setPositiveButton(getString(R.string.btn_ok), (orderHanderDialog, which) -> {
            unBindPay(item);
            orderHanderDialog.dismiss();
        });
        unBindDialog.setNegativeButton(getString(R.string.btn_cancel), (orderHanderDialog, which) -> orderHanderDialog.dismiss());
        unBindDialog.show();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.tv_add) {
            PayBindActivity.startMe(this);
        } else if (id == R.id.menu_back) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserPayTypes();
        OkGo.getInstance().cancelTag(this);
//        getBindPayInfo();
    }

    public void getUserPayTypes() {

        OkGo.<UserPayTypeModel>get(Constants.OTC_GET_USER_PAY_TYPE).tag(this).execute(new NewJsonSubCallBack<UserPayTypeModel>() {
            @Override
            public void onSuc(Response<UserPayTypeModel> response) {
                adapter.setItem(response.body().getData().getPaymentWayList());
                adapter.setState(UserPayBindAdapter.LOADING_END);
            }

            @Override
            public void onE(Response<UserPayTypeModel> response) {
            }

        });

    }


    private void changePayStutes(UserPayTypeModel.DataBean.PaymentWayListBean item) {

        HttpParams params = new HttpParams();
        params.put("id", item.getId());
        if (item.getOnline() == 0) {//如果是下线状态则上线  否则是下线
            params.put("online", 1);
        } else {
            params.put("online", 0);
        }

        OkGo.<BaseRes>get(Constants.OTC_ONLINE_PAYWAY).tag(this).params(params).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                getUserPayTypes();
            }

            @Override
            public void onE(Response<BaseRes> response) {
                getUserPayTypes();
            }

        });
    }

    private void unBindPay(UserPayTypeModel.DataBean.PaymentWayListBean item) {

        OkGo.<BaseRes>get(Constants.OTC_UNBIND_PAYWAY).tag(this).params("id", item.getId()).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                getUserPayTypes();
            }

            @Override
            public void onE(Response<BaseRes> response) {
            }

        });
    }


}
