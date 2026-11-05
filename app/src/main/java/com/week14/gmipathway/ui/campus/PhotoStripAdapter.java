package com.week14.gmipathway.ui.campus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.week14.gmipathway.R;
import com.week14.gmipathway.model.CampusPhoto;

import java.util.List;

/**
 * The sideways strip of photo thumbnails under each facility.
 */
class PhotoStripAdapter extends RecyclerView.Adapter<PhotoStripAdapter.PhotoHolder> {

    interface OnPhotoClickListener {
        void onPhotoClick(int position);
    }

    private final List<CampusPhoto> photos;
    private final OnPhotoClickListener listener;

    PhotoStripAdapter(List<CampusPhoto> photos, OnPhotoClickListener listener) {
        this.photos = photos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_campus_photo, parent, false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        CampusPhoto photo = photos.get(position);
        holder.image.setImageResource(photo.getImageRes());
        holder.itemView.setContentDescription(
                holder.itemView.getContext().getString(photo.getCaptionRes()));
        holder.itemView.setOnClickListener(view ->
                listener.onPhotoClick(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        PhotoHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photo_image);
        }
    }
}
