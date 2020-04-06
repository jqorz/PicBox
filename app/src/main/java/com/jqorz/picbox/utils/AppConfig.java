package com.jqorz.picbox.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.LinkedList;

/**
 * APP配置信息
 */

public class AppConfig {

    private static Application sApplication;
    private static LinkedList<Activity> sActivityList = new LinkedList<>();
    private static Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            sActivityList.remove(activity);
        }
    };

    private AppConfig() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(@NonNull final Context context) {
        AppConfig.sApplication = (Application) context.getApplicationContext();
        AppConfig.sApplication.registerActivityLifecycleCallbacks(mCallbacks);
    }

    public static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    public static LinkedList<Activity> getActivityList() {
        return sActivityList;
    }

    public static void setTopActivity(Activity activity) {
        if (sActivityList.contains(activity)) {
            if (!sActivityList.getLast().equals(activity)) {
                sActivityList.remove(activity);
                sActivityList.addLast(activity);
            }
        } else {
            sActivityList.addLast(activity);
        }
    }
}
