package com.github.mobile.ui.notification;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Jacob on 2014-12-13.
 */
public class Preferences {
    private static String PREF_STRINGS_NAME = "notification";
    private static String logTag = "Preferences";
    private static SharedPreferences prefs;

    public static void initializePreferences(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_STRINGS_NAME, Context.MODE_PRIVATE);
        } else {
            Log.e(logTag, "Preferences is already initialized");
        }
    }

    private static boolean isInitialized() {
        return prefs != null;
    }

    private synchronized static void setGeneral(PREF_KEY key, String value) {
        if (isInitialized()) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(key.getKey(), value);
            save(edit);
            Log.d(logTag, "saved " + key + ": " + value + " to preferences");
        } else {
            Log.e(logTag, "Preferences is not initialized");
        }
    }

    private static boolean isEditorApplyAvailable() {
        return SDK_INT >= GINGERBREAD;
    }

    /**
     * Save preferences in given editor
     *
     * @param editor
     */
    public synchronized static void save(final SharedPreferences.Editor editor) {
        if (isEditorApplyAvailable())
            editor.apply();
        else
            editor.commit();
    }

    private synchronized static String getGeneral(PREF_KEY key) {
        if (isInitialized()) {
            String value = prefs.getString(key.getKey(), "");
            if (value.equals("")) {
                Log.e(logTag, "You have no " + key + " saved in prefereneces");
            } else {
                Log.d(logTag, "Got " + key + " from preferences: " + value);
            }
            return value;
        } else {
            Log.e(logTag, "Preferences is not initialized");
            return "";
        }
    }

    public static void ClearPreferences() {
        setUsername("");
        setPassword("");
        setTimeInterval("60"); // 60 seconds between checks
    }

    public static enum PREF_KEY {
        TIME_INTERVAL("time_interval"),
        USERNAME("username"),
        PASSWORD("password"),
        EMPTY("empty");

        private final String key;

        private PREF_KEY(String key) {
            this.key = key;
        }

        private String getKey() {
            return key;
        }

        public static PREF_KEY getKey(String key) {
            for (PREF_KEY pref : values()) {
                if (pref.getKey().equals(key)) {
                    return pref;
                }
            }
            return PREF_KEY.EMPTY;
        }
    }


    public static void setUsername(String username) {
        setGeneral(PREF_KEY.USERNAME, username);
    }

    public static void setPassword(String password) {
        setGeneral(PREF_KEY.PASSWORD, password);
    }

    public static void setTimeInterval(String time_interval) {
        setGeneral(PREF_KEY.TIME_INTERVAL, time_interval);
    }

    public static String getTimeInterval() {
        return getGeneral(PREF_KEY.TIME_INTERVAL);
    }

    public static String getUsername() {
        return getGeneral(PREF_KEY.USERNAME);
    }

    public static String getPassword() {
        return getGeneral(PREF_KEY.PASSWORD);
    }
}