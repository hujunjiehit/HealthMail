package com.june.healthmail.fragement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.june.healthmail.R;
import com.june.healthmail.activity.DetailPostActivity;
import com.june.healthmail.activity.NewPostActivity;
import com.june.healthmail.activity.WebViewActivity;
import com.june.healthmail.adapter.PostAdapter;
import com.june.healthmail.http.ApiService;
import com.june.healthmail.http.HttpManager;
import com.june.healthmail.http.bean.GetActivityConfigBean;
import com.june.healthmail.model.PostModel;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.ShowProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by bjhujunjie on 2017/4/8.
 */

public class DiscoveryFragment extends Fragment implements View.OnClickListener {

    private View layout;
    private TextView tvNotification1;
    private TextView tvNotification2;
    private TextView tvNotification3;
    private View layoutEmpty;
    private View layoutContent;
    private TextView tvActivityTitle;
    private TextView tvActivityDesc;
    private TextView tvActivityUrl;
    private Retrofit mRetrofit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return layout;
        }
        layout = inflater.inflate(R.layout.fragment_discovery, container, false);
        initView();
        setOnListener();
        init();
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(PreferenceHelper.getInstance().getHasActivity() == 1) {
            //有活动
            getActivityConfigs();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 将layout从父组件中移除
        ViewGroup parent = (ViewGroup) layout.getParent();
        parent.removeView(layout);
    }

    private void initView() {
        tvNotification1 = (TextView) layout.findViewById(R.id.notification_1);
        tvNotification2 = (TextView) layout.findViewById(R.id.notification_2);
        tvNotification3 = (TextView) layout.findViewById(R.id.notification_3);
        layoutEmpty = layout.findViewById(R.id.empty_activity);
        layoutContent = layout.findViewById(R.id.activity_content);
        tvActivityTitle = (TextView) layout.findViewById(R.id.activity_tittle);

        tvActivityDesc = (TextView) layout.findViewById(R.id.tv_activity_desc);
        tvActivityUrl = (TextView) layout.findViewById(R.id.tv_activity_url);
        if(PreferenceHelper.getInstance().getHasActivity() == 1) {
            //有活动
            layoutContent.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            getActivityConfigs();
        } else {
            //无活动
            tvActivityTitle.setText("活动中心");
            layoutContent.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);

        }
    }

    private void getActivityConfigs() {
        final ShowProgress showProgress = new ShowProgress(getActivity());
        if(showProgress != null && !showProgress.isShowing()){
            showProgress.setMessage("正在获取活动详情");
            showProgress.show();
        }
        if(mRetrofit == null) {
            mRetrofit = HttpManager.getInstance().getRetrofit();
        }
        mRetrofit.create(ApiService.class).getActivityConfig().enqueue(new Callback<GetActivityConfigBean>() {
            @Override
            public void onResponse(Call<GetActivityConfigBean> call, Response<GetActivityConfigBean> response) {
                if(showProgress != null && showProgress.isShowing()){
                    showProgress.dismiss();
                }

                final GetActivityConfigBean bean = response.body();
                tvActivityTitle.setText(bean.getActivityTitle());
                tvActivityDesc.setText(bean.getActivityContent());
                tvActivityDesc.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(bean.getActivityUrl())){
                    tvActivityUrl.setText(bean.getUrlDesc());
                    tvActivityUrl.setVisibility(View.VISIBLE);
                    tvActivityUrl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openTaobaoShopping(bean.getActivityUrl());
                        }
                    });
                }else {
                    tvActivityUrl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<GetActivityConfigBean> call, Throwable t) {
                if(showProgress != null && showProgress.isShowing()){
                    showProgress.dismiss();
                }
            }
        });
    }

    private void setOnListener() {
    }

    private void openTaobaoShopping(final String url){
        Intent intent = new Intent();
        if (CommonUntils.checkPackage(getActivity(),"com.taobao.taobao")){
            Log.e("test","taobao is not installed");
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            startActivity(intent);
        } else {
            intent.putExtra("url",url);
            intent.setClass(getActivity(),WebViewActivity.class);
            startActivity(intent);
        }
    }

    private void init() {
        String str = PreferenceHelper.getInstance().getNotification();
        if(str.contains("|")) {
            String[] arays = str.split("[|]");
            for(int i = 0; i < 3; i++){
                if(arays[i] != null){
                    if(i == 0){
                        tvNotification1.setVisibility(View.VISIBLE);
                        tvNotification1.setText(arays[i]);
                    }else if(i == 1) {
                        tvNotification2.setVisibility(View.VISIBLE);
                        tvNotification2.setText(arays[i]);
                    } else if(i == 2){
                        tvNotification3.setVisibility(View.VISIBLE);
                        tvNotification3.setText(arays[i]);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
