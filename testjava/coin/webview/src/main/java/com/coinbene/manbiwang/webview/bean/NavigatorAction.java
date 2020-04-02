package com.coinbene.manbiwang.webview.bean;

public class NavigatorAction extends BaseAction {

    protected String title;


    //设置webview右上角文案和动作
    protected String text;  //右上角文字，优先解析icon，icon不为"share"，才显示text
    protected String icon;  //icon为 "share" 显示分享图标
    protected String url;  //右上角点击跳转的schema

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
