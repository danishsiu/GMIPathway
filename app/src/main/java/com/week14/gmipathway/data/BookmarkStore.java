package com.week14.gmipathway.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Remembers which courses the prospect has saved, using SharedPreferences.
 */
public final class BookmarkStore {
    private static final String PREFS_NAME = "bookmarks";
    private static final String KEY_COURSE_IDS = "courseIds";

    private final SharedPreferences preferences;

    public BookmarkStore(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isSaved(String courseId) {
        return getIds().contains(courseId);
    }

    /** Saves the course, or removes it if it was already saved. */
    public void toggle(String courseId) {
        // Android does not allow changes to the set it returns, so this edits a copy.
        Set<String> ids = new HashSet<>(getIds());
        if (!ids.add(courseId)) {
            ids.remove(courseId);
        }
        preferences.edit().putStringSet(KEY_COURSE_IDS, ids).apply();
    }

    private Set<String> getIds() {
        return preferences.getStringSet(KEY_COURSE_IDS, Collections.emptySet());
    }
}
