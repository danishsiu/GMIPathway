package com.week14.gmipathway.ui.eligibility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.week14.gmipathway.R;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.model.EligibilityOutcome;
import com.week14.gmipathway.model.EligibilityReason;
import com.week14.gmipathway.model.EligibilityStatus;
import com.week14.gmipathway.ui.common.DetailViews;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists results in collapsible sections by status, with the action that suits each one.
 */
class OutcomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_OUTCOME = 1;
    private static final float CHEVRON_EXPANDED = 180f;
    private static final float CHEVRON_COLLAPSED = 0f;

    private static final EligibilityStatus[] DISPLAY_ORDER = {
            EligibilityStatus.ELIGIBLE,
            EligibilityStatus.CONTACT_GMI,
            EligibilityStatus.NOT_ELIGIBLE,
            EligibilityStatus.NOT_ASSESSED
    };

    interface Listener {
        void onCourseClick(Course course);

        void onApplyClick();

        void onContactClick();
    }

    /** One status group and whether its cards are showing. */
    private static final class Section {
        final EligibilityStatus status;
        final List<EligibilityOutcome> outcomes;
        boolean expanded;

        Section(EligibilityStatus status, List<EligibilityOutcome> outcomes) {
            this.status = status;
            this.outcomes = outcomes;
            // "Not assessed" says little, so it starts closed to keep the useful results in view.
            this.expanded = status != EligibilityStatus.NOT_ASSESSED;
        }
    }

    private final List<Section> sections = new ArrayList<>();
    /** Holds a Section or an EligibilityOutcome, in display order. */
    private final List<Object> rows = new ArrayList<>();
    private final Listener listener;

    OutcomeAdapter(List<EligibilityOutcome> outcomes, Listener listener) {
        this.listener = listener;
        for (EligibilityStatus status : DISPLAY_ORDER) {
            List<EligibilityOutcome> matches = new ArrayList<>();
            for (EligibilityOutcome outcome : outcomes) {
                if (outcome.getStatus() == status) {
                    matches.add(outcome);
                }
            }
            if (!matches.isEmpty()) {
                sections.add(new Section(status, matches));
            }
        }
        rebuildRows();
    }

    private void rebuildRows() {
        rows.clear();
        for (Section section : sections) {
            rows.add(section);
            if (section.expanded) {
                rows.addAll(section.outcomes);
            }
        }
    }

    private void toggle(Section section) {
        int headerPosition = rows.indexOf(section);
        section.expanded = !section.expanded;
        rebuildRows();
        notifyItemChanged(headerPosition);
        if (section.expanded) {
            notifyItemRangeInserted(headerPosition + 1, section.outcomes.size());
        } else {
            notifyItemRangeRemoved(headerPosition + 1, section.outcomes.size());
        }
    }

    @StringRes
    private static int getSectionTitle(EligibilityStatus status) {
        switch (status) {
            case ELIGIBLE:
                return R.string.eligibility_section_eligible;
            case CONTACT_GMI:
                return R.string.eligibility_section_contact;
            case NOT_ELIGIBLE:
                return R.string.eligibility_section_not_eligible;
            case NOT_ASSESSED:
            default:
                return R.string.eligibility_section_not_assessed;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position) instanceof EligibilityOutcome ? TYPE_OUTCOME : TYPE_HEADER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(
                    inflater.inflate(R.layout.item_section_header, parent, false));
        }
        return new OutcomeViewHolder(
                inflater.inflate(R.layout.item_eligibility_outcome, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object row = rows.get(position);
        if (holder instanceof HeaderViewHolder) {
            bindSection((HeaderViewHolder) holder, (Section) row);
            return;
        }
        bindOutcome((OutcomeViewHolder) holder, (EligibilityOutcome) row);
    }

    private void bindSection(HeaderViewHolder holder, Section section) {
        Context context = holder.itemView.getContext();
        holder.title.setText(context.getString(R.string.eligibility_section_title,
                context.getString(getSectionTitle(section.status)), section.outcomes.size()));
        holder.chevron.setRotation(section.expanded ? CHEVRON_EXPANDED : CHEVRON_COLLAPSED);
        DetailViews.updateExpandedState(holder.itemView, section.expanded);
        holder.itemView.setOnClickListener(view -> toggle(section));
    }

    private void bindOutcome(OutcomeViewHolder holder, EligibilityOutcome outcome) {
        Context context = holder.itemView.getContext();
        Course course = outcome.getCourse();
        holder.department.setText(course.getDepartment());
        holder.name.setText(course.getName());
        holder.itemView.setOnClickListener(view -> listener.onCourseClick(course));

        List<String> lines = new ArrayList<>();
        for (EligibilityReason reason : outcome.getReasons()) {
            lines.add(ReasonFormatter.format(context, reason));
        }
        lines.addAll(outcome.getNotes());
        holder.details.setText(DetailViews.bulletList(context, lines));
        holder.details.setVisibility(lines.isEmpty() ? View.GONE : View.VISIBLE);

        switch (outcome.getStatus()) {
            case ELIGIBLE:
                holder.showStatus(R.drawable.ic_check_circle, R.string.eligibility_status_eligible,
                        androidx.appcompat.R.attr.colorPrimary);
                holder.showAction(R.string.apply_now, view -> listener.onApplyClick());
                break;
            case CONTACT_GMI:
                holder.showStatus(R.drawable.ic_help, R.string.eligibility_status_contact,
                        com.google.android.material.R.attr.colorTertiary);
                holder.showAction(R.string.eligibility_guided_button,
                        view -> listener.onContactClick());
                break;
            case NOT_ELIGIBLE:
                holder.showStatus(R.drawable.ic_cancel, R.string.eligibility_status_not_eligible,
                        androidx.appcompat.R.attr.colorError);
                holder.action.setVisibility(View.GONE);
                break;
            case NOT_ASSESSED:
            default:
                holder.showStatus(R.drawable.ic_help, R.string.eligibility_status_not_assessed,
                        com.google.android.material.R.attr.colorOnSurfaceVariant);
                holder.action.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView chevron;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.header_title);
            chevron = itemView.findViewById(R.id.header_chevron);
            itemView.findViewById(R.id.header_icon).setVisibility(View.GONE);
            itemView.findViewById(R.id.header_count).setVisibility(View.GONE);
            chevron.setVisibility(View.VISIBLE);
            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }

    static class OutcomeViewHolder extends RecyclerView.ViewHolder {
        final TextView department;
        final TextView name;
        final ImageView statusIcon;
        final TextView status;
        final TextView details;
        final Button action;

        OutcomeViewHolder(@NonNull View itemView) {
            super(itemView);
            department = itemView.findViewById(R.id.outcome_department);
            name = itemView.findViewById(R.id.outcome_name);
            statusIcon = itemView.findViewById(R.id.outcome_status_icon);
            status = itemView.findViewById(R.id.outcome_status);
            details = itemView.findViewById(R.id.outcome_reasons);
            action = itemView.findViewById(R.id.outcome_action);
        }

        void showStatus(@DrawableRes int iconRes, @StringRes int textRes, @AttrRes int colorAttr) {
            int color = MaterialColors.getColor(itemView, colorAttr);
            statusIcon.setImageResource(iconRes);
            statusIcon.setImageTintList(ColorStateList.valueOf(color));
            status.setText(textRes);
            status.setTextColor(color);
        }

        void showAction(@StringRes int textRes, View.OnClickListener onClick) {
            action.setVisibility(View.VISIBLE);
            action.setText(textRes);
            action.setOnClickListener(onClick);
        }
    }
}
