package com.cioc.monomerce.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by admin on 30/04/18.
 */

public class BackgroundReceiver extends BroadcastReceiver {
    SessionManager sessionManager;
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        sessionManager = new SessionManager(context);
        if (sessionManager.getCsrfId() != "" && sessionManager.getSessionId() != "") {
            context.startService(new Intent(context, BackgroundService.class));
        }
    }
}