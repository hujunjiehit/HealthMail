package com.coinbene.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.service.ServiceRepo;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Ø
 *
 * @author mengxiangdong
 * @date 2018/1/15
 */

public class ActivityLifeCallback implements Application.ActivityLifecycleCallbacks {

	private static final String TAG = ActivityLifeCallback.class.getSimpleName();
	private int resumed;
	private int paused;
	private int started;
	private int stopped;
	private int created;
	private int destroyed;
	private boolean isBackGround = false;

	private long stopTime = 0;

	private int current = 0;

	public static final int TIME_15_MIN = 0;
	public static final int TIME_72_HOUR = 1;
	public static final int TIME_1_WEEK = 2;

	public static final long periodTime = CBRepository.getCurrentEnvironment().getLockedTime()[TIME_15_MIN];
	public static final long periodTime_72_hour = CBRepository.getCurrentEnvironment().getLockedTime()[TIME_72_HOUR];
	public static final long periodTime_one_week = CBRepository.getCurrentEnvironment().getLockedTime()[TIME_1_WEEK];

	public WeakReference<Activity> currentAcitivty;

	/**
	 * 维护Activity 的list
	 */
	public final List<WeakReference<Activity>> weakReferences = Collections.synchronizedList(new LinkedList<>());

	public Activity getCurrentAcitivty() {
		return currentAcitivty.get();
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		created++;

		weakReferences.add(new WeakReference<>(activity));

		//退出app重新打开
		if (created == 1) {
			//app重新启动，如果设置了指纹或者手势解锁，需要锁上
			ServiceRepo.getUserService().lockUserInfo();

			long nowTime = System.currentTimeMillis();
			long quiteTime = SpUtil.get(CBRepository.getContext(), SpUtil.PRE_QUIT_TIME, 0L);
			if (nowTime - quiteTime > periodTime_72_hour) {
				//退出app时间大于72小时，退出登陆
				ServiceRepo.getUserService().logOut();
			}
		}
	}

	@Override
	public void onActivityStarted(Activity activity) {
		++started;
	}

	@Override
	public void onActivityResumed(Activity activity) {

		currentAcitivty = new WeakReference<>(activity);

		current++;
		++resumed;

		if (isAppForeground() && isBackGround) {
			//从后台切到前台

			DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appGroundFore, "");

			isBackGround = false;

			long nowTime = System.currentTimeMillis();

			if (nowTime - stopTime > periodTime_72_hour) {
				//后台进入前台时间大于72小时，退出登陆
				ServiceRepo.getUserService().logOut();
			} else {
				//小于72小时

				//设置了指纹或者手势解锁
				if (nowTime - stopTime > periodTime) {
					//超过15分钟，锁上
					ServiceRepo.getUserService().lockUserInfo();
				}
			}
		}
	}

	@Override
	public void onActivityPaused(Activity activity) {
		++paused;
		current--;
	}

	@Override
	public void onActivityStopped(Activity activity) {
		++stopped;
		if (isAppVisible()) {
			return;
		}

		//从前台切到后台
		DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appForeground, "");

		isBackGround = true;

		stopTime = System.currentTimeMillis();
		if (current == 0) {
			SpUtil.put(CBRepository.getContext(), SpUtil.PRE_QUIT_TIME, stopTime, true);
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		destroyed++;

		synchronized (weakReferences) {
			Iterator<WeakReference<Activity>> it = weakReferences.iterator();
			while (it.hasNext()) {
				final WeakReference<Activity> weakReference = it.next();
				if (weakReference.get() == null || weakReference.get() == activity) {
					it.remove();
				}
			}
		}
	}


	public boolean isAppVisible() {
		return started > stopped;
	}

	public boolean isAppForeground() {
		return resumed > paused;
	}

	public void recreateAll() {
		SpUtil.putAppRecreate(true);
		SpUtil.putFragmentRecreate(true);
		synchronized (weakReferences) {
			final Iterator<WeakReference<Activity>> it = weakReferences.iterator();
			while (it.hasNext()) {
				final Activity activity = it.next().get();
				if (activity != null && !"SettingDefinedActivity".equals(activity.getClass().getSimpleName())) {
					activity.recreate();
				}
			}
		}
	}
}
