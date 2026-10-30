package com.week14.gmipathway.ui.enquiry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.week14.gmipathway.R;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.DetailViews;
import com.week14.gmipathway.ui.common.ExternalLinks;

/**
 * Shows GMI's contact details. Each row opens the matching app, such as the phone or maps app.
 */
public class ContactUsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        setUpToolbar(R.string.contact_title);
        applyWindowInsets();

        bindContactRows();
        bindSocialChip(R.id.chip_facebook, R.string.url_facebook);
        bindSocialChip(R.id.chip_instagram, R.string.url_instagram);
        bindSocialChip(R.id.chip_x, R.string.url_x);
        bindSocialChip(R.id.chip_youtube, R.string.url_youtube);
        bindSocialChip(R.id.chip_tiktok, R.string.url_tiktok);
        bindSocialChip(R.id.chip_linkedin, R.string.url_linkedin);

        findViewById(R.id.button_map).setOnClickListener(view ->
                ExternalLinks.openCampusMap(this));
        findViewById(R.id.button_enquiry).setOnClickListener(view ->
                startActivity(new Intent(this, EnquiryFormActivity.class)));
    }

    private void bindContactRows() {
        bindRow(R.id.row_address, R.drawable.ic_location_on, R.string.contact_address_label,
                getString(R.string.address_full), view -> ExternalLinks.openCampusMap(this));
        bindRow(R.id.row_phone, R.drawable.ic_call, R.string.contact_phone_label,
                getString(R.string.phone_main),
                view -> ExternalLinks.dial(this, getString(R.string.phone_main)));
        bindRow(R.id.row_admissions, R.drawable.ic_call, R.string.contact_admissions_label,
                getString(R.string.phone_admissions_display),
                view -> ExternalLinks.dial(this, getString(R.string.phone_admissions)));
        bindRow(R.id.row_fax, R.drawable.ic_print, R.string.contact_fax_label,
                getString(R.string.phone_fax), null);
        bindRow(R.id.row_email, R.drawable.ic_mail, R.string.contact_email_label,
                getString(R.string.email_enquiry),
                view -> ExternalLinks.composeEmail(
                        this, getString(R.string.email_enquiry), "", ""));
        bindRow(R.id.row_whatsapp, R.drawable.ic_chat, R.string.contact_whatsapp_label,
                getString(R.string.phone_whatsapp),
                view -> ExternalLinks.openWebPage(this, getString(R.string.url_whatsapp)));
        bindRow(R.id.row_website, R.drawable.ic_language, R.string.contact_website_label,
                getString(R.string.website_display),
                view -> ExternalLinks.openWebPage(this, getString(R.string.url_website)));
    }

    /** Fills in a row. A null action leaves it as plain text, as for the fax number. */
    private void bindRow(@IdRes int rowId, @DrawableRes int iconRes, @StringRes int labelRes,
                         String value, @Nullable View.OnClickListener action) {
        View row = findViewById(rowId);
        DetailViews.bindInfoRow(row, iconRes, labelRes, value);
        if (action != null) {
            row.setOnClickListener(action);
            row.findViewById(R.id.row_chevron).setVisibility(View.VISIBLE);
        }
    }

    private void bindSocialChip(@IdRes int chipId, @StringRes int urlRes) {
        findViewById(chipId).setOnClickListener(view ->
                ExternalLinks.openWebPage(this, getString(urlRes)));
    }
}
