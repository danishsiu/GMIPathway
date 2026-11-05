package com.week14.gmipathway.ui.campus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.week14.gmipathway.R;
import com.week14.gmipathway.data.CampusCatalog;
import com.week14.gmipathway.model.CampusPhoto;
import com.week14.gmipathway.model.Facility;
import com.week14.gmipathway.ui.common.BaseActivity;

import java.util.List;

/**
 * Shows one facility's photos full screen. Swipe left or right to move between them.
 */
public class PhotoViewerActivity extends BaseActivity {

    private static final String EXTRA_FACILITY = "facility";
    private static final String EXTRA_POSITION = "position";

    private List<CampusPhoto> photos;
    private TextView caption;
    private TextView counter;

    public static Intent newIntent(Context context, int facilityIndex, int position) {
        return new Intent(context, PhotoViewerActivity.class)
                .putExtra(EXTRA_FACILITY, facilityIndex)
                .putExtra(EXTRA_POSITION, position);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        List<Facility> facilities = CampusCatalog.getFacilities();
        int facilityIndex = getIntent().getIntExtra(EXTRA_FACILITY, 0);
        if (facilityIndex < 0 || facilityIndex >= facilities.size()) {
            finish();
            return;
        }
        Facility facility = facilities.get(facilityIndex);
        setUpToolbar(facility.getNameRes());
        applyWindowInsets();

        photos = facility.getPhotos();
        caption = findViewById(R.id.photo_caption);
        counter = findViewById(R.id.photo_counter);
        ViewPager2 pager = findViewById(R.id.photo_pager);
        pager.setAdapter(new PhotoPageAdapter(photos));
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                showPage(position);
            }
        });
        // ViewPager2 restores its own page after rotation, so only set it the first time.
        if (savedInstanceState == null) {
            int start = getIntent().getIntExtra(EXTRA_POSITION, 0);
            pager.setCurrentItem(Math.max(0, Math.min(start, photos.size() - 1)), false);
        }
        // ViewPager2 reports the first page in the middle of laying out the screen, too late
        // for the counter to be measured. Filling both in now means they show from the start.
        showPage(pager.getCurrentItem());
    }

    private void showPage(int position) {
        caption.setText(photos.get(position).getCaptionRes());
        counter.setText(getString(R.string.photo_counter, position + 1, photos.size()));
    }

    private static class PhotoPageAdapter extends RecyclerView.Adapter<PhotoPageAdapter.PageHolder> {
        private final List<CampusPhoto> photos;

        PhotoPageAdapter(List<CampusPhoto> photos) {
            this.photos = photos;
        }

        @NonNull
        @Override
        public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_photo_page, parent, false);
            return new PageHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PageHolder holder, int position) {
            CampusPhoto photo = photos.get(position);
            holder.image.setImageResource(photo.getImageRes());
            holder.image.setContentDescription(
                    holder.image.getContext().getString(photo.getCaptionRes()));
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }

        static class PageHolder extends RecyclerView.ViewHolder {
            final ImageView image;

            PageHolder(@NonNull View itemView) {
                super(itemView);
                image = (ImageView) itemView;
            }
        }
    }
}
