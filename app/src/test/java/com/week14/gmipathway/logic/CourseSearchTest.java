package com.week14.gmipathway.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.Course;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks the course search against the real course data in both languages.
 */
public class CourseSearchTest {
    private static List<Course> english;
    private static List<Course> malay;

    @BeforeClass
    public static void loadCourses() throws IOException {
        english = load("src/main/assets/courses.json");
        malay = load("src/main/assets/courses_ms.json");
    }

    private static List<Course> load(String path) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            return CourseRepository.parseCatalog(reader).getCourses();
        }
    }

    @Test
    public void findsAProgrammeByPartOfItsName() {
        assertEquals(List.of("software_engineering"), ids(CourseSearch.search(english, "software")));
    }

    @Test
    public void ignoresCapitalLettersAndExtraSpaces() {
        assertEquals(List.of("gapp"), ids(CourseSearch.search(english, "  GAPP ")));
    }

    @Test
    public void everyWordMustMatchInAnyOrder() {
        assertEquals(List.of("cnc_precision"),
                ids(CourseSearch.search(english, "precision cnc technology")));
    }

    @Test
    public void findsProgrammesByDepartment() {
        assertEquals(3, CourseSearch.search(english, "computer & information").size());
    }

    @Test
    public void findsProgrammesByCode() {
        assertEquals(List.of("software_engineering"), ids(CourseSearch.search(english, "FA 9541")));
    }

    @Test
    public void returnsNothingWhenNoCourseMatches() {
        assertTrue(CourseSearch.search(english, "medicine").isEmpty());
    }

    @Test
    public void searchesMalayTextAndEnglishProgrammeNames() {
        assertEquals(List.of("software_engineering"), ids(CourseSearch.search(malay, "perisian pintar")));
        assertEquals(List.of("software_engineering"), ids(CourseSearch.search(malay, "software")));
    }

    private static List<String> ids(List<Course> courses) {
        List<String> ids = new ArrayList<>();
        for (Course course : courses) {
            ids.add(course.getId());
        }
        return ids;
    }
}
