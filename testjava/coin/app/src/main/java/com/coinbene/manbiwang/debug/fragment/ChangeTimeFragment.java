package com.coinbene.manbiwang.debug.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ding
 * 2019-06-15
 * com.coinbene.manbiwang.settings.about.debug.fragment
 */
public class ChangeTimeFragment extends BaseFragment {

    @BindView(R2.id.spinner)
    Spinner spinner;
    @BindView(R2.id.text_desc)
    TextView textDesc;
    @BindView(R2.id.btn_restart)
    Button btnRestart;
    Unbinder unbinder;
    private View mRootView;

    private String[] envDesc = {
            "正常模式",
            "短时间模式",
    };

    private int preIndex;
    private int curIndex;

    private static int NORMAL_TIME = 0;

    private static int SHORT_TIME = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.settings_fragment_change_locked_time, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {
        btnRestart = mRootView.findViewById(R.id.btn_restart);
        spinner = mRootView.findViewById(R.id.spinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, envDesc);

        spinner.setAdapter(spinnerAdapter);


        curIndex = SpUtil.get(mActivity, SpUtil.CURRENT_LOCK_TIME, NORMAL_TIME);
        preIndex = curIndex;
        spinner.setSelection(preIndex, true);

        refreshData(curIndex);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curIndex = position;
                refreshData(curIndex);
                if (preIndex != position) {
                    btnRestart.setVisibility(View.VISIBLE);
                } else {
                    btnRestart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRestart.setOnClickListener(v -> {
            SpUtil.put(mActivity, SpUtil.CURRENT_LOCK_TIME, curIndex);

            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            System.exit(0);
        });
    }

    private void refreshData(int curIndex) {

        if (curIndex == 0) {
            textDesc.setText("正常模式");
        } else {
            textDesc.setText("短时间模式，10秒对应15分钟，30秒对应72小时");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
