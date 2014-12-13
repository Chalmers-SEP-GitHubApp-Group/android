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
package com.github.mobile.ui.notification;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.github.mobile.Intents;
import static com.github.mobile.Intents.EXTRA_NOTIFICATION;
import com.github.mobile.R.drawable;
import com.github.mobile.R.layout;
import com.github.mobile.R.string;
import com.github.mobile.R.id;
import com.github.mobile.RequestFuture;
import com.github.mobile.core.issue.IssueFilter;
import com.github.mobile.persistence.AccountDataManager;
import com.github.mobile.ui.ConfirmDialogFragment;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.issue.FilterListFragment;
import com.github.mobile.ui.user.HomeActivity;
import com.google.inject.Inject;
import com.github.mobile.Intents;

/**
 * Activity to display a list of saved {@link com.github.mobile.core.issue.IssueFilter} objects
 */
public class NotificationActivity extends DialogFragmentActivity{

    /**
     * Create intent to browse issue filters
     *
     * @return intent
     */
    public static Intent createIntent(Notification notification) {
        return new Intents.Builder("notification.VIEW").notification(notification).toIntent();
    }
//
//    private static final String ARG_FILTER = "filter";
//
//    private static final int REQUEST_DELETE = 1;

//    @Inject
//    private AccountDataManager cache;

//    private FilterListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.notification_newcommit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notification");
        actionBar.setIcon(drawable.ic_action_email);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Notification notification = (Notification) getIntent().getSerializableExtra(EXTRA_NOTIFICATION);



        TextView committer = (TextView) findViewById(id.commit_committer);
        committer.setText("Committer: " + "TITLE: " + notification.getContentTitle() + " text: " + notification.getContentText());


//        TextView date = (TextView) rootView.findViewById(R.id.commit_date);
//        TextView message = (TextView) rootView.findViewById(R.id.Commit_Message);
//        TextView sha = (TextView) rootView.findViewById(R.id.Commit_SHA);
//        TextView totalAdditions = (TextView) rootView.findViewById(R.id.commit_total_additions);
//        TextView totalDeletions = (TextView) rootView.findViewById(R.id.commit_total_deletions);

//        committer.setText("Committer: " + commit.getCommitter().getName());
//        Date dateObj = commit.getDate();
//        String formatedDate = DateFormat.getDateFormat(context).format(dateObj) + " " + DateFormat.getTimeFormat(context).format(dateObj);
//        date.setText(formatedDate);
//        message.setText(commit.getMessage());
//        sha.setText(commit.getSha());
//        ArrayList<File> files = commit.getChangedFiles();
//        Collections.sort(files);
//        int additions = 0;
//        int deletions = 0;
//        for(File file : files){
//            addRowView(file);
//            additions += file.getAdditions();
//            deletions += file.getDeletions();
//        }
//        totalAdditions.setText("Total Additions: " + additions);
//        totalDeletions.setText("Total Deletions: " + deletions);


//        fragment = (FilterListFragment) getSupportFragmentManager()
//                .findFragmentById(android.R.id.list);
//        fragment.getListView().setOnItemLongClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, NotificationDashboardActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
