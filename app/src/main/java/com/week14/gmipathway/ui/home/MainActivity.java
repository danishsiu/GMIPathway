package com.week14.gmipathway.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.graphics.Insets;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.ExternalLinks;
import com.week14.gmipathway.ui.courses.CourseListActivity;
import com.week14.gmipathway.ui.courses.SavedCoursesActivity;
import com.week14.gmipathway.ui.eligibility.EligibilityActivity;
import com.week14.gmipathway.ui.enquiry.AboutUsActivity;
import com.week14.gmipathway.ui.enquiry.ContactUsActivity;
import com.week14.gmipathway.ui.enquiry.FaqActivity;
import com.week14.gmipathway.ui.settings.SettingsActivity;

/**
 * Home screen: a welcome header, key facts and shortcuts to every part of the app.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyWindowInsets();

        int programmeCount = CourseRepository.getInstance(this).getCourses().size();
        bindStat(R.id.stat_programmes, String.valueOf(programmeCount), R.string.home_stat_programmes);
        bindStat(R.id.stat_centres, getString(R.string.home_stat_centres_value),
                R.string.home_stat_centres);
        bindStat(R.id.stat_practical, getString(R.string.home_stat_practical_value),
                R.string.home_stat_practical);

        bindCard(R.id.card_courses, R.drawable.ic_school, R.string.home_courses_title,
                R.string.home_courses_subtitle, CourseListActivity.class);
        bindCard(R.id.card_eligibility, R.drawable.ic_fact_check, R.string.home_eligibility_title,
                R.string.home_eligibility_subtitle, EligibilityActivity.class);
        bindCard(R.id.card_contact, R.drawable.ic_call, R.string.home_contact_title,
                R.string.home_contact_subtitle, ContactUsActivity.class);
        bindCard(R.id.card_about, R.drawable.ic_info, R.string.home_about_title,
                R.string.home_about_subtitle, AboutUsActivity.class);
        bindCard(R.id.card_saved, R.drawable.ic_bookmark_border, R.string.home_saved_title,
                R.string.home_saved_subtitle, SavedCoursesActivity.class);
        bindCard(R.id.card_faq, R.drawable.ic_help, R.string.home_faq_title,
                R.string.home_faq_subtitle, FaqActivity.class);

        findViewById(R.id.button_apply).setOnClickListener(view ->
                ExternalLinks.openWebPage(this, getString(R.string.url_online_application)));
        findViewById(R.id.button_settings).setOnClickListener(view ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    /** The home screen has no toolbar: the header fills this space, or a strip in landscape. */
    @Override
    protected View[] getTopInsetViews() {
        return new View[]{findViewById(R.id.home_header), findViewById(R.id.home_status_strip)};
    }

    /** The landscape welcome panel runs behind the cutout and navigation bar. */
    @Override
    protected void onInsetsApplied(Insets bars, int bottomInset) {
        View panel = findViewById(R.id.home_panel);
        if (panel != null) {
            panel.setPadding(bars.left, 0, 0, bottomInset);
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) panel.getLayoutParams();
            params.leftMargin = -bars.left;
            params.bottomMargin = -bottomInset;
            panel.setLayoutParams(params);
        }
    }

    private void bindStat(@IdRes int statId, String value, @StringRes int labelRes) {
        View stat = findViewById(statId);
        ((TextView) stat.findViewById(R.id.stat_value)).setText(value);
        ((TextView) stat.findViewById(R.id.stat_label)).setText(labelRes);
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
