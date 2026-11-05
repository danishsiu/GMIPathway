package com.week14.gmipathway.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.week14.gmipathway.model.Facility;
import com.week14.gmipathway.model.TechCentre;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks the campus data, so a missing photo or a broken link is caught before the app runs.
 */
public class CampusCatalogTest {

    @Test
    public void hasTheEightFacilityGroupsFromGmisGallery() {
        assertEquals(8, CampusCatalog.getFacilities().size());
    }

    @Test
    public void everyFacilityHasPhotos() {
        for (Facility facility : CampusCatalog.getFacilities()) {
            assertFalse(facility.getPhotos().isEmpty());
        }
    }

    @Test
    public void everyCentreLinksToGmisWebsiteOverHttps() {
        List<TechCentre> all = new ArrayList<>(CampusCatalog.getTechCentres());
        all.addAll(CampusCatalog.getExamCentres());
        Set<String> urls = new HashSet<>();
        for (TechCentre centre : all) {
            assertTrue(centre.getUrl(), centre.getUrl().startsWith("https://www.gmi.edu.my/"));
            assertTrue("Duplicate link " + centre.getUrl(), urls.add(centre.getUrl()));
        }
    }

    @Test
    public void listsTwelveTechnologyCentresAndThreeExamCentres() {
        // GMI's page lists 13 centres; iFactory is marked "currently not available" and left out.
        assertEquals(12, CampusCatalog.getTechCentres().size());
        assertEquals(3, CampusCatalog.getExamCentres().size());
    }
}
