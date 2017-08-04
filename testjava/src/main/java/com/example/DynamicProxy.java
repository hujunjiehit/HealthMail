package com.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by june on 2017/8/3.
 */

public class DynamicProxy implements InvocationHandler {

  private Object obj;

  public DynamicProxy(Object obj) {
    this.obj = obj;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object result = method.invoke(obj,args);
    return result;
  }
}
