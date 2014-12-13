package com.github.mobile.ui.notification;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notification.db";
    private static final int DATABASE_VERSION = 1;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(RepositoryDataSource.getCreateTableString());
        database.execSQL(RepositoryBranchDataSource.getCreateTableString());
        database.execSQL(NotificationDataSource.getCreateTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
            "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS "+RepositoryDataSource.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+RepositoryBranchDataSource.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+NotificationDataSource.TABLE_NAME);
        onCreate(db);
    }

}