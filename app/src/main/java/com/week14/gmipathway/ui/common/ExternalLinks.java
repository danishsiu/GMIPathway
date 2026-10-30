package com.week14.gmipathway.ui.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.week14.gmipathway.R;

/**
 * Opens other apps for links, calls, email and maps.
 * If the device has no suitable app, a message is shown instead of the app crashing.
 */
public final class ExternalLinks {

    private ExternalLinks() {
    }

    public static void openWebPage(Context context, String url) {
        start(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)), R.string.error_no_browser);
    }

    /** Opens the phone app with the number filled in, without calling it. */
    public static void dial(Context context, String phoneNumber) {
        Uri number = Uri.parse("tel:" + phoneNumber.replace(" ", ""));
        start(context, new Intent(Intent.ACTION_DIAL, number), R.string.error_no_dialer);
    }

    public static void composeEmail(Context context, String address, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + address));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        start(context, intent, R.string.error_no_email);
    }

    /** Lets the prospect choose an app to share the text with. */
    public static void shareText(Context context, String chooserTitle, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, chooserTitle));
    }

    /** Opens the campus in a maps app, or in the browser if there is no maps app. */
    public static void openCampusMap(Context context) {
        Uri location = Uri.parse(context.getString(R.string.map_geo_uri));
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, location));
        } catch (ActivityNotFoundException e) {
            openWebPage(context, context.getString(R.string.map_web_url));
        }
    }

    private static void start(Context context, Intent intent, @StringRes int errorRes) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, errorRes, Toast.LENGTH_LONG).show();
        }
    }
}
