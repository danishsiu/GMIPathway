package com.week14.gmipathway.ui.courses;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.week14.gmipathway.R;
import com.week14.gmipathway.model.ProgrammeCategory;

/**
 * Supplies one course list page per programme category.
 */
class CoursePagerAdapter extends FragmentStateAdapter {
    private static final ProgrammeCategory[] CATEGORIES = ProgrammeCategory.values();

    CoursePagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CourseListFragment.newInstance(CATEGORIES[position]);
    }

    @Override
    public int getItemCount() {
        return CATEGORIES.length;
    }

    @StringRes
    static int getTitleRes(int position) {
        switch (CATEGORIES[position]) {
            case GAPP:
                return R.string.tab_gapp;
            case GUFP:
                return R.string.tab_gufp;
            case DIPLOMA:
            default:
                return R.string.tab_diploma;
        }
    }
}
