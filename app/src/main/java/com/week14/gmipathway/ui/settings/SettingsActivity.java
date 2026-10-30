package com.week14.gmipathway.ui.settings;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.week14.gmipathway.R;
import com.week14.gmipathway.data.AppSettings;
import com.week14.gmipathway.ui.common.BaseActivity;

/**
 * Lets the prospect switch between English and Bahasa Melayu, and between light and dark.
 * Android reopens every screen after either change, so the whole app updates at once.
 */
public class SettingsActivity extends BaseActivity {
    private static final String LANGUAGE_ENGLISH = "en";
    private static final String LANGUAGE_MALAY = "ms";

    private AppSettings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpToolbar(R.string.settings_title);
        applyWindowInsets();

        settings = new AppSettings(this);
        setUpLanguage();
        setUpAppearance();
    }

    private void setUpLanguage() {
        MaterialButtonToggleGroup group = findViewById(R.id.language_group);
        boolean malay = LANGUAGE_MALAY.equals(getString(R.string.language_tag));
        group.check(malay ? R.id.language_malay : R.id.language_english);
        group.addOnButtonCheckedListener((toggleGroup, buttonId, isChecked) -> {
            if (isChecked) {
                selectLanguage(buttonId == R.id.language_malay ? LANGUAGE_MALAY : LANGUAGE_ENGLISH);
            }
        });
    }

    private void selectLanguage(String language) {
        if (!language.equals(getString(R.string.language_tag))) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language));
        }
    }

    private void setUpAppearance() {
        MaterialButtonToggleGroup group = findViewById(R.id.appearance_group);
        group.check(getAppearanceButton(settings.getNightMode()));
        group.addOnButtonCheckedListener((toggleGroup, buttonId, isChecked) -> {
            if (isChecked) {
                selectNightMode(getNightMode(buttonId));
            }
        });
    }

    private void selectNightMode(@AppCompatDelegate.NightMode int mode) {
        if (mode != settings.getNightMode()) {
            settings.setNightMode(mode);
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }

    @IdRes
    private static int getAppearanceButton(@AppCompatDelegate.NightMode int mode) {
        switch (mode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                return R.id.appearance_light;
            case AppCompatDelegate.MODE_NIGHT_YES:
                return R.id.appearance_dark;
            default:
                return R.id.appearance_system;
        }
    }

    @AppCompatDelegate.NightMode
    private static int getNightMode(@IdRes int buttonId) {
        if (buttonId == R.id.appearance_light) {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
        if (buttonId == R.id.appearance_dark) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }
}
