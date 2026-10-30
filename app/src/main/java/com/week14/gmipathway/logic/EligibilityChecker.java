package com.week14.gmipathway.logic;

import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.CourseCatalog;
import com.week14.gmipathway.model.EligibilityInput;
import com.week14.gmipathway.model.EligibilityOutcome;
import com.week14.gmipathway.model.EligibilityReason;
import com.week14.gmipathway.model.EligibilityStatus;
import com.week14.gmipathway.model.EntryGroup;
import com.week14.gmipathway.model.EntryRequirement;
import com.week14.gmipathway.model.EntryRule;
import com.week14.gmipathway.model.Qualification;
import com.week14.gmipathway.model.SubjectGrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compares a prospect's results with each programme's entry rules.
 * The rules come from courses.json, so changing a requirement never needs a code change.
 * This class uses no Android code, which lets plain JUnit tests run it against the real data.
 */
public final class EligibilityChecker {
    private final CourseCatalog catalog;

    public EligibilityChecker(CourseCatalog catalog) {
        this.catalog = catalog;
    }

    /** Returns one outcome for every programme, in catalogue order. */
    public List<EligibilityOutcome> check(EligibilityInput input) {
        List<EligibilityOutcome> outcomes = new ArrayList<>();
        for (Course course : catalog.getCourses()) {
            outcomes.add(checkCourse(course, input));
        }
        return outcomes;
    }

    private EligibilityOutcome checkCourse(Course course, EligibilityInput input) {
        EntryGroup group = findEntryGroup(course.getEntryGroupId());
        if (group == null) {
            return singleReason(course, EligibilityStatus.CONTACT_GMI,
                    EligibilityReason.Type.UNPUBLISHED);
        }

        List<EntryRule> rules = findRules(group, input.getQualificationId(), false);
        if (rules.isEmpty()) {
            return singleReason(course, EligibilityStatus.NOT_ASSESSED,
                    EligibilityReason.Type.NO_RULE);
        }

        // Meeting any one rule is enough. If none is met, explain the closest one.
        Attempt closest = null;
        for (EntryRule rule : rules) {
            Attempt attempt = evaluate(rule, input);
            if (attempt.isMet()) {
                return eligibleOutcome(course, group, rule, input);
            }
            if (closest == null || attempt.unmet.size() < closest.unmet.size()) {
                closest = attempt;
            }
        }
        return shortfallOutcome(course, closest);
    }

    private EligibilityOutcome eligibleOutcome(Course course, EntryGroup group, EntryRule rule,
                                               EligibilityInput input) {
        List<String> notes = new ArrayList<>();
        if (rule.getNote() != null) {
            notes.add(rule.getNote());
        }
        for (EntryRule extra : findRules(group, input.getQualificationId(), true)) {
            if (extra.getNote() != null && evaluate(extra, input).isMet()) {
                notes.add(extra.getNote());
            }
        }
        return new EligibilityOutcome(
                course, EligibilityStatus.ELIGIBLE, Collections.emptyList(), notes);
    }

    private EligibilityOutcome shortfallOutcome(Course course, Attempt attempt) {
        if (!attempt.onlyContactRequirementsUnmet()) {
            return new EligibilityOutcome(course, EligibilityStatus.NOT_ELIGIBLE,
                    attempt.unmet, Collections.emptyList());
        }
        String contactNote = attempt.rule.getContactNote();
        return new EligibilityOutcome(course, EligibilityStatus.CONTACT_GMI, attempt.unmet,
                contactNote == null ? Collections.emptyList()
                        : Collections.singletonList(contactNote));
    }

    private EligibilityOutcome singleReason(Course course, EligibilityStatus status,
                                            EligibilityReason.Type type) {
        return new EligibilityOutcome(course, status,
                Collections.singletonList(EligibilityReason.of(type)), Collections.emptyList());
    }

    /** Checks every part of a rule and records whatever is not met. */
    private Attempt evaluate(EntryRule rule, EligibilityInput input) {
        Qualification qualification = findQualification(input.getQualificationId());
        Map<String, String> grades = toGradeMap(input.getGrades());
        Attempt attempt = new Attempt(rule);

        if (rule.isCertificateRequired() && !holdsCertificate(qualification, grades)) {
            attempt.add(EligibilityReason.certificate(qualification.getCertificateSubjects()), false);
        }
        if (countSubjectsAtGrade(qualification, grades, rule.getMinSubjectGrade())
                < rule.getMinSubjects()) {
            attempt.add(EligibilityReason.subjectCount(rule.getMinSubjects(),
                    qualification.resolveGrade(rule.getMinSubjectGrade())), false);
        }
        for (EntryRequirement requirement : rule.getRequirements()) {
            checkRequirement(requirement, qualification, input, grades, attempt);
        }
        return attempt;
    }

    private void checkRequirement(EntryRequirement requirement, Qualification qualification,
                                  EligibilityInput input, Map<String, String> grades,
                                  Attempt attempt) {
        // Supporting requirements use the other qualification's results and grade scale.
        Qualification scale = requirement.isSupporting()
                ? findQualification(qualification.getSupportingQualificationId()) : qualification;
        Map<String, String> source = requirement.isSupporting()
                ? toGradeMap(input.getSupportingGrades()) : grades;
        boolean usesGroup = requirement.getSubjectGroup() != null;
        List<String> allowed = usesGroup
                ? scale.getSubjectGroup(requirement.getSubjectGroup()) : requirement.getSubjects();

        for (String subject : allowed) {
            if (scale.meetsGrade(source.get(subject), requirement.getMinGrade())) {
                return;
            }
        }

        String grade = scale.resolveGrade(requirement.getMinGrade());
        attempt.add(usesGroup
                ? EligibilityReason.subjectGroup(requirement.getSubjectGroup(), grade)
                : EligibilityReason.subject(allowed, grade, requirement.isSupporting()),
                requirement.isContactIfMissing());
    }

    private boolean holdsCertificate(Qualification qualification, Map<String, String> grades) {
        for (String subject : qualification.getCertificateSubjects()) {
            if (!qualification.meetsGrade(grades.get(subject), Qualification.LEVEL_PASS)) {
                return false;
            }
        }
        return true;
    }

    private int countSubjectsAtGrade(Qualification qualification, Map<String, String> grades,
                                     String minimumLevel) {
        int count = 0;
        for (String grade : grades.values()) {
            if (qualification.meetsGrade(grade, minimumLevel)) {
                count++;
            }
        }
        return count;
    }

    private static Map<String, String> toGradeMap(List<SubjectGrade> entries) {
        Map<String, String> grades = new HashMap<>();
        for (SubjectGrade entry : entries) {
            grades.put(entry.getSubject(), entry.getGrade());
        }
        return grades;
    }

    private List<EntryRule> findRules(EntryGroup group, String qualificationId, boolean noteOnly) {
        List<EntryRule> rules = new ArrayList<>();
        for (EntryRule rule : group.getRules()) {
            if (rule.getQualificationId().equals(qualificationId)
                    && rule.isNoteOnly() == noteOnly) {
                rules.add(rule);
            }
        }
        return rules;
    }

    private EntryGroup findEntryGroup(String id) {
        for (EntryGroup group : catalog.getEntryGroups()) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }

    private Qualification findQualification(String id) {
        for (Qualification qualification : catalog.getQualifications()) {
            if (qualification.getId().equals(id)) {
                return qualification;
            }
        }
        throw new IllegalArgumentException("Unknown qualification: " + id);
    }

    /** The result of trying one rule. */
    private static final class Attempt {
        final EntryRule rule;
        final List<EligibilityReason> unmet = new ArrayList<>();
        private boolean hasFirmShortfall;

        Attempt(EntryRule rule) {
            this.rule = rule;
        }

        /** Records a shortfall. GMI may waive a contact-only one, such as reinforcement Mathematics. */
        void add(EligibilityReason reason, boolean contactOnly) {
            unmet.add(reason);
            hasFirmShortfall |= !contactOnly;
        }

        boolean isMet() {
            return unmet.isEmpty();
        }

        boolean onlyContactRequirementsUnmet() {
            return !hasFirmShortfall;
        }
    }
}
