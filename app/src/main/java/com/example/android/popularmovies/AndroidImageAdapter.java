package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by qlzh727 on 12/13/15.
 */
public class AndroidImageAdapter extends ArrayAdapter<Movie> {
    public AndroidImageAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie currentMovie = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.image_item_view, parent, false);
        ImageView movieImageView = (ImageView) rootView.findViewById(R.id.movie_poster_item);
        movieImageView.setImageResource(currentMovie.imageResourceId);
        return rootView;
    }
}
