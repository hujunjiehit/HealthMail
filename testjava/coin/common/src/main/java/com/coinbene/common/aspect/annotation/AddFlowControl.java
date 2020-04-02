package com.coinbene.common.aspect.annotation;

import com.coinbene.common.rxjava.FlowControlStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by june
 * on 2019-09-26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AddFlowControl {
	FlowControlStrategy strategy() default FlowControlStrategy.throttleFirst;
	int timeInterval() default 2000;
}
