package com.zh.android.minihandler.sample;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {
    private static final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private ToastUtil() {
    }

    public static void toast(Context context, String msg) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        };
        if (Looper.getMainLooper() == Looper.myLooper()) {
            task.run();
        } else {
            mMainHandler.post(task);
        }
    }
}