package com.week14.gmipathway.ui.eligibility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.logic.EligibilityChecker;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.EligibilityInput;
import com.week14.gmipathway.model.EligibilityOutcome;
import com.week14.gmipathway.model.EligibilityStatus;
import com.week14.gmipathway.model.Qualification;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.ExternalLinks;
import com.week14.gmipathway.ui.courses.CourseDetailActivity;
import com.week14.gmipathway.ui.enquiry.ContactUsActivity;

import java.util.List;

/**
 * Runs the Eligibility Checker and shows how the prospect's results match each programme.
 */
public class EligibilityResultActivity extends BaseActivity
        implements OutcomeAdapter.Listener {
    private static final String EXTRA_INPUT = "input";
    private static final String STATE_EXPANDED = "expandedSections";

    private OutcomeAdapter adapter;

    public static Intent createIntent(Context context, EligibilityInput input) {
        return new Intent(context, EligibilityResultActivity.class).putExtra(EXTRA_INPUT, input);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility_result);
        setUpToolbar(R.string.eligibility_result_title);
        applyWindowInsets();

        EligibilityInput input = IntentCompat.getSerializableExtra(
                getIntent(), EXTRA_INPUT, EligibilityInput.class);
        if (input == null) {
            finish();
            return;
        }

        CourseRepository repository = CourseRepository.getInstance(this);
        List<EligibilityOutcome> outcomes =
                new EligibilityChecker(repository.getCatalog()).check(input);

        Qualification qualification = repository.findQualification(input.getQualificationId());
        ((TextView) findViewById(R.id.result_summary)).setText(
                getString(R.string.eligibility_result_summary, qualification.getName()));
        showHeadline(outcomes);
        adapter = new OutcomeAdapter(outcomes, this);
        if (savedInstanceState != null) {
            adapter.setExpandedSections(savedInstanceState.getStringArrayList(STATE_EXPANDED));
        }
        ((RecyclerView) findViewById(R.id.outcome_list)).setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            outState.putStringArrayList(STATE_EXPANDED, adapter.getExpandedSections());
        }
    }

    /** Answers the main question first, with a friendly next step if nothing matches. */
    private void showHeadline(List<EligibilityOutcome> outcomes) {
        int eligible = 0;
        for (EligibilityOutcome outcome : outcomes) {
            if (outcome.getStatus() == EligibilityStatus.ELIGIBLE) {
                eligible++;
            }
        }
        TextView headline = findViewById(R.id.result_headline);
        if (eligible > 0) {
            headline.setText(getResources().getQuantityString(
                    R.plurals.eligibility_result_headline, eligible, eligible));
            return;
        }
        headline.setText(R.string.eligibility_no_match_title);
        findViewById(R.id.no_match_card).setVisibility(View.VISIBLE);
        findViewById(R.id.button_contact).setOnClickListener(view -> onContactClick());
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
