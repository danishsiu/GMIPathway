package com.week14.gmipathway.ui.campus;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CampusCatalog;
import com.week14.gmipathway.model.TechCentre;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.ExternalLinks;

import java.util.List;

/**
 * Lists GMI's technology centres and examination centres, each linking to its page on GMI's site.
 */
public class TechCentresActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_centres);
        setUpToolbar(R.string.centres_title);
        applyWindowInsets();

        addCentres(findViewById(R.id.centre_container), CampusCatalog.getTechCentres());
        addCentres(findViewById(R.id.exam_container), CampusCatalog.getExamCentres());
    }

    private void addCentres(ViewGroup container, List<TechCentre> centres) {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (TechCentre centre : centres) {
            View card = inflater.inflate(R.layout.item_tech_centre, container, false);
            String name = getString(centre.getNameRes());

            ImageView photo = card.findViewById(R.id.centre_photo);
            if (centre.getPhotoRes() == TechCentre.NO_PHOTO) {
                photo.setVisibility(View.GONE);
            } else {
                photo.setImageResource(centre.getPhotoRes());
            }

            TextView partner = card.findViewById(R.id.centre_partner);
            if (TextUtils.isEmpty(centre.getPartner())) {
                partner.setVisibility(View.GONE);
            } else {
                partner.setText(getString(R.string.centres_partner, centre.getPartner()));
            }

            ((TextView) card.findViewById(R.id.centre_name)).setText(name);
            ((TextView) card.findViewById(R.id.centre_description))
                    .setText(centre.getDescriptionRes());

            View link = card.findViewById(R.id.centre_link);
            link.setContentDescription(getString(R.string.centres_visit_page_for, name));
            link.setOnClickListener(view -> ExternalLinks.openWebPage(this, centre.getUrl()));
            container.addView(card);
        }
    }
}
