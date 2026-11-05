package com.week14.gmipathway.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.week14.gmipathway.model.NewsItem;

import org.junit.Test;

import java.util.List;

/**
 * Checks that GMI's news feed is read correctly, including posts with missing parts.
 * The sample follows the format gmi.edu.my returned on 5 November 2026.
 */
public class NewsRepositoryTest {

    private static final String FEED = "["
            // A normal post: the medium_large photo is preferred.
            + "{\"date\":\"2026-08-17T16:44:17\","
            + "\"link\":\"https://www.gmi.edu.my/news/integrity/\","
            + "\"title\":{\"rendered\":\"GMI Integrity Strengthening Programme 2026\"},"
            + "\"excerpt\":{\"rendered\":\"<p>Experts from MACC and MARA&#8230;</p>\"},"
            + "\"_embedded\":{\"wp:featuredmedia\":[{\"source_url\":\"https://x/full.jpg\","
            + "\"media_details\":{\"sizes\":{"
            + "\"medium\":{\"source_url\":\"https://x/medium.jpg\"},"
            + "\"medium_large\":{\"source_url\":\"https://x/medium_large.jpg\"},"
            + "\"full\":{\"source_url\":\"https://x/full.jpg\"}}}}]}},"
            // GMI's Cisco post has no medium_large or large size, so the full photo is used.
            + "{\"date\":\"2026-08-17T15:14:27\","
            + "\"link\":\"https://www.gmi.edu.my/news/cisco/\","
            + "\"title\":{\"rendered\":\"Top 10 Percent Worldwide\"},"
            + "\"excerpt\":{\"rendered\":\"\"},"
            + "\"_embedded\":{\"wp:featuredmedia\":[{\"source_url\":\"https://x/cisco.png\","
            + "\"media_details\":{\"sizes\":{"
            + "\"medium\":{\"source_url\":\"https://x/cisco-300.png\"},"
            + "\"full\":{\"source_url\":\"https://x/cisco.png\"}}}}]}},"
            // A post without a photo.
            + "{\"date\":\"2026-07-29T14:42:11\","
            + "\"link\":\"https://www.gmi.edu.my/news/sabah/\","
            + "\"title\":{\"rendered\":\"GMI Welcomes Yayasan Sabah\"},"
            + "\"excerpt\":{\"rendered\":\"<p>Visit.</p>\"}},"
            // A post with no title is skipped.
            + "{\"date\":\"2026-07-01T00:00:00\",\"link\":\"https://www.gmi.edu.my/news/x/\","
            + "\"title\":{\"rendered\":\"\"}}"
            + "]";

    @Test
    public void readsTitleLinkDateAndExcerpt() {
        NewsItem first = NewsRepository.parse(FEED).get(0);
        assertEquals("GMI Integrity Strengthening Programme 2026", first.getTitleHtml());
        assertEquals("https://www.gmi.edu.my/news/integrity/", first.getLink());
        assertEquals("2026-08-17T16:44:17", first.getDate());
        assertEquals("<p>Experts from MACC and MARA&#8230;</p>", first.getExcerptHtml());
    }

    @Test
    public void prefersTheMediumLargePhoto() {
        assertEquals("https://x/medium_large.jpg",
                NewsRepository.parse(FEED).get(0).getImageUrl());
    }

    @Test
    public void fallsBackToTheFullPhotoWhenSmallerSizesAreMissing() {
        assertEquals("https://x/cisco.png", NewsRepository.parse(FEED).get(1).getImageUrl());
    }

    @Test
    public void postWithoutPhotoHasNoImage() {
        assertNull(NewsRepository.parse(FEED).get(2).getImageUrl());
    }

    @Test
    public void skipsPostsWithoutATitle() {
        List<NewsItem> items = NewsRepository.parse(FEED);
        assertEquals(3, items.size());
    }

    @Test
    public void emptyFeedGivesNoNews() {
        assertEquals(0, NewsRepository.parse("[]").size());
    }
}
