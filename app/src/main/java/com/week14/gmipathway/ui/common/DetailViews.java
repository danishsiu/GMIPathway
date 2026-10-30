package com.week14.gmipathway.ui.common;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.week14.gmipathway.R;

import java.util.List;

/**
 * Builds the reusable pieces of the detail screens: titled cards, info rows and formatted text.
 */
public final class DetailViews {

    private DetailViews() {
    }

    /** Adds a titled card to the container. Empty text adds nothing. */
    public static void addSection(ViewGroup container, @StringRes int titleRes, CharSequence body) {
        if (TextUtils.isEmpty(body)) {
            return;
        }
        View section = LayoutInflater.from(container.getContext())
                .inflate(R.layout.view_detail_section, container, false);
        ((TextView) section.findViewById(R.id.section_title)).setText(titleRes);
        ((TextView) section.findViewById(R.id.section_body)).setText(body);
        container.addView(section);
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

    /** Joins items into a bulleted list, one item per line. */
    public static CharSequence bulletList(Context context, List<String> items) {
        StringBuilder text = new StringBuilder();
        for (String item : items) {
            if (text.length() > 0) {
                text.append('\n');
            }
            text.append(context.getString(R.string.bullet_item, item));
        }
        return text;
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
