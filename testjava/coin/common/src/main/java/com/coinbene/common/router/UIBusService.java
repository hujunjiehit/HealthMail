package com.coinbene.common.router;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UIBusService implements UIBus {

    private static volatile UIBus instance;

    List<UIRouter> uiRouters = new CopyOnWriteArrayList<UIRouter>();
    HashMap<UIRouter, Integer> priorities = new HashMap<UIRouter, Integer>();

    private UIBusService(){

    }

    public static UIBus getInstance() {
        if(instance == null) {
            synchronized (UIBus.class){
                if(instance == null) {
                    instance = new UIBusService();
                }
            }
        }
        return instance;
    }

    @Override
    public void register(UIRouter router, int priority) {
        int i = 0;
        for (UIRouter temp : uiRouters) {
            Integer tp = priorities.get(temp);
            if (tp == null || tp <= priority) {
                break;
            }
            i++;
        }
        uiRouters.add(i, router);
        priorities.put(router, Integer.valueOf(priority));
    }

    @Override
    public void register(UIRouter router) {
        register(router, UIBusService.PRIORITY_NORMAL);
    }

    @Override
    public void unregister(UIRouter router) {
        int i = 0;
        for (UIRouter temp : uiRouters) {
            if (router == temp) {
                break;
            }
            i++;
        }
        uiRouters.remove(i);
        priorities.remove(router);
    }

    @Override
    public boolean openUri(Context context, String url, Bundle bundle) {
        openUri(context, url, bundle, 0);
        return true;
    }

    @Override
    public boolean openUri(Context context, String url, Bundle bundle, int requestCode) {
        url = url.trim();
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            return openUri(context, uri, bundle, requestCode);
        }
        return true;
    }

    @Override
    public boolean openUri(Context context, Uri uri, Bundle bundle) {
        openUri(context, uri, bundle, 0);
        return true;
    }

    @Override
    public boolean openUri(Context context, Uri uri, Bundle bundle, int requestCode) {
        if (uri == null) {
            return false;
        }
        String scheme = uri.getScheme();
        if (scheme == null) {
            return false;
        }
        return openTargetPage(context, uri, bundle, requestCode);
    }

    private boolean openTargetPage(Context context, Uri uri, Bundle bundle, int requestCode) {
        for (UIRouter router : uiRouters) {
            try {
                if (router.verifyUri(uri) && router.openUri(context, uri, bundle, requestCode)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean verifyUri(Uri uri) {
        for(UIRouter router : uiRouters) {
            if(router.verifyUri(uri)) {
                return true;
            }
        }

        //进行wap降级或者更新提示

        return false;
    }
}
