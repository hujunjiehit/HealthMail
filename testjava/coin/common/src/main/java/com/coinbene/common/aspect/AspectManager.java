package com.coinbene.common.aspect;

import com.coinbene.common.rxjava.FlowControl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by june
 * on 2019-09-26
 */
public class AspectManager {

	private static volatile AspectManager mInstance;

	private Map<String, FlowControl> flowControlMap;

	private AspectManager() {

	}

	public static AspectManager getInstance() {
		if (mInstance == null) {
			synchronized (AspectManager.class) {
				if (mInstance == null) {
					mInstance = new AspectManager();
				}
			}
		}
		return mInstance;
	}

	public Map<String, FlowControl> getFlowControlMap() {
		if (flowControlMap == null) {
			flowControlMap = new HashMap<>();
		}
		return flowControlMap;
	}

}
