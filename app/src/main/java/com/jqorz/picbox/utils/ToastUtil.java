package com.jqorz.picbox.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.jqorz.picbox.R;

import io.reactivex.Observable;

/**
 * Toast工具类，可避免吐司长时间显示
 */
public class ToastUtil {
    @ColorInt
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");


    @ColorInt
    private static final int NORMAL_COLOR = Color.parseColor("#71000000");


    private static Toast currentToast;

    //*******************************************普通 使用ApplicationContext 方法*********************
    /**
     * Toast 替代方法 ：立即显示无需等待
     */
    private static Toast mToast;
    private static long mExitTime;

    @SuppressLint("CheckResult")
    private static void normal(@NonNull final String message) {
        Observable.just(0).compose(RxTransformer.switchSchedulers()).subscribe(integer -> normal(AppConfig.getApp(), message, Toast.LENGTH_SHORT, null, false).show());
    }


    @CheckResult
    private static Toast normal(@NonNull Context context, @NonNull String message, int duration, Drawable icon, boolean withIcon) {
        return custom(context, message, icon, DEFAULT_TEXT_COLOR, duration, withIcon);
    }


    @CheckResult
    private static Toast custom(@NonNull Context context, @NonNull String message, Drawable icon, @ColorInt int textColor, int duration, boolean withIcon) {
        return custom(context, message, icon, textColor, -1, duration, withIcon, false);
    }

    //*******************************************内需方法********************************************


    @CheckResult
    private static Toast custom(@NonNull Context context, @NonNull String message, Drawable icon, @ColorInt int textColor, @ColorInt int tintColor, int duration, boolean withIcon, boolean shouldTint) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = new Toast(context);
        final View toastLayout = LayoutInflater.from(context).inflate(R.layout.toast_center, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint) {
            drawableFrame = shapeDrawableFrame(context, tintColor);
        } else {
            drawableFrame = shapeDrawableFrame(context, NORMAL_COLOR);
        }
        setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null) {
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            }
            setBackground(toastIcon, icon);
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setTextColor(textColor);
        toastTextView.setText(message);

        currentToast.setView(toastLayout);
        currentToast.setDuration(duration);
        currentToast.setGravity(Gravity.CENTER, 0, 0);
        return currentToast;
    }

    private static Drawable shapeDrawableFrame(@NonNull Context context, @ColorInt int tintColor) {
        GradientDrawable toastDrawable = (GradientDrawable) getDrawable(context, R.drawable.toast_bg);
        toastDrawable.setColor(tintColor);
        return toastDrawable;
    }
    //===========================================内需方法============================================


    //******************************************系统 Toast 替代方法***************************************

    private static void setBackground(@NonNull View view, Drawable drawable) {
        view.setBackground(drawable);
    }

    private static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }


    /**
     * 封装了Toast的方法 :需要等待
     */
    public static void showToastShort(String str) {
        Toast.makeText(AppConfig.getApp(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 封装了Toast的方法 :需要等待
     */
    public static void showToastShort(int resId) {
        Toast.makeText(AppConfig.getApp(), AppConfig.getApp().getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 封装了Toast的方法 :需要等待
     */
    public static void showToastLong(String str) {
        Toast.makeText(AppConfig.getApp(), str, Toast.LENGTH_LONG).show();
    }

    /**
     * 封装了Toast的方法 :需要等待
     */
    public static void showToastLong(int resId) {
        Toast.makeText(AppConfig.getApp(), AppConfig.getApp().getString(resId), Toast.LENGTH_LONG).show();
    }

    /**
     * Toast 替代方法 ：立即显示无需等待
     *
     * @param msg 显示内容
     */
    public static void showToast(String msg) {
        ToastUtil.normal(msg);
    }

    /**
     * Toast 替代方法 ：立即显示无需等待
     *
     * @param resId String资源ID
     */
    @SuppressLint("ShowToast")
    public static void showToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(AppConfig.getApp(), AppConfig.getApp().getString(resId), Toast.LENGTH_LONG);
        } else {
            mToast.setText(AppConfig.getApp().getString(resId));
        }
        mToast.show();
    }

    /**
     * Toast 替代方法 ：立即显示无需等待
     *
     * @param context  实体
     * @param resId    String资源ID
     * @param duration 显示时长
     */
    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getString(resId), duration);
    }
    //===========================================Toast 替代方法======================================

    /**
     * Toast 替代方法 ：立即显示无需等待
     *
     * @param context  实体
     * @param msg      要显示的字符串
     * @param duration 显示时长
     */
    @SuppressLint("ShowToast")
    public static void showToast(Context context, String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static boolean doubleClickExit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtil.normal("再按一次退出");
            mExitTime = System.currentTimeMillis();
            return false;
        }
        return true;
    }

}
