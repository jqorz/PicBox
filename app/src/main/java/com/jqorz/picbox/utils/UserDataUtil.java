package com.jqorz.picbox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 使用SharedPreference保存和读取本地数据
 */
public class UserDataUtil {
    private static final String SETTING_USE_FINGERPRINT = "SETTING_USE_FINGERPRINT";

    private static final String FILE_NAME = "user.data";
    private static SharedPreferences data;
    private volatile static UserDataUtil mInstance;

    private UserDataUtil() {
    }

    private static UserDataUtil getInstance() {
        if (mInstance == null) {
            synchronized (UserDataUtil.class) {
                if (mInstance == null) {
                    mInstance = new UserDataUtil();
                }
            }
        }
        return mInstance;
    }

    /*
     * ----------------------------对外公布的方法----------------------------
     */

    /**
     * 用户设置-使用指纹识别
     */
    public static boolean loadSettingUseFingerprint() {
        return getInstance()._loadBoolData(AppConfig.getApp(), FILE_NAME, SETTING_USE_FINGERPRINT);
    }

    public static void updateSettingUseFingerprint(  boolean use) {
        getInstance()._updateLocalData(AppConfig.getApp(), FILE_NAME, SETTING_USE_FINGERPRINT, use);
    }


    /*
    ------------------------------- 内部方法---------------------------------------
     */
    private String _loadStringData(Context context, String name, String input) {
        data = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return data.getString(input, "");
    }

    private boolean _loadBoolData(Context context, String name, String input) {
        data = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return data.getBoolean(input, false);
    }

    //默认True的Boolean
    private boolean _loadTrueBoolData(Context context, String name, String input) {
        data = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return data.getBoolean(input, true);
    }

    private int _loadIntData(Context context, String name, String input) {
        data = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return data.getInt(input, 0);
    }

    private void _clearData(Context context, String[] names) {
        for (String name : names) {
            data = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            Editor editor = data.edit();
            editor.clear();
            editor.apply();
        }

    }

    /**
     * 更改本地用户信息
     *
     * @param context 所需上下文
     * @param key     想要更改的项
     * @param content 想要更改的内容
     */
    private void _updateLocalData(Context context, String name, String key,
                                  String content) {

        data = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Editor editor = data.edit();
        editor.putString(key, content);
        editor.apply();
    }

    private void _updateLocalData(Context context, String name, String key,
                                  boolean flag) {

        data = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Editor editor = data.edit();
        editor.putBoolean(key, flag);
        editor.apply();

    }

    private void _updateLocalData(Context context, String name, String key,
                                  int m) {

        data = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Editor editor = data.edit();
        editor.putInt(key, m);
        editor.apply();

    }


}
