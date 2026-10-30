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
import com.week14.gmipathway.ui.common.DetailViews;

import java.util.List;

/**
 * Shows each programme's result as a card, with the action that suits its status.
 */
class OutcomeAdapter extends RecyclerView.Adapter<OutcomeAdapter.OutcomeViewHolder> {

    interface Listener {
        void onCourseClick(Course course);

        void onApplyClick();

        void onContactClick();
    }

    private final List<EligibilityOutcome> outcomes;
    private final Listener listener;

    OutcomeAdapter(List<EligibilityOutcome> outcomes, Listener listener) {
        this.outcomes = outcomes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OutcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_eligibility_outcome, parent, false);
        return new OutcomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutcomeViewHolder holder, int position) {
        EligibilityOutcome outcome = outcomes.get(position);
        Context context = holder.itemView.getContext();
        holder.name.setText(outcome.getCourse().getName());
        holder.reasons.setText(DetailViews.bulletList(context, outcome.getReasons()));
        holder.reasons.setVisibility(outcome.getReasons().isEmpty() ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(view -> listener.onCourseClick(outcome.getCourse()));

        switch (outcome.getStatus()) {
            case ELIGIBLE:
                holder.showStatus(R.drawable.ic_check_circle, R.string.eligibility_status_eligible,
                        androidx.appcompat.R.attr.colorPrimary);
                holder.action.setVisibility(View.VISIBLE);
                holder.action.setText(R.string.apply_now);
                holder.action.setOnClickListener(view -> listener.onApplyClick());
                break;
            case NOT_ELIGIBLE:
                holder.showStatus(R.drawable.ic_cancel, R.string.eligibility_status_not_eligible,
                        androidx.appcompat.R.attr.colorError);
                holder.action.setVisibility(View.GONE);
                break;
            case CONTACT_GMI:
            default:
                holder.showStatus(R.drawable.ic_help, R.string.eligibility_status_contact,
                        com.google.android.material.R.attr.colorOnSurfaceVariant);
                holder.action.setVisibility(View.VISIBLE);
                holder.action.setText(R.string.eligibility_guided_button);
                holder.action.setOnClickListener(view -> listener.onContactClick());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return outcomes.size();
    }

    static class OutcomeViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView statusIcon;
        final TextView status;
        final TextView reasons;
        final Button action;

        OutcomeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.outcome_name);
            statusIcon = itemView.findViewById(R.id.outcome_status_icon);
            status = itemView.findViewById(R.id.outcome_status);
            reasons = itemView.findViewById(R.id.outcome_reasons);
            action = itemView.findViewById(R.id.outcome_action);
        }

        void showStatus(@DrawableRes int iconRes, @StringRes int textRes, @AttrRes int colorAttr) {
            int color = MaterialColors.getColor(itemView, colorAttr);
            statusIcon.setImageResource(iconRes);
            statusIcon.setImageTintList(ColorStateList.valueOf(color));
            status.setText(textRes);
            status.setTextColor(color);
        }
    }
}
