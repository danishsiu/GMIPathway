package com.week14.gmipathway.data;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.week14.gmipathway.model.NewsItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Downloads GMI's latest news from the public WordPress feed on gmi.edu.my.
 * The last download is kept on the phone, so the news still shows when it is offline.
 * Call {@link #load()} on a background thread.
 */
public final class NewsRepository {

    /** The 12 newest posts, with each post's photo, and only the fields the app shows. */
    private static final String FEED_URL = "https://www.gmi.edu.my/wp-json/wp/v2/posts"
            + "?per_page=12&_embed=wp:featuredmedia"
            + "&_fields=date,link,title,excerpt,_links.wp:featuredmedia,_embedded";
    private static final String CACHE_FILE = "news_cache.json";
    private static final int TIMEOUT_MS = 15000;
    /** Smallest first. All are wide enough for a phone-width card. */
    private static final String[] IMAGE_SIZES = {"medium_large", "large", "full"};

    /** The news and where it came from. */
    public static final class Result {
        public final List<NewsItem> items;
        /** True when the download failed and these are the saved copy. */
        public final boolean fromCache;
        /** When the saved copy was downloaded, in milliseconds. */
        public final long savedAt;

        Result(List<NewsItem> items, boolean fromCache, long savedAt) {
            this.items = items;
            this.fromCache = fromCache;
            this.savedAt = savedAt;
        }
    }

    private final File cacheFile;

    public NewsRepository(Context context) {
        cacheFile = new File(context.getFilesDir(), CACHE_FILE);
    }

    /**
     * Downloads the news, or falls back to the saved copy.
     *
     * @throws IOException if the download fails and there is no saved copy
     */
    public Result load() throws IOException {
        try {
            String json = download(FEED_URL);
            List<NewsItem> items = parse(json);
            save(json);
            return new Result(items, false, System.currentTimeMillis());
        } catch (IOException | JsonParseException | IllegalStateException e) {
            String saved = readSaved();
            if (saved == null) {
                throw e instanceof IOException ? (IOException) e : new IOException(e);
            }
            return new Result(parse(saved), true, cacheFile.lastModified());
        }
    }

    /** Turns the feed into news items. Posts without a title or link are skipped. */
    @VisibleForTesting
    static List<NewsItem> parse(String json) {
        JsonArray posts = JsonParser.parseString(json).getAsJsonArray();
        List<NewsItem> items = new ArrayList<>();
        for (JsonElement element : posts) {
            JsonObject post = element.getAsJsonObject();
            String title = rendered(post, "title");
            String link = string(post, "link");
            if (title.isEmpty() || link.isEmpty()) {
                continue;
            }
            items.add(new NewsItem(title, rendered(post, "excerpt"), string(post, "date"), link,
                    imageUrl(post)));
        }
        return Collections.unmodifiableList(items);
    }

    /** Reads the post's photo URL from the embedded media, picking the first size that exists. */
    @Nullable
    private static String imageUrl(JsonObject post) {
        try {
            JsonObject media = post.getAsJsonObject("_embedded")
                    .getAsJsonArray("wp:featuredmedia").get(0).getAsJsonObject();
            JsonObject sizes = media.getAsJsonObject("media_details").getAsJsonObject("sizes");
            for (String size : IMAGE_SIZES) {
                if (sizes.has(size)) {
                    return sizes.getAsJsonObject(size).get("source_url").getAsString();
                }
            }
            return media.has("source_url") ? media.get("source_url").getAsString() : null;
        } catch (RuntimeException e) {
            // Posts without a photo have no embedded media, or GMI may change the format.
            return null;
        }
    }

    private static String rendered(JsonObject post, String field) {
        JsonElement value = post.get(field);
        if (value == null || !value.isJsonObject()) {
            return "";
        }
        return string(value.getAsJsonObject(), "rendered");
    }

    private static String string(JsonObject object, String field) {
        JsonElement value = object.get(field);
        return value != null && value.isJsonPrimitive() ? value.getAsString().trim() : "";
    }

    private static String download(String address) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        try {
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("GMI's news feed returned HTTP " + status);
            }
            try (InputStream in = connection.getInputStream()) {
                return new String(readAll(in), StandardCharsets.UTF_8);
            }
        } finally {
            connection.disconnect();
        }
    }

    static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    private void save(String json) {
        try (OutputStream out = new FileOutputStream(cacheFile)) {
            out.write(json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Saving is a bonus for offline use. The news on screen is unaffected.
        }
    }

    @Nullable
    private String readSaved() {
        if (!cacheFile.exists()) {
            return null;
        }
        try (InputStream in = new FileInputStream(cacheFile)) {
            return new String(readAll(in), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
}
