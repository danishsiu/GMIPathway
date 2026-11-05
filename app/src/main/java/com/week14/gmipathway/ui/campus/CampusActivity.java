package com.week14.gmipathway.ui.campus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CampusCatalog;
import com.week14.gmipathway.model.Facility;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.ExternalLinks;

import java.util.List;

/**
 * Shows GMI's campus: photo galleries of each facility, the virtual tour spots and a link to the
 * technology centres. The photos ship inside the app, so this screen works offline.
 */
public class CampusActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus);
        setUpToolbar(R.string.campus_title);
        applyWindowInsets();

        View centresCard = findViewById(R.id.card_tech_centres);
        ((ImageView) centresCard.findViewById(R.id.link_icon))
                .setImageResource(R.drawable.ic_precision_manufacturing);
        ((TextView) centresCard.findViewById(R.id.link_title)).setText(R.string.centres_title);
        ((TextView) centresCard.findViewById(R.id.link_subtitle))
                .setText(R.string.campus_centres_link_subtitle);
        centresCard.setOnClickListener(view ->
                startActivity(new Intent(this, TechCentresActivity.class)));

        addFacilities(findViewById(R.id.facility_container));
        addTourSpots(findViewById(R.id.tour_spots));
        findViewById(R.id.button_virtual_tour).setOnClickListener(view ->
                ExternalLinks.openWebPage(this, getString(R.string.url_virtual_tour)));
    }

    private void addFacilities(ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(this);
        List<Facility> facilities = CampusCatalog.getFacilities();
        for (int i = 0; i < facilities.size(); i++) {
            Facility facility = facilities.get(i);
            int facilityIndex = i;
            View section = inflater.inflate(R.layout.view_facility_section, container, false);
            ((TextView) section.findViewById(R.id.facility_name)).setText(facility.getNameRes());
            ((TextView) section.findViewById(R.id.facility_summary))
                    .setText(facility.getSummaryRes());
            int count = facility.getPhotos().size();
            ((TextView) section.findViewById(R.id.facility_count)).setText(
                    getResources().getQuantityString(R.plurals.campus_photo_count, count, count));

            RecyclerView strip = section.findViewById(R.id.facility_photos);
            strip.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            strip.setAdapter(new PhotoStripAdapter(facility.getPhotos(), position ->
                    startActivity(PhotoViewerActivity.newIntent(this, facilityIndex, position))));
            container.addView(section);
        }
    }

    /** The tour spots are labels only. The tour itself is on GMI's website. */
    private void addTourSpots(ChipGroup group) {
        for (String spot : getResources().getStringArray(R.array.campus_tour_spots)) {
            Chip chip = new Chip(this);
            chip.setText(spot);
            chip.setClickable(false);
            chip.setFocusable(false);
            chip.setEnsureMinTouchTargetSize(false);
            group.addView(chip);
        }
        // Screen readers hear one sentence instead of 15 separate chips.
        group.setContentDescription(getString(R.string.campus_tour_spots_description,
                TextUtils.join(", ", getResources().getStringArray(R.array.campus_tour_spots))));
        group.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        for (int i = 0; i < group.getChildCount(); i++) {
            group.getChildAt(i).setImportantForAccessibility(
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }
}
