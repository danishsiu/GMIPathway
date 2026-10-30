package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * A labelled set of subjects, such as "Semester 1" and its subjects.
 */
public class SubjectGroup {
    private String label;
    private List<String> subjects;

    public String getLabel() {
        return label;
    }

    public List<String> getSubjects() {
        return subjects == null ? Collections.emptyList() : subjects;
    }
}
