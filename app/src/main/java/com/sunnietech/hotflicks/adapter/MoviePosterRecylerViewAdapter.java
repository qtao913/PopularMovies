package com.sunnietech.hotflicks.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sunnietech.hotflicks.R;
import com.sunnietech.hotflicks.fragment.MoviePosterMainFragment;

/**
 * Created by qlzh727 on 4/10/16.
 */
public class MoviePosterRecylerViewAdapter extends RecyclerView.Adapter<MoviePosterRecylerViewAdapter.ViewHolder> {
    private Cursor cursor;
    private Activity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.movie_poster_item);
        }
    }

    public MoviePosterRecylerViewAdapter(Cursor cursor, Activity activity) {
        this.cursor = cursor;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item_view, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String imagePath = cursor.getString(MoviePosterMainFragment.COLUMN_IMAGE_URL);
        if (imagePath == null) {
            Glide.with(activity).load(R.drawable.image_place_holder).centerCrop().into(holder.imageView);
        } else {
            Glide.with(activity).load(imagePath).centerCrop().into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null)
            count = cursor.getCount();
        return count;
    }
}
