package com.jqorz.picbox;

import android.app.Application;

import com.jqorz.picbox.BuildConfig;
import com.jqorz.picbox.utils.AppConfig;

/**
 * @author jqorz
 * @since 2018/7/23
 */
public class MyApplication extends Application {
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);
    }
}
