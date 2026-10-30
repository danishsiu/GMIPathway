package com.week14.gmipathway.model;

/**
 * How a prospect's results compare with a programme's entry requirements.
 */
public enum EligibilityStatus {
    ELIGIBLE,
    /** The app cannot decide, so the prospect should ask GMI. */
    CONTACT_GMI,
    NOT_ELIGIBLE,
    /** The checker has no rule for this qualification on this programme. */
    NOT_ASSESSED
}
