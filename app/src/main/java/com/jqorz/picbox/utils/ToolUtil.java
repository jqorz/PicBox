package com.jqorz.picbox.utils;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.util.TypedValue;


/**
 * @author jqorz
 * @since 2018/7/24
 */
public class ToolUtil {
    /**
     * 不要使用此方法，因为dimen有很多个文件，使用此方法只会取得原始尺寸
     */
    public static int dp2px(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    /**
     * 取dp尺寸使用此方法
     */
    public static int dp2px(Context context, @DimenRes int dimenId) {
        return context.getResources().getDimensionPixelSize(dimenId);
    }
}
