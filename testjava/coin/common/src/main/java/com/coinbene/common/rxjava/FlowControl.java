package com.coinbene.common.rxjava;

import androidx.annotation.NonNull;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by june
 * on 2019-07-31
 */
public abstract class FlowControl {

	private Flowable<String> flowable;
	private FlowableEmitter<String> mEmitter;
	private Disposable mDisposable;

	private FlowControlStrategy flowControlStrategy; // 流量控制策略
	private int timeInterval; // 单位毫秒

	private ProceedingJoinPoint proceedingJoinPoint;

	/**
	 * 必须new带参数的实例
	 */
	public FlowControl(){
	}

	public FlowControl(FlowControlStrategy strategy, int timeInterval) {
		this.flowControlStrategy = strategy;
		this.timeInterval = timeInterval;
	}

	public void sendRequest(String tag) {
		if (flowable == null) {
			flowable = Flowable.create(flowableEmitter -> mEmitter = flowableEmitter, BackpressureStrategy.ERROR);
		}

		if (mDisposable == null || mDisposable.isDisposed()) {
			if (flowControlStrategy == FlowControlStrategy.throttleFirst) {
				mDisposable = flowable.throttleFirst(timeInterval, TimeUnit.MILLISECONDS)
						.subscribe(s -> doAction(s));
			} else if (flowControlStrategy == FlowControlStrategy.throttleLast) {
				mDisposable = flowable.throttleLast(timeInterval, TimeUnit.MILLISECONDS)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(s -> doAction(s));
			} else if (flowControlStrategy == FlowControlStrategy.debounce){
				mDisposable = flowable.debounce(timeInterval, TimeUnit.MILLISECONDS)
						.subscribe(s -> doAction(s));
			}
		}

		if (mEmitter != null) {
			mEmitter.onNext(tag);
		}
	}

	public void destroy() {
		if (mDisposable != null && !mDisposable.isDisposed()) {
			mDisposable.dispose();
		}
		flowable = null;
		mEmitter = null;
		mDisposable = null;
		proceedingJoinPoint = null;
		flowControlStrategy = null;
	}

	public boolean check(){
		return flowControlStrategy != null;
	}
	/**
	 * sendRequest 会被限流，如果通过了限流策略才会执行doAction方法
	 * @param tag
	 */
	public abstract void doAction (String tag);

	public void setFlowControlStrategy(FlowControlStrategy flowControlStrategy) {
		this.flowControlStrategy = flowControlStrategy;
	}

	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

	public FlowControlStrategy getFlowControlStrategy() {
		return flowControlStrategy;
	}

	public int getTimeInterval() {
		return timeInterval;
	}

	public ProceedingJoinPoint getProceedingJoinPoint() {
		return proceedingJoinPoint;
	}

	public void setProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
		this.proceedingJoinPoint = proceedingJoinPoint;
	}

	@NonNull
	@Override
	public String toString() {
		return "Strategy:" + flowControlStrategy + ", timeInterval: " + timeInterval + "  :" + hashCode();
	}
}
