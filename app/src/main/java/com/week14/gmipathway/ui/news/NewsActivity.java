package com.week14.gmipathway.ui.news;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.ImageLoader;
import com.week14.gmipathway.data.NewsRepository;
import com.week14.gmipathway.ui.common.BaseActivity;
import com.week14.gmipathway.ui.common.ExternalLinks;

import java.util.Locale;

/**
 * GMI's latest news, downloaded from gmi.edu.my. Unlike the rest of the app this needs the
 * internet, so it shows the last download when offline, or a message if there is none.
 */
public class NewsActivity extends BaseActivity {

    private NewsViewModel viewModel;
    private NewsAdapter adapter;
    private RecyclerView list;
    private View progress;
    private View error;
    private TextView offlineBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setUpToolbar(R.string.news_title);
        applyWindowInsets();

        list = findViewById(R.id.news_list);
        progress = findViewById(R.id.news_progress);
        error = findViewById(R.id.news_error);
        offlineBanner = findViewById(R.id.news_offline_banner);

        Locale locale = Locale.forLanguageTag(getString(R.string.language_tag));
        adapter = new NewsAdapter(ImageLoader.getInstance(this), locale,
                item -> ExternalLinks.openWebPage(this, item.getLink()));
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        viewModel.getState().observe(this, this::render);
        findViewById(R.id.news_retry).setOnClickListener(view -> viewModel.refresh());
        viewModel.loadIfNeeded();
    }

    private void render(NewsViewModel.State state) {
        NewsRepository.Result result = state.result;
        boolean hasNews = result != null && !result.items.isEmpty();
        if (hasNews) {
            adapter.submit(result.items);
        }
        list.setVisibility(hasNews ? View.VISIBLE : View.GONE);
        progress.setVisibility(state.loading ? View.VISIBLE : View.GONE);
        error.setVisibility(!state.loading && !hasNews ? View.VISIBLE : View.GONE);

        boolean showBanner = hasNews && result.fromCache && !state.loading;
        offlineBanner.setVisibility(showBanner ? View.VISIBLE : View.GONE);
        if (showBanner) {
            long now = System.currentTimeMillis();
            // Under a minute, Android would say "0 minutes ago".
            CharSequence when = now - result.savedAt < DateUtils.MINUTE_IN_MILLIS
                    ? getString(R.string.news_saved_just_now)
                    : DateUtils.getRelativeTimeSpanString(result.savedAt, now,
                    DateUtils.MINUTE_IN_MILLIS);
            offlineBanner.setText(getString(R.string.news_offline_banner, when));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            viewModel.refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
