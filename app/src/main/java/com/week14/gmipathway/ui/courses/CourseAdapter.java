package com.week14.gmipathway.ui.courses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.model.Course;

import java.util.List;

/**
 * Shows each course as a card in the course list.
 */
class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    private final List<Course> courses;
    private final OnCourseClickListener listener;

    CourseAdapter(List<Course> courses, OnCourseClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.department.setText(course.getDepartment());
        holder.name.setText(course.getName());
        holder.summary.setText(course.getSummary());
        holder.duration.setText(course.getDuration());
        holder.intake.setText(course.getIntake());
        holder.itemView.setOnClickListener(view -> listener.onCourseClick(course));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        final TextView department;
        final TextView name;
        final TextView summary;
        final TextView duration;
        final TextView intake;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            department = itemView.findViewById(R.id.course_department);
            name = itemView.findViewById(R.id.course_name);
            summary = itemView.findViewById(R.id.course_summary);
            duration = itemView.findViewById(R.id.course_duration);
            intake = itemView.findViewById(R.id.course_intake);
        }
    }
}
