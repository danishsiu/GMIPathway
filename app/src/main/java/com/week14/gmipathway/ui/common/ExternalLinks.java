package com.week14.gmipathway.ui.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
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

    /** Web links need the internet, unlike the course data, which ships inside the app. */
    public static void openWebPage(Context context, String url) {
        if (!isOnline(context)) {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_LONG).show();
            return;
        }
        start(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)), R.string.error_no_browser);
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager connectivity = context.getSystemService(ConnectivityManager.class);
        NetworkCapabilities network =
                connectivity.getNetworkCapabilities(connectivity.getActiveNetwork());
        return network != null && network.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    /** Opens the phone app with the number filled in, without calling it. */
    public static void dial(Context context, String phoneNumber) {
        Uri number = Uri.parse("tel:" + phoneNumber.replace(" ", ""));
        start(context, new Intent(Intent.ACTION_DIAL, number), R.string.error_no_dialer);
    }

    /** Opens the email app with the message filled in. Returns false if there is no email app. */
    public static boolean composeEmail(Context context, String address, String subject,
                                       String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + address));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        return start(context, intent, R.string.error_no_email);
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

    /** Starts the other app, or shows the error message if none can open it. */
    private static boolean start(Context context, Intent intent, @StringRes int errorRes) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, errorRes, Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
