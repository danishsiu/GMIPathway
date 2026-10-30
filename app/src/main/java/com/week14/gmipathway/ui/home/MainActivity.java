package com.week14.gmipathway.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.week14.gmipathway.R;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.ExternalLinks;
import com.week14.gmipathway.ui.courses.CourseListActivity;
import com.week14.gmipathway.ui.eligibility.EligibilityActivity;
import com.week14.gmipathway.ui.enquiry.AboutUsActivity;
import com.week14.gmipathway.ui.enquiry.ContactUsActivity;

/**
 * Home screen: a welcome header and shortcuts to every part of the app.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyWindowInsets();

        bindCard(R.id.card_courses, R.drawable.ic_school, R.string.home_courses_title,
                R.string.home_courses_subtitle, CourseListActivity.class);
        bindCard(R.id.card_eligibility, R.drawable.ic_fact_check, R.string.home_eligibility_title,
                R.string.home_eligibility_subtitle, EligibilityActivity.class);
        bindCard(R.id.card_contact, R.drawable.ic_call, R.string.home_contact_title,
                R.string.home_contact_subtitle, ContactUsActivity.class);
        bindCard(R.id.card_about, R.drawable.ic_info, R.string.home_about_title,
                R.string.home_about_subtitle, AboutUsActivity.class);

        findViewById(R.id.button_apply).setOnClickListener(view ->
                ExternalLinks.openWebPage(this, getString(R.string.url_online_application)));
    }

    /** The home screen has no toolbar, so its header extends behind the status bar instead. */
    @Override
    protected View getTopInsetView() {
        return findViewById(R.id.home_header);
    }

    private void bindCard(@IdRes int cardId, @DrawableRes int iconRes, @StringRes int titleRes,
                          @StringRes int subtitleRes, Class<?> destination) {
        View card = findViewById(cardId);
        ((ImageView) card.findViewById(R.id.card_icon)).setImageResource(iconRes);
        ((TextView) card.findViewById(R.id.card_title)).setText(titleRes);
        ((TextView) card.findViewById(R.id.card_subtitle)).setText(subtitleRes);
        card.setOnClickListener(view -> startActivity(new Intent(this, destination)));
    }
}
