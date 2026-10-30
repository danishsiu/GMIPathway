package com.week14.gmipathway.model;

import java.io.Serializable;

/**
 * A subject and the grade the prospect entered for it.
 * Either value is null until the prospect selects it on the form.
 */
public class SubjectGrade implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subject;
    private String grade;

    public SubjectGrade(String subject, String grade) {
        this.subject = subject;
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
