package com.github.mobile.ui.notification;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by Jacob on 2014-12-12.
 */
public interface DataSource<E> {
    public void create(SQLiteDatabase database, E data);
    public List<E> read(SQLiteDatabase database, int numberOfResults);
    public void update(SQLiteDatabase database, E data);
    public void delete(SQLiteDatabase database, E data);
}
