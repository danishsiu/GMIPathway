package com.week14.gmipathway.ui.common;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.week14.gmipathway.R;

/**
 * Shared set-up for every screen: drawing behind the system bars and the toolbar with a back arrow.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // The app bar is always blue, so the status bar icons always stay light.
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
    }

    /** Shows the toolbar with a title and a back arrow. */
    protected void setUpToolbar(@StringRes int titleRes) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titleRes);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /** Returning to the previous screen keeps its state, such as the selected tab. */
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    /**
     * Keeps content clear of the status bar, navigation bar and keyboard.
     * The screen's root view must have the id "main".
     */
    protected void applyWindowInsets() {
        View root = findViewById(R.id.main);
        View topView = getTopInsetView();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, 0, bars.right, Math.max(bars.bottom, keyboard.bottom));
            if (topView != null) {
                topView.setPadding(0, bars.top, 0, 0);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** Returns the view that extends behind the status bar. */
    protected View getTopInsetView() {
        return findViewById(R.id.app_bar);
    }
}
