package com.github.mobile.ui.notification;


import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NotificationHandler{
    private ScheduledFuture<?> future;
    private static NotificationManager notificationManager;
    private static Context context;
    private static String TAG = "NotificationHandler";
    private Runnable runnable;
    private ScheduledExecutorService scheduler;

    public NotificationHandler(Context contexts){
        context = contexts;

        runnable = new Runnable() {
            public void run() {
                new NotificationUpdateThread().execute(context);
            }
        };

    }

    public void start(){
        scheduler = Executors.newSingleThreadScheduledExecutor();
        future = scheduler.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
    }

    public void stop(){
        if(future != null){
            future.cancel(false);
            scheduler.shutdown();
            DatabaseManager.getInstance(context).close();
        }else{
            Log.e(TAG, "you haven't started before you cancel");
        }
    }
}
