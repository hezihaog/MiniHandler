package com.zh.android.minihandler.sample;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppMonitor {
    /**
     * 注册了的监听器
     */
    private List<Callback> mCallbacks;
    /**
     * 是否初始化了
     */
    private boolean isInited;
    /**
     * 活跃Activity的数量
     */
    private int mActiveActivityCount = 0;
    /**
     * 存活的Activity数量
     */
    private int mAliveActivityCount = 0;

    public static AppMonitor get() {
        return SingleHolder.INSTANCE;
    }

    private static final class SingleHolder {
        private static final AppMonitor INSTANCE = new AppMonitor();
    }

    public void initialize(Context context) {
        if (isInited) {
            return;
        }
        mCallbacks = new CopyOnWriteArrayList<>();
        registerLifecycle(context);
        isInited = true;
    }

    /**
     * 注册生命周期
     */
    private void registerLifecycle(Context context) {
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mAliveActivityCount++;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mActiveActivityCount++;
                notifyChange();
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mActiveActivityCount--;
                notifyChange();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mAliveActivityCount--;
                notifyAppAliveChange();
            }
        });
    }

    /**
     * 判断App是否活着
     */
    public boolean isAppAlive() {
        return mAliveActivityCount > 0;
    }

    /**
     * 判断App是否在前台
     */
    public boolean isAppForeground() {
        return mActiveActivityCount > 0;
    }

    /**
     * 判断App是否在后台
     */
    public boolean isAppBackground() {
        return mActiveActivityCount <= 0;
    }

    /**
     * 通知监听者
     */
    private void notifyChange() {
        if (mActiveActivityCount > 0) {
            for (Callback callback : mCallbacks) {
                callback.onAppForeground();
            }
        } else {
            for (Callback callback : mCallbacks) {
                callback.onAppBackground();
            }
        }
    }

    /**
     * 通知监听者界面销毁
     */
    private void notifyAppAliveChange() {
        if (mAliveActivityCount == 0) {
            for (Callback callback : mCallbacks) {
                callback.onAppUIDestroyed();
            }
        }
    }

    public interface Callback {
        /**
         * 当App切换到前台时回调
         */
        void onAppForeground();

        /**
         * App切换到后台时回调
         */
        void onAppBackground();

        /**
         * App所有界面都销毁了
         */
        void onAppUIDestroyed();
    }

    public static class CallbackAdapter implements Callback {

        @Override
        public void onAppForeground() {
        }

        @Override
        public void onAppBackground() {
        }

        @Override
        public void onAppUIDestroyed() {
        }
    }

    /**
     * 注册回调
     */
    public void register(Callback callback) {
        if (mCallbacks.contains(callback)) {
            return;
        }
        mCallbacks.add(callback);
    }

    /**
     * 注销回调
     */
    public void unRegister(Callback callback) {
        if (!mCallbacks.contains(callback)) {
            return;
        }
        mCallbacks.remove(callback);
    }
}