package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * The result of checking one programme, with the reasons shown to the prospect.
 */
public class EligibilityOutcome {
    private final Course course;
    private final EligibilityStatus status;
    private final List<String> reasons;

    public EligibilityOutcome(Course course, EligibilityStatus status, List<String> reasons) {
        this.course = course;
        this.status = status;
        this.reasons = Collections.unmodifiableList(reasons);
    }

    public Course getCourse() {
        return course;
    }

    public EligibilityStatus getStatus() {
        return status;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
