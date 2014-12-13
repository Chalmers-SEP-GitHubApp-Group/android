package com.github.mobile.ui.notification;

import android.content.Context;

/**
 * Created by Jacob on 2014-12-12.
 */
public class DatabaseManager {

    private static DBHelper dbHelper;


    public static synchronized DBHelper getInstance(Context context){
        if(dbHelper == null){
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    public static synchronized DBHelper getInstance(){
        return dbHelper;
    }
}
