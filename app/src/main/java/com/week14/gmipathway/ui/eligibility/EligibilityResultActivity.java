package com.week14.gmipathway.ui.eligibility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.EligibilityInput;
import com.week14.gmipathway.model.EligibilityOutcome;
import com.week14.gmipathway.model.EligibilityStatus;
import com.week14.gmipathway.model.Qualification;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.ExternalLinks;
import com.week14.gmipathway.ui.courses.CourseDetailActivity;
import com.week14.gmipathway.ui.enquiry.ContactUsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shows how the prospect's results compare with each programme.
 */
public class EligibilityResultActivity extends BaseActivity
        implements OutcomeAdapter.Listener {
    private static final String EXTRA_INPUT = "input";

    private CourseRepository repository;

    public static Intent createIntent(Context context, EligibilityInput input) {
        return new Intent(context, EligibilityResultActivity.class).putExtra(EXTRA_INPUT, input);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility_result);
        setUpToolbar(R.string.eligibility_result_title);
        applyWindowInsets();

        repository = CourseRepository.getInstance(this);
        EligibilityInput input = IntentCompat.getSerializableExtra(
                getIntent(), EXTRA_INPUT, EligibilityInput.class);
        if (input == null) {
            finish();
            return;
        }

        bindSummary(input);
        ((RecyclerView) findViewById(R.id.outcome_list))
                .setAdapter(new OutcomeAdapter(buildPendingOutcomes(), this));
    }

    private void bindSummary(EligibilityInput input) {
        Qualification qualification = repository.findQualification(input.getQualificationId());
        int subjectCount = input.getGrades().size();
        ((TextView) findViewById(R.id.result_summary)).setText(getResources().getQuantityString(
                R.plurals.eligibility_result_summary, subjectCount,
                qualification.getName(), subjectCount));
    }

    /** Temporary: Phase 3 replaces this with the real EligibilityChecker. */
    private List<EligibilityOutcome> buildPendingOutcomes() {
        List<String> reason =
                Collections.singletonList(getString(R.string.eligibility_pending_reason));
        List<EligibilityOutcome> outcomes = new ArrayList<>();
        for (Course course : repository.getCourses()) {
            outcomes.add(new EligibilityOutcome(course, EligibilityStatus.CONTACT_GMI, reason));
        }
        return outcomes;
    }

    @Override
    public void onCourseClick(Course course) {
        startActivity(CourseDetailActivity.createIntent(this, course.getId()));
    }

    @Override
    public void onApplyClick() {
        ExternalLinks.openWebPage(this, getString(R.string.url_online_application));
    }

    @Override
    public void onContactClick() {
        startActivity(new Intent(this, ContactUsActivity.class));
    }
}
