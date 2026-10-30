package com.week14.gmipathway.ui.courses;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.week14.gmipathway.R;
import com.week14.gmipathway.data.BookmarkStore;
import com.week14.gmipathway.data.CourseRepository;
import com.week14.gmipathway.logic.CourseSearch;
import com.week14.gmipathway.model.Course;
import com.week14.gmipathway.ui.common.BaseActivity;

import java.util.List;

/**
 * Course list with one swipeable tab each for Diploma, Pre-University and Degree.
 * Searching replaces the tabs with one list of matches from every category.
 */
public class CourseListActivity extends BaseActivity {
    private static final String STATE_QUERY = "query";

    private TabLayout tabs;
    private ViewPager2 pager;
    private RecyclerView searchResults;
    private TextView searchEmpty;
    private String query = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        setUpToolbar(R.string.courses_title);
        applyWindowInsets();

        pager = findViewById(R.id.pager);
        tabs = findViewById(R.id.tabs);
        searchResults = findViewById(R.id.search_results);
        searchEmpty = findViewById(R.id.search_empty);
        pager.setAdapter(new CoursePagerAdapter(this));
        new TabLayoutMediator(tabs, pager, (tab, position) ->
                tab.setText(CoursePagerAdapter.getTitleRes(position))).attach();

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(STATE_QUERY, "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.courses_search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        styleSearchText(searchView);

        // Runs before the listener is added, so restoring a search does not clear it.
        if (!query.isEmpty()) {
            String restoredQuery = query;
            searchItem.expandActionView();
            searchView.setQuery(restoredQuery, false);
            // Otherwise the keyboard reopens over the results after every rotation.
            searchView.clearFocus();
            showSearch(restoredQuery);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                showSearch(text);
                return true;
            }
        });
        return true;
    }

    /** The search box is built by AppCompat, so it needs the app's text style applied here. */
    private void styleSearchText(SearchView searchView) {
        TextView text = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        TextViewCompat.setTextAppearance(text, R.style.TextAppearance_GMIPathway_BodyLarge);
        text.setTextColor(ContextCompat.getColor(this, R.color.on_app_bar));
    }

    private void showSearch(String text) {
        query = text.trim();
        boolean searching = !query.isEmpty();
        tabs.setVisibility(searching ? View.GONE : View.VISIBLE);
        pager.setVisibility(searching ? View.GONE : View.VISIBLE);
        if (!searching) {
            searchResults.setVisibility(View.GONE);
            searchEmpty.setVisibility(View.GONE);
            return;
        }
        List<Course> matches =
                CourseSearch.search(CourseRepository.getInstance(this).getCourses(), query);
        searchResults.setVisibility(matches.isEmpty() ? View.GONE : View.VISIBLE);
        searchEmpty.setVisibility(matches.isEmpty() ? View.VISIBLE : View.GONE);
        searchEmpty.setText(getString(R.string.courses_search_empty, query));
        searchResults.setAdapter(new CourseAdapter(matches, new BookmarkStore(this),
                course -> startActivity(CourseDetailActivity.createIntent(this, course.getId()))));
    }

    /** A course may have been saved or removed elsewhere, so every row is refreshed. */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (searchResults.getAdapter() != null) {
            searchResults.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_QUERY, query);
    }
}
