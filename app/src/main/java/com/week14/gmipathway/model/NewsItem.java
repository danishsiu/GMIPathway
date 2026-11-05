package com.week14.gmipathway.model;

import androidx.annotation.Nullable;

/**
 * One news post from GMI's website. The title and excerpt are HTML, as GMI publishes them.
 */
public final class NewsItem {
    private final String titleHtml;
    private final String excerptHtml;
    private final String date;
    private final String link;
    @Nullable
    private final String imageUrl;

    public NewsItem(String titleHtml, String excerptHtml, String date, String link,
                    @Nullable String imageUrl) {
        this.titleHtml = titleHtml;
        this.excerptHtml = excerptHtml;
        this.date = date;
        this.link = link;
        this.imageUrl = imageUrl;
    }

    public String getTitleHtml() {
        return titleHtml;
    }

    public String getExcerptHtml() {
        return excerptHtml;
    }

    /** The publish date in GMI's format, for example "2026-08-17T16:44:17". */
    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    /** The post's photo, or null when the post has none. */
    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }
}
