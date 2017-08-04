package com.example;


import java.lang.reflect.Proxy;

public class MyClass {

  public static void main(String[] args) {
    System.out.println("Hello");

    ILawsuit xiaomin = new Xiaoming();
    ILawsuit xiaohong = new Xiaohong();

    //构造动态代理
    DynamicProxy dynamicProxy = new DynamicProxy(xiaohong);

    //获取classloader
    ClassLoader cl = xiaomin.getClass().getClassLoader();
    ILawsuit proxy = (ILawsuit)Proxy.newProxyInstance(cl,new Class[] {ILawsuit.class},dynamicProxy);

    proxy.submit();
    proxy.burden();
    proxy.defend();
    proxy.finish();
  }
}
