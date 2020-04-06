package com.jqorz.picbox.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.core.os.CancellationSignal;

import com.jqorz.picbox.frame.MainActivity;
import com.jqorz.picbox.R;
import com.jqorz.picbox.view.ninepointlock.NineLockView;

/**
 * @author jqorz
 * @since 2018/8/15
 */
public class DialogHelper {

    public static void createNoHardwareDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.no_sensor_dialog_title)
                .setMessage(R.string.no_sensor_dialog_message)
                .setIcon(R.drawable.ic_warning)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel_btn_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .show();
    }

    public static void createNoFingerprintDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.no_fingerprint_enrolled_dialog_title)
                .setMessage(R.string.no_fingerprint_enrolled_dialog_message)
                .setIcon(R.drawable.ic_warning)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel_btn_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .show();
    }

    public static void createTestDialog(final Activity activity, final FingerprintResultHelper.FingerprintResultListener fingerprintResultListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.nine_point_lock_dialog_title);
        builder.setView(R.layout.layout_nine_point_lock);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.cancel_btn_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fingerprintResultListener.onFingerprintFail();
                activity.finish();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fingerprintResultListener.onFingerprintSuccess();
            }
        });
        AlertDialog dialog = builder.create();
        NineLockView nineLockView = dialog.findViewById(R.id.mScreenLockView);
        dialog.show();

    }

    public static AlertDialog createProgressDialog(final Activity activity, boolean isLock) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(isLock ? R.string.pic_lock : R.string.pic_unlock);
        builder.setView(R.layout.layout_progress_bar);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static AlertDialog createCheckFingerprintDialog(final Activity activity, final CancellationSignal cancellationSignal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.fingerprint_wait_dialog_title);
        builder.setMessage(R.string.fingerprint_wait_dialog_message);
        builder.setIcon(R.drawable.ic_fingerprint);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.cancel_btn_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancellationSignal.cancel();
                activity.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }


    public static void showRequestPermissionDialog(final Activity activity, final String[] permissions) {
        new AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.my_app_name) + "需要存储权限&指纹权限才能正常使用")
                .setCancelable(false)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.requestPermissions(permissions, MainActivity.REQUEST_PERMISSIONS);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }
}
