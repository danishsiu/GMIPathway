package com.week14.gmipathway.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The results a prospect enters on the Eligibility Checker form.
 * Supporting grades hold the SPM results that some STPM routes also need.
 */
public class EligibilityInput implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String qualificationId;
    private final ArrayList<SubjectGrade> grades = new ArrayList<>();
    private final ArrayList<SubjectGrade> supportingGrades = new ArrayList<>();

    public EligibilityInput(String qualificationId) {
        this.qualificationId = qualificationId;
    }

    public String getQualificationId() {
        return qualificationId;
    }

    public List<SubjectGrade> getGrades() {
        return grades;
    }

    public List<SubjectGrade> getSupportingGrades() {
        return supportingGrades;
    }
}
