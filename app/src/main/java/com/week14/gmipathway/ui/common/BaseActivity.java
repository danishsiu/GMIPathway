package com.week14.gmipathway.ui.common;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

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
        ViewGroup root = findViewById(R.id.main);
        View[] topViews = getTopInsetViews();
        // Top views stretch over the root's padding, so it must not clip them.
        root.setClipToPadding(false);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottom = Math.max(bars.bottom, keyboard.bottom);
            view.setPadding(bars.left, 0, bars.right, bottom);
            for (View topView : topViews) {
                if (topView != null) {
                    topView.setPadding(bars.left, bars.top, bars.right, 0);
                    ViewGroup.MarginLayoutParams params =
                            (ViewGroup.MarginLayoutParams) topView.getLayoutParams();
                    params.leftMargin = -bars.left;
                    params.rightMargin = -bars.right;
                    topView.setLayoutParams(params);
                }
            }
            onInsetsApplied(bars, bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** Lets a screen adjust views that need more than the standard insets. */
    protected void onInsetsApplied(Insets bars, int bottomInset) {
    }

    /** Returns the views that extend behind the status bar. Missing views can be left null. */
    protected View[] getTopInsetViews() {
        return new View[]{findViewById(R.id.app_bar)};
    }
}
