package com.example;

/**
 * Created by june on 2017/8/3.
 */

public class Xiaoming implements ILawsuit {
  @Override
  public void submit() {
    System.out.println("submit1");
  }

  @Override
  public void burden() {
    System.out.println("burden1");

  }

  @Override
  public void defend() {
    System.out.println("defend1");
  }

  @Override
  public void finish() {
    System.out.println("finish1");
  }
}
