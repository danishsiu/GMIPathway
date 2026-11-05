package com.week14.gmipathway.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.week14.gmipathway.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Downloads news photos in the background and shows them in an ImageView.
 * Photos are kept in memory while the app runs, and on the phone so they show offline.
 * A recycled list row never shows an old photo, because each view remembers the URL it wants.
 */
public final class ImageLoader {

    private static final int TIMEOUT_MS = 15000;
    private static final int THREADS = 3;
    private static final long FADE_MS = 150;
    private static final String DISK_FOLDER = "news_images";

    @Nullable
    private static ImageLoader instance;

    private final LruCache<String, Bitmap> memory;
    private final File diskFolder;
    private final ExecutorService executor = Executors.newFixedThreadPool(THREADS);
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    private ImageLoader(Context context) {
        // An eighth of the app's memory, counted in kilobytes.
        int maxKb = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);
        memory = new LruCache<String, Bitmap>(maxKb) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        diskFolder = new File(context.getCacheDir(), DISK_FOLDER);
    }

    public static synchronized ImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoader(context.getApplicationContext());
        }
        return instance;
    }

    /** Shows the photo at the URL in the view. A null URL clears the view. */
    public void load(ImageView view, @Nullable String url) {
        view.setTag(R.id.image_loader_url, url);
        // A recycled row may still be part-way through an earlier fade.
        view.animate().cancel();
        view.setAlpha(1f);
        if (url == null) {
            view.setImageDrawable(null);
            return;
        }
        Bitmap cached = memory.get(url);
        if (cached != null) {
            view.setImageBitmap(cached);
            return;
        }
        view.setImageDrawable(null);
        int targetWidth = Math.max(view.getWidth(),
                view.getResources().getDisplayMetrics().widthPixels);
        executor.execute(() -> {
            Bitmap bitmap = fetch(url, targetWidth);
            if (bitmap == null) {
                return;
            }
            memory.put(url, bitmap);
            mainThread.post(() -> {
                if (url.equals(view.getTag(R.id.image_loader_url))) {
                    view.setAlpha(0f);
                    view.setImageBitmap(bitmap);
                    view.animate().alpha(1f).setDuration(FADE_MS).start();
                }
            });
        });
    }

    @Nullable
    private Bitmap fetch(String url, int targetWidth) {
        File file = new File(diskFolder, Integer.toHexString(url.hashCode()) + ".img");
        try {
            if (!file.exists()) {
                download(url, file);
            }
            return decode(file, targetWidth);
        } catch (IOException e) {
            file.delete();
            return null;
        }
    }

    private static void download(String address, File file) throws IOException {
        File folder = file.getParentFile();
        if (folder != null && !folder.exists() && !folder.mkdirs()) {
            throw new IOException("Could not create " + folder);
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        // Writing to a temporary file first means a dropped connection never leaves half a photo.
        File partial = new File(file.getPath() + ".part");
        try {
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + connection.getResponseCode());
            }
            try (InputStream in = connection.getInputStream();
                 OutputStream out = new FileOutputStream(partial)) {
                out.write(NewsRepository.readAll(in));
            }
            if (!partial.renameTo(file)) {
                throw new IOException("Could not save " + file);
            }
        } finally {
            partial.delete();
            connection.disconnect();
        }
    }

    /** Decodes at no more than twice the needed width, to save memory. */
    @Nullable
    private static Bitmap decode(File file, int targetWidth) throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream in = new FileInputStream(file)) {
            BitmapFactory.decodeStream(in, null, bounds);
        }
        if (bounds.outWidth <= 0) {
            throw new IOException("Not an image: " + file);
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        while (bounds.outWidth / (options.inSampleSize * 2) >= targetWidth) {
            options.inSampleSize *= 2;
        }
        try (InputStream in = new FileInputStream(file)) {
            return BitmapFactory.decodeStream(in, null, options);
        }
    }
}
