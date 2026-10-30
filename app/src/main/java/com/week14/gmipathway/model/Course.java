package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * One GMI programme, read from assets/courses.json.
 * Optional text fields are null when GMI has not published them.
 */
public class Course {
    private String id;
    private String name;
    private ProgrammeCategory category;
    private String department;
    private String code;
    private int credits;
    private String summary;
    private String description;
    private String duration;
    private String intake;
    private String studyMode;
    private String fee;
    private List<String> coreAreas;
    private List<String> certificates;
    private List<String> careers;
    private List<String> progression;
    private List<SubjectGroup> subjectGroups;
    private String entryGroupId;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProgrammeCategory getCategory() {
        return category;
    }

    public String getDepartment() {
        return department;
    }

    public String getCode() {
        return code;
    }

    public int getCredits() {
        return credits;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public String getIntake() {
        return intake;
    }

    public String getStudyMode() {
        return studyMode;
    }

    public String getFee() {
        return fee;
    }

    public List<String> getCoreAreas() {
        return orEmpty(coreAreas);
    }

    public List<String> getCertificates() {
        return orEmpty(certificates);
    }

    public List<String> getCareers() {
        return orEmpty(careers);
    }

    public List<String> getProgression() {
        return orEmpty(progression);
    }

    public List<SubjectGroup> getSubjectGroups() {
        return orEmpty(subjectGroups);
    }

    /** Returns the shared entry rule group, or null if GMI has not published one. */
    public String getEntryGroupId() {
        return entryGroupId;
    }

    private static <T> List<T> orEmpty(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
