package com.week14.gmipathway.ui.courses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.model.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows courses as cards under a heading for each department.
 */
class CourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COURSE = 1;

    interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    /** Holds a department name (String) or a Course, in display order. */
    private final List<Object> rows = new ArrayList<>();
    private final OnCourseClickListener listener;

    /** Expects courses from the same department to be next to each other. */
    CourseAdapter(List<Course> courses, OnCourseClickListener listener) {
        this.listener = listener;
        String currentDepartment = null;
        for (Course course : courses) {
            if (!course.getDepartment().equals(currentDepartment)) {
                currentDepartment = course.getDepartment();
                rows.add(currentDepartment);
            }
            rows.add(course);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position) instanceof Course ? TYPE_COURSE : TYPE_HEADER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(
                    inflater.inflate(R.layout.item_section_header, parent, false));
        }
        return new CourseViewHolder(inflater.inflate(R.layout.item_course, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object row = rows.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).title.setText((String) row);
            return;
        }
        Course course = (Course) row;
        CourseViewHolder courseHolder = (CourseViewHolder) holder;
        courseHolder.name.setText(course.getName());
        courseHolder.summary.setText(course.getSummary());
        courseHolder.duration.setText(course.getDuration());
        courseHolder.intake.setText(course.getIntake());
        courseHolder.itemView.setOnClickListener(view -> listener.onCourseClick(course));
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.header_title);
        }
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView summary;
        final TextView duration;
        final TextView intake;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.course_name);
            summary = itemView.findViewById(R.id.course_summary);
            duration = itemView.findViewById(R.id.course_duration);
            intake = itemView.findViewById(R.id.course_intake);
        }
    }
}
