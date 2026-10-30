package com.week14.gmipathway.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.CourseCatalog;
import com.week14.gmipathway.model.EntryGroup;
import com.week14.gmipathway.model.EntryRequirement;
import com.week14.gmipathway.model.EntryRule;
import com.week14.gmipathway.model.ProgrammeCategory;
import com.week14.gmipathway.model.Qualification;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks the real course data, so a mistake in courses.json is caught before the app runs.
 */
public class CourseCatalogTest {
    private static CourseCatalog catalog;

    @BeforeClass
    public static void loadCatalog() throws IOException {
        try (Reader reader = Files.newBufferedReader(
                Paths.get("src/main/assets/courses.json"), StandardCharsets.UTF_8)) {
            catalog = CourseRepository.parseCatalog(reader);
        }
    }

    @Test
    public void listsEveryProgrammeInEachCategory() {
        assertEquals(18, catalog.getCourses().size());
        assertEquals(15, countCourses(ProgrammeCategory.DIPLOMA));
        assertEquals(2, countCourses(ProgrammeCategory.PRE_UNIVERSITY));
        assertEquals(1, countCourses(ProgrammeCategory.DEGREE));
    }

    @Test
    public void diplomasAreGroupedByDepartment() {
        assertEquals(5, countDiplomas("Electrical Engineering"));
        assertEquals(7, countDiplomas("Mechanical Engineering"));
        assertEquals(3, countDiplomas("Computer & Information"));

        // The list shows one heading per department, so each department must be in one block.
        Set<String> finished = new HashSet<>();
        String current = null;
        for (Course course : catalog.getCourses()) {
            if (course.getCategory() != ProgrammeCategory.DIPLOMA) {
                continue;
            }
            if (!course.getDepartment().equals(current)) {
                assertTrue(course.getDepartment(), finished.add(course.getDepartment()));
                current = course.getDepartment();
            }
        }
    }

    @Test
    public void gappAndGufpArePreUniversityAndBeltIsTheDegree() {
        assertEquals(ProgrammeCategory.PRE_UNIVERSITY, findCourse("gapp").getCategory());
        assertEquals(ProgrammeCategory.PRE_UNIVERSITY, findCourse("gufp").getCategory());
        assertEquals(ProgrammeCategory.DEGREE, findCourse("belt").getCategory());
    }

    @Test
    public void beltHasItsRegisteredCreditsAndAllSemesters() {
        Course belt = findCourse("belt");
        assertEquals(140, belt.getCredits());
        assertEquals(10, belt.getSubjectGroups().size());
    }

    @Test
    public void courseIdsAreUnique() {
        Set<String> ids = new HashSet<>();
        for (Course course : catalog.getCourses()) {
            assertTrue("Duplicate id: " + course.getId(), ids.add(course.getId()));
        }
    }

    @Test
    public void everyCourseHasItsCoreInformation() {
        for (Course course : catalog.getCourses()) {
            String id = course.getId();
            assertFalse(id, course.getName().isEmpty());
            assertFalse(id, course.getDepartment().isEmpty());
            assertFalse(id, course.getCode().isEmpty());
            assertFalse(id, course.getSummary().isEmpty());
            assertFalse(id, course.getDescription().isEmpty());
            assertFalse(id, course.getDuration().isEmpty());
            assertFalse(id, course.getIntake().isEmpty());
            assertFalse(id, course.getFee().isEmpty());
            assertFalse(id, course.getCoreAreas().isEmpty());
            assertTrue(id, course.getCredits() > 0);
        }
    }

    @Test
    public void everyEntryGroupReferenceExists() {
        for (Course course : catalog.getCourses()) {
            if (course.getEntryGroupId() != null) {
                assertNotNull(course.getId(), findEntryGroup(course.getEntryGroupId()));
            }
        }
    }

    @Test
    public void cyberSecurityHasNoEntryRulesBecauseGmiPublishesNone() {
        assertNull(findCourse("cyber_security").getEntryGroupId());
    }

    @Test
    public void precisionToolingHidesItsUnconfirmedSubjectList() {
        assertTrue(findCourse("precision_tooling").getSubjectGroups().isEmpty());
    }

    @Test
    public void everyQualificationHasGradesAndSubjects() {
        assertEquals(6, catalog.getQualifications().size());
        for (Qualification qualification : catalog.getQualifications()) {
            assertFalse(qualification.getId(), qualification.getGrades().isEmpty());
            assertFalse(qualification.getId(), qualification.getSubjects().isEmpty());
        }
    }

    @Test
    public void everyRuleUsesRealGradesAndSubjects() {
        assertRulesAreValid(catalog);
    }

    /** A typing mistake in a rule would silently fail every prospect, so each name is checked. */
    static void assertRulesAreValid(CourseCatalog catalog) {
        for (EntryGroup group : catalog.getEntryGroups()) {
            for (EntryRule rule : group.getRules()) {
                Qualification qualification = findQualification(catalog, rule.getQualificationId());
                if (rule.getMinSubjects() > 0) {
                    assertGradeExists(qualification, rule.getMinSubjectGrade());
                }
                for (EntryRequirement requirement : rule.getRequirements()) {
                    Qualification scale = requirement.isSupporting()
                            ? findQualification(catalog,
                                    qualification.getSupportingQualificationId())
                            : qualification;
                    assertGradeExists(scale, requirement.getMinGrade());
                    assertSubjectsExist(scale, requirement);
                    if (requirement.isSupporting()) {
                        assertSupportingSubjectsCollected(qualification, requirement);
                    }
                }
            }
        }
    }

    private static void assertGradeExists(Qualification qualification, String level) {
        assertTrue(qualification.getId() + " has no grade " + level,
                qualification.getGrades().contains(qualification.resolveGrade(level)));
    }

    private static void assertSubjectsExist(Qualification scale, EntryRequirement requirement) {
        if (requirement.getSubjectGroup() != null) {
            List<String> group = scale.getSubjectGroup(requirement.getSubjectGroup());
            assertFalse(requirement.getSubjectGroup(), group.isEmpty());
            for (String subject : group) {
                assertTrue(subject, offeredBy(scale).contains(subject));
            }
            return;
        }
        for (String subject : requirement.getSubjects()) {
            assertTrue(scale.getId() + " does not offer " + subject,
                    offeredBy(scale).contains(subject));
        }
    }

    /** A supporting requirement can only pass if the form collects at least one of its subjects. */
    private static void assertSupportingSubjectsCollected(Qualification qualification,
                                                          EntryRequirement requirement) {
        Set<String> collected = new HashSet<>(qualification.getSupportingSubjects());
        collected.addAll(qualification.getSupportingOptionalSubjects());
        collected.retainAll(requirement.getSubjects());
        assertFalse(qualification.getId() + " never collects " + requirement.getSubjects(),
                collected.isEmpty());
    }

    private static Set<String> offeredBy(Qualification qualification) {
        Set<String> subjects = new HashSet<>(qualification.getSubjects());
        subjects.addAll(qualification.getRequiredSubjects());
        subjects.addAll(qualification.getDefaultSubjects());
        return subjects;
    }

    private static Qualification findQualification(CourseCatalog catalog, String id) {
        for (Qualification qualification : catalog.getQualifications()) {
            if (qualification.getId().equals(id)) {
                return qualification;
            }
        }
        throw new AssertionError("No qualification with id " + id);
    }

    private int countDiplomas(String department) {
        int count = 0;
        for (Course course : catalog.getCourses()) {
            if (course.getCategory() == ProgrammeCategory.DIPLOMA
                    && course.getDepartment().equals(department)) {
                count++;
            }
        }
        return count;
    }

    private int countCourses(ProgrammeCategory category) {
        int count = 0;
        for (Course course : catalog.getCourses()) {
            if (course.getCategory() == category) {
                count++;
            }
        }
        return count;
    }

    private Course findCourse(String id) {
        for (Course course : catalog.getCourses()) {
            if (course.getId().equals(id)) {
                return course;
            }
        }
        throw new AssertionError("No course with id " + id);
    }

    private EntryGroup findEntryGroup(String id) {
        for (EntryGroup group : catalog.getEntryGroups()) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }
}
