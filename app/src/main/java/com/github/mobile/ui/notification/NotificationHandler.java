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


//    private void fireNotification(Notification notification){
//        Intent resultIntent = new Intent(context, MainActivity.class);
//        resultIntent.setAction(VIEW.NOTIFICATIONS.getName());
//
//        PendingIntent contentIntent = PendingIntent.getActivity(context, notification.getId(), resultIntent,PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        long[] pattern = {500,500,500,500,500,500,500,500,500};
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        builder.setContentIntent(contentIntent);
//        builder.setSmallIcon(R.drawable.ic_launcher);
//        builder.setContentTitle(notification.getContentTitle());
//        builder.setContentText(notification.getContentText());
//        if(Preferences.getSoundIsOn()){
//            builder.setSound(alarmSound);
//        }
//        builder.setVibrate(pattern);
//        builder.setAutoCancel(true);
//        if(notification instanceof CommitNotification){
//            CommitNotification commitNot = (CommitNotification) notification;
//            builder.setWhen(commitNot.getData().getDate().getTime());
//        }
//
//        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(notification.getId(), builder.build());
//        for(NotificationListener listener : listenerList){
//            listener.notificationRecieved();
//        }
//    }
//
//    public static void viewedNotification(Notification notification){
//        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Log.d("PrincePolo", "cancel id: " + notification.getId());
//        notificationManager.cancel(notification.getId());
//    }
}
