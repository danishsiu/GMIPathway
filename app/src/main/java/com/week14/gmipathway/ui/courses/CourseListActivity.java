package com.week14.gmipathway.ui.courses;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.week14.gmipathway.R;
import com.week14.gmipathway.ui.common.BaseActivity;

/**
 * Course list with one swipeable tab each for Diploma, GAPP and GUFP.
 */
public class CourseListActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        setUpToolbar(R.string.courses_title);
        applyWindowInsets();

        ViewPager2 pager = findViewById(R.id.pager);
        TabLayout tabs = findViewById(R.id.tabs);
        pager.setAdapter(new CoursePagerAdapter(this));
        new TabLayoutMediator(tabs, pager, (tab, position) ->
                tab.setText(CoursePagerAdapter.getTitleRes(position))).attach();
    }
}
