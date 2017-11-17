package com.june.healthmail.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.june.healthmail.R;
import com.june.healthmail.http.ApiService;
import com.june.healthmail.http.HttpManager;
import com.june.healthmail.http.bean.GetModuleConfigBean;
import com.june.healthmail.http.bean.Topic;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.view.CommonTopicView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by bjhujunjie on 2017/4/8.
 */

public class DiscoveryFragment extends Fragment implements View.OnClickListener {

    private View layout;
    private PullToRefreshListView mPullToRefreshView;
    private LinearLayout mContainer;
    private List<View> mViews;
    private List<Topic> mTopics;
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
        getModuleConfig();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 将layout从父组件中移除
        ViewGroup parent = (ViewGroup) layout.getParent();
        parent.removeView(layout);
    }

    private void initView() {
        mContainer = (LinearLayout) layout.findViewById(R.id.content_linearlayout);
    }

    private void setOnListener() {

    }

    private void init() {
        Log.e("test","init getData");
        mViews = new ArrayList<>();
        mTopics = PreferenceHelper.getInstance().getTopicList();

        //从缓存的topic list中获取数据 更新界面
        if(mTopics != null && mTopics.size() > 0) {
            showTopics();
        }
    }

    private void getModuleConfig() {
//        final ShowProgress showProgress = new ShowProgress(getActivity());
//        if(showProgress != null && !showProgress.isShowing()){
//            showProgress.setMessage("正在获取活动详情");
//            showProgress.show();
//        }
        if(mRetrofit == null) {
            mRetrofit = HttpManager.getInstance().getRetrofit();
        }
        mRetrofit.create(ApiService.class).getModuleConfig().enqueue(new Callback<GetModuleConfigBean>() {
            @Override
            public void onResponse(Call<GetModuleConfigBean> call, Response<GetModuleConfigBean> response) {
//                if(showProgress != null && showProgress.isShowing()){
//                    showProgress.dismiss();
//                }


                final GetModuleConfigBean bean = response.body();


                Log.e("test","size = " + bean.getRet().size());
                if(bean.getRet().size() > 0) {
                    handleTopicList(bean.getRet());
                }
            }

            @Override
            public void onFailure(Call<GetModuleConfigBean> call, Throwable t) {
//                if(showProgress != null && showProgress.isShowing()){
//                    showProgress.dismiss();
//                }
            }
        });
    }

    private void handleTopicList(List<Topic> list) {
        //先对比数据有没有变化，如果变化了，才需要刷新View，否则直接返回
        if(mTopics != null && mTopics.size() == list.size()) {
            boolean isEqual = true;
            for(int i = 0 ; i < mTopics.size(); i++) {
                if(mTopics.get(i) == null && list.get(i) == null) {
                    continue;
                }else if(mTopics.get(i) != null && list.get(i) != null) {
                    if(!mTopics.get(i).equals(list.get(i))){
                        isEqual = false;
                        break;
                    }
                }
            }

            if(isEqual) {
                Log.d("test", "same data, do not reconstruct");
                return;
            }else {
                Log.d("test", "reconstruct");
            }
        }

        //每次获取成功之后，都将结果跟新到缓存
        PreferenceHelper.getInstance().setTopicList(list);
        mTopics = list;
        showTopics();
    }

    private void showTopics() {
        //排序之后再显示的逻辑在这里
        List<Topic> sortList = mTopics;
        Collections.sort(sortList);


        mViews.clear();
        for (Topic topic:sortList) {
            if(topic.getEnable() == 1) {
                CommonTopicView topicView = new CommonTopicView(getActivity());
                topicView.setData(topic);
                mViews.add(topicView);
            }
        }
        addAllViews();
    }

    private void addAllViews() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContainer.removeAllViews();
                for(int i = 0; i < mViews.size(); i++) {
                    mContainer.addView(mViews.get(i));
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
