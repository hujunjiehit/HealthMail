package com.coinbene.manbiwang.websocket;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by dhh on 2017/10/24.
 * <p>
 * override the method of you want to use
 * <p>
 * 根据业务需求重写你想使用的方法
 */

public abstract class WebSocketSubscriber implements Observer<WebSocketInfo> {
    private boolean hasOpened;
    protected Disposable disposable;

    @Override
    public final void onNext(@NonNull WebSocketInfo webSocketInfo) {
        if (webSocketInfo.isOnOpen()) {
            hasOpened = true;
            onOpen(webSocketInfo.getWebSocket());
        } else if (webSocketInfo.getString() != null) {
            onMessage(webSocketInfo.getString());
        } else if (webSocketInfo.getByteString() != null) {
            onMessage(webSocketInfo.getByteString());
        } else if (webSocketInfo.isOnReconnect()) {
            onReconnect();
        }
    }

    /**
     * Callback when the WebSocket is opened
     *
     * @param webSocket
     */
    protected void onOpen(@NonNull WebSocket webSocket) {
        //连接成功
        onConnectSuccess();
    }

    protected void onMessage(@NonNull String text) {
    }

    protected void onMessage(@NonNull ByteString byteString) {
    }

    /**
     * Callback when the WebSocket is connect success
     */
    protected void onReconnect() {
    }

    protected void onConnectSuccess() {
    }

    protected void onClose() {
    }

    @Override
    public final void onSubscribe(Disposable disposable) {
        this.disposable = disposable;
    }

    public final void dispose() {
        Log.d("websocket", "dispose ==> " + this);
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public Disposable getDisposable() {
        return disposable;
    }

    @Override
    public final void onComplete() {
        if (hasOpened) {
            onClose();
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}
