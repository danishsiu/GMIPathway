package com.week14.gmipathway.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;

import java.util.List;

/**
 * The sideways row of highlight cards on the home screen.
 * Each highlight has a photo, a short label, a headline, a line of detail and an action.
 */
class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.HighlightHolder> {

    /** One highlight. The action runs when the card is tapped. */
    static final class Highlight {
        @DrawableRes
        final int imageRes;
        @StringRes
        final int labelRes;
        @StringRes
        final int titleRes;
        @StringRes
        final int bodyRes;
        final Runnable action;

        Highlight(@DrawableRes int imageRes, @StringRes int labelRes, @StringRes int titleRes,
                  @StringRes int bodyRes, Runnable action) {
            this.imageRes = imageRes;
            this.labelRes = labelRes;
            this.titleRes = titleRes;
            this.bodyRes = bodyRes;
            this.action = action;
        }
    }

    private final List<Highlight> highlights;

    HighlightAdapter(List<Highlight> highlights) {
        this.highlights = highlights;
    }

    @NonNull
    @Override
    public HighlightHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_highlight, parent, false);
        return new HighlightHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HighlightHolder holder, int position) {
        Highlight highlight = highlights.get(position);
        holder.image.setImageResource(highlight.imageRes);
        holder.label.setText(highlight.labelRes);
        holder.title.setText(highlight.titleRes);
        holder.body.setText(highlight.bodyRes);
        holder.itemView.setOnClickListener(view -> highlight.action.run());
    }

    @Override
    public int getItemCount() {
        return highlights.size();
    }

    static class HighlightHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView label;
        final TextView title;
        final TextView body;

        HighlightHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.highlight_image);
            label = itemView.findViewById(R.id.highlight_label);
            title = itemView.findViewById(R.id.highlight_title);
            body = itemView.findViewById(R.id.highlight_body);
        }
    }
}
