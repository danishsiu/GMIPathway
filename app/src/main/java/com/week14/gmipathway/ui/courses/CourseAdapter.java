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
import com.week14.gmipathway.ui.common.DetailViews;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows courses as cards under a heading for each department.
 * With more than one department, a collapsible list starts closed, as a short overview.
 */
class CourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COURSE = 1;
    private static final float CHEVRON_EXPANDED = 180f;
    private static final float CHEVRON_COLLAPSED = 0f;

    interface OnCourseClickListener {
        void onCourseClick(Course course);

        /** Only the saved list needs this, because it must drop a course that is removed. */
        default void onBookmarkChanged() {
        }
    }

    /** A department, its courses, and whether they are showing. */
    private static final class Department {
        final String id;
        final String name;
        final List<Course> courses;
        boolean expanded;

        Department(List<Course> courses, boolean expanded) {
            this.id = courses.get(0).getDepartmentId();
            this.name = courses.get(0).getDepartment();
            this.courses = courses;
            this.expanded = expanded;
        }
    }

    private final List<Department> departmentList = new ArrayList<>();
    /** Holds a Department or a Course, in display order. */
    private final List<Object> rows = new ArrayList<>();
    private final BookmarkStore bookmarks;
    private final OnCourseClickListener listener;
    private final boolean collapsible;

    /** A list of everything, used for search results and saved courses. */
    CourseAdapter(List<Course> courses, BookmarkStore bookmarks, OnCourseClickListener listener) {
        this(courses, bookmarks, listener, false);
    }

    CourseAdapter(List<Course> courses, BookmarkStore bookmarks, OnCourseClickListener listener,
                  boolean allowCollapsing) {
        this.bookmarks = bookmarks;
        this.listener = listener;
        Map<String, List<Course>> departments = new LinkedHashMap<>();
        for (Course course : courses) {
            if (!departments.containsKey(course.getDepartmentId())) {
                departments.put(course.getDepartmentId(), new ArrayList<>());
            }
            departments.get(course.getDepartmentId()).add(course);
        }
        // One department is already short, so there is nothing to tidy away.
        collapsible = allowCollapsing && departments.size() > 1;
        for (List<Course> courseGroup : departments.values()) {
            departmentList.add(new Department(courseGroup, !collapsible));
        }
        rebuildRows();
    }

    /** Returns the departments that are open, so the screen can keep them after rotation. */
    ArrayList<String> getExpandedDepartments() {
        ArrayList<String> expanded = new ArrayList<>();
        for (Department department : departmentList) {
            if (department.expanded) {
                expanded.add(department.id);
            }
        }
        return expanded;
    }

    /** Call this before the list shows the adapter. */
    void setExpandedDepartments(List<String> expanded) {
        for (Department department : departmentList) {
            department.expanded = expanded.contains(department.id);
        }
        rebuildRows();
    }

    private void rebuildRows() {
        rows.clear();
        for (Department department : departmentList) {
            rows.add(department);
            if (department.expanded) {
                rows.addAll(department.courses);
            }
        }
    }

    private void toggle(Department department) {
        int headerPosition = rows.indexOf(department);
        department.expanded = !department.expanded;
        rebuildRows();
        notifyItemChanged(headerPosition);
        if (department.expanded) {
            notifyItemRangeInserted(headerPosition + 1, department.courses.size());
        } else {
            notifyItemRangeRemoved(headerPosition + 1, department.courses.size());
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
            return new HeaderViewHolder(inflater.inflate(collapsible
                    ? R.layout.item_department_header : R.layout.item_section_header,
                    parent, false));
        }
        return new CourseViewHolder(inflater.inflate(R.layout.item_course, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object row = rows.get(position);
        if (holder instanceof HeaderViewHolder) {
            bindHeader((HeaderViewHolder) holder, (Department) row);
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

    private void bindHeader(HeaderViewHolder holder, Department department) {
        holder.bind(department);
        if (collapsible) {
            holder.chevron.setRotation(
                    department.expanded ? CHEVRON_EXPANDED : CHEVRON_COLLAPSED);
            DetailViews.updateExpandedState(holder.itemView, department.expanded);
            holder.itemView.setOnClickListener(view -> toggle(department));
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView count;
        final ImageView chevron;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.header_icon);
            title = itemView.findViewById(R.id.header_title);
            count = itemView.findViewById(R.id.header_count);
            chevron = itemView.findViewById(R.id.header_chevron);
        }

        void bind(Department department) {
            Resources resources = itemView.getResources();
            int courseCount = department.courses.size();
            icon.setImageResource(getDepartmentIcon(department.id));
            title.setText(department.name);
            count.setText(resources.getQuantityString(
                    R.plurals.course_count, courseCount, courseCount));
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
