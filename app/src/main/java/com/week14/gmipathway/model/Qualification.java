package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A qualification the Eligibility Checker accepts: its grade scale and the subjects its form offers.
 */
public class Qualification {
    /** Subject group names used by the entry rules in courses.json. */
    public static final String GROUP_SCIENCE_TECHNICAL = "SCIENCE_TECHNICAL";
    public static final String GROUP_SCIENCE_TECHNICAL_VOCATIONAL = "SCIENCE_TECHNICAL_VOCATIONAL";

    /** Rule levels that stand for this qualification's credit and pass grades. */
    public static final String LEVEL_CREDIT = "CREDIT";
    public static final String LEVEL_PASS = "PASS";

    private String id;
    private String name;
    private String subjectLabel;
    private String gradeLabel;
    private List<String> grades;
    private String creditGrade;
    private String passGrade;
    private List<String> certificateSubjects;
    private List<String> requiredSubjects;
    private List<String> defaultSubjects;
    private List<String> subjects;
    private Map<String, List<String>> subjectGroups;
    private String supportingQualificationId;
    private List<String> supportingSubjects;
    private List<String> supportingOptionalSubjects;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /** Returns a label to replace "Subject" on the form, or null to keep it. */
    public String getSubjectLabel() {
        return subjectLabel;
    }

    /** Returns a label to replace "Grade" on the form, or null to keep it. */
    public String getGradeLabel() {
        return gradeLabel;
    }

    /** Grades from best to worst, so a lower index always means a better grade. */
    public List<String> getGrades() {
        return orEmpty(grades);
    }

    /** Subjects that must be passed to hold the certificate, such as Sejarah for SPM. */
    public List<String> getCertificateSubjects() {
        return orEmpty(certificateSubjects);
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

    /** Returns the subjects in a named group, or an empty list if there is no such group. */
    public List<String> getSubjectGroup(String groupName) {
        if (subjectGroups == null || !subjectGroups.containsKey(groupName)) {
            return Collections.emptyList();
        }
        return subjectGroups.get(groupName);
    }

    /** Returns the other qualification whose results are also needed, or null if there is none. */
    public String getSupportingQualificationId() {
        return supportingQualificationId;
    }

    /** Supporting subjects every candidate must enter. */
    public List<String> getSupportingSubjects() {
        return orEmpty(supportingSubjects);
    }

    /** Supporting subjects the prospect may remove. */
    public List<String> getSupportingOptionalSubjects() {
        return orEmpty(supportingOptionalSubjects);
    }

    /** Turns CREDIT or PASS into this qualification's grade. An exact grade is returned as it is. */
    public String resolveGrade(String level) {
        if (LEVEL_CREDIT.equals(level)) {
            return creditGrade;
        }
        if (LEVEL_PASS.equals(level)) {
            return passGrade;
        }
        return level;
    }

    /** Returns true if the grade is at least the minimum. A missing grade never meets it. */
    public boolean meetsGrade(String grade, String minimumLevel) {
        int rank = getGrades().indexOf(grade);
        int minimumRank = getGrades().indexOf(resolveGrade(minimumLevel));
        return rank >= 0 && minimumRank >= 0 && rank <= minimumRank;
    }

    private static List<String> orEmpty(List<String> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
