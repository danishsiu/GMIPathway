package com.week14.gmipathway.model;

import androidx.annotation.StringRes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A group of campus facilities, such as the library or the sports facilities, with its photos.
 */
public final class Facility {
    @StringRes
    private final int nameRes;
    @StringRes
    private final int summaryRes;
    private final List<CampusPhoto> photos;

    public Facility(@StringRes int nameRes, @StringRes int summaryRes, CampusPhoto... photos) {
        this.nameRes = nameRes;
        this.summaryRes = summaryRes;
        this.photos = Collections.unmodifiableList(Arrays.asList(photos));
    }

    @StringRes
    public int getNameRes() {
        return nameRes;
    }

    @StringRes
    public int getSummaryRes() {
        return summaryRes;
    }

    public List<CampusPhoto> getPhotos() {
        return photos;
    }
}
