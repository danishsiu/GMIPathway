package com.week14.gmipathway.ui.funding;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.week14.gmipathway.R;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.DetailViews;
import com.week14.gmipathway.ui.common.ExternalLinks;

import java.util.Arrays;

/**
 * Loans, scholarships and aid that GMI lists for Malaysian students, and how to reach GMI's
 * sponsorship unit. Source: research/campus_research.md, section 4.
 */
public class FinancialAidActivity extends BaseActivity {
    private static final String STATE_EXPANDED = "expanded";

    private boolean[] expanded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_aid);
        setUpToolbar(R.string.aid_title);
        applyWindowInsets();

        DetailViews.addHighlightSection(findViewById(R.id.aid_rules), R.string.aid_rules_title,
                DetailViews.bulletList(this, Arrays.asList(
                        getString(R.string.aid_rule_malaysian),
                        getString(R.string.aid_rule_one_sponsor),
                        getString(R.string.aid_rule_approval))));

        String[] titles = getResources().getStringArray(R.array.aid_scheme_titles);
        String[] bodies = getResources().getStringArray(R.array.aid_scheme_bodies);
        expanded = savedInstanceState != null
                ? savedInstanceState.getBooleanArray(STATE_EXPANDED)
                : new boolean[titles.length];

        ViewGroup schemes = findViewById(R.id.aid_schemes);
        for (int i = 0; i < titles.length; i++) {
            int index = i;
            DetailViews.addExpandableSection(schemes, titles[i], formatBody(bodies[i]),
                    expanded[i], isExpanded -> expanded[index] = isExpanded);
        }

        findViewById(R.id.button_aid_call).setOnClickListener(view ->
                ExternalLinks.dial(this, getString(R.string.phone_sponsorship)));
        findViewById(R.id.button_aid_email).setOnClickListener(view ->
                ExternalLinks.composeEmail(this, getString(R.string.email_sponsorship),
                        getString(R.string.aid_email_subject), ""));
        findViewById(R.id.button_aid_website).setOnClickListener(view ->
                ExternalLinks.openWebPage(this, getString(R.string.url_financial_aid)));
    }

    /** A body written as several lines becomes a bulleted list, one line per sponsor. */
    private CharSequence formatBody(String body) {
        if (body.indexOf('\n') < 0) {
            return body;
        }
        return DetailViews.bulletList(this, Arrays.asList(body.split("\n")));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(STATE_EXPANDED, expanded);
    }
}
