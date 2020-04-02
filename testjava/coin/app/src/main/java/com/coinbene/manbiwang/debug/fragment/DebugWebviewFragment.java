package com.coinbene.manbiwang.debug.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.coinbene.common.base.BaseFragment;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.zxing.ScannerActivity;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.mylhyl.zxing.scanner.common.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class DebugWebviewFragment extends BaseFragment {

    @BindView(R2.id.btn_debug_x5)
    Button btnDebugX5;
    @BindView(R2.id.btn_debug_jsbridge)
    Button btnDebugJsbridge;
    @BindView(R2.id.btn_goto_webview)
    Button btnGotoWebview;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.settings_fragment_debug_webview, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R2.id.btn_debug_x5, R2.id.btn_debug_jsbridge, R2.id.btn_goto_webview})
    public void onViewClicked(View view) {
        int id = view.getId();

        //腾讯X5
        if (id == R.id.btn_debug_x5){
            UIBusService.getInstance().openUri(getContext(),"http://dev.dev.atomchain.vip/fetools/index.html#/dev/jsbridge", null);
            return;
        }

        //jsbridge
        if (id == R.id.btn_debug_jsbridge){
            UIBusService.getInstance().openUri(getContext(),"file:///android_asset/demo.html", null);
            return;
        }

        if (id == R.id.btn_goto_webview){
            ScannerActivity.scanUrl = true;
            Intent intent = new Intent(getContext(), ScannerActivity.class);
            startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            return;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Activity.RESULT_FIRST_USER){
            if (data != null) {
                String url = data.getStringExtra(Scanner.Scan.RESULT);
                if (!TextUtils.isEmpty(url)) {
                    UIBusService.getInstance().openUri(getContext(), UrlUtil.parseUrl(url), null);
                }
            }
        }

    }
}
