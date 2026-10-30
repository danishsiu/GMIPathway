package com.week14.gmipathway.ui.enquiry;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.week14.gmipathway.R;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.DetailViews;

import java.util.Arrays;

/**
 * Introduces GMI: its background, vision, mission, approach and accreditation.
 */
public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        setUpToolbar(R.string.about_title);
        applyWindowInsets();

        ViewGroup sections = findViewById(R.id.section_container);
        DetailViews.addSection(sections, R.string.about_section_who,
                getString(R.string.about_who_body));
        DetailViews.addSection(sections, R.string.about_section_vision,
                getString(R.string.about_vision_body));
        DetailViews.addSection(sections, R.string.about_section_mission,
                getString(R.string.about_mission_body));
        DetailViews.addSection(sections, R.string.about_section_german,
                DetailViews.bulletList(this, Arrays.asList(
                        getString(R.string.about_german_practical),
                        getString(R.string.about_german_centres),
                        getString(R.string.about_german_language),
                        getString(R.string.about_german_industry))));
        DetailViews.addSection(sections, R.string.about_section_history,
                DetailViews.bulletList(this, Arrays.asList(
                        getString(R.string.about_history_1991),
                        getString(R.string.about_history_1992),
                        getString(R.string.about_history_2001),
                        getString(R.string.about_history_2008),
                        getString(R.string.about_history_2024))));
        DetailViews.addSection(sections, R.string.about_section_governance,
                getString(R.string.about_governance_body));
        DetailViews.addSection(sections, R.string.about_section_accreditation,
                getString(R.string.about_accreditation_body));
    }
}
