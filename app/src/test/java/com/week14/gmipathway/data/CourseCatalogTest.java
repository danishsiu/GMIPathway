package com.week14.gmipathway.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.CourseCatalog;
import com.week14.gmipathway.model.EntryGroup;
import com.week14.gmipathway.model.ProgrammeCategory;
import com.week14.gmipathway.model.Qualification;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks the real course data, so a mistake in courses.json is caught before the app runs.
 */
public class CourseCatalogTest {
    private static CourseCatalog catalog;

    @BeforeClass
    public static void loadCatalog() throws IOException {
        try (Reader reader = Files.newBufferedReader(
                Paths.get("src/main/assets/courses.json"), StandardCharsets.UTF_8)) {
            catalog = CourseRepository.parseCatalog(reader);
        }
    }

    @Test
    public void listsEveryProgrammeInEachCategory() {
        assertEquals(17, catalog.getCourses().size());
        assertEquals(15, countCourses(ProgrammeCategory.DIPLOMA));
        assertEquals(1, countCourses(ProgrammeCategory.GAPP));
        assertEquals(1, countCourses(ProgrammeCategory.GUFP));
    }

    @Test
    public void courseIdsAreUnique() {
        Set<String> ids = new HashSet<>();
        for (Course course : catalog.getCourses()) {
            assertTrue("Duplicate id: " + course.getId(), ids.add(course.getId()));
        }
    }

    @Test
    public void everyCourseHasItsCoreInformation() {
        for (Course course : catalog.getCourses()) {
            String id = course.getId();
            assertFalse(id, course.getName().isEmpty());
            assertFalse(id, course.getDepartment().isEmpty());
            assertFalse(id, course.getCode().isEmpty());
            assertFalse(id, course.getSummary().isEmpty());
            assertFalse(id, course.getDescription().isEmpty());
            assertFalse(id, course.getDuration().isEmpty());
            assertFalse(id, course.getIntake().isEmpty());
            assertFalse(id, course.getFee().isEmpty());
            assertFalse(id, course.getCoreAreas().isEmpty());
            assertFalse(id, course.getProgression().isEmpty());
            assertTrue(id, course.getCredits() > 0);
        }
    }

    @Test
    public void everyEntryGroupReferenceExists() {
        for (Course course : catalog.getCourses()) {
            if (course.getEntryGroupId() != null) {
                assertNotNull(course.getId(), findEntryGroup(course.getEntryGroupId()));
            }
        }
    }

    @Test
    public void cyberSecurityHasNoEntryRulesBecauseGmiPublishesNone() {
        assertNull(findCourse("cyber_security").getEntryGroupId());
    }

    @Test
    public void precisionToolingHidesItsUnconfirmedSubjectList() {
        assertTrue(findCourse("precision_tooling").getSubjectGroups().isEmpty());
    }

    @Test
    public void everyQualificationHasGradesAndSubjects() {
        assertEquals(4, catalog.getQualifications().size());
        for (Qualification qualification : catalog.getQualifications()) {
            assertFalse(qualification.getId(), qualification.getGrades().isEmpty());
            assertFalse(qualification.getId(), qualification.getSubjects().isEmpty());
        }
    }

    private int countCourses(ProgrammeCategory category) {
        int count = 0;
        for (Course course : catalog.getCourses()) {
            if (course.getCategory() == category) {
                count++;
            }
        }
        return count;
    }

    private Course findCourse(String id) {
        for (Course course : catalog.getCourses()) {
            if (course.getId().equals(id)) {
                return course;
            }
        }
        throw new AssertionError("No course with id " + id);
    }

    private EntryGroup findEntryGroup(String id) {
        for (EntryGroup group : catalog.getEntryGroups()) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }
}
