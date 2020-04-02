package com.coinbene.common.utils;

import com.hjq.toast.ToastUtils;

/**
 * toastutils
 */
public class ToastUtil {

//    private static ToastCompat toast = null;
//
//    public static void show(int id) {
//        show(CBRepository.getContext().getResources().getString(id));
//    }
//
//    public static void show(Context context, int id) {
//        show(context, context.getResources().getString(id));
//    }
//
//    public static void show(String msg) {
//        if (TextUtils.isEmpty(msg)) return;
//        int duration;
//        if (msg.length() > 10) {
//            duration = Toast.LENGTH_LONG;
//        } else {
//            duration = Toast.LENGTH_SHORT;
//        }
//        if (toast == null) {
//            toast = ToastCompat.makeText(CBRepository.getContext(), msg, duration);
//        } else {
//            toast.setText(msg);
//        }
//        toast.show();
//    }
//
//    public static void show(Context context, String msg) {
//        if (TextUtils.isEmpty(msg)) return;
//        int duration;
//        if (msg.length() > 10) {
//            duration = Toast.LENGTH_LONG;
//        } else {
//            duration = Toast.LENGTH_SHORT;
//        }
//        if (toast == null) {
//            toast = ToastCompat.makeText(context, msg, duration);
//        } else {
//            toast.setText(msg);
//        }
//        toast.show();
//    }


    public static void show(int id) {
        ToastUtils.show(id);
//        show(CBRepository.getContext().getResources().getString(id));
    }
    public static void show(String msg) {
        ToastUtils.show(msg);
//        show(CBRepository.getContext().getResources().getString(id));
    }

}