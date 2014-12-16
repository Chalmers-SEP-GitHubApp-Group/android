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

import android.content.res.Resources;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.github.mobile.ui.FragmentStatePagerAdapter;

/**
 * Pager adapter for the issues dashboard
 */
public class NotificationDashboardPagerAdapter extends FragmentStatePagerAdapter {

    private final Resources resources;

    private static final String TAG = "NotificationDashboardPagerAdapter";

    /**
     * Create pager adapter
     *
     * @param activity
     */
    public NotificationDashboardPagerAdapter(final SherlockFragmentActivity activity) {
        super(activity);
        resources = activity.getResources();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(final int position) {
//        String filter = null;
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new NotificationListFragment();
                break;
            case 1:
                fragment = new NotificationSettingsFragment();
                break;
            default:
                return null;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        switch (position) {
            case 0: // TODO fix strings
                return "Notifications";
            case 1:
                return "Settings";
            //resources.getString(string.tab_mentioned);
            default:
                return null;
        }
    }
}
