package com.week14.gmipathway.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;

import com.week14.gmipathway.R;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.home.MainActivity;

/**
 * Shows the branded splash screen briefly, then opens the home screen.
 * Lint prefers no splash Activity, but the project brief asks for a custom branded screen.
 * It follows the system SplashScreen API, so the two splash stages match on every Android version.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private static final long SPLASH_DELAY_MS = 1500;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openHome = () -> {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Must run before super.onCreate so the system splash hands over to this screen.
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(openHome, SPLASH_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        // Stops a leftover timer from opening the home screen after this screen has closed.
        handler.removeCallbacks(openHome);
        super.onDestroy();
    }
}
