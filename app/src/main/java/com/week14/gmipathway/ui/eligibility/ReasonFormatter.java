package com.week14.gmipathway.ui.eligibility;

import android.content.Context;
import android.text.TextUtils;

import com.week14.gmipathway.R;
import com.week14.gmipathway.model.EligibilityReason;
import com.week14.gmipathway.model.Qualification;

/**
 * Turns a checker reason into a sentence from strings.xml, keeping the logic free of display text.
 */
final class ReasonFormatter {

    private ReasonFormatter() {
    }

    static String format(Context context, EligibilityReason reason) {
        switch (reason.getType()) {
            case CERTIFICATE:
                return context.getString(R.string.eligibility_reason_certificate,
                        join(context, reason, R.string.eligibility_list_and));
            case SUBJECT_COUNT:
                return context.getResources().getQuantityString(
                        R.plurals.eligibility_reason_count, reason.getCount(),
                        reason.getCount(), reason.getGrade());
            case SUBJECT:
                return context.getString(reason.isSupporting()
                                ? R.string.eligibility_reason_supporting
                                : R.string.eligibility_reason_subject,
                        reason.getGrade(), join(context, reason, R.string.eligibility_list_or));
            case SUBJECT_GROUP:
                return context.getString(
                        Qualification.GROUP_SCIENCE_TECHNICAL.equals(reason.getSubjectGroup())
                                ? R.string.eligibility_reason_science_technical
                                : R.string.eligibility_reason_science_technical_vocational,
                        reason.getGrade());
            case UNPUBLISHED:
                return context.getString(R.string.eligibility_reason_unpublished);
            case NO_RULE:
            default:
                return context.getString(R.string.eligibility_reason_no_rule);
        }
    }

    private static String join(Context context, EligibilityReason reason, int separatorRes) {
        return TextUtils.join(context.getString(separatorRes), reason.getSubjects());
    }
}
