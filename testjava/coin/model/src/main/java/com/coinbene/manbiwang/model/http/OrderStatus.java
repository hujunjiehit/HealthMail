package com.coinbene.manbiwang.model.http;

public class OrderStatus {

    private String orderStatu;
    private int orderStatuColor;
    private String orderDis;
    private int orderDisColor;

    public OrderStatus(String orderStatu, int orderStatuColor, String orderDis, int orderDisColor) {
        this.orderStatu = orderStatu;
        this.orderStatuColor = orderStatuColor;
        this.orderDis = orderDis;
        this.orderDisColor = orderDisColor;
    }

    public String getOrderStatu() {
        return orderStatu;
    }

    public void setOrderStatu(String orderStatu) {
        this.orderStatu = orderStatu;
    }

    public int getOrderStatuColor() {
        return orderStatuColor;
    }

    public void setOrderStatuColor(int orderStatuColor) {
        this.orderStatuColor = orderStatuColor;
    }

    public String getOrderDis() {
        return orderDis;
    }

    public void setOrderDis(String orderDis) {
        this.orderDis = orderDis;
    }

    public int getOrderDisColor() {
        return orderDisColor;
    }

    public void setOrderDisColor(int orderDisColor) {
        this.orderDisColor = orderDisColor;
    }
}
