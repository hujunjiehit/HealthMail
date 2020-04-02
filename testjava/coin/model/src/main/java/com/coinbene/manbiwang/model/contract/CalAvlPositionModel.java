package com.coinbene.manbiwang.model.contract;

public class CalAvlPositionModel {

	private String inputPrice;
	private String avlBalance;
	private int curLever;
	private int currentDirection;
	private int curOrderType;

	public int getCurOrderType() {
		return curOrderType;
	}

	public void setCurOrderType(int curOrderType) {
		this.curOrderType = curOrderType;
	}

	public int getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(int currentDirection) {
		this.currentDirection = currentDirection;
	}

	public CalAvlPositionModel() {
	}

	public String getInputPrice() {
		return inputPrice;
	}

	public void setInputPrice(String inputPrice) {
		this.inputPrice = inputPrice;
	}


	public String getAvlBalance() {
		return avlBalance;
	}

	public void setAvlBalance(String avlBalance) {
		this.avlBalance = avlBalance;
	}

	public int getCurLever() {
		return curLever;
	}

	public void setCurLever(int curLever) {
		this.curLever = curLever;
	}
}
