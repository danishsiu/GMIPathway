package com.week14.gmipathway.model;

import java.util.Collections;
import java.util.List;

/**
 * Entry requirements shared by every programme under the same accreditation standard.
 * Routes and notes are shown on the course page. Rules are what the Eligibility Checker applies.
 */
public class EntryGroup {
    private String id;
    private String standard;
    private List<EntryRoute> routes;
    private List<String> notes;
    private List<EntryRule> rules;

    public String getId() {
        return id;
    }

    public String getStandard() {
        return standard;
    }

    public List<EntryRoute> getRoutes() {
        return routes == null ? Collections.emptyList() : routes;
    }

    public List<String> getNotes() {
        return notes == null ? Collections.emptyList() : notes;
    }

    public List<EntryRule> getRules() {
        return rules == null ? Collections.emptyList() : rules;
    }
}
