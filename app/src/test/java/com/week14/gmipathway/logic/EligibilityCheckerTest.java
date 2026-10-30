package com.week14.gmipathway.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.CourseCatalog;
import com.week14.gmipathway.model.EligibilityInput;
import com.week14.gmipathway.model.EligibilityOutcome;
import com.week14.gmipathway.model.EligibilityReason;
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
 * Tests the checker against the real rules in courses.json.
 * Programme ids: mechatronics uses the BEM rules, precision_tooling the MQA Engineering rules,
 * industrial_design the MBOT rules, software_engineering the Computing rules.
 */
public class EligibilityCheckerTest {
    private static EligibilityChecker checker;

    @BeforeClass
    public static void loadRules() throws IOException {
        try (Reader reader = Files.newBufferedReader(
                Paths.get("src/main/assets/courses.json"), StandardCharsets.UTF_8)) {
            CourseCatalog catalog = CourseRepository.parseCatalog(reader);
            checker = new EligibilityChecker(catalog);
        }
    }

    // ---------- Fully eligible ----------

    @Test
    public void strongSpmResultsQualifyForEveryDiplomaWithPublishedRules() {
        List<EligibilityOutcome> outcomes = checker.check(strongSpm());
        for (EligibilityOutcome outcome : outcomes) {
            String id = outcome.getCourse().getId();
            if (id.equals("cyber_security") || id.equals("belt")) {
                continue;
            }
            assertEquals(id, EligibilityStatus.ELIGIBLE, outcome.getStatus());
        }
    }

    @Test
    public void everyProgrammeGetsAnOutcome() {
        assertEquals(18, checker.check(strongSpm()).size());
    }

    // ---------- Borderline: exactly the minimum grades ----------

    @Test
    public void exactMinimumGradesMeetTheBemRules() {
        EligibilityInput input = spm("Bahasa Melayu", "E", "Sejarah", "E", "English", "G",
                "Mathematics", "C", "Physics", "C", "Accounting", "C");
        assertStatus(input, "mechatronics", EligibilityStatus.ELIGIBLE);
    }

    @Test
    public void oneGradeBelowCreditFailsAndNamesTheSubject() {
        EligibilityInput input = spm("Bahasa Melayu", "E", "Sejarah", "E", "English", "E",
                "Mathematics", "D", "Physics", "C", "Accounting", "C", "Business", "C");
        EligibilityOutcome outcome = outcomeFor(input, "mechatronics");
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, outcome.getStatus());
        EligibilityReason reason = findReason(outcome, EligibilityReason.Type.SUBJECT);
        assertTrue(reason.getSubjects().contains("Mathematics"));
        assertEquals("C", reason.getGrade());
    }

    @Test
    public void cPlusBeatsTheCreditLineButDDoesNotCount() {
        EligibilityInput input = spm("Bahasa Melayu", "D", "Sejarah", "E", "English", "E",
                "Mathematics", "C+", "Science", "C+");
        EligibilityOutcome outcome = outcomeFor(input, "industrial_design");
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, outcome.getStatus());
        EligibilityReason reason = findReason(outcome, EligibilityReason.Type.SUBJECT_COUNT);
        assertEquals(3, reason.getCount());
    }

    // ---------- Missing compulsory subject ----------

    @Test
    public void failingSejarahMeansNoSpmCertificate() {
        EligibilityInput input = spm("Bahasa Melayu", "A", "Sejarah", "G", "English", "A",
                "Mathematics", "A", "Physics", "A", "Chemistry", "A");
        EligibilityOutcome outcome = outcomeFor(input, "mechatronics");
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, outcome.getStatus());
        assertTrue(findReason(outcome, EligibilityReason.Type.CERTIFICATE)
                .getSubjects().contains("Sejarah"));
    }

    @Test
    public void noScienceOrTechnicalSubjectFailsTheBemRules() {
        EligibilityInput input = spm("Bahasa Melayu", "A", "Sejarah", "A", "English", "A",
                "Mathematics", "A", "Accounting", "A");
        EligibilityOutcome outcome = outcomeFor(input, "mechatronics");
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, outcome.getStatus());
        assertEquals("SCIENCE_TECHNICAL",
                findReason(outcome, EligibilityReason.Type.SUBJECT_GROUP).getSubjectGroup());
    }

    @Test
    public void vocationalCountsForTheMqaEngineeringRulesButNotTheBemRules() {
        EligibilityInput input = spm("Bahasa Melayu", "C", "Sejarah", "E", "English", "E",
                "Mathematics", "C", "Vocational (MPV) subject", "C");
        assertStatus(input, "precision_tooling", EligibilityStatus.ELIGIBLE);
        assertStatus(input, "mechatronics", EligibilityStatus.NOT_ELIGIBLE);
    }

    // ---------- Not eligible for anything ----------

    @Test
    public void weakResultsQualifyForNothing() {
        EligibilityInput input = spm("Bahasa Melayu", "D", "Sejarah", "E", "English", "E",
                "Mathematics", "E");
        for (EligibilityOutcome outcome : checker.check(input)) {
            assertFalse(outcome.getCourse().getId(),
                    outcome.getStatus() == EligibilityStatus.ELIGIBLE);
        }
    }

    // ---------- Rule differences GMI publishes ----------

    @Test
    public void bemSpmRouteHasNoEnglishPassButMqaEngineeringDoes() {
        EligibilityInput input = spm("Bahasa Melayu", "C", "Sejarah", "E", "English", "G",
                "Mathematics", "C", "Physics", "C");
        assertStatus(input, "mechatronics", EligibilityStatus.ELIGIBLE);
        assertStatus(input, "precision_tooling", EligibilityStatus.NOT_ELIGIBLE);
    }

    @Test
    public void creativeMultimediaAcceptsAnyThreeSpmCreditsButIsStricterForOLevel() {
        EligibilityInput spmInput = spm("Bahasa Melayu", "C", "Sejarah", "C", "English", "C",
                "Mathematics", "G");
        assertStatus(spmInput, "creative_multimedia", EligibilityStatus.ELIGIBLE);

        EligibilityInput oLevel = input("OLEVEL", "English", "C", "Economics", "C", "History", "C");
        assertStatus(oLevel, "creative_multimedia", EligibilityStatus.NOT_ELIGIBLE);
    }

    // ---------- Second qualification: O-Level / IGCSE ----------

    @Test
    public void oLevelNeedsAPassInEnglishForBemProgrammes() {
        EligibilityInput passed = input("OLEVEL", "English", "E", "Mathematics", "C",
                "Physics", "C", "Geography", "C");
        assertStatus(passed, "mechatronics", EligibilityStatus.ELIGIBLE);

        EligibilityInput failed = input("OLEVEL", "English", "F", "Mathematics", "C",
                "Physics", "C", "Geography", "C");
        assertStatus(failed, "mechatronics", EligibilityStatus.NOT_ELIGIBLE);
    }

    @Test
    public void oLevelCreditIsGradeC() {
        EligibilityInput input = input("OLEVEL", "English", "D", "Mathematics", "D", "Physics", "D");
        assertStatus(input, "industrial_design", EligibilityStatus.NOT_ELIGIBLE);
    }

    // ---------- UEC ----------

    @Test
    public void uecGradeBMeansB6OrBetter() {
        EligibilityInput borderline = input("UEC", "English", "B6", "Mathematics", "B6",
                "Physics", "B6");
        assertStatus(borderline, "industrial_design", EligibilityStatus.ELIGIBLE);

        EligibilityInput below = input("UEC", "English", "C7", "Mathematics", "B6", "Physics", "B6");
        assertStatus(below, "industrial_design", EligibilityStatus.NOT_ELIGIBLE);
    }

    @Test
    public void uecIsNotAssessedWhereGmiListsNoRoute() {
        EligibilityInput input = input("UEC", "English", "A1", "Mathematics", "A1", "Physics", "A1");
        assertStatus(input, "mechatronics", EligibilityStatus.NOT_ASSESSED);
    }

    // ---------- Pre-university programmes ----------

    @Test
    public void gufpNeedsCreditsInAllSixSubjects() {
        EligibilityInput input = spm("Bahasa Melayu", "C", "Sejarah", "E", "English", "C",
                "Mathematics", "C", "Additional Mathematics", "C", "Physics", "C", "Chemistry", "D");
        EligibilityOutcome outcome = outcomeFor(input, "gufp");
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, outcome.getStatus());
        assertTrue(findReason(outcome, EligibilityReason.Type.SUBJECT)
                .getSubjects().contains("Chemistry"));
    }

    @Test
    public void gappSelfSponsoredNeedsFiveCreditsWithoutTheMaraNote() {
        EligibilityInput input = spm("Bahasa Melayu", "C", "Sejarah", "E", "English", "C",
                "Mathematics", "C", "Additional Mathematics", "C", "Physics", "C", "Chemistry", "C");
        EligibilityOutcome outcome = outcomeFor(input, "gapp");
        assertEquals(EligibilityStatus.ELIGIBLE, outcome.getStatus());
        assertTrue(outcome.getNotes().isEmpty());
    }

    @Test
    public void eightAsAddTheMaraYtpNote() {
        EligibilityOutcome outcome = outcomeFor(strongSpm(), "gapp");
        assertEquals(EligibilityStatus.ELIGIBLE, outcome.getStatus());
        assertEquals(1, outcome.getNotes().size());
        assertTrue(outcome.getNotes().get(0).contains("MARA YTP"));
    }

    // ---------- Software Engineering's Mathematics allowance ----------

    @Test
    public void threeCreditsWithoutMathematicsAsksTheProspectToContactGmi() {
        EligibilityInput input = spm("Bahasa Melayu", "C", "Sejarah", "C", "English", "C",
                "Mathematics", "D");
        EligibilityOutcome outcome = outcomeFor(input, "software_engineering");
        assertEquals(EligibilityStatus.CONTACT_GMI, outcome.getStatus());
        assertTrue(outcome.getNotes().get(0).contains("reinforcement"));
    }

    @Test
    public void tooFewCreditsIsNotEligibleEvenWithTheMathematicsAllowance() {
        EligibilityInput input = spm("Bahasa Melayu", "C", "Sejarah", "E", "English", "C",
                "Mathematics", "D");
        assertStatus(input, "software_engineering", EligibilityStatus.NOT_ELIGIBLE);
    }

    // ---------- Programme without published rules ----------

    @Test
    public void cyberSecurityAlwaysAsksTheProspectToContactGmi() {
        EligibilityOutcome outcome = outcomeFor(strongSpm(), "cyber_security");
        assertEquals(EligibilityStatus.CONTACT_GMI, outcome.getStatus());
        assertEquals(EligibilityReason.Type.UNPUBLISHED, outcome.getReasons().get(0).getType());
    }

    // ---------- STPM with supporting SPM results ----------

    @Test
    public void stpmUsesSpmResultsForTheMqaEngineeringRules() {
        EligibilityInput input = stpm(new String[]{"Economics", "C"},
                "Bahasa Melayu", "E", "Sejarah", "E", "Mathematics", "E", "English", "E");
        assertStatus(input, "precision_tooling", EligibilityStatus.ELIGIBLE);
        assertStatus(input, "industrial_design", EligibilityStatus.ELIGIBLE);
    }

    @Test
    public void stpmGradeCMinusIsNotAPrincipal() {
        EligibilityInput input = stpm(new String[]{"Economics", "C-"},
                "Bahasa Melayu", "E", "Sejarah", "E", "Mathematics", "E", "English", "E");
        assertStatus(input, "industrial_design", EligibilityStatus.NOT_ELIGIBLE);
    }

    @Test
    public void stpmSoftwareEngineeringNeedsAnSpmMathematicsCredit() {
        EligibilityInput credit = stpm(new String[]{"Economics", "C"},
                "Bahasa Melayu", "E", "Sejarah", "E", "Mathematics", "C", "English", "E");
        assertStatus(credit, "software_engineering", EligibilityStatus.ELIGIBLE);

        EligibilityInput noCredit = stpm(new String[]{"Economics", "C"},
                "Bahasa Melayu", "E", "Sejarah", "E", "Mathematics", "D", "English", "E");
        assertStatus(noCredit, "software_engineering", EligibilityStatus.CONTACT_GMI);
    }

    // ---------- Degree: BELT ----------

    @Test
    public void spmAloneIsNotAssessedForTheDegree() {
        assertStatus(strongSpm(), "belt", EligibilityStatus.NOT_ASSESSED);
    }

    @Test
    public void engineeringTechnologyDiplomaNeedsCgpa230() {
        assertStatus(diploma("Engineering Technology diploma", "2.30+", "C", "E"),
                "belt", EligibilityStatus.ELIGIBLE);
        assertStatus(diploma("Engineering Technology diploma", "2.00+", "C", "E"),
                "belt", EligibilityStatus.NOT_ELIGIBLE);
    }

    @Test
    public void engineeringDiplomaNeedsCgpa200() {
        assertStatus(diploma("Engineering diploma", "2.00+", "C", "E"),
                "belt", EligibilityStatus.ELIGIBLE);
        assertStatus(diploma("Engineering diploma", "<2.00", "C", "E"),
                "belt", EligibilityStatus.NOT_ELIGIBLE);
    }

    @Test
    public void degreeNeedsAnSpmCreditInBahasaMelayuAndAPassInSejarah() {
        EligibilityOutcome outcome =
                outcomeFor(diploma("Engineering Technology diploma", "3.00+", "D", "E"), "belt");
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, outcome.getStatus());
        assertTrue(findReason(outcome, EligibilityReason.Type.SUBJECT)
                .getSubjects().contains("Bahasa Melayu"));
        assertTrue(findReason(outcome, EligibilityReason.Type.SUBJECT).isSupporting());

        assertStatus(diploma("Engineering Technology diploma", "3.00+", "C", "G"),
                "belt", EligibilityStatus.NOT_ELIGIBLE);
    }

    @Test
    public void eligibleDegreeOutcomeRemindsTheProspectAboutMuet() {
        EligibilityOutcome outcome =
                outcomeFor(diploma("Engineering diploma", "3.00+", "C", "E"), "belt");
        assertTrue(outcome.getNotes().get(0).contains("MUET"));
    }

    @Test
    public void stpmDegreeRouteChecksAllThreeSubjects() {
        EligibilityInput eligible = stpm(
                new String[]{"Pengajian Am", "C", "Mathematics (T)", "C", "Physics", "C"},
                "Bahasa Melayu", "C", "Sejarah", "E", "Mathematics", "C", "English", "E");
        assertStatus(eligible, "belt", EligibilityStatus.ELIGIBLE);

        EligibilityInput noScience = stpm(
                new String[]{"Pengajian Am", "C", "Mathematics (T)", "C", "Economics", "A"},
                "Bahasa Melayu", "C", "Sejarah", "E", "Mathematics", "C", "English", "E");
        assertStatus(noScience, "belt", EligibilityStatus.NOT_ELIGIBLE);
    }

    @Test
    public void foundationWithBiologyAlsoNeedsAnSpmPhysicsCredit() {
        EligibilityInput withoutPhysics = foundation("Biology",
                "Bahasa Melayu", "C", "Sejarah", "E");
        assertStatus(withoutPhysics, "belt", EligibilityStatus.NOT_ELIGIBLE);

        EligibilityInput withPhysics = foundation("Biology",
                "Bahasa Melayu", "C", "Sejarah", "E", "Physics", "C");
        assertStatus(withPhysics, "belt", EligibilityStatus.ELIGIBLE);

        EligibilityInput withChemistry = foundation("Chemistry",
                "Bahasa Melayu", "C", "Sejarah", "E");
        assertStatus(withChemistry, "belt", EligibilityStatus.ELIGIBLE);
    }

    // ---------- Helpers ----------

    private static EligibilityInput strongSpm() {
        return spm("Bahasa Melayu", "A", "Sejarah", "A", "English", "A", "Mathematics", "A",
                "Additional Mathematics", "A", "Physics", "A", "Chemistry", "A", "Biology", "A");
    }

    private static EligibilityInput spm(String... pairs) {
        return input("SPM", pairs);
    }

    private static EligibilityInput input(String qualificationId, String... pairs) {
        EligibilityInput input = new EligibilityInput(qualificationId);
        addPairs(input.getGrades(), pairs);
        return input;
    }

    private static EligibilityInput stpm(String[] stpmPairs, String... spmPairs) {
        EligibilityInput input = input("STPM", stpmPairs);
        addPairs(input.getSupportingGrades(), spmPairs);
        return input;
    }

    private static EligibilityInput diploma(String type, String cgpa, String bahasaMelayu,
                                            String sejarah) {
        EligibilityInput input = input("DIPLOMA", type, cgpa);
        addPairs(input.getSupportingGrades(), "Bahasa Melayu", bahasaMelayu, "Sejarah", sejarah);
        return input;
    }

    private static EligibilityInput foundation(String science, String... spmPairs) {
        EligibilityInput input = input("FOUNDATION", "Mathematics", "C", science, "C");
        addPairs(input.getSupportingGrades(), spmPairs);
        return input;
    }

    private static void addPairs(List<SubjectGrade> target, String... pairs) {
        for (int i = 0; i < pairs.length; i += 2) {
            target.add(new SubjectGrade(pairs[i], pairs[i + 1]));
        }
    }

    private static void assertStatus(EligibilityInput input, String courseId,
                                     EligibilityStatus expected) {
        assertEquals(courseId, expected, outcomeFor(input, courseId).getStatus());
    }

    private static EligibilityOutcome outcomeFor(EligibilityInput input, String courseId) {
        for (EligibilityOutcome outcome : checker.check(input)) {
            if (outcome.getCourse().getId().equals(courseId)) {
                return outcome;
            }
        }
        throw new AssertionError("No outcome for " + courseId);
    }

    private static EligibilityReason findReason(EligibilityOutcome outcome,
                                                EligibilityReason.Type type) {
        for (EligibilityReason reason : outcome.getReasons()) {
            if (reason.getType() == type) {
                return reason;
            }
        }
        throw new AssertionError("No " + type + " reason");
    }
}
