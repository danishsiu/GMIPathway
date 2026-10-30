package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * The result of checking one programme.
 * Reasons explain a shortfall. Notes are extra guidance, such as a MUET requirement.
 */
public class EligibilityOutcome {
    private final Course course;
    private final EligibilityStatus status;
    private final List<EligibilityReason> reasons;
    private final List<String> notes;

    public EligibilityOutcome(Course course, EligibilityStatus status,
                              List<EligibilityReason> reasons, List<String> notes) {
        this.course = course;
        this.status = status;
        this.reasons = Collections.unmodifiableList(reasons);
        this.notes = Collections.unmodifiableList(notes);
    }

    public Course getCourse() {
        return course;
    }

    public EligibilityStatus getStatus() {
        return status;
    }

    public List<EligibilityReason> getReasons() {
        return reasons;
    }

    public List<String> getNotes() {
        return notes;
    }
}
