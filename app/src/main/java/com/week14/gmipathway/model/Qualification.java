package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * A qualification the Eligibility Checker accepts, with the grades and subjects its form offers.
 */
public class Qualification {
    private String id;
    private String name;
    private List<String> grades;
    private List<String> requiredSubjects;
    private List<String> defaultSubjects;
    private List<String> subjects;
    private String supportingQualificationId;
    private List<String> supportingSubjects;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /** Grades from best to worst, so a lower index always means a better grade. */
    public List<String> getGrades() {
        return orEmpty(grades);
    }

    /** Subjects every candidate must enter. Their form rows cannot be removed. */
    public List<String> getRequiredSubjects() {
        return orEmpty(requiredSubjects);
    }

    /** Subjects suggested on the form, which the prospect may remove. */
    public List<String> getDefaultSubjects() {
        return orEmpty(defaultSubjects);
    }

    /** Subjects offered by the "Add subject" dropdown. */
    public List<String> getSubjects() {
        return orEmpty(subjects);
    }

    /** Returns the other qualification whose results are also needed, or null if there is none. */
    public String getSupportingQualificationId() {
        return supportingQualificationId;
    }

    public List<String> getSupportingSubjects() {
        return orEmpty(supportingSubjects);
    }

    private static List<String> orEmpty(List<String> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
