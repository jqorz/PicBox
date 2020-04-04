package com.jqorz.picbox.utils;

import android.app.Application;

/**
 * @author jqorz
 * @since 2018/7/23
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);
    }
}
