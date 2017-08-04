package com.example;

/**
 * Created by june on 2017/8/3.
 */

public class Xiaohong implements ILawsuit {

  @Override
  public void submit() {
    System.out.println("submit2");
  }

  @Override
  public void burden() {
    System.out.println("burden2");

  }

  @Override
  public void defend() {
    System.out.println("defend2");
  }

  @Override
  public void finish() {
    System.out.println("finish2");
  }
}
