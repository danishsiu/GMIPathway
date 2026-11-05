package com.week14.gmipathway.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * One campus photo that ships inside the app, with a short caption.
 */
public final class CampusPhoto {
    @DrawableRes
    private final int imageRes;
    @StringRes
    private final int captionRes;

    public CampusPhoto(@DrawableRes int imageRes, @StringRes int captionRes) {
        this.imageRes = imageRes;
        this.captionRes = captionRes;
    }

    @DrawableRes
    public int getImageRes() {
        return imageRes;
    }

    @StringRes
    public int getCaptionRes() {
        return captionRes;
    }
}
