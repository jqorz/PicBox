package com.jqorz.picbox.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jqorz.picbox.R;


/**
 * @author jqorz
 * @since 2017/12/25
 * Toast工具类，可避免吐司长时间显示
 */
public class ToastUtil {


    private static Toast mCenterToast = null;

    @SuppressLint("ShowToast")
    public static void showToast(String text) {
//        if (AppConfig.getApp() == null)
//            return;
//        if (mToast == null) {
//            mToast = Toast.makeText(AppConfig.getApp(), text, Toast.LENGTH_SHORT);
//        } else {
//            mToast.setText(text);
//            mToast.setDuration(Toast.LENGTH_SHORT);
//        }
//        mToast.show();
        Context context = AppConfig.getApp();
        if (context != null) {
            View v = LayoutInflater.from(context).inflate(R.layout.toast_center, null);
            if (mCenterToast == null) {
                mCenterToast = new Toast(context);
                mCenterToast.setGravity(Gravity.CENTER, 0, 0);
                mCenterToast.setDuration(Toast.LENGTH_SHORT);
            }
            TextView tv = v.findViewById(R.id.tv_Toast);
            tv.setText(text);
            mCenterToast.setView(v);
            //显示提示
            mCenterToast.show();
        }
    }

    public static void clearToast() {
        if (mCenterToast != null) {
            mCenterToast.cancel();
        }
    }


}
