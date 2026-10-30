package com.week14.gmipathway.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.home.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Shows the branded splash screen while the course data loads, then opens the home screen.
 * Lint prefers no splash Activity, but the project brief asks for a custom branded screen.
 * It follows the system SplashScreen API, so the two splash stages match on every Android version.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    /** Keeps the branding on screen long enough to read, even if the data loads instantly. */
    private static final long MIN_DISPLAY_MS = 1500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService loader = Executors.newSingleThreadExecutor();
    private final Runnable openHome = () -> {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Must run before super.onCreate so the system splash hands over to this screen.
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        keepInstituteNameAboveNavigationBar();
        animateText(findViewById(R.id.splash_title));
        animateText(findViewById(R.id.splash_tagline));
        loadDataThenOpenHome();
    }

    /** Only this text moves. The logo must stay centred so it lines up with the system splash. */
    private void keepInstituteNameAboveNavigationBar() {
        int baseMargin = getResources().getDimensionPixelSize(R.dimen.spacing_xlarge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash_institute),
                (view, insets) -> {
                    ViewGroup.MarginLayoutParams params =
                            (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.bottomMargin = baseMargin
                            + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                    view.setLayoutParams(params);
                    return insets;
                });
    }

    /** Reads the course data away from the main thread, so the screen never freezes. */
    private void loadDataThenOpenHome() {
        long startedAt = SystemClock.uptimeMillis();
        loader.execute(() -> {
            CourseRepository.getInstance(this);
            long elapsed = SystemClock.uptimeMillis() - startedAt;
            handler.postDelayed(openHome, Math.max(0, MIN_DISPLAY_MS - elapsed));
        });
    }

    private void animateText(View view) {
        view.setAlpha(0f);
        view.setTranslationY(getResources().getDimension(R.dimen.splash_text_shift));
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(getResources().getInteger(R.integer.splash_text_fade_ms))
                .start();
    }

    @Override
    protected void onDestroy() {
        // Stops a leftover timer from opening the home screen after this screen has closed.
        handler.removeCallbacks(openHome);
        loader.shutdown();
        super.onDestroy();
    }
}
