package com.github.mobile.ui.notification;
/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.mobile.R;
import com.github.mobile.ui.DialogFragment;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


/**
 * Fragment to display a pageable list of dashboard issues
 */
public class NotificationListFragment extends DialogFragment implements DataSourceListener{

    private static final String TAG = "NotificationListFragment";

    private NotificationListArrayAdapter adapter;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.notification_list, container, false);

        ListView listview = (ListView) rootView.findViewById(R.id.notification_list_view);

        adapter = new NotificationListArrayAdapter(getActivity(), new ArrayList<Notification>());

        dataSourceAdded();

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                Notification not = adapter.getNotification(position);
                not.setHasBeenViewed(true);
                DBHelper dbHelper = DatabaseManager.getInstance(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Log.d(TAG, "open writable database");

                NotificationDataSource dao = new NotificationDataSource();
                dao.update(db, not);
                adapter.notifyDataSetChanged();
                startActivity(NotificationActivity.createIntent(not));
            }
        });
        NotificationDataSource.addListener(this);
        return rootView;
    }

    @Override
    public void dataSourceUpdated() {

    }

    @Override
    public void dataSourceAdded() {
        Log.d(TAG, "notify datasource added");
        DBHelper dbHelper = DatabaseManager.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "open readable database");

        NotificationDataSource notificationDataSource = new NotificationDataSource();

        adapter.setNotification(notificationDataSource.read(db,20));
    }

    @Override
    public void dataSourceDeleted() {

    }

    @Override
    public void onDestroyView(){
        NotificationDataSource.removeListener(this);
        super.onDestroyView();
    }
}
