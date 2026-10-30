package com.week14.gmipathway.ui.courses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.BookmarkStore;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.ui.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists the courses the prospect has bookmarked.
 */
public class SavedCoursesActivity extends BaseActivity
        implements CourseAdapter.OnCourseClickListener {

    private BookmarkStore bookmarks;
    private RecyclerView savedList;
    private View emptyState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_courses);
        setUpToolbar(R.string.saved_title);
        applyWindowInsets();

        bookmarks = new BookmarkStore(this);
        savedList = findViewById(R.id.saved_list);
        emptyState = findViewById(R.id.saved_empty);
        findViewById(R.id.button_browse).setOnClickListener(view ->
                startActivity(new Intent(this, CourseListActivity.class)));
    }

    /** Runs on every return to this screen, because a course may have been saved elsewhere. */
    @Override
    protected void onResume() {
        super.onResume();
        showSavedCourses();
    }

    private void showSavedCourses() {
        List<Course> saved = new ArrayList<>();
        for (Course course : CourseRepository.getInstance(this).getCourses()) {
            if (bookmarks.isSaved(course.getId())) {
                saved.add(course);
            }
        }
        savedList.setVisibility(saved.isEmpty() ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(saved.isEmpty() ? View.VISIBLE : View.GONE);
        savedList.setAdapter(new CourseAdapter(saved, bookmarks, this));
    }

    @Override
    public void onCourseClick(Course course) {
        startActivity(CourseDetailActivity.createIntent(this, course.getId()));
    }

    @Override
    public void onBookmarkChanged() {
        showSavedCourses();
    }
}
