package com.week14.gmipathway.logic;

import com.week14.gmipathway.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Finds courses by the words a prospect types.
 */
public final class CourseSearch {

    private CourseSearch() {
    }

    /** Returns the courses whose text contains every word of the query, in any order. */
    public static List<Course> search(List<Course> courses, String query) {
        String[] words = normalise(query).trim().split("\\s+");
        List<Course> matches = new ArrayList<>();
        for (Course course : courses) {
            if (containsAll(searchableText(course), words)) {
                matches.add(course);
            }
        }
        return matches;
    }

    private static String searchableText(Course course) {
        return normalise(course.getName() + " " + course.getDepartment() + " "
                + course.getSummary() + " " + course.getCode());
    }

    private static boolean containsAll(String text, String[] words) {
        for (String word : words) {
            if (!text.contains(word)) {
                return false;
            }
        }
        return true;
    }

    private static String normalise(String text) {
        return text.toLowerCase(Locale.ROOT);
    }
}
