package com.week14.gmipathway.ui.courses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.EntryGroup;
import com.week14.gmipathway.model.EntryRoute;
import com.week14.gmipathway.model.SubjectGroup;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.DetailViews;
import com.week14.gmipathway.ui.common.ExternalLinks;

/**
 * Shows everything about one course, with a button to apply online.
 */
public class CourseDetailActivity extends BaseActivity {
    private static final String EXTRA_COURSE_ID = "courseId";

    private CourseRepository repository;

    public static Intent createIntent(Context context, String courseId) {
        return new Intent(context, CourseDetailActivity.class)
                .putExtra(EXTRA_COURSE_ID, courseId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        setUpToolbar(R.string.course_detail_title);
        applyWindowInsets();

        repository = CourseRepository.getInstance(this);
        Course course = repository.findCourse(getIntent().getStringExtra(EXTRA_COURSE_ID));
        if (course == null) {
            finish();
            return;
        }

        bindHeader(course);
        bindInfoRows(course);
        bindSections(course);
        findViewById(R.id.button_apply).setOnClickListener(view ->
                ExternalLinks.openWebPage(this, getString(R.string.url_online_application)));
    }

    private void bindHeader(Course course) {
        ((TextView) findViewById(R.id.course_department)).setText(course.getDepartment());
        ((TextView) findViewById(R.id.course_name)).setText(course.getName());
        ((TextView) findViewById(R.id.course_summary)).setText(course.getSummary());
    }

    private void bindInfoRows(Course course) {
        ViewGroup rows = findViewById(R.id.info_container);
        DetailViews.addInfoRow(rows, R.drawable.ic_tag, R.string.course_info_code, course.getCode());
        DetailViews.addInfoRow(rows, R.drawable.ic_schedule, R.string.course_info_duration,
                course.getDuration());
        DetailViews.addInfoRow(rows, R.drawable.ic_event, R.string.course_info_intake,
                course.getIntake());
        DetailViews.addInfoRow(rows, R.drawable.ic_work, R.string.course_info_mode,
                course.getStudyMode());
        DetailViews.addInfoRow(rows, R.drawable.ic_workspace_premium, R.string.course_info_credits,
                String.valueOf(course.getCredits()));
        DetailViews.addInfoRow(rows, R.drawable.ic_payments, R.string.course_info_fee,
                course.getFee());
    }

    private void bindSections(Course course) {
        ViewGroup sections = findViewById(R.id.section_container);
        DetailViews.addSection(sections, R.string.course_section_about, course.getDescription());
        DetailViews.addSection(sections, R.string.course_section_core,
                DetailViews.bulletList(this, course.getCoreAreas()));
        DetailViews.addSection(sections, R.string.course_section_certificates,
                formatCertificates(course));
        DetailViews.addSection(sections, R.string.course_section_subjects, formatSubjects(course));
        DetailViews.addSection(sections, R.string.course_section_careers,
                DetailViews.bulletList(this, course.getCareers()));
        DetailViews.addSection(sections, R.string.course_section_entry,
                formatEntryRequirements(course));
        DetailViews.addSection(sections, R.string.course_section_progression,
                DetailViews.bulletList(this, course.getProgression()));
    }

    private CharSequence formatCertificates(Course course) {
        if (course.getCertificates().isEmpty()) {
            return "";
        }
        return new SpannableStringBuilder(DetailViews.bulletList(this, course.getCertificates()))
                .append("\n\n")
                .append(getString(R.string.course_certificates_note));
    }

    private CharSequence formatSubjects(Course course) {
        if (course.getSubjectGroups().isEmpty()) {
            return getString(R.string.course_subjects_unavailable);
        }
        SpannableStringBuilder text = new SpannableStringBuilder();
        for (SubjectGroup group : course.getSubjectGroups()) {
            DetailViews.appendHeading(text, group.getLabel());
            text.append(DetailViews.bulletList(this, group.getSubjects()));
        }
        return text;
    }

    private CharSequence formatEntryRequirements(Course course) {
        EntryGroup group = course.getEntryGroupId() == null
                ? null : repository.findEntryGroup(course.getEntryGroupId());
        if (group == null) {
            return getString(R.string.course_entry_unpublished);
        }
        SpannableStringBuilder text = new SpannableStringBuilder(
                getString(R.string.course_entry_standard, group.getStandard()));
        for (EntryRoute route : group.getRoutes()) {
            DetailViews.appendHeading(text, route.getQualification());
            text.append(route.getRequirement());
        }
        for (String note : group.getNotes()) {
            text.append("\n\n").append(note);
        }
        return text;
    }
}
