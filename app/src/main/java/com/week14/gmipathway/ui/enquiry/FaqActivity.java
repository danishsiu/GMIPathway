package com.week14.gmipathway.ui.enquiry;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.week14.gmipathway.R;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.DetailViews;

/**
 * Lists common questions. Tapping a question opens its answer.
 * Every answer comes from the sources listed in research/faq_sources.md.
 */
public class FaqActivity extends BaseActivity {
    private static final String STATE_EXPANDED = "expanded";

    private boolean[] expanded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        setUpToolbar(R.string.faq_title);
        applyWindowInsets();

        String[] questions = getResources().getStringArray(R.array.faq_questions);
        String[] answers = getResources().getStringArray(R.array.faq_answers);
        expanded = savedInstanceState != null
                ? savedInstanceState.getBooleanArray(STATE_EXPANDED)
                : new boolean[questions.length];

        ViewGroup container = findViewById(R.id.faq_container);
        for (int i = 0; i < questions.length; i++) {
            int index = i;
            DetailViews.addExpandableSection(container, questions[i], answers[i], expanded[i],
                    isExpanded -> expanded[index] = isExpanded);
        }
        findViewById(R.id.button_contact).setOnClickListener(view ->
                startActivity(new Intent(this, ContactUsActivity.class)));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(STATE_EXPANDED, expanded);
    }
}
