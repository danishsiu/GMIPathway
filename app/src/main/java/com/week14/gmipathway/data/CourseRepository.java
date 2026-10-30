package com.week14.gmipathway.data;

import android.content.Context;

import com.google.gson.Gson;
import com.week14.gmipathway.R;
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
 * Gives the app one shared copy of the course data, in the language the app is using.
 * English is in assets/courses.json and Bahasa Melayu is in assets/courses_ms.json.
 */
public final class CourseRepository {
    private static CourseRepository instance;

    private final String catalogFile;
    private final CourseCatalog catalog;

    private CourseRepository(String catalogFile, CourseCatalog catalog) {
        this.catalogFile = catalogFile;
        this.catalog = catalog;
    }

    /**
     * Returns the shared repository, loading the data the first time it is needed and again if
     * the language changes. Pass an Activity, because only it knows the language chosen in the app.
     */
    public static synchronized CourseRepository getInstance(Context context) {
        String file = context.getString(R.string.catalog_file);
        if (instance == null || !instance.catalogFile.equals(file)) {
            instance = new CourseRepository(file, loadCatalog(context, file));
        }
        return instance;
    }

    private static CourseCatalog loadCatalog(Context context, String file) {
        try (Reader reader = new InputStreamReader(
                context.getAssets().open(file), StandardCharsets.UTF_8)) {
            return parseCatalog(reader);
        } catch (IOException e) {
            // The file ships inside the app, so this only fails if the build itself is broken.
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    /** Parses catalog JSON. Kept separate from Android so unit tests can check the real data. */
    public static CourseCatalog parseCatalog(Reader reader) {
        return new Gson().fromJson(reader, CourseCatalog.class);
    }

    public CourseCatalog getCatalog() {
        return catalog;
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
