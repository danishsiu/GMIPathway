package com.week14.gmipathway.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Remembers the light or dark choice. AppCompat stores the language choice by itself.
 */
public final class AppSettings {
    private static final String PREFS_NAME = "settings";
    private static final String KEY_NIGHT_MODE = "nightMode";

    private final SharedPreferences preferences;

    public AppSettings(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Returns one of the AppCompatDelegate night modes. New installs follow the phone. */
    @AppCompatDelegate.NightMode
    public int getNightMode() {
        return preferences.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setNightMode(@AppCompatDelegate.NightMode int mode) {
        preferences.edit().putInt(KEY_NIGHT_MODE, mode).apply();
    }
}
