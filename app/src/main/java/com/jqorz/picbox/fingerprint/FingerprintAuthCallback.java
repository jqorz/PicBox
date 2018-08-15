package com.jqorz.picbox.fingerprint;


import android.os.Handler;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import com.jqorz.picbox.helper.FingerprintResultHelper;

/**
 * @author jqorz
 * @since 2018/8/13
 */

public class FingerprintAuthCallback extends FingerprintManagerCompat.AuthenticationCallback {

    private Handler handler;

    public FingerprintAuthCallback(Handler handler) {
        super();

        this.handler = handler;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);

        if (handler != null) {
            handler.obtainMessage(FingerprintResultHelper.MSG_AUTH_ERROR, errMsgId, 0).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        super.onAuthenticationHelp(helpMsgId, helpString);

        if (handler != null) {
            handler.obtainMessage(FingerprintResultHelper.MSG_AUTH_HELP, helpMsgId, 0).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        if (handler != null) {
            handler.obtainMessage(FingerprintResultHelper.MSG_AUTH_SUCCESS).sendToTarget();
        }

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();

        if (handler != null) {
            handler.obtainMessage(FingerprintResultHelper.MSG_AUTH_FAILED).sendToTarget();
        }
    }
}