package com.coinbene.manbiwang.aspect;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.aspect.AspectManager;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.aspect.annotation.PostBrowserData;
import com.coinbene.common.aspect.annotation.PostClickData;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.rxjava.FlowControl;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Map;

/**
 * Created by june
 * on 2019-09-26
 * <p>
 * 此文件千万不能删
 */
@Aspect
public class AspectHelper {


	public static final String TAG = "AspectHelper";

	@Around("execution(* *(..)) && @annotation(com.coinbene.common.aspect.annotation.NeedLogin)")
	public void needLoginHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

//		DLog.d(TAG, proceedingJoinPoint.getSignature().getName() + "-Before");

		if (ServiceRepo.getUserService().isLogin()) {
			/**
			 * 如果用户登录则执行原方法
			 */
			proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
		} else {
			/**
			 * 未登录情况下
			 */
			MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());


			NeedLogin annotation = methodSignature.getMethod().getAnnotation(NeedLogin.class);


			if (annotation != null && annotation.jump() == true) {
				//需要跳转到登陆界面
				Context mContext = null;
				String mRouteUrl = "";


				//proceedingJoinPoint.getThis()可以获取到调用该方法的对象
				if (proceedingJoinPoint.getThis() instanceof Context) {
					mContext = (Context) proceedingJoinPoint.getThis();
				} else if (proceedingJoinPoint.getThis() instanceof Fragment) {
					mContext = ((Fragment) proceedingJoinPoint.getThis()).getContext();
				} else if (proceedingJoinPoint.getThis() instanceof View) {
					mContext = ((View) proceedingJoinPoint.getThis()).getContext();
				}

				//proceedingJoinPoint.getArgs()可以获取到方法的所有参数
				for (Object param : proceedingJoinPoint.getArgs()) {
					if (param instanceof Context && mContext == null) {
						mContext = (Context) param;
					}

					//拿到登陆之后需要跳转的url
					if (param instanceof String) {
						if (((String) param).toLowerCase().startsWith("coinbene://")) {
							mRouteUrl = (String) param;
						}
					}

					//拿到登陆之后需要跳转的url
					if (param instanceof Uri) {
						if ((param.toString()).toLowerCase().startsWith("coinbene://")) {
							mRouteUrl = param.toString();
						}
					}
				}


				if (mContext == null) {
					mContext = CBRepository.getContext();
				}

				gotoLoginOrLock(mContext, mRouteUrl);
			} else {
				//不需要跳转到登陆界面，则直接返回
				return;
			}
		}
	}


	@Around("execution(* *(..)) && @annotation(com.coinbene.common.aspect.annotation.AddFlowControl)")
	public void flowControlHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		String key = proceedingJoinPoint.getSignature().getDeclaringTypeName() + "." + proceedingJoinPoint.getSignature().getName();
		//DLog.d(TAG, key + "-Before--AddFlowControl");
		Map<String, FlowControl> cacheMap = AspectManager.getInstance().getFlowControlMap();
		if (cacheMap.get(key) == null) {
			FlowControl flowControl = new FlowControl() {
				@Override
				public void doAction(String tag) {
					try {
						for (Object arg : getProceedingJoinPoint().getArgs()) {
						}
						getProceedingJoinPoint().proceed(getProceedingJoinPoint().getArgs());
					} catch (Throwable throwable) {
						throwable.printStackTrace();
					}
				}
			};
			MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
			AddFlowControl annotation = methodSignature.getMethod().getAnnotation(AddFlowControl.class);
			flowControl.setTimeInterval(annotation.timeInterval());
			flowControl.setFlowControlStrategy(annotation.strategy());
			cacheMap.put(key, flowControl);
		}

		if (cacheMap.get(key).check()) {
			long time = System.currentTimeMillis();
//			DLog.d(TAG, "FlowControl = " + cacheMap.get(proceedingJoinPoint.getSignature().getName()));
//			DLog.d(TAG, proceedingJoinPoint.getSignature().getName() + " send request ==> " + time);
			cacheMap.get(key).setProceedingJoinPoint(proceedingJoinPoint);
			cacheMap.get(key).sendRequest("" + time);
		}
	}


	@Around("execution(* *(..)) && @annotation(com.coinbene.common.aspect.annotation.PostClickData)")
	public void postClickDataHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
		PostClickData annotation = methodSignature.getMethod().getAnnotation(PostClickData.class);
		if (annotation != null && !TextUtils.isEmpty(annotation.value())) {
			PostPointHandler.postClickData(annotation.value());
			proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
		}
	}

	@Around("execution(* *(..)) && @annotation(com.coinbene.common.aspect.annotation.PostBrowserData)")
	public void postBrowserDataHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		MethodSignature methodSignature = ((MethodSignature) proceedingJoinPoint.getSignature());
		PostBrowserData annotation = methodSignature.getMethod().getAnnotation(PostBrowserData.class);
		if (annotation != null && !TextUtils.isEmpty(annotation.value())) {
			PostPointHandler.postBrowerData(annotation.value());
			proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
		}
	}

	/**
	 * 修复华为手机onActivityPause崩溃问题
	 *
	 * @param proceedingJoinPoint
	 * @throws Throwable
	 */
	@Around("execution(* com.huawei.android.hms.agent.common.*.onActivity*(..))")
	public void bugRepair(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		try {
			proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 跳转登录或解锁
	 * @param context
	 */
	private void gotoLoginOrLock(Context context, String routeUrl){
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null || TextUtils.isEmpty(userTable.token)) {
			ARouter.getInstance().build(RouteHub.User.loginActivity)
					.withString("routeUrl", routeUrl)
					.navigation(context);
		} else if (UserInfoController.getInstance().isSetFingerPrint() || UserInfoController.getInstance().isGesturePwdSet()) {
			if (UserInfoController.getInstance().isLocked()) {
				if (UserInfoController.getInstance().isSetFingerPrint() && !UserInfoController.getInstance().isGesturePwdSet()) {
					//只设置指纹
					ARouter.getInstance().build(RouteHub.User.fingerprintCheckActivity)
							.withInt("index", Constants.TAB_INDEX_DEFAULT)
							.withString("routeUrl", routeUrl)
							.navigation(context);
				} else {
					ARouter.getInstance().build(RouteHub.User.patternCheckActivity)
							.withInt("index", Constants.TAB_INDEX_DEFAULT)
							.withString("routeUrl", routeUrl)
							.navigation(context);
				}
			}
		}
	}


}
