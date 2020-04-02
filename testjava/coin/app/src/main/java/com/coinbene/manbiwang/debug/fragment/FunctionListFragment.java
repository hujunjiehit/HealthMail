package com.coinbene.manbiwang.debug.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.model.http.CrashModel;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.debug.crash.CrashDetailActivity;
import com.coinbene.manbiwang.debug.networkcapture.NetworkCaptureListActivity;
import com.coinbene.manbiwang.user.R;
import com.tencent.mmkv.MMKV;

public class FunctionListFragment extends BaseFragment {

    Button btnChangeEnv;
    Button btnDebugWebview;

    ChangeEnvFragment mChangeEnvFragment;
    DebugWebviewFragment mDebugWebviewFragment;
    ChangeTimeFragment mChangeTimeFragment;

    private Button btnChangeTime;
    private CheckBox cbEnableNewKline;
    private CheckBox cbUpsAndDowns;
    private TextView tvTest;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.settings_fragment_function_list, container, false);
        init();
        return mRootView;
    }

    private void init() {
        btnChangeEnv = mRootView.findViewById(R.id.btn_change_env);
        btnDebugWebview = mRootView.findViewById(R.id.btn_debug_webview);
        btnChangeTime = mRootView.findViewById(R.id.btn_change_time);
        cbEnableNewKline =  mRootView.findViewById(R.id.cb_enable_newkline);
        cbUpsAndDowns =  mRootView.findViewById(R.id.cb_ups_and_downs);
        tvTest = mRootView.findViewById(R.id.tv_test);

        cbEnableNewKline.setChecked(SpUtil.getEnableNewKline());
        //cbUpsAndDowns.setChecked(SpUtil.getUpsAndDownsType() == Constants.UPS_AND_DOWNS_TYPE_24H);

        btnChangeEnv.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if(mChangeEnvFragment == null) {
                mChangeEnvFragment = new ChangeEnvFragment();
            }
            transaction.replace(R.id.fragment_container, mChangeEnvFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        mRootView.findViewById(R.id.btn_show_last_crash).setOnClickListener( v -> {
            CrashModel crashModel = MMKV.defaultMMKV().decodeParcelable("lastCrash", CrashModel.class);
            if (crashModel != null) {
                //展示崩溃信息
                CrashDetailActivity.startMe(getActivity(), crashModel);
            } else {
                //没有保存的崩溃信息
                ToastUtil.show("未获取到最近一次的崩溃信息");
            }
        });

        btnChangeTime.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if(mChangeTimeFragment == null) {
                mChangeTimeFragment = new ChangeTimeFragment();
            }
            transaction.replace(R.id.fragment_container, mChangeTimeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnDebugWebview.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if(mDebugWebviewFragment == null) {
                mDebugWebviewFragment = new DebugWebviewFragment();
            }
            transaction.replace(R.id.fragment_container, mDebugWebviewFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });

        mRootView.findViewById(R.id.btn_debug_network_capture).setOnClickListener(v -> {
            NetworkCaptureListActivity.startMe(getActivity());
        });

        cbEnableNewKline.setOnCheckedChangeListener((buttonView, isChecked) -> SpUtil.setEnableNewKline(isChecked));

        //cbUpsAndDowns.setOnCheckedChangeListener((buttonView, isChecked) -> SpUtil.setUpsAndDownsType(isChecked ? Constants.UPS_AND_DOWNS_TYPE_24H : Constants.UPS_AND_DOWNS_TYPE_ZERO));


//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("getSystemLocale:" + LanguageHelper.getSystemLocale() + "\n");
//        tvTest.setText(stringBuilder.toString());
    }
}
