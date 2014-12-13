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
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
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

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.DataService;

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

    private TextView committer, date, message, sha, totalAdditions, totalDeletions;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.notification_newcommit);
        context = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notification");
        actionBar.setIcon(drawable.ic_action_email);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Notification notification = (Notification) getIntent().getSerializableExtra(EXTRA_NOTIFICATION);


        committer = (TextView) findViewById(id.commit_committer);
        date = (TextView) findViewById(id.commit_date);
        message = (TextView) findViewById(id.Commit_Message);
        sha = (TextView) findViewById(id.Commit_SHA);
        totalAdditions = (TextView) findViewById(id.commit_total_additions);
        totalDeletions = (TextView) findViewById(id.commit_total_deletions);
        committer.setText("Committer: ");
        date.setText("");
        message.setText("");
        sha.setText("");
        totalAdditions.setText("Total Additions: ");
        totalDeletions.setText("Total Deletions: ");

        new AsyncTask<Notification, Void, RepositoryCommit>(){

            @Override
            protected RepositoryCommit doInBackground(Notification... params) {
                Notification notification = params[0];

                if(notification.getContentTitle().contains("Commit")){
                    DBHelper dbhelper = DatabaseManager.getInstance();
                    SQLiteDatabase db = dbhelper.getReadableDatabase();

                    RepositoryDataSource dataSource = new RepositoryDataSource();


                    Repository repo = dataSource.find(db, notification.getRepoId());

                    GitHubClient client = new GitHubClient();
                    client.setCredentials(Preferences.getUsername(), Preferences.getPassword());
                    CommitService service = new CommitService(client);

                    try {
                        return service.getCommit(repo, notification.getSha());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else{

                }


                return null;
            }

            @Override
            protected void onPostExecute(RepositoryCommit commit){
                if(commit == null){
                    //FUCK
                }else{
                    committer.setText("Committer: " + commit.getCommit().getCommitter().getName());
                    Date createdDate = commit.getCommit().getCommitter().getDate();
                    String formatedDate = DateFormat.getDateFormat(context).format(createdDate) + " " + DateFormat.getTimeFormat(context).format(createdDate);
                    date.setText(formatedDate);
                    message.setText(commit.getCommit().getMessage());
                    sha.setText(commit.getSha());

                    List<CommitFile> files = commit.getFiles();
                    //Collections.sort(files);

                    for(CommitFile file : files){
                        addRowView(file);
                    }
                    totalAdditions.setText("Total Additions: " + commit.getStats().getAdditions());
                    totalDeletions.setText("Total Deletions: " + commit.getStats().getDeletions());
                }
            }
        }.execute(notification);
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

    private void addRowView(CommitFile file){
        ViewGroup parent = (ViewGroup) findViewById(id.commit_changed_files_list);
        View rowView = this.getLayoutInflater().inflate(layout.notification_newcommit_diffrow, parent, false);

        if(file.getStatus().equals("added")){
            ImageView icon = (ImageView) rowView.findViewById(id.directory_icon);
            icon.setImageResource(drawable.file_added);
        }else if(file.getStatus().equals("removed")){
            ImageView icon = (ImageView) rowView.findViewById(id.directory_icon);
            icon.setImageResource(drawable.file_deleted);
        }
        TextView name = (TextView) rowView.findViewById(id.directory_name);
        TextView path = (TextView) rowView.findViewById(id.directory_path);
        TextView additions = (TextView) rowView.findViewById(id.commit_list_additions);
        TextView deletions = (TextView) rowView.findViewById(id.commit_list_deletions);
        name.setText(file.getFilename());
        path.setText(file.getStatus());
        additions.setText(file.getAdditions() + "");
        deletions.setText(file.getDeletions() + "");
        parent.addView(rowView);
    }
}
