package com.week14.gmipathway;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.week14.gmipathway.data.AppSettings;

/**
 * Applies the saved light or dark choice before the first screen opens.
 */
public class GmiPathwayApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(new AppSettings(this).getNightMode());
    }
}
