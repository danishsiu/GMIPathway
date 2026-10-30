package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * Why a rule was not met. It holds facts, not sentences, so the screen can word it from strings.xml.
 */
public final class EligibilityReason {

    public enum Type {
        /** The prospect does not hold the full certificate, such as a pass in SPM. */
        CERTIFICATE,
        /** Too few subjects reached the required grade. */
        SUBJECT_COUNT,
        /** A named subject did not reach the required grade. */
        SUBJECT,
        /** No subject in a group reached the required grade. */
        SUBJECT_GROUP,
        /** The checker has no rule for this qualification on this programme. */
        NO_RULE,
        /** GMI has not published entry requirements for this programme. */
        UNPUBLISHED
    }

    private final Type type;
    private final List<String> subjects;
    private final String subjectGroup;
    private final String grade;
    private final int count;
    private final boolean supporting;

    private EligibilityReason(Type type, List<String> subjects, String subjectGroup, String grade,
                              int count, boolean supporting) {
        this.type = type;
        this.subjects = subjects;
        this.subjectGroup = subjectGroup;
        this.grade = grade;
        this.count = count;
        this.supporting = supporting;
    }

    public static EligibilityReason of(Type type) {
        return new EligibilityReason(type, Collections.emptyList(), null, null, 0, false);
    }

    public static EligibilityReason certificate(List<String> subjects) {
        return new EligibilityReason(Type.CERTIFICATE, subjects, null, null, 0, false);
    }

    public static EligibilityReason subjectCount(int count, String grade) {
        return new EligibilityReason(
                Type.SUBJECT_COUNT, Collections.emptyList(), null, grade, count, false);
    }

    public static EligibilityReason subject(List<String> subjects, String grade, boolean supporting) {
        return new EligibilityReason(Type.SUBJECT, subjects, null, grade, 0, supporting);
    }

    public static EligibilityReason subjectGroup(String groupName, String grade) {
        return new EligibilityReason(
                Type.SUBJECT_GROUP, Collections.emptyList(), groupName, grade, 0, false);
    }

    public Type getType() {
        return type;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public String getGrade() {
        return grade;
    }

    public int getCount() {
        return count;
    }

    public boolean isSupporting() {
        return supporting;
    }
}
