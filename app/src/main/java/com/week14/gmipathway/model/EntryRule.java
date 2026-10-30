package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * One way to qualify for a programme with a given qualification, stored as data in courses.json.
 * When a qualification has several rules, meeting any one of them is enough.
 */
public class EntryRule {
    private String qualificationId;
    private boolean certificateRequired;
    private int minSubjects;
    private String minSubjectGrade;
    private List<EntryRequirement> requirements;
    private String note;
    private String contactNote;
    private boolean noteOnly;

    public String getQualificationId() {
        return qualificationId;
    }

    /** True when the prospect must hold the full certificate, such as a pass in SPM. */
    public boolean isCertificateRequired() {
        return certificateRequired;
    }

    /** Returns how many different subjects must reach {@link #getMinSubjectGrade()}. */
    public int getMinSubjects() {
        return minSubjects;
    }

    public String getMinSubjectGrade() {
        return minSubjectGrade;
    }

    public List<EntryRequirement> getRequirements() {
        return requirements == null ? Collections.emptyList() : requirements;
    }

    /** Returns extra guidance shown when this rule is met, or null if there is none. */
    public String getNote() {
        return note;
    }

    /** Returns the guidance shown when only contact-GMI requirements are missing, or null. */
    public String getContactNote() {
        return contactNote;
    }

    /** True when this rule only adds its note, and does not decide eligibility. */
    public boolean isNoteOnly() {
        return noteOnly;
    }
}
