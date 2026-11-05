package com.week14.gmipathway.ui.courses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.BookmarkStore;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.EntryGroup;
import com.week14.gmipathway.model.EntryRoute;
import com.week14.gmipathway.model.ProgrammeCategory;
import com.week14.gmipathway.model.SubjectGroup;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.DetailViews;
import com.week14.gmipathway.ui.common.ExternalLinks;
import com.week14.gmipathway.ui.enquiry.EnquiryFormActivity;

import java.util.Arrays;

/**
 * Shows everything about one course, with a button to apply online.
 */
public class CourseDetailActivity extends BaseActivity {
    private static final String EXTRA_COURSE_ID = "courseId";
    private static final String STATE_SUBJECTS_EXPANDED = "subjectsExpanded";
    private static final String STATE_ENTRY_EXPANDED = "entryExpanded";

    private CourseRepository repository;
    private BookmarkStore bookmarks;
    private Course course;
    private boolean subjectsExpanded;
    private boolean entryExpanded;

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

        if (savedInstanceState != null) {
            subjectsExpanded = savedInstanceState.getBoolean(STATE_SUBJECTS_EXPANDED);
            entryExpanded = savedInstanceState.getBoolean(STATE_ENTRY_EXPANDED);
        }

        repository = CourseRepository.getInstance(this);
        bookmarks = new BookmarkStore(this);
        course = repository.findCourse(getIntent().getStringExtra(EXTRA_COURSE_ID));
        if (course == null) {
            finish();
            return;
        }

        bindHeader(course);
        bindInfoRows(course);
        bindSections(course);
        findViewById(R.id.button_apply).setOnClickListener(view ->
                ExternalLinks.openWebPage(this, getString(R.string.url_online_application)));
        findViewById(R.id.button_enquire).setOnClickListener(view ->
                startActivity(EnquiryFormActivity.createIntent(this, course.getName())));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SUBJECTS_EXPANDED, subjectsExpanded);
        outState.putBoolean(STATE_ENTRY_EXPANDED, entryExpanded);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        return true;
    }

    /** Shows whether this course is saved. Runs again after invalidateOptionsMenu. */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean saved = bookmarks.isSaved(course.getId());
        MenuItem bookmark = menu.findItem(R.id.action_bookmark);
        bookmark.setIcon(saved ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_border);
        bookmark.setTitle(saved ? R.string.course_unsave : R.string.course_save);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_bookmark) {
            bookmarks.toggle(course.getId());
            invalidateOptionsMenu();
            return true;
        }
        if (item.getItemId() == R.id.action_share) {
            ExternalLinks.shareText(this, getString(R.string.course_share_chooser),
                    getString(R.string.course_share_text, course.getName(),
                            getString(R.string.institute_name), course.getDuration(),
                            course.getIntake(), getString(R.string.url_online_application)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindHeader(Course course) {
        ((ImageView) findViewById(R.id.course_hero)).setImageResource(getHeroPhoto(course));
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
        DetailViews.addExpandableSection(sections, R.string.course_section_subjects,
                formatSubjects(course), subjectsExpanded,
                expanded -> subjectsExpanded = expanded);
        DetailViews.addSection(sections, R.string.course_section_careers,
                DetailViews.bulletList(this, course.getCareers()));
        DetailViews.addExpandableSection(sections, R.string.course_section_entry,
                formatEntryRequirements(course), entryExpanded,
                expanded -> entryExpanded = expanded);
        addPathway(sections, course);
    }

    /** A photo of the labs in the programme's department, from the bundled campus photos. */
    @DrawableRes
    private static int getHeroPhoto(Course course) {
        switch (course.getDepartmentId()) {
            case Course.DEPARTMENT_ELECTRICAL:
                return R.drawable.fac_elec_dcs_emerson;
            case Course.DEPARTMENT_MECHANICAL:
                return R.drawable.fac_mech_cnc;
            case Course.DEPARTMENT_COMPUTER:
                return R.drawable.fac_comp_computer_lab;
            default:
                return R.drawable.fac_preu_chemistry;
        }
    }

    /**
     * Shows the programme as three steps: what you need first, what you study at GMI,
     * and where it leads. Without a progression list, step 3 points to Career prospects above.
     */
    private void addPathway(ViewGroup sections, Course course) {
        CharSequence entry;
        if (course.getEntryGroupId() == null) {
            entry = getString(R.string.pathway_entry_unpublished);
        } else if (course.getCategory() == ProgrammeCategory.DEGREE) {
            entry = getString(R.string.pathway_entry_degree);
        } else {
            entry = getString(R.string.pathway_entry_spm);
        }
        CharSequence after = course.getProgression().isEmpty()
                ? getString(R.string.pathway_after_careers)
                : DetailViews.bulletList(this, course.getProgression());
        DetailViews.addPathway(sections, R.string.pathway_title,
                Arrays.asList(getString(R.string.pathway_step_before),
                        getString(R.string.pathway_step_during),
                        getString(R.string.pathway_step_after)),
                Arrays.asList(entry,
                        getString(R.string.pathway_during_body, course.getDuration()),
                        after));
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
