package com.week14.gmipathway.logic;

import static org.junit.Assert.assertEquals;

import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.CourseCatalog;
import com.week14.gmipathway.model.EligibilityInput;
import com.week14.gmipathway.model.EligibilityOutcome;
import com.week14.gmipathway.model.EligibilityStatus;
import com.week14.gmipathway.model.SubjectGrade;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Proves the sample grades in docs/demo_script.md give the results that script promises.
 */
public class DemoScenarioTest {
    private static EligibilityChecker checker;

    @BeforeClass
    public static void loadRules() throws IOException {
        try (Reader reader = Files.newBufferedReader(
                Paths.get("src/main/assets/courses.json"), StandardCharsets.UTF_8)) {
            CourseCatalog catalog = CourseRepository.parseCatalog(reader);
            checker = new EligibilityChecker(catalog);
        }
    }

    @Test
    public void strongSpmQualifiesForSixteenProgrammes() {
        EligibilityInput input = input("SPM", "Bahasa Melayu", "B", "English", "C",
                "Mathematics", "B", "Sejarah", "C", "Additional Mathematics", "C",
                "Physics", "B", "Chemistry", "C");
        assertCounts(input, 16, 1, 0, 1);
    }

    @Test
    public void weakSpmQualifiesForNothing() {
        EligibilityInput input = input("SPM", "Bahasa Melayu", "C", "English", "D",
                "Mathematics", "E", "Sejarah", "E", "Physics", "E", "Accounting", "D");
        assertCounts(input, 0, 1, 16, 1);
    }

    @Test
    public void oLevelWithoutAdditionalMathematicsMissesGappAndGufp() {
        EligibilityInput input = input("OLEVEL", "English", "C", "Mathematics", "B",
                "Physics", "C", "Chemistry", "C");
        assertCounts(input, 14, 1, 2, 1);
    }

    @Test
    public void weakOLevelQualifiesForNothing() {
        EligibilityInput input = input("OLEVEL", "English", "E", "Mathematics", "D", "Physics", "D");
        assertCounts(input, 0, 1, 16, 1);
    }

    @Test
    public void diplomaHolderQualifiesForTheDegree() {
        EligibilityInput input = input("DIPLOMA", "Engineering Technology diploma", "2.30+");
        input.getSupportingGrades().add(new SubjectGrade("Bahasa Melayu", "C"));
        input.getSupportingGrades().add(new SubjectGrade("Sejarah", "E"));
        assertCounts(input, 1, 1, 0, 16);
        assertEquals(EligibilityStatus.ELIGIBLE, outcomeFor(input, "belt").getStatus());
    }

    private static void assertCounts(EligibilityInput input, int eligible, int contact,
                                     int notEligible, int notAssessed) {
        List<EligibilityOutcome> outcomes = checker.check(input);
        assertEquals(eligible, count(outcomes, EligibilityStatus.ELIGIBLE));
        assertEquals(contact, count(outcomes, EligibilityStatus.CONTACT_GMI));
        assertEquals(notEligible, count(outcomes, EligibilityStatus.NOT_ELIGIBLE));
        assertEquals(notAssessed, count(outcomes, EligibilityStatus.NOT_ASSESSED));
    }

    private static int count(List<EligibilityOutcome> outcomes, EligibilityStatus status) {
        int count = 0;
        for (EligibilityOutcome outcome : outcomes) {
            if (outcome.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    private static EligibilityOutcome outcomeFor(EligibilityInput input, String courseId) {
        for (EligibilityOutcome outcome : checker.check(input)) {
            if (outcome.getCourse().getId().equals(courseId)) {
                return outcome;
            }
        }
        throw new AssertionError("No outcome for " + courseId);
    }

    private static EligibilityInput input(String qualificationId, String... pairs) {
        EligibilityInput input = new EligibilityInput(qualificationId);
        for (int i = 0; i < pairs.length; i += 2) {
            input.getGrades().add(new SubjectGrade(pairs[i], pairs[i + 1]));
        }
        return input;
    }
}
