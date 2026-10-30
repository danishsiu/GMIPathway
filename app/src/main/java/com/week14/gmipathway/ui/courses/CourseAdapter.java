package com.week14.gmipathway.ui.courses;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.BookmarkStore;
import com.week14.gmipathway.model.Course;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows courses as cards under a heading for each department.
 */
class CourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COURSE = 1;

    interface OnCourseClickListener {
        void onCourseClick(Course course);

        /** Only the saved list needs this, because it must drop a course that is removed. */
        default void onBookmarkChanged() {
        }
    }

    /** A department heading and how many programmes it has. */
    private static final class DepartmentHeader {
        final String id;
        final String name;
        final int courseCount;

        DepartmentHeader(Course firstCourse, int courseCount) {
            this.id = firstCourse.getDepartmentId();
            this.name = firstCourse.getDepartment();
            this.courseCount = courseCount;
        }
    }

    /** Holds a DepartmentHeader or a Course, in display order. */
    private final List<Object> rows = new ArrayList<>();
    private final BookmarkStore bookmarks;
    private final OnCourseClickListener listener;

    CourseAdapter(List<Course> courses, BookmarkStore bookmarks, OnCourseClickListener listener) {
        this.bookmarks = bookmarks;
        this.listener = listener;
        Map<String, List<Course>> departments = new LinkedHashMap<>();
        for (Course course : courses) {
            if (!departments.containsKey(course.getDepartmentId())) {
                departments.put(course.getDepartmentId(), new ArrayList<>());
            }
            departments.get(course.getDepartmentId()).add(course);
        }
        for (List<Course> department : departments.values()) {
            rows.add(new DepartmentHeader(department.get(0), department.size()));
            rows.addAll(department);
        }
    }

    @DrawableRes
    private static int getDepartmentIcon(String departmentId) {
        switch (departmentId) {
            case Course.DEPARTMENT_ELECTRICAL:
                return R.drawable.ic_bolt;
            case Course.DEPARTMENT_MECHANICAL:
                return R.drawable.ic_precision_manufacturing;
            case Course.DEPARTMENT_COMPUTER:
                return R.drawable.ic_computer;
            default:
                return R.drawable.ic_school;
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
            ((HeaderViewHolder) holder).bind((DepartmentHeader) row);
            return;
        }
        Course course = (Course) row;
        CourseViewHolder courseHolder = (CourseViewHolder) holder;
        courseHolder.name.setText(course.getName());
        courseHolder.summary.setText(course.getSummary());
        courseHolder.duration.setText(course.getDuration());
        courseHolder.intake.setText(course.getIntake());
        courseHolder.itemView.setOnClickListener(view -> listener.onCourseClick(course));
        courseHolder.showBookmark(course, bookmarks.isSaved(course.getId()));
        courseHolder.bookmark.setOnClickListener(view -> {
            bookmarks.toggle(course.getId());
            courseHolder.showBookmark(course, bookmarks.isSaved(course.getId()));
            listener.onBookmarkChanged();
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView count;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.header_icon);
            title = itemView.findViewById(R.id.header_title);
            count = itemView.findViewById(R.id.header_count);
        }

        void bind(DepartmentHeader header) {
            Resources resources = itemView.getResources();
            icon.setImageResource(getDepartmentIcon(header.id));
            title.setText(header.name);
            count.setText(resources.getQuantityString(
                    R.plurals.course_count, header.courseCount, header.courseCount));
        }
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView summary;
        final TextView duration;
        final TextView intake;
        final ImageButton bookmark;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.course_name);
            summary = itemView.findViewById(R.id.course_summary);
            duration = itemView.findViewById(R.id.course_duration);
            intake = itemView.findViewById(R.id.course_intake);
            bookmark = itemView.findViewById(R.id.course_bookmark);
        }

        void showBookmark(Course course, boolean saved) {
            bookmark.setImageResource(saved ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);
            bookmark.setContentDescription(itemView.getContext().getString(
                    saved ? R.string.course_bookmark_remove : R.string.course_bookmark_add,
                    course.getName()));
        }
    }
}
