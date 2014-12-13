package com.github.mobile.ui.notification;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jacob on 2014-12-12.
 */
public class NotificationDataSource implements DataSource<Notification>{
    private static String TAG = "NotificationDataSource";

    public static String TABLE_NAME = "notifications";
    public static String COLUMN_ID = "id";
    public static String COLUMN_CONTENT_TITLE = "contentTitle";
    public static String COLUMN_CONTENT_TEXT = "contentText";
    public static String COLUMN_DATE = "date";
    public static String COLUMN_HAS_BEEN_VIEWED = "hasBeenViewed";

    private static List<DataSourceListener> listenerList = new ArrayList<DataSourceListener>();

    public static String getCreateTableString(){
        return "CREATE TABLE "+TABLE_NAME+" ("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COLUMN_CONTENT_TITLE+" TEXT, "+COLUMN_CONTENT_TEXT+" TEXT, " +
            COLUMN_DATE+" TEXT, "+COLUMN_HAS_BEEN_VIEWED+" INTEGER);";
    }

    private static String[] AllColumns = {COLUMN_ID, COLUMN_CONTENT_TITLE, COLUMN_CONTENT_TEXT, COLUMN_DATE, COLUMN_HAS_BEEN_VIEWED};



    public NotificationDataSource() {

    }

    public void create(SQLiteDatabase database, List<Notification> notifications) {
        ContentValues values = new ContentValues();
        for(Notification notification : notifications){
            values.clear();
            values.put(COLUMN_CONTENT_TITLE, notification.getContentTitle());
            values.put(COLUMN_CONTENT_TEXT, notification.getContentText());
            values.put(COLUMN_DATE, parseDateToString(notification.getDate()));
            values.put(COLUMN_HAS_BEEN_VIEWED, notification.hasBeenViewed());
            long insertId = database.insert(TABLE_NAME, null, values);
        }
        Log.d(TAG, "Adding list of notifications to the database");
        notifyListenerCreate();
    }

    @Override
    public void create(SQLiteDatabase database, Notification notification) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTENT_TITLE, notification.getContentTitle());
        values.put(COLUMN_CONTENT_TEXT, notification.getContentText());
        values.put(COLUMN_DATE, parseDateToString(notification.getDate()));
        values.put(COLUMN_HAS_BEEN_VIEWED, notification.hasBeenViewed());

        long insertId = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "Adding one notification to the database");
        notifyListenerCreate();
    }

    @Override
    public List<Notification> read(SQLiteDatabase database, int numberOfResults) {
        if(numberOfResults <= 0){
            numberOfResults = 10;
        }
        List<Notification> notifications = new ArrayList<Notification>();

        Cursor cursor = database.query(TABLE_NAME, AllColumns, null, null, null, null, COLUMN_DATE + " DESC", numberOfResults + "");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Notification notification = cursorToNotification(cursor);
            notifications.add(notification);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return notifications;
    }

    @Override
    public void update(SQLiteDatabase database, Notification notification) {
        if(notification.getId() >= 0){
            ContentValues values = new ContentValues();
            values.put(COLUMN_CONTENT_TITLE, notification.getContentTitle());
            values.put(COLUMN_CONTENT_TEXT, notification.getContentText());
            values.put(COLUMN_DATE, parseDateToString(notification.getDate()));
            values.put(COLUMN_HAS_BEEN_VIEWED, notification.hasBeenViewed());

            long insertId = database.update(TABLE_NAME, values, COLUMN_ID + "=" + notification.getId(), null);
        }else{
            Log.e(TAG, "Notification had no id set!");
        }
        notifyListenerUpdate();
    }

    @Override
    public void delete(SQLiteDatabase database, Notification notification) {
        long id = notification.getId();
        database.delete(TABLE_NAME, COLUMN_ID + " = " + id, null);
        notifyListenerDelete();
    }

    public static boolean addListener(DataSourceListener listener) {
        Log.d(TAG, "Listener added");
        return listenerList.add(listener);
    }

    public static boolean removeListener(DataSourceListener listener) {
        Log.d(TAG, "Listener removed");
        return listenerList.remove(listener);
    }

    private Notification cursorToNotification(Cursor cursor) {
        Notification notification = new Notification();
        notification.setId(cursor.getInt(0));
        notification.setContentTitle(cursor.getString(1));
        notification.setContentText(cursor.getString(2));
        notification.setDate(parseStringToDate(cursor.getString(3)));
        notification.setHasBeenViewed(cursor.getInt(4) == 1);

        return notification;
    }

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private Date parseStringToDate(String date){
        if (date == null) {
            return null;
        } else {
            //"2014-10-20T17:06:48.172+02:00"
            Date realDate = null;
            try{
                realDate = formatter.parse(date);

            }catch(ParseException e){
                e.printStackTrace();
            }
            return realDate;
        }
    }

    private String parseDateToString(Date date){
        if (date == null) {
            return null;
        } else {
            //"2014-10-20T17:06:48.172+02:00"
            return formatter.format(date);
        }
    }


    private synchronized static void notifyListenerCreate() {
        Log.d(TAG, "Notify listener for create");
        for(DataSourceListener listener : listenerList){
            Log.d(TAG, "loop");
            listener.dataSourceAdded();
        }
    }


    private synchronized static void notifyListenerUpdate() {
        Log.d(TAG, "Notify listener for update");
        for(DataSourceListener listener : listenerList){
            listener.dataSourceUpdated();
        }
    }


    private synchronized static void notifyListenerDelete() {
        Log.d(TAG, "Notify listener for delete");
        for(DataSourceListener listener : listenerList){
            listener.dataSourceDeleted();
        }
    }
}
