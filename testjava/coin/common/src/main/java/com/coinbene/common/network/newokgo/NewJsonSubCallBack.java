package com.coinbene.common.network.newokgo;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.okgo.AppThrowable;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.network.okgo.OkException;
import com.coinbene.common.utils.ToastUtil;
import com.lzy.okgo.model.Response;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by zhangle on 2018/2/7.
 */

public abstract class NewJsonSubCallBack<T> extends NewJsonCallback<T> {

	@Override
	public void onSuccess(Response<T> response) {

		//这句代码是希望开发者打的tag 的 fragment或者activity  ，如果activity被销毁 就直接retrun掉  ，解决之前在onSuc回调中 view空指针的问题
		if (response.getRawCall().request().tag() != null) {
			if (response.getRawCall().request().tag() instanceof Fragment) {
				Activity activity = ((Fragment) response.getRawCall().request().tag()).getActivity();
				if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
					return;
				}
			} else if (response.getRawCall().request().tag() instanceof Activity) {
				Activity activity = (Activity) response.getRawCall().request().tag();
				if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
					return;
				}
			}
		}
		if (response.body() == null) {//如果返回为空则直接回调onE
			onE(response);
		} else if (response.body() instanceof BaseRes) {//如果不是空返回正确格式
			if (((BaseRes) response.body()).isSuccess() || ((BaseRes) response.body()).isOrderCanceling()) {//判断是否为200正常返回
				onSuc(response);
			} else {//如果不是200  toast msg，然后回调onE,方便业务端走逻辑
				dealServerError(response);
			}
		} else {//有些不是BaseResponse格式  丢给回调自己处理
			onSuc(response);
		}
	}

	@Override
	public void onError(Response<T> response) {//处理失败的本地异常
		super.onError(response);
		if (response.getException() != null) {
			AppThrowable throwable = OkException.handleException(response.getRawResponse(), response.getException());
//			postBugly(throwable, response);

			String msg = throwable.getMessage();
			if (response.code() != 404) {
				ToastUtil.show(msg);
			}
		}
		onE(response);//不管怎样都要给当前请求回调过去  让自己处理
	}

	public abstract void onSuc(Response<T> response);

	public abstract void onE(Response<T> response);

	/**
	 * 子类可以重写本方法;如果重写本方法，onE()方法和本方法部分功能重叠
	 *
	 * @param response
	 */
	public void dealServerError(Response<T> response) {
		if (response.body() != null && !TextUtils.isEmpty(((BaseRes) response.body()).message)) {
			if (!((BaseRes) response.body()).isExpired()) {
				ToastUtil.show(((BaseRes) response.body()).message);
			}
		}
		onE(response);
	}


	@Deprecated
	public void onFail(String msg) {
		ToastUtil.show(msg);
	}

//	public void postBugly(AppThrowable throwable, Response<T> response) {
//		if (throwable != null && throwable.getCode() != 200 && throwable.getCode() != OkException.NOT_FOUND && throwable.getCode() != OkException.REQUEST_TIMEOUT) {
//
//			UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
//			if (userInfo != null) {
//				CrashReport.setUserId(String.valueOf(userInfo.userId));
//			}
//			if (response.getRawResponse() != null && response.getRawResponse().request() != null&& response.getRawResponse().request().url() != null) {
//				CrashReport.putUserData(CBRepository.getContext(), "url", response.getRawResponse().request().url().toString());
//			}
//			CrashReport.putUserData(CBRepository.getContext(), "code", String.valueOf(throwable.getCode()));
//			CrashReport.putUserData(CBRepository.getContext(), "response", response.body() == null ? "" : response.body().toString());
//			CrashReport.postCatchedException(throwable);
//
//		}
//
//
//	}

}
