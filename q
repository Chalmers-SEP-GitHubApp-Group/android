[1mdiff --git a/app/AndroidManifest.xml b/app/AndroidManifest.xml[m
[1mindex 39559f9..d3bad4c 100644[m
[1m--- a/app/AndroidManifest.xml[m
[1m+++ b/app/AndroidManifest.xml[m
[36m@@ -340,6 +340,30 @@[m
                 <category android:name="android.intent.category.DEFAULT" />[m
             </intent-filter>[m
         </activity>[m
[32m+[m[32m        <activity[m
[32m+[m[32m                android:name=".ui.notification.NotificationDashboardActivity"[m
[32m+[m[32m                android:configChanges="orientation|keyboardHidden|screenSize"[m
[32m+[m[32m                android:hardwareAccelerated="true" >[m
[32m+[m[32m            <intent-filter>[m
[32m+[m[32m                <action android:name="com.github.mobile.notification.dashboard.VIEW" />[m
[32m+[m
[32m+[m[32m                <category android:name="android.intent.category.DEFAULT" />[m
[32m+[m[32m            </intent-filter>[m
[32m+[m[32m        </activity>[m
[32m+[m[32m        <activity[m
[32m+[m[32m                android:name=".ui.notification.NotificationActivity"[m
[32m+[m[32m                android:configChanges="orientation|keyboardHidden|screenSize"[m
[32m+[m[32m                android:hardwareAccelerated="true" >[m
[32m+[m[32m            <intent-filter>[m
[32m+[m[32m                <action android:name="com.github.mobile.notification.VIEW" />[m
[32m+[m
[32m+[m[32m                <category android:name="android.intent.category.DEFAULT" />[m
[32m+[m[32m            </intent-filter>[m
[32m+[m
[32m+[m[32m            <!--<meta-data[m
[32m+[m[32m                    android:name="android.app.default_searchable"[m
[32m+[m[32m                    android:value=".ui.issue.IssueSearchActivity" />-->[m
[32m+[m[32m        </activity>[m
 [m
         <service[m
             android:name=".sync.SyncAdapterService"[m
[1mdiff --git a/app/src/main/java/com/github/mobile/Intents.java b/app/src/main/java/com/github/mobile/Intents.java[m
[1mindex 2f1505c..ded76c8 100644[m
[1m--- a/app/src/main/java/com/github/mobile/Intents.java[m
[1m+++ b/app/src/main/java/com/github/mobile/Intents.java[m
[36m@@ -28,6 +28,8 @@[m [mimport org.eclipse.egit.github.core.Repository;[m
 import org.eclipse.egit.github.core.RepositoryId;[m
 import org.eclipse.egit.github.core.User;[m
 [m
[32m+[m[32mimport com.github.mobile.ui.notification.Notification;[m
[32m+[m
 /**[m
  * Helper for creating intents[m
  */[m
[36m@@ -171,6 +173,11 @@[m [mpublic class Intents {[m
     public static final String EXTRA_PATH = INTENT_EXTRA_PREFIX + "PATH";[m
 [m
     /**[m
[32m+[m[32m     * Notification handle[m
[32m+[m[32m     */[m
[32m+[m[32m    public static final String EXTRA_NOTIFICATION = INTENT_EXTRA_PREFIX + "NOTIFICATION";[m
[32m+[m
[32m+[m[32m    /**[m
      * Resolve the {@link RepositoryId} referenced by the given intent[m
      *[m
      * @param intent[m
[36m@@ -273,6 +280,16 @@[m [mpublic class Intents {[m
         }[m
 [m
         /**[m
[32m+[m[32m         * Add user to intent being built up[m
[32m+[m[32m         *[m
[32m+[m[32m         * @param user[m
[32m+[m[32m         * @return this builder;[m
[32m+[m[32m         */[m
[32m+[m[32m        public Builder notification(Notification notification) {[m
[32m+[m[32m            return add(EXTRA_NOTIFICATION, notification);[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        /**[m
          * Add extra field data value to intent being built up[m
          *[m
          * @param fieldName[m
[1mdiff --git a/app/src/main/java/com/github/mobile/persistence/AccountDataManager.java b/app/src/main/java/com/github/mobile/persistence/AccountDataManager.java[m
[1mindex 1d766fd..066a330 100644[m
[1m--- a/app/src/main/java/com/github/mobile/persistence/AccountDataManager.java[m
[1m+++ b/app/src/main/java/com/github/mobile/persistence/AccountDataManager.java[m
[36m@@ -178,6 +178,7 @@[m [mpublic class AccountDataManager {[m
                 .loadOrRequest(resource);[m
     }[m
 [m
[32m+[m
     /**[m
      * Get bookmarked issue filters[m
      * <p/>[m
[1mdiff --git a/app/src/main/java/com/github/mobile/ui/user/HomeActivity.java b/app/src/main/java/com/github/mobile/ui/user/HomeActivity.java[m
[1mindex f7ab78b..214802a 100644[m
[1m--- a/app/src/main/java/com/github/mobile/ui/user/HomeActivity.java[m
[1m+++ b/app/src/main/java/com/github/mobile/ui/user/HomeActivity.java[m
[36m@@ -19,6 +19,7 @@[m [mimport static com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST;[m
 import static com.github.mobile.ui.user.HomeDropdownListAdapter.ACTION_BOOKMARKS;[m
 import static com.github.mobile.ui.user.HomeDropdownListAdapter.ACTION_DASHBOARD;[m
 import static com.github.mobile.ui.user.HomeDropdownListAdapter.ACTION_GISTS;[m
[32m+[m[32mimport static com.github.mobile.ui.user.HomeDropdownListAdapter.ACTION_NOTIFICATION_DASHBOARD;[m
 import static com.github.mobile.util.TypefaceUtils.ICON_FOLLOW;[m
 import static com.github.mobile.util.TypefaceUtils.ICON_NEWS;[m
 import static com.github.mobile.util.TypefaceUtils.ICON_PUBLIC;[m
[36m@@ -46,6 +47,8 @@[m [mimport com.github.mobile.ui.TabPagerActivity;[m
 import com.github.mobile.ui.gist.GistsActivity;[m
 import com.github.mobile.ui.issue.FiltersViewActivity;[m
 import com.github.mobile.ui.issue.IssueDashboardActivity;[m
[32m+[m[32mimport com.github.mobile.ui.notification.NotificationDashboardActivity;[m
[32m+[m[32mimport com.github.mobile.ui.notification.NotificationHandler;[m
 import com.github.mobile.ui.repo.OrganizationLoader;[m
 import com.github.mobile.util.AvatarLoader;[m
 import com.github.mobile.util.PreferenceUtils;[m
[36m@@ -92,13 +95,25 @@[m [mpublic class HomeActivity extends TabPagerActivity<HomePagerAdapter> implements[m
     @Inject[m
     private SharedPreferences sharedPreferences;[m
 [m
[32m+[m[32m    private NotificationHandler handler;[m
[32m+[m
     @Override[m
     protected void onCreate(Bundle savedInstanceState) {[m
         super.onCreate(savedInstanceState);[m
 [m
[32m+[m[32m        handler = new NotificationHandler(this);[m
[32m+[m
[32m+[m[32m        handler.start();[m
[32m+[m
         getSupportLoaderManager().initLoader(0, null, this);[m
     }[m
 [m
[32m+[m[32m    @Override[m
[32m+[m[32m    protected void onDestroy() {[m
[32m+[m[32m        handler.stop();[m
[32m+[m[32m        super.onDestroy();[m
[32m+[m[32m    }[m
[32m+[m
     private void reloadOrgs() {[m
         getSupportLoaderManager().restartLoader(0, null,[m
                 new LoaderCallbacks<List<User>>() {[m
[36m@@ -227,6 +242,9 @@[m [mpublic class HomeActivity extends TabPagerActivity<HomePagerAdapter> implements[m
             case ACTION_BOOKMARKS:[m
                 startActivity(FiltersViewActivity.createIntent());[m
                 break;[m
[32m+[m[32m            case ACTION_NOTIFICATION_DASHBOARD:[m
[32m+[m[32m                startActivity(NotificationDashboardActivity.createIntent());[m
[32m+[m[32m                break;[m
             }[m
             int orgSelected = homeAdapter.getSelected();[m
             ActionBar actionBar = getSupportActionBar();[m
[1mdiff --git a/app/src/main/java/com/github/mobile/ui/user/HomeDropdownListAdapter.java b/app/src/main/java/com/github/mobile/ui/user/HomeDropdownListAdapter.java[m
[1mindex b18e042..58560b1 100644[m
[1m--- a/app/src/main/java/com/github/mobile/ui/user/HomeDropdownListAdapter.java[m
[1m+++ b/app/src/main/java/com/github/mobile/ui/user/HomeDropdownListAdapter.java[m
[36m@@ -40,6 +40,11 @@[m [mimport org.eclipse.egit.github.core.User;[m
 public class HomeDropdownListAdapter extends SingleTypeAdapter<Object> {[m
 [m
     /**[m
[32m+[m[32m     * Action for notifications[m
[32m+[m[32m     */[m
[32m+[m[32m    public static final int ACTION_NOTIFICATION_DASHBOARD = 3;[m
[32m+[m
[32m+[m[32m    /**[m
      * Action for Gists[m
      */[m
     public static final int ACTION_GISTS = 0;[m
[36m@@ -54,7 +59,7 @@[m [mpublic class HomeDropdownListAdapter extends SingleTypeAdapter<Object> {[m
      */[m
     public static final int ACTION_BOOKMARKS = 2;[m
 [m
[31m-    private static final int NON_ORG_ITEMS = 3;[m
[32m+[m[32m    private static final int NON_ORG_ITEMS = 4;[m
 [m
     private final AvatarLoader avatars;[m
 [m
[36m@@ -122,7 +127,8 @@[m [mpublic class HomeDropdownListAdapter extends SingleTypeAdapter<Object> {[m
         if (orgCount > 0)[m
             all.addAll(orgs);[m
 [m
[31m-        // Add dummy objects for gists, issue dashboard, and bookmarks[m
[32m+[m[32m        // Add dummy objects for gists, issue dashboard, NOTIFICATION and bookmarks[m
[32m+[m[32m        all.add(new Object());[m
         all.add(new Object());[m
         all.add(new Object());[m
         all.add(new Object());[m
[36m@@ -195,7 +201,12 @@[m [mpublic class HomeDropdownListAdapter extends SingleTypeAdapter<Object> {[m
         case ACTION_BOOKMARKS:[m
             setText(0, string.bookmarks);[m
             setActionIcon(imageView(1), drawable.dropdown_bookmark);[m
[31m-            break;[m
[32m+[m[32m        break;[m
[32m+[m[32m        case ACTION_NOTIFICATION_DASHBOARD:[m
[32m+[m[32m            //setText(0, string.bookmarks); TODO fix the string[m
[32m+[m[32m            setText(0, "Notification");[m
[32m+[m[32m            setActionIcon(imageView(1), drawable.ic_action_email);[m
[32m+[m[32m        break;[m
         default:[m
             User user = (User) item;[m
             setText(0, user.getLogin());[m
