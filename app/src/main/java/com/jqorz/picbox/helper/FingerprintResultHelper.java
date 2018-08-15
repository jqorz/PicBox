package com.jqorz.picbox.helper;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v4.os.CancellationSignal;
import android.util.Log;

import com.jqorz.picbox.R;
import com.jqorz.picbox.utils.ToastUtil;

/**
 * @author jqorz
 * @since 2018/8/15
 */
public class FingerprintResultHelper {
    public static final int MSG_AUTH_ERROR = 0x11;
    public static final int MSG_AUTH_HELP = 0x12;
    public static final int MSG_AUTH_SUCCESS = 0x10;
    public static final int MSG_AUTH_FAILED = 0x13;
    private Context mContext;
    private String TAG = getClass().getSimpleName();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d(TAG, "msg: " + msg.what + " ,arg1: " + msg.arg1);
            switch (msg.what) {
                case MSG_AUTH_SUCCESS:
                    setResultInfo(R.string.fingerprint_success);
                    DialogHelper.cancelCheckFingerprintDialog();
                    break;
                case MSG_AUTH_FAILED:
                    setResultInfo(R.string.fingerprint_not_recognized);
                    break;
                case MSG_AUTH_ERROR:
                    handleErrorCode(msg.arg1);
                    break;
                case MSG_AUTH_HELP:
                    handleHelpCode(msg.arg1);
                    break;
            }
        }
    };

    public FingerprintResultHelper(Context mContext) {
        this.mContext = mContext;
    }

    public Handler getHandler() {
        return handler;
    }

    private void handleErrorCode(int code) {
        switch (code) {
            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
//                setResultInfo(R.string.ErrorCanceled_warning);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE:
                setResultInfo(R.string.ErrorHwUnavailable_warning);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
                setResultInfo(R.string.ErrorLockout_warning);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_NO_SPACE:
                setResultInfo(R.string.ErrorNoSpace_warning);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                setResultInfo(R.string.ErrorTimeout_warning);
                break;
            case FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS:
                setResultInfo(R.string.ErrorUnableToProcess_warning);
                break;
        }
    }

    private void handleHelpCode(int code) {
        switch (code) {
            case FingerprintManager.FINGERPRINT_ACQUIRED_GOOD:
                setResultInfo(R.string.AcquiredGood_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY:
                setResultInfo(R.string.AcquiredImageDirty_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT:
                setResultInfo(R.string.AcquiredInsufficient_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL:
                setResultInfo(R.string.AcquiredPartial_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST:
                setResultInfo(R.string.AcquiredTooFast_warning);
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW:
                setResultInfo(R.string.AcquiredToSlow_warning);
                break;
        }
    }

    private void setResultInfo(@StringRes int stringRes) {
        ToastUtil.showToast(mContext.getString(stringRes));
    }
}
