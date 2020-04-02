package com.coinbene.common.websocket.model;

/**
 * Created by june
 * on 2020-01-21
 */
public class WsKline {

	/**
	 * c : 7513.01
	 * h : 7513.37
	 * l : 7510.02
	 * o : 7510.24
	 * v : 60.5929
	 * t : 1578278880
	 */

	private float c;
	private float h;
	private float l;
	private float o;
	private float v;
	private long t;

	public float getC() {
		return c;
	}

	public void setC(float c) {
		this.c = c;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}

	public float getL() {
		return l;
	}

	public void setL(float l) {
		this.l = l;
	}

	public float getO() {
		return o;
	}

	public void setO(float o) {
		this.o = o;
	}

	public float getV() {
		return v;
	}

	public void setV(float v) {
		this.v = v;
	}

	public long getT() {
		return t;
	}

	public void setT(long t) {
		this.t = t;
	}
}
