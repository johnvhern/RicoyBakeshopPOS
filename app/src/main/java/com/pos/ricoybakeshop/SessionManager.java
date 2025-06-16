package com.pos.ricoybakeshop;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USERNAME = "username";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getLoggedInUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public boolean isLoggedIn() {
        return getLoggedInUsername() != null;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
