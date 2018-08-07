package com.cioc.monomerce.backend;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

/**
 * Created by admin on 30/04/18.
 */

public class BackgroundService extends Service {
    public static final String ACTION = "com.cioc.libreerp.backendservice";
    Session session;
    SessionManager sessionManager;
    public static final long INTERVAL = 1000 * 5;//variable to execute services every 10 second
    private Handler mHandler = new Handler(); // run on another Thread to avoid crash
    private Timer mTimer = null;// timer handling
    TimerTask timerTask;
    //    boolean internetAvailable;
    Client client;
    CompletableFuture<ExitInfo> exitInfoCompletableFuture;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupported Operation");
    }

    @Override
    public void onCreate() {
        sessionManager = new SessionManager(this);

        if (mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, INTERVAL); // schedule task

    }


    //inner class of TimeDisplayTimerTask
    private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast at every 10 second
                    Boolean internetAvailable = false;
                    //Toast.makeText(getApplicationContext(), "service running" + client.toString() + exitInfoCompletableFuture.isDone() , Toast.LENGTH_SHORT).show();
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                    if (netInfo != null) {
                        internetAvailable = true;
                    }

                    if ((exitInfoCompletableFuture == null || exitInfoCompletableFuture.isDone()) && internetAvailable) {
                        session = new Session();
                        session.addOnJoinListener(this::demonstrateSubscribe);
                        client = new Client(session, "ws://wamp.cioc.in:8080/ws", "default");
                        exitInfoCompletableFuture = client.connect();
                    }
                }


                public void demonstrateSubscribe(Session session, SessionDetails details) {

                    String usrname = sessionManager.getUsername();

                    CompletableFuture<Subscription> subFuture = session.subscribe("service.self." + usrname,
                            this::onEvent);
                    subFuture.whenComplete((subscription, throwable) -> {
                        if (throwable == null) {
                            System.out.println("Subscribed to topic " + subscription.topic);
                            Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
                        } else {
                            throwable.printStackTrace();
                        }
                    });
                }

                private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
                    System.out.println(String.format("Got event: %s", args.get(0)));

                    Toast.makeText(BackgroundService.this, args.toString(), Toast.LENGTH_SHORT).show();

                    // add a notification strip here

                }
            });
        }
    }



    @Override
    public void onDestroy() {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        intent = new Intent("com.cioc.libreerp.backendservice");
        sendBroadcast(intent);
        return START_STICKY;
    }
}
