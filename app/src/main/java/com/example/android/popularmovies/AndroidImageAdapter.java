package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by qlzh727 on 12/13/15.
 */
public class AndroidImageAdapter extends ArrayAdapter<Movie> {
    private final Activity currentActivity;

    static class ViewHolder {
        public ImageView movieItemView;
    }

    public AndroidImageAdapter(Activity currentActivity, List<Movie> movies) {
        super(currentActivity, 0, movies);
        this.currentActivity = currentActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = currentActivity.getLayoutInflater();
            itemView = inflater.inflate(R.layout.image_item_view, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.movieItemView = (ImageView)itemView.findViewById(R.id.movie_poster_item);
            itemView.setTag(viewHolder);
        }

        // fill data
        ViewHolder currentHolder = (ViewHolder)itemView.getTag();
        Movie currentMovie = getItem(position);
        //currentHolder.movieItemView.setImageResource(currentMovie.imageResourceId);
        Picasso.with(currentActivity).setDebugging(true);
        Picasso.with(currentActivity).load(currentMovie.imagePath).into(currentHolder.movieItemView);
        return itemView;
    }
}
