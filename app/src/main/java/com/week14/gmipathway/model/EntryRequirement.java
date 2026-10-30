package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * One condition inside an entry rule: a minimum grade in a named subject or a subject group.
 */
public class EntryRequirement {
    private List<String> subjects;
    private String subjectGroup;
    private String minGrade;
    private boolean supporting;
    private boolean contactIfMissing;

    /** Any one of these subjects meets the requirement. Empty when a subject group is used. */
    public List<String> getSubjects() {
        return subjects == null ? Collections.emptyList() : subjects;
    }

    /** Returns the name of a subject group, or null when named subjects are used. */
    public String getSubjectGroup() {
        return subjectGroup;
    }

    /** Returns CREDIT, PASS or an exact grade from the qualification's scale. */
    public String getMinGrade() {
        return minGrade;
    }

    /** True when this checks the supporting results, such as SPM results for an STPM holder. */
    public boolean isSupporting() {
        return supporting;
    }

    /** True when missing this sends the prospect to GMI instead of ruling them out. */
    public boolean isContactIfMissing() {
        return contactIfMissing;
    }
}
