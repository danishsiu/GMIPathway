package com.week14.gmipathway.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * A GMI technology centre or examination centre, with a link to its page on GMI's website.
 */
public final class TechCentre {
    /** Used when GMI's page has no photo of the centre itself. */
    public static final int NO_PHOTO = 0;

    @StringRes
    private final int nameRes;
    @StringRes
    private final int descriptionRes;
    @DrawableRes
    private final int photoRes;
    @Nullable
    private final String partner;
    private final String url;

    public TechCentre(@StringRes int nameRes, @StringRes int descriptionRes,
                      @DrawableRes int photoRes, @Nullable String partner, String url) {
        this.nameRes = nameRes;
        this.descriptionRes = descriptionRes;
        this.photoRes = photoRes;
        this.partner = partner;
        this.url = url;
    }

    @StringRes
    public int getNameRes() {
        return nameRes;
    }

    @StringRes
    public int getDescriptionRes() {
        return descriptionRes;
    }

    @DrawableRes
    public int getPhotoRes() {
        return photoRes;
    }

    /** The industry partner named on GMI's page, or null when the page names none. */
    @Nullable
    public String getPartner() {
        return partner;
    }

    public String getUrl() {
        return url;
    }
}
