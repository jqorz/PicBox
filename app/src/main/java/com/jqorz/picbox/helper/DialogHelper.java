package com.jqorz.picbox.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.os.CancellationSignal;

import com.jqorz.picbox.MainActivity;
import com.jqorz.picbox.R;

/**
 * @author jqorz
 * @since 2018/8/15
 */
public class DialogHelper {
    private static AlertDialog dialog;

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
                .create().show();
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
                .create().show();
    }

    public static void createCheckFingerprintDialog(final Activity activity, final CancellationSignal cancellationSignal) {
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
        dialog = builder.create();
        dialog.show();
    }

    public static void cancelCheckFingerprintDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
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
