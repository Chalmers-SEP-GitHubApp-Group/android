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
import static com.github.mobile.Intents.EXTRA_USER;
import static com.github.mobile.util.TypefaceUtils.ICON_COMMENT;
import static com.github.mobile.util.TypefaceUtils.ICON_WIKI;
import static com.github.mobile.util.TypefaceUtils.ICON_FOLLOW;
import static com.github.mobile.util.TypefaceUtils.ICON_WATCH;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.github.mobile.Intents;
import com.github.mobile.R.drawable;
import com.github.mobile.R.string;
import com.github.mobile.ui.TabPagerActivity;
import com.github.mobile.ui.user.HomeActivity;

import org.eclipse.egit.github.core.User;

/**
 * Dashboard activity for issues
 */
public class NotificationDashboardActivity extends
    TabPagerActivity<NotificationDashboardPagerAdapter> {

    private static final String TAG = "NotificationDashboardActivity";

    public static Intent createIntent() {
        return new Intents.Builder("notification.dashboard.VIEW").toIntent();
    }

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle(string.dashboard_issues_title); TODO fix string
        actionBar.setTitle("Notification");
        actionBar.setIcon(drawable.ic_action_email);
        actionBar.setDisplayHomeAsUpEnabled(true);

        configureTabPager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected NotificationDashboardPagerAdapter createAdapter() {
        return new NotificationDashboardPagerAdapter(this);
    }

    @Override
    protected String getIcon(int position) {
        switch (position) {
            case 0:
                return ICON_COMMENT;
            case 1:
                return ICON_WIKI;
            default:
                return super.getIcon(position);
        }
    }
}
