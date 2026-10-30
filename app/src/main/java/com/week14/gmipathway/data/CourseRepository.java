package com.week14.gmipathway.data;

import android.content.Context;

import com.google.gson.Gson;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.CourseCatalog;
import com.week14.gmipathway.model.EntryGroup;
import com.week14.gmipathway.model.ProgrammeCategory;
import com.week14.gmipathway.model.Qualification;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Gives the app one shared copy of the course data in assets/courses.json.
 */
public final class CourseRepository {
    private static final String CATALOG_FILE = "courses.json";

    private static CourseRepository instance;

    private final CourseCatalog catalog;

    private CourseRepository(CourseCatalog catalog) {
        this.catalog = catalog;
    }

    /** Returns the shared repository, loading the data the first time it is needed. */
    public static synchronized CourseRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CourseRepository(loadCatalog(context.getApplicationContext()));
        }
        return instance;
    }

    private static CourseCatalog loadCatalog(Context context) {
        try (Reader reader = new InputStreamReader(
                context.getAssets().open(CATALOG_FILE), StandardCharsets.UTF_8)) {
            return parseCatalog(reader);
        } catch (IOException e) {
            // The file ships inside the app, so this only fails if the build itself is broken.
            throw new IllegalStateException("Could not read " + CATALOG_FILE, e);
        }
    }

    /** Parses catalog JSON. Kept separate from Android so unit tests can check the real data. */
    public static CourseCatalog parseCatalog(Reader reader) {
        return new Gson().fromJson(reader, CourseCatalog.class);
    }

    public List<Course> getCourses() {
        return catalog.getCourses();
    }

    public List<Course> getCourses(ProgrammeCategory category) {
        List<Course> matches = new ArrayList<>();
        for (Course course : catalog.getCourses()) {
            if (course.getCategory() == category) {
                matches.add(course);
            }
        }
        return matches;
    }

    /** Returns the course with this id, or null if there is none. */
    public Course findCourse(String id) {
        for (Course course : catalog.getCourses()) {
            if (course.getId().equals(id)) {
                return course;
            }
        }
        return null;
    }

    /** Returns the entry group with this id, or null if there is none. */
    public EntryGroup findEntryGroup(String id) {
        for (EntryGroup group : catalog.getEntryGroups()) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }

    public List<Qualification> getQualifications() {
        return catalog.getQualifications();
    }

    /** Returns the qualification with this id, or null if there is none. */
    public Qualification findQualification(String id) {
        for (Qualification qualification : catalog.getQualifications()) {
            if (qualification.getId().equals(id)) {
                return qualification;
            }
        }
        return null;
    }
}
