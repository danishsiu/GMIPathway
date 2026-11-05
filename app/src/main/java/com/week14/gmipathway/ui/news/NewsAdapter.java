package com.week14.gmipathway.ui.news;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.ImageLoader;
import com.week14.gmipathway.model.NewsItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * News cards: photo, date, headline and the opening lines. Tapping a card opens the article.
 */
class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    interface OnNewsClickListener {
        void onNewsClick(NewsItem item);
    }

    private final List<NewsItem> items = new ArrayList<>();
    private final OnNewsClickListener listener;
    private final ImageLoader imageLoader;
    private final DateFormat displayDate;

    NewsAdapter(ImageLoader imageLoader, Locale locale, OnNewsClickListener listener) {
        this.imageLoader = imageLoader;
        this.listener = listener;
        displayDate = DateFormat.getDateInstance(DateFormat.LONG, locale);
    }

    /** Each download replaces the whole list, so there are no finer changes to report. */
    @SuppressLint("NotifyDataSetChanged")
    void submit(List<NewsItem> news) {
        items.clear();
        items.addAll(news);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        NewsItem item = items.get(position);
        String title = toPlainText(item.getTitleHtml());
        String excerpt = toPlainText(item.getExcerptHtml());
        String date = formatDate(item.getDate());

        holder.title.setText(title);
        holder.date.setText(date);
        holder.date.setVisibility(date.isEmpty() ? View.GONE : View.VISIBLE);
        holder.excerpt.setText(excerpt);
        holder.excerpt.setVisibility(excerpt.isEmpty() ? View.GONE : View.VISIBLE);
        holder.image.setVisibility(item.getImageUrl() == null ? View.GONE : View.VISIBLE);
        imageLoader.load(holder.image, item.getImageUrl());

        holder.itemView.setContentDescription(holder.itemView.getContext()
                .getString(R.string.news_item_description, title, date));
        holder.itemView.setOnClickListener(view -> listener.onNewsClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** GMI's text contains HTML tags and entities such as &#8217;. */
    private static String toPlainText(String html) {
        return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                .replace('\n', ' ').replaceAll("\\s+", " ").trim();
    }

    private String formatDate(String raw) {
        if (raw.length() < 10) {
            return "";
        }
        try {
            SimpleDateFormat feedFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date parsed = feedFormat.parse(raw.substring(0, 10));
            return parsed == null ? "" : displayDate.format(parsed);
        } catch (ParseException e) {
            return "";
        }
    }

    static class NewsHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView date;
        final TextView title;
        final TextView excerpt;

        NewsHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.news_image);
            date = itemView.findViewById(R.id.news_date);
            title = itemView.findViewById(R.id.news_title);
            excerpt = itemView.findViewById(R.id.news_excerpt);
        }
    }
}
