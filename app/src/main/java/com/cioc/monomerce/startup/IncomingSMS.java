package com.cioc.monomerce.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class IncomingSMS extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();
            //Pass on the text to our listener.
            if (mListener == null) {
                return;
            }
            mListener.messageReceived(messageBody);
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
