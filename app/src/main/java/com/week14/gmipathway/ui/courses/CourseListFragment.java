package com.week14.gmipathway.ui.courses;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.BookmarkStore;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.ProgrammeCategory;

/**
 * Lists the courses in one programme category.
 */
public class CourseListFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";
    private static final String STATE_EXPANDED = "expandedDepartments";

    private CourseAdapter adapter;

    public CourseListFragment() {
        super(R.layout.fragment_course_list);
    }

    static CourseListFragment newInstance(ProgrammeCategory category) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_CATEGORY, category.name());
        CourseListFragment fragment = new CourseListFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgrammeCategory category =
                ProgrammeCategory.valueOf(requireArguments().getString(ARG_CATEGORY));
        adapter = new CourseAdapter(
                CourseRepository.getInstance(requireContext()).getCourses(category),
                new BookmarkStore(requireContext()),
                course -> startActivity(
                        CourseDetailActivity.createIntent(requireContext(), course.getId())),
                true);
        if (savedInstanceState != null) {
            adapter.setExpandedDepartments(savedInstanceState.getStringArrayList(STATE_EXPANDED));
        }
        ((RecyclerView) view.findViewById(R.id.course_list)).setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            outState.putStringArrayList(STATE_EXPANDED, adapter.getExpandedDepartments());
        }
    }

    /** A course may have been saved or removed elsewhere, so every row is refreshed. */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
