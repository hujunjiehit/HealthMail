package com.june.healthmail.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.june.healthmail.http.ApiService;
import com.june.healthmail.http.HttpManager;
import com.june.healthmail.http.bean.BaseBean;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.ShowProgress;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by june on 2017/10/28.
 */

public abstract class BaseFragment extends Fragment {

  private Unbinder mUnbinder;
  protected View mRootView;

  private UserInfo userInfo;
  private ShowProgress showProgress;
  private Retrofit mRetrofit;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(setLayout(), container, false);
    mUnbinder = ButterKnife.bind(this,mRootView);
    setListener();
    initData();
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    return mRootView;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if(mUnbinder != Unbinder.EMPTY) {
      mUnbinder.unbind();
    }
  }

  public abstract int setLayout();
  public abstract void setListener();
  public abstract void initData();

  protected void checkPermission(final Class cls, final int flag){
    if (showProgress == null) {
      showProgress = new ShowProgress(getActivity());
    }
    if (!showProgress.isShowing()) {
      showProgress.setMessage("正在验证当前用户权限，请稍后...");
      showProgress.show();
    }

    if (mRetrofit == null) {
      mRetrofit = HttpManager.getInstance().getRetrofit();
    }
    mRetrofit.create(ApiService.class).getPermission(userInfo.getUsername(), userInfo.getUserType(), userInfo.getAppVersion()).enqueue(new Callback<BaseBean>() {
      @Override
      public void onResponse(Call<BaseBean> call, Response<BaseBean> response) {
        if (showProgress != null && showProgress.isShowing()) {
          showProgress.dismiss();
        }
        BaseBean bean = response.body();
        if (bean.getStatus().equals("ok") && bean.getMessage().equals("验证成功,checksum=111")) {
          Intent it = new Intent(getActivity(), cls);
          it.putExtra("flag",flag);
          startActivity(it);
        } else {
          Toast.makeText(getActivity(), bean.getMessage(), Toast.LENGTH_LONG).show();
        }
      }

      @Override
      public void onFailure(Call<BaseBean> call, Throwable t) {
        if (showProgress != null && showProgress.isShowing()) {
          showProgress.dismiss();
        }
        Toast.makeText(getActivity(), "网络请求失败", Toast.LENGTH_LONG).show();
      }
    });
  }

  protected void checkPermission(final Class cls) {
    checkPermission(cls, 0);
  }
}
