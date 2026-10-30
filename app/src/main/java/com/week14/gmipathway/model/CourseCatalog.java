package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * Everything stored in assets/courses.json.
 */
public class CourseCatalog {
    private List<Course> courses;
    private List<EntryGroup> entryGroups;
    private List<Qualification> qualifications;

    public List<Course> getCourses() {
        return courses == null ? Collections.emptyList() : courses;
    }

    public List<EntryGroup> getEntryGroups() {
        return entryGroups == null ? Collections.emptyList() : entryGroups;
    }

    public List<Qualification> getQualifications() {
        return qualifications == null ? Collections.emptyList() : qualifications;
    }
}
