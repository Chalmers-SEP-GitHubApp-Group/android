package com.github.mobile.ui.notification;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.TypedResource;

/**
 * Created by Jacob on 2014-12-12.
 */
public class RepositoryBranchDataSource implements DataSource<RepositoryBranchWrapper>{
    private static String TAG = "RepositoryDataSource";

    public static String TABLE_NAME = "branches";
    public static String COLUMN_ID = "id";
    public static String COLUMN_REPO_ID = "repoId";
    public static String COLUMN_NAME = "name";
    public static String COLUMN_TYPE = "type";
    public static String COLUMN_URL = "url";
    public static String COLUMN_SHA = "sha";

    private static List<DataSourceListener> listnerList = new ArrayList<DataSourceListener>();

    public static String getCreateTableString(){
        return "CREATE TABLE "+TABLE_NAME+" ("+COLUMN_ID+" INTEGER PRIMARY KEY, "+COLUMN_REPO_ID+" INTEGER, "+COLUMN_NAME+" TEXT, "+COLUMN_TYPE+" TEXT, "+COLUMN_URL+" TEXT, "+COLUMN_SHA+" TEXT);";
    }

    private static String[] AllColumns = {COLUMN_ID, COLUMN_REPO_ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_URL, COLUMN_SHA};



    public RepositoryBranchDataSource() {
        listnerList = new ArrayList<DataSourceListener>();
    }

    public void createMany(SQLiteDatabase database, List<RepositoryBranch> branchList, Repository repo) {
        for(RepositoryBranch branch : branchList){
            ContentValues values = new ContentValues();

            values.put(COLUMN_REPO_ID, repo.getId());
            values.put(COLUMN_NAME, branch.getName());
            TypedResource commit = branch.getCommit();
            values.put(COLUMN_TYPE, commit.getType());
            values.put(COLUMN_URL, commit.getUrl());
            values.put(COLUMN_SHA, commit.getSha());

            long insertId = database.insert(TABLE_NAME, null, values);
        }
        notifyListenerCreate();
    }

    @Override
    public void create(SQLiteDatabase database, RepositoryBranchWrapper branch) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_REPO_ID, branch.getRepoId());
        values.put(COLUMN_NAME, branch.getName());
        TypedResource commit = branch.getCommit();
        values.put(COLUMN_TYPE, commit.getType());
        values.put(COLUMN_URL, commit.getUrl());
        values.put(COLUMN_SHA, commit.getSha());

        long insertId = database.insert(TABLE_NAME, null, values);
        notifyListenerCreate();
    }

    @Override
    public List<RepositoryBranchWrapper> read(SQLiteDatabase database, int numberOfResults) {
        if(numberOfResults <= 0){
            numberOfResults = 10;
        }

        List<RepositoryBranchWrapper> branchList = new ArrayList<RepositoryBranchWrapper>();

        Cursor cursor = database.query(TABLE_NAME, AllColumns, null, null, null, null, null, numberOfResults + "");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RepositoryBranchWrapper repo = cursorToRepositoryBranch(cursor);
            branchList.add(repo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return branchList;
    }

    public List<RepositoryBranchWrapper> readForRepo(SQLiteDatabase database, long repoId) {
        List<RepositoryBranchWrapper> branchList = new ArrayList<RepositoryBranchWrapper>();

        Cursor cursor = database.query(TABLE_NAME, AllColumns, COLUMN_REPO_ID + " = " + repoId, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RepositoryBranchWrapper repo = cursorToRepositoryBranch(cursor);
            branchList.add(repo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return branchList;
    }

    @Override
    public void update(SQLiteDatabase database, RepositoryBranchWrapper branch) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, branch.getId());
        values.put(COLUMN_REPO_ID, branch.getRepoId());
        values.put(COLUMN_NAME, branch.getName());
        TypedResource commit = branch.getCommit();
        values.put(COLUMN_TYPE, commit.getType());
        values.put(COLUMN_URL, commit.getUrl());
        values.put(COLUMN_SHA, commit.getSha());

        long insertId = database.update(TABLE_NAME, values, COLUMN_ID + "=" + branch.getId(), null);
        notifyListenerUpdate();
    }

    @Override
    public void delete(SQLiteDatabase database, RepositoryBranchWrapper branch) {
        int id = branch.getId();
        database.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        notifyListenerDelete();
    }


    public static boolean addListener(DataSourceListener listener) {
        return listnerList.add(listener);
    }

    public static boolean removeListener(DataSourceListener listener) {
        return listnerList.remove(listener);
    }

    private RepositoryBranchWrapper cursorToRepositoryBranch(Cursor cursor) {
        RepositoryBranchWrapper branch = new RepositoryBranchWrapper();

        branch.setId(cursor.getInt(0));
        branch.setRepoId(cursor.getInt(1));
        branch.setName(cursor.getString(2));

        TypedResource commit = new TypedResource();
        commit.setType(cursor.getString(3));
        commit.setUrl(cursor.getString(4));
        commit.setSha(cursor.getString(5));
        branch.setCommit(commit);

        return branch;
    }


    private void notifyListenerCreate() {
        for(DataSourceListener listener : listnerList){
            listener.dataSourceAdded();
        }
    }


    private void notifyListenerUpdate() {
        for(DataSourceListener listener : listnerList){
            listener.dataSourceUpdated();
        }
    }



    private void notifyListenerDelete() {
        for(DataSourceListener listener : listnerList){
            listener.dataSourceDeleted();
        }
    }
}
