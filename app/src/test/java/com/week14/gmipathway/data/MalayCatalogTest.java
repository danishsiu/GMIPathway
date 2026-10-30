package com.week14.gmipathway.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.week14.gmipathway.logic.EligibilityChecker;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.CourseCatalog;
import com.week14.gmipathway.model.EligibilityInput;
import com.week14.gmipathway.model.EligibilityOutcome;
import com.week14.gmipathway.model.Qualification;
import com.week14.gmipathway.model.SubjectGrade;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checks that courses_ms.json only changes the wording of courses.json, never the facts or rules.
 */
public class MalayCatalogTest {
    private static CourseCatalog english;
    private static CourseCatalog malay;

    @BeforeClass
    public static void loadCatalogs() throws IOException {
        english = load("src/main/assets/courses.json");
        malay = load("src/main/assets/courses_ms.json");
    }

    private static CourseCatalog load(String path) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            return CourseRepository.parseCatalog(reader);
        }
    }

    @Test
    public void keepsEveryProgrammeWithItsOfficialDetails() {
        assertEquals(english.getCourses().size(), malay.getCourses().size());
        for (int i = 0; i < english.getCourses().size(); i++) {
            Course en = english.getCourses().get(i);
            Course ms = malay.getCourses().get(i);
            assertEquals(en.getId(), ms.getId());
            assertEquals(en.getName(), ms.getName());
            assertEquals(en.getCode(), ms.getCode());
            assertEquals(en.getCredits(), ms.getCredits());
            assertEquals(en.getCategory(), ms.getCategory());
            assertEquals(en.getDepartmentId(), ms.getDepartmentId());
            assertEquals(en.getEntryGroupId(), ms.getEntryGroupId());
            assertEquals(en.getCoreAreas(), ms.getCoreAreas());
            assertEquals(en.getCareers(), ms.getCareers());
        }
    }

    @Test
    public void translatesTheTextProspectsRead() {
        for (int i = 0; i < english.getCourses().size(); i++) {
            Course en = english.getCourses().get(i);
            Course ms = malay.getCourses().get(i);
            assertNotEquals(en.getId(), en.getSummary(), ms.getSummary());
            assertNotEquals(en.getId(), en.getDescription(), ms.getDescription());
            assertNotEquals(en.getId(), en.getFee(), ms.getFee());
            assertNotEquals(en.getId(), en.getIntake(), ms.getIntake());
        }
    }

    @Test
    public void everyRuleUsesRealGradesAndSubjects() {
        CourseCatalogTest.assertRulesAreValid(malay);
    }

    /** Tries every grade level, with all subjects and with only the core subjects. */
    @Test
    public void bothLanguagesGiveTheSameResultForEveryGradeLevel() {
        EligibilityChecker englishChecker = new EligibilityChecker(english);
        EligibilityChecker malayChecker = new EligibilityChecker(malay);

        for (int i = 0; i < english.getQualifications().size(); i++) {
            Qualification en = english.getQualifications().get(i);
            Qualification ms = malay.getQualifications().get(i);
            for (String grade : en.getGrades()) {
                assertSameResults(englishChecker, malayChecker, en, ms, grade, allSubjects(en));
                assertSameResults(englishChecker, malayChecker, en, ms, grade, coreSubjects(en));
            }
        }
    }

    private void assertSameResults(EligibilityChecker englishChecker, EligibilityChecker malayChecker,
                                   Qualification en, Qualification ms, String grade,
                                   List<String> subjects) {
        Map<String, String> names = subjectNames(en, ms);
        EligibilityInput englishInput = new EligibilityInput(en.getId());
        EligibilityInput malayInput = new EligibilityInput(ms.getId());
        for (String subject : subjects) {
            englishInput.getGrades().add(new SubjectGrade(subject, grade));
            malayInput.getGrades().add(new SubjectGrade(names.get(subject), grade));
        }
        addSupportingGrades(en, ms, grade, englishInput, malayInput);

        List<EligibilityOutcome> englishOutcomes = englishChecker.check(englishInput);
        List<EligibilityOutcome> malayOutcomes = malayChecker.check(malayInput);
        for (int i = 0; i < englishOutcomes.size(); i++) {
            EligibilityOutcome expected = englishOutcomes.get(i);
            EligibilityOutcome actual = malayOutcomes.get(i);
            String label = en.getId() + " " + grade + " " + expected.getCourse().getId();
            assertEquals(label, expected.getCourse().getId(), actual.getCourse().getId());
            assertEquals(label, expected.getStatus(), actual.getStatus());
            assertEquals(label, expected.getReasons().size(), actual.getReasons().size());
        }
    }

    /** Uses the same position on the SPM scale, so supporting results vary with the main grade. */
    private void addSupportingGrades(Qualification en, Qualification ms, String grade,
                                     EligibilityInput englishInput, EligibilityInput malayInput) {
        if (en.getSupportingQualificationId() == null) {
            return;
        }
        List<String> scale = findQualification(english, en.getSupportingQualificationId()).getGrades();
        String supportingGrade =
                scale.get(Math.min(en.getGrades().indexOf(grade), scale.size() - 1));
        for (int i = 0; i < en.getSupportingSubjects().size(); i++) {
            englishInput.getSupportingGrades().add(
                    new SubjectGrade(en.getSupportingSubjects().get(i), supportingGrade));
            malayInput.getSupportingGrades().add(
                    new SubjectGrade(ms.getSupportingSubjects().get(i), supportingGrade));
        }
    }

    private List<String> allSubjects(Qualification qualification) {
        List<String> subjects = new ArrayList<>(qualification.getRequiredSubjects());
        for (String subject : qualification.getSubjects()) {
            if (!subjects.contains(subject)) {
                subjects.add(subject);
            }
        }
        return subjects;
    }

    private List<String> coreSubjects(Qualification qualification) {
        List<String> subjects = new ArrayList<>(qualification.getRequiredSubjects());
        subjects.addAll(qualification.getDefaultSubjects());
        return subjects;
    }

    private Map<String, String> subjectNames(Qualification en, Qualification ms) {
        Map<String, String> names = new HashMap<>();
        addNames(names, en.getSubjects(), ms.getSubjects());
        addNames(names, en.getRequiredSubjects(), ms.getRequiredSubjects());
        addNames(names, en.getDefaultSubjects(), ms.getDefaultSubjects());
        return names;
    }

    private void addNames(Map<String, String> names, List<String> en, List<String> ms) {
        assertEquals(en.size(), ms.size());
        for (int i = 0; i < en.size(); i++) {
            names.put(en.get(i), ms.get(i));
        }
    }

    private Qualification findQualification(CourseCatalog catalog, String id) {
        for (Qualification qualification : catalog.getQualifications()) {
            if (qualification.getId().equals(id)) {
                return qualification;
            }
        }
        throw new AssertionError("No qualification with id " + id);
    }
}
