package com.week14.gmipathway.ui.common;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.transition.TransitionManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.view.ViewCompat;

import com.week14.gmipathway.R;

import java.util.List;

/**
 * Builds the reusable pieces of the detail screens: titled cards, info rows and formatted text.
 */
public final class DetailViews {
    private static final float CHEVRON_EXPANDED = 180f;
    private static final float CHEVRON_COLLAPSED = 0f;

    /** Tells the screen when a section opens or closes, so it can keep that after rotation. */
    public interface OnSectionToggleListener {
        void onSectionToggle(boolean expanded);
    }

    private DetailViews() {
    }

    /** Adds a titled card to the container. Empty text adds nothing. */
    public static void addSection(ViewGroup container, @StringRes int titleRes, CharSequence body) {
        if (TextUtils.isEmpty(body)) {
            return;
        }
        View section = inflateSection(container);
        ((TextView) section.findViewById(R.id.section_title)).setText(titleRes);
        ((TextView) section.findViewById(R.id.section_body)).setText(body);
        container.addView(section);
    }

    /** Adds a tinted card for a key statement. */
    public static void addHighlightSection(ViewGroup container, @StringRes int titleRes,
                                           CharSequence body) {
        View section = LayoutInflater.from(container.getContext())
                .inflate(R.layout.view_highlight_section, container, false);
        ((TextView) section.findViewById(R.id.highlight_title)).setText(titleRes);
        ((TextView) section.findViewById(R.id.highlight_body)).setText(body);
        container.addView(section);
    }

    /** Adds a timeline from entries written as "1991: What happened." */
    public static void addTimeline(ViewGroup container, @StringRes int titleRes,
                                   List<String> entries) {
        ViewGroup section = (ViewGroup) inflateSection(container);
        ((TextView) section.findViewById(R.id.section_title)).setText(titleRes);
        section.findViewById(R.id.section_body).setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        for (int i = 0; i < entries.size(); i++) {
            String entry = entries.get(i);
            int separator = entry.indexOf(": ");
            View row = inflater.inflate(R.layout.item_timeline_row, section, false);
            ((TextView) row.findViewById(R.id.timeline_year))
                    .setText(entry.substring(0, separator));
            ((TextView) row.findViewById(R.id.timeline_event))
                    .setText(entry.substring(separator + 2));
            boolean last = i == entries.size() - 1;
            row.findViewById(R.id.timeline_line).setVisibility(last ? View.INVISIBLE : View.VISIBLE);
            section.addView(row);
        }
        container.addView(section);
    }

    /** The first section on a page needs no line above it. */
    private static View inflateSection(ViewGroup container) {
        View section = LayoutInflater.from(container.getContext())
                .inflate(R.layout.view_detail_section, container, false);
        if (container.getChildCount() == 0) {
            section.findViewById(R.id.section_divider).setVisibility(View.GONE);
        }
        return section;
    }

    /** Adds a titled card whose text opens and closes when the title is tapped. */
    public static void addExpandableSection(ViewGroup container, @StringRes int titleRes,
                                            CharSequence body, boolean expanded,
                                            OnSectionToggleListener listener) {
        addExpandableSection(container, container.getContext().getString(titleRes), body, expanded,
                listener);
    }

    /** Adds a titled card whose text opens and closes when the title is tapped. */
    public static void addExpandableSection(ViewGroup container, CharSequence title,
                                            CharSequence body, boolean expanded,
                                            OnSectionToggleListener listener) {
        View section = inflateSection(container);
        View header = section.findViewById(R.id.section_header);
        View chevron = section.findViewById(R.id.section_chevron);
        TextView text = section.findViewById(R.id.section_body);
        ((TextView) section.findViewById(R.id.section_title)).setText(title);
        text.setText(body);
        chevron.setVisibility(View.VISIBLE);
        chevron.setRotation(expanded ? CHEVRON_EXPANDED : CHEVRON_COLLAPSED);
        text.setVisibility(expanded ? View.VISIBLE : View.GONE);
        header.setMinimumHeight(
                container.getResources().getDimensionPixelSize(R.dimen.min_touch_target));
        header.setBackgroundResource(getSelectableBackground(container.getContext()));
        updateExpandedState(header, expanded);

        header.setOnClickListener(view -> {
            boolean nowExpanded = text.getVisibility() != View.VISIBLE;
            TransitionManager.beginDelayedTransition((ViewGroup) section.getRootView());
            text.setVisibility(nowExpanded ? View.VISIBLE : View.GONE);
            chevron.animate()
                    .rotation(nowExpanded ? CHEVRON_EXPANDED : CHEVRON_COLLAPSED)
                    .start();
            updateExpandedState(header, nowExpanded);
            listener.onSectionToggle(nowExpanded);
        });
        container.addView(section);
    }

    /** Lets screen readers announce whether a section is open. */
    public static void updateExpandedState(View header, boolean expanded) {
        ViewCompat.setStateDescription(header, header.getContext().getString(
                expanded ? R.string.section_expanded : R.string.section_collapsed));
    }

    /** Adds an icon, label and value row. An empty value adds nothing. */
    public static void addInfoRow(ViewGroup container, @DrawableRes int iconRes,
                                  @StringRes int labelRes, CharSequence value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        View row = LayoutInflater.from(container.getContext())
                .inflate(R.layout.view_info_row, container, false);
        bindInfoRow(row, iconRes, labelRes, value);
        container.addView(row);
    }

    /** Fills in an info row that already exists in a layout. */
    public static void bindInfoRow(View row, @DrawableRes int iconRes,
                                   @StringRes int labelRes, CharSequence value) {
        ((ImageView) row.findViewById(R.id.row_icon)).setImageResource(iconRes);
        ((TextView) row.findViewById(R.id.row_label)).setText(labelRes);
        ((TextView) row.findViewById(R.id.row_value)).setText(value);
    }

    /** Joins items into a bulleted list. Long items wrap in line with their own text. */
    public static CharSequence bulletList(Context context, List<String> items) {
        int gap = context.getResources().getDimensionPixelSize(R.dimen.spacing_small);
        SpannableStringBuilder text = new SpannableStringBuilder();
        for (String item : items) {
            if (text.length() > 0) {
                text.append('\n');
            }
            int start = text.length();
            text.append(item);
            text.setSpan(new BulletSpan(gap), start, text.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return text;
    }

    @DrawableRes
    private static int getSelectableBackground(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(
                android.R.attr.selectableItemBackground, value, true);
        return value.resourceId;
    }

    /** Adds a bold heading to the text, after a blank line if there is already text. */
    public static void appendHeading(SpannableStringBuilder text, String heading) {
        if (text.length() > 0) {
            text.append("\n\n");
        }
        int start = text.length();
        text.append(heading);
        text.setSpan(new StyleSpan(Typeface.BOLD), start, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.append('\n');
    }
}
