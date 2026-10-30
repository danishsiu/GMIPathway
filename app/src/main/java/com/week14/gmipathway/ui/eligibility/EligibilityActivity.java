package com.week14.gmipathway.ui.eligibility;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.EligibilityInput;
import com.week14.gmipathway.model.Qualification;
import com.week14.gmipathway.model.SubjectGrade;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.enquiry.ContactUsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects the prospect's qualification and grades.
 * Subjects and grades come from dropdowns, so an invalid grade can never be entered.
 */
public class EligibilityActivity extends BaseActivity {
    private static final String STATE_INPUT = "input";
    private static final String STATE_OTHER_ROUTE = "otherRoute";

    private CourseRepository repository;
    private List<Qualification> qualifications;

    /** What the prospect has entered so far, or null before a qualification is chosen. */
    private EligibilityInput input;
    private boolean otherRouteSelected;

    private NestedScrollView scrollView;
    private MaterialAutoCompleteTextView qualificationInput;
    private View formGroup;
    private View supportingGroup;
    private View guidedCard;
    private View checkButton;
    private ViewGroup primaryRows;
    private ViewGroup supportingRows;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility);
        setUpToolbar(R.string.eligibility_title);
        applyWindowInsets();

        repository = CourseRepository.getInstance(this);
        qualifications = repository.getQualifications();
        bindViews();

        if (savedInstanceState != null) {
            input = BundleCompat.getSerializable(
                    savedInstanceState, STATE_INPUT, EligibilityInput.class);
            otherRouteSelected = savedInstanceState.getBoolean(STATE_OTHER_ROUTE);
        }
        setUpQualificationDropdown();
        renderForm();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_INPUT, input);
        outState.putBoolean(STATE_OTHER_ROUTE, otherRouteSelected);
    }

    private void bindViews() {
        scrollView = findViewById(R.id.eligibility_scroll);
        qualificationInput = findViewById(R.id.qualification_input);
        formGroup = findViewById(R.id.form_group);
        supportingGroup = findViewById(R.id.supporting_group);
        guidedCard = findViewById(R.id.guided_card);
        checkButton = findViewById(R.id.button_check);
        primaryRows = findViewById(R.id.primary_rows);
        supportingRows = findViewById(R.id.supporting_rows);

        findViewById(R.id.button_add_subject).setOnClickListener(view -> addEmptyRow());
        findViewById(R.id.button_contact).setOnClickListener(view ->
                startActivity(new Intent(this, ContactUsActivity.class)));
        checkButton.setOnClickListener(view -> submit());
    }

    private void setUpQualificationDropdown() {
        List<String> names = new ArrayList<>();
        for (Qualification qualification : qualifications) {
            names.add(qualification.getName());
        }
        names.add(getString(R.string.eligibility_other_option));
        qualificationInput.setSimpleItems(names.toArray(new String[0]));
        qualificationInput.setOnItemClickListener((parent, view, position, id) -> {
            selectQualification(position);
            renderForm();
        });
    }

    /** Starts a fresh form. The position after the last qualification is "Other qualification". */
    private void selectQualification(int position) {
        otherRouteSelected = position == qualifications.size();
        if (otherRouteSelected) {
            input = null;
            return;
        }
        Qualification qualification = qualifications.get(position);
        input = new EligibilityInput(qualification.getId());
        for (String subject : qualification.getRequiredSubjects()) {
            input.getGrades().add(new SubjectGrade(subject, null));
        }
        for (String subject : qualification.getDefaultSubjects()) {
            input.getGrades().add(new SubjectGrade(subject, null));
        }
        for (String subject : qualification.getSupportingSubjects()) {
            input.getSupportingGrades().add(new SubjectGrade(subject, null));
        }
        for (String subject : qualification.getSupportingOptionalSubjects()) {
            input.getSupportingGrades().add(new SubjectGrade(subject, null));
        }
    }

    /** Rebuilds the form from {@link #input}, including after a screen rotation. */
    private void renderForm() {
        Qualification qualification = getSelectedQualification();
        boolean showForm = qualification != null;
        formGroup.setVisibility(showForm ? View.VISIBLE : View.GONE);
        findViewById(R.id.check_bar).setVisibility(showForm ? View.VISIBLE : View.GONE);
        guidedCard.setVisibility(otherRouteSelected ? View.VISIBLE : View.GONE);

        if (otherRouteSelected) {
            qualificationInput.setText(getString(R.string.eligibility_other_option), false);
        } else if (showForm) {
            qualificationInput.setText(qualification.getName(), false);
        }

        primaryRows.removeAllViews();
        supportingRows.removeAllViews();
        supportingGroup.setVisibility(View.GONE);
        if (!showForm) {
            return;
        }
        for (SubjectGrade entry : input.getGrades()) {
            addPrimaryRow(entry, qualification);
        }
        renderSupportingRows(qualification);
        refreshSubjectOptions();
    }

    /** Some routes also look at SPM results, so those are collected in a second section. */
    private void renderSupportingRows(Qualification qualification) {
        Qualification supporting = repository.findQualification(
                qualification.getSupportingQualificationId() == null
                        ? "" : qualification.getSupportingQualificationId());
        if (supporting == null) {
            return;
        }
        supportingGroup.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.supporting_heading)).setText(
                getString(R.string.eligibility_supporting_heading, supporting.getName()));
        for (SubjectGrade entry : input.getSupportingGrades()) {
            boolean optional =
                    qualification.getSupportingOptionalSubjects().contains(entry.getSubject());
            addRow(supportingRows, entry, true, optional, supporting.getGrades());
        }
    }

    private void addPrimaryRow(SubjectGrade entry, Qualification qualification) {
        boolean required = qualification.getRequiredSubjects().contains(entry.getSubject());
        View row = addRow(primaryRows, entry, required, !required, qualification.getGrades());
        if (qualification.getSubjectLabel() != null) {
            ((TextInputLayout) row.findViewById(R.id.subject_layout))
                    .setHint(qualification.getSubjectLabel());
        }
        if (qualification.getGradeLabel() != null) {
            ((TextInputLayout) row.findViewById(R.id.grade_layout))
                    .setHint(qualification.getGradeLabel());
        }
    }

    /** Adds one row. A locked subject cannot be changed, and only a removable row has an X. */
    private View addRow(ViewGroup container, SubjectGrade entry, boolean subjectLocked,
                        boolean removable, List<String> grades) {
        // Fixed names fit beside the grade. Chosen subjects can be long, so they get their own line.
        View row = getLayoutInflater().inflate(subjectLocked
                ? R.layout.item_subject_grade : R.layout.item_subject_grade_open, container, false);
        row.setTag(entry);
        TextInputLayout subjectLayout = row.findViewById(R.id.subject_layout);
        TextInputLayout gradeLayout = row.findViewById(R.id.grade_layout);
        MaterialAutoCompleteTextView subjectInput = row.findViewById(R.id.subject_input);
        MaterialAutoCompleteTextView gradeInput = row.findViewById(R.id.grade_input);
        View removeButton = row.findViewById(R.id.button_remove);

        gradeInput.setSimpleItems(grades.toArray(new String[0]));
        gradeInput.setText(entry.getGrade(), false);
        gradeInput.setOnItemClickListener((parent, view, position, id) -> {
            entry.setGrade(grades.get(position));
            gradeLayout.setError(null);
        });

        subjectInput.setDropDownWidth(getSubjectListWidth());
        subjectInput.setText(entry.getSubject(), false);
        if (subjectLocked) {
            showAsPlainText(subjectLayout, subjectInput);
        } else {
            subjectInput.setOnItemClickListener((parent, view, position, id) -> {
                entry.setSubject(parent.getItemAtPosition(position).toString());
                subjectLayout.setError(null);
                refreshSubjectOptions();
            });
        }
        removeButton.setVisibility(removable ? View.VISIBLE : View.GONE);
        removeButton.setOnClickListener(view -> removeRow(container, row, entry));
        container.addView(row);
        return row;
    }

    /** A fixed subject reads as a label, so it does not look like a field that can be edited. */
    private void showAsPlainText(TextInputLayout layout, MaterialAutoCompleteTextView input) {
        layout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        layout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
        layout.setHintEnabled(false);
        input.setBackground(null);
        input.setFocusable(false);
        input.setClickable(false);
        input.setPadding(0, 0, 0, 0);
        input.setMinHeight(getResources().getDimensionPixelSize(R.dimen.field_height));
        input.setGravity(Gravity.CENTER_VERTICAL);
    }

    /** Long subject names need more room than the narrow subject field has on small phones. */
    private int getSubjectListWidth() {
        int margins = 2 * getResources().getDimensionPixelSize(R.dimen.spacing_medium);
        return getResources().getDisplayMetrics().widthPixels - margins;
    }

    private void addEmptyRow() {
        Qualification qualification = getSelectedQualification();
        if (qualification == null) {
            return;
        }
        if (getAvailableSubjects(qualification, null).isEmpty()) {
            showMessage(R.string.eligibility_error_no_more_subjects);
            return;
        }
        SubjectGrade entry = new SubjectGrade(null, null);
        input.getGrades().add(entry);
        addPrimaryRow(entry, qualification);
        refreshSubjectOptions();
    }

    private void removeRow(ViewGroup container, View row, SubjectGrade entry) {
        input.getGrades().remove(entry);
        input.getSupportingGrades().remove(entry);
        container.removeView(row);
        refreshSubjectOptions();
    }

    /** Offers each row only the subjects no other row is using, so none can be entered twice. */
    private void refreshSubjectOptions() {
        Qualification qualification = getSelectedQualification();
        if (qualification == null) {
            return;
        }
        for (int i = 0; i < primaryRows.getChildCount(); i++) {
            View row = primaryRows.getChildAt(i);
            SubjectGrade entry = (SubjectGrade) row.getTag();
            if (qualification.getRequiredSubjects().contains(entry.getSubject())) {
                continue;
            }
            MaterialAutoCompleteTextView subjectInput = row.findViewById(R.id.subject_input);
            subjectInput.setAdapter(new ArrayAdapter<>(this, R.layout.item_dropdown_option,
                    R.id.option_text, getAvailableSubjects(qualification, entry)));
            subjectInput.setText(entry.getSubject(), false);
        }
    }

    /** Lists the subjects still free for a row. Pass null to ask about a new row. */
    private List<String> getAvailableSubjects(Qualification qualification,
                                              @Nullable SubjectGrade current) {
        List<String> available = new ArrayList<>(qualification.getSubjects());
        for (SubjectGrade entry : input.getGrades()) {
            if (entry != current) {
                available.remove(entry.getSubject());
            }
        }
        return available;
    }

    private void submit() {
        if (!validate()) {
            return;
        }
        startActivity(EligibilityResultActivity.createIntent(this, input));
    }

    /** Marks every unfinished field, then scrolls to the first one. */
    private boolean validate() {
        if (input.getGrades().isEmpty()) {
            showMessage(R.string.eligibility_error_no_subjects);
            return false;
        }
        View firstError = markMissingFields(primaryRows, null);
        firstError = markMissingFields(supportingRows, firstError);
        if (firstError == null) {
            return true;
        }
        Rect position = new Rect();
        firstError.getDrawingRect(position);
        scrollView.offsetDescendantRectToMyCoords(firstError, position);
        scrollView.smoothScrollTo(0, position.top);
        showMessage(R.string.eligibility_error_incomplete);
        return false;
    }

    private View markMissingFields(ViewGroup rows, @Nullable View firstError) {
        for (int i = 0; i < rows.getChildCount(); i++) {
            View row = rows.getChildAt(i);
            SubjectGrade entry = (SubjectGrade) row.getTag();
            TextInputLayout subjectLayout = row.findViewById(R.id.subject_layout);
            TextInputLayout gradeLayout = row.findViewById(R.id.grade_layout);
            if (entry.getSubject() == null) {
                subjectLayout.setError(getString(R.string.eligibility_error_required));
            }
            if (entry.getGrade() == null) {
                gradeLayout.setError(getString(R.string.eligibility_error_required));
            }
            if (firstError == null && (entry.getSubject() == null || entry.getGrade() == null)) {
                firstError = row;
            }
        }
        return firstError;
    }

    @Nullable
    private Qualification getSelectedQualification() {
        return input == null ? null : repository.findQualification(input.getQualificationId());
    }

    private void showMessage(int messageRes) {
        Snackbar.make(findViewById(R.id.main), messageRes, Snackbar.LENGTH_LONG)
                .setAnchorView(checkButton)
                .show();
    }
}
