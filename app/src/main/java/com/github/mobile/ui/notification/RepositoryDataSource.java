package com.github.mobile.ui.notification;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

/**
 * Created by Jacob on 2014-12-12.
 */
public class RepositoryDataSource implements DataSource<Repository>{
    private static String TAG = "RepositoryDataSource";

    public static String TABLE_NAME = "repos";
    public static String COLUMN_ID = "id";
    public static String COLUMN_OWNER_ID = "ownerId";
    public static String COLUMN_OWNER_NAME = "ownerName";
    public static String COLUMN_OWNER_AVATAR_URL = "ownerAvatarUrl";
    public static String COLUMN_REPO_ID = "repoId";
    public static String COLUMN_REPO_NAME = "repoName";
    public static String COLUMN_PRIVATE = "private";
    public static String COLUMN_FORK = "fork";
    public static String COLUMN_DESCRIPTION = "description";
    public static String COLUMN_FORKS = "forks";
    public static String COLUMN_WATCHERS = "watchers";
    public static String COLUMN_LANGUAGE = "language";
    public static String COLUMN_HAS_ISSUES = "hasIssues";
    public static String COLUMN_MIRROR_URL = "mirrorUrl";
    public static String COLUMN_OWNER_LOGIN = "ownerLogin";

    private static List<DataSourceListener> listnerList= new ArrayList<DataSourceListener>();

    public static String getCreateTableString(){
        return "CREATE TABLE "+TABLE_NAME+" ("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COLUMN_OWNER_ID+" INTEGER, "+ COLUMN_OWNER_NAME +" TEXT, " +
            COLUMN_OWNER_AVATAR_URL +" TEXT, "+COLUMN_REPO_ID+" INTEGER, "+COLUMN_REPO_NAME+" TEXT, "+COLUMN_PRIVATE+" INTEGER, "+COLUMN_FORK+" INTEGER, " +
            COLUMN_DESCRIPTION+" TEXT, "+COLUMN_FORKS+" INTEGER, "+COLUMN_WATCHERS+" INTEGER, "+COLUMN_LANGUAGE+" TEXT, "+COLUMN_HAS_ISSUES+" INTEGER, "+COLUMN_MIRROR_URL+" TEXT, "+COLUMN_OWNER_LOGIN+" TEXT);";
    }

    private static String[] AllColumns = {COLUMN_ID, COLUMN_OWNER_ID, COLUMN_OWNER_NAME, COLUMN_OWNER_AVATAR_URL, COLUMN_REPO_ID, COLUMN_REPO_NAME,
        COLUMN_PRIVATE, COLUMN_FORK, COLUMN_DESCRIPTION, COLUMN_FORKS, COLUMN_WATCHERS,
        COLUMN_LANGUAGE, COLUMN_HAS_ISSUES, COLUMN_MIRROR_URL, COLUMN_OWNER_LOGIN};



    public RepositoryDataSource() {
        listnerList = new ArrayList<DataSourceListener>();
    }


    public synchronized void createMany(SQLiteDatabase database, List<Repository> repoList) {
        ContentValues values = new ContentValues();
        for(Repository repo : repoList){
            //(Do not call the other create since that will send many notifications at the same time...)
            values.clear();
            User owner = repo.getOwner();
            values.put(COLUMN_OWNER_ID, owner.getId());
            values.put(COLUMN_OWNER_NAME, owner.getName());
            values.put(COLUMN_OWNER_AVATAR_URL, owner.getAvatarUrl());

            values.put(COLUMN_REPO_ID, repo.getId());
            values.put(COLUMN_REPO_NAME, repo.getName());
            values.put(COLUMN_PRIVATE, repo.isPrivate() ? 1 : 0);
            values.put(COLUMN_FORK, repo.isFork() ? 1 : 0);
            values.put(COLUMN_DESCRIPTION, repo.getDescription());
            values.put(COLUMN_FORKS, repo.getForks());
            values.put(COLUMN_WATCHERS, repo.getWatchers());
            values.put(COLUMN_LANGUAGE, repo.getLanguage());
            values.put(COLUMN_HAS_ISSUES, repo.isHasIssues() ? 1 : 0);
            values.put(COLUMN_MIRROR_URL, repo.getMirrorUrl());

            values.put(COLUMN_OWNER_LOGIN, owner.getLogin());

            long insertId = database.insert(TABLE_NAME, null, values);
        }
        notifyListenerCreate();
    }

    @Override
    public synchronized void create(SQLiteDatabase database, Repository repo) {
        ContentValues values = new ContentValues();

        User owner = repo.getOwner();
        values.put(COLUMN_OWNER_ID, owner.getId());
        values.put(COLUMN_OWNER_NAME, owner.getName());
        values.put(COLUMN_OWNER_AVATAR_URL, owner.getAvatarUrl());

        values.put(COLUMN_REPO_ID, repo.getId());
        values.put(COLUMN_REPO_NAME, repo.getName());
        values.put(COLUMN_PRIVATE, repo.isPrivate() ? 1 : 0);
        values.put(COLUMN_FORK, repo.isFork() ? 1 : 0);
        values.put(COLUMN_DESCRIPTION, repo.getDescription());
        values.put(COLUMN_FORKS, repo.getForks());
        values.put(COLUMN_WATCHERS, repo.getWatchers());
        values.put(COLUMN_LANGUAGE, repo.getLanguage());
        values.put(COLUMN_HAS_ISSUES, repo.isHasIssues() ? 1 : 0);
        values.put(COLUMN_MIRROR_URL, repo.getMirrorUrl());

        values.put(COLUMN_OWNER_LOGIN, owner.getLogin());

        long insertId = database.insert(TABLE_NAME, null, values);

        notifyListenerCreate();
    }

    @Override
    public synchronized List<Repository> read(SQLiteDatabase database, int numberOfResults) {
        if(numberOfResults <= 0){
            numberOfResults = 10;
        }

        List<Repository> repoList = new ArrayList<Repository>();

        Cursor cursor = database.query(TABLE_NAME, AllColumns, null, null, null, null, null, numberOfResults + "");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Repository repo = cursorToRepository(cursor);
            repoList.add(repo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return repoList;
    }


    public synchronized Repository find(SQLiteDatabase database, long id) {
        List<Repository> repoList = new ArrayList<Repository>();

        Cursor cursor = database.query(TABLE_NAME, AllColumns, COLUMN_REPO_ID + "=" + id, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Repository repo = cursorToRepository(cursor);
            repoList.add(repo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return repoList.get(0);
    }

    @Override
    public synchronized void update(SQLiteDatabase database, Repository repo) {
        ContentValues values = new ContentValues();

        User owner = repo.getOwner();
        values.put(COLUMN_OWNER_ID, owner.getId());
        values.put(COLUMN_OWNER_NAME, owner.getName());
        values.put(COLUMN_OWNER_AVATAR_URL, owner.getAvatarUrl());

        values.put(COLUMN_REPO_ID, repo.getId());
        values.put(COLUMN_REPO_NAME, repo.getName());
        values.put(COLUMN_PRIVATE, repo.isPrivate() ? 1 : 0);
        values.put(COLUMN_FORK, repo.isFork() ? 1 : 0);
        values.put(COLUMN_DESCRIPTION, repo.getDescription());
        values.put(COLUMN_FORKS, repo.getForks());
        values.put(COLUMN_WATCHERS, repo.getWatchers());
        values.put(COLUMN_LANGUAGE, repo.getLanguage());
        values.put(COLUMN_HAS_ISSUES, repo.isHasIssues() ? 1 : 0);
        values.put(COLUMN_MIRROR_URL, repo.getMirrorUrl());
        values.put(COLUMN_OWNER_LOGIN, owner.getLogin());

        long insertId = database.update(TABLE_NAME, values, COLUMN_REPO_ID + "=" + repo.getId(), null);
        notifyListenerUpdate();
    }

    @Override
    public synchronized void delete(SQLiteDatabase database, Repository repo) {
        long id = repo.getId();
        database.delete(TABLE_NAME, COLUMN_REPO_ID + "=" + id, null);
        notifyListenerDelete();
    }


    public static boolean addListener(DataSourceListener listener) {
        return listnerList.add(listener);
    }


    public static boolean removeListener(DataSourceListener listener) {
        return listnerList.remove(listener);
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

    private Repository cursorToRepository(Cursor cursor) {
        Repository repo = new Repository();
        User owner = new User();
        owner.setId(cursor.getInt(1));
        owner.setLogin(cursor.getString(2));
        owner.setAvatarUrl(cursor.getString(3));

        repo.setId(cursor.getLong(4));
        repo.setName(cursor.getString(5));
        repo.setPrivate(cursor.getInt(6) == 1);
        repo.setFork(cursor.getInt(7) == 1);
        repo.setDescription(cursor.getString(8));
        repo.setForks(cursor.getInt(9));
        repo.setWatchers(cursor.getInt(10));
        repo.setLanguage(cursor.getString(11));
        repo.setHasIssues(cursor.getInt(12) == 1);
        repo.setMirrorUrl(cursor.getString(13));

        owner.setLogin(cursor.getString(14));
        repo.setOwner(owner);
        return repo;
    }
}
