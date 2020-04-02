package com.coinbene.common.router;

public interface UIBus extends UIRouter{

    static final int PRIORITY_NORMAL = 0;
    static final int PRIORITY_LOW = -1000;
    static final int PRIORITY_HIGH = 1000;

    void register(UIRouter router, int priority);

    void register(UIRouter router);

    void unregister(UIRouter router);
}
