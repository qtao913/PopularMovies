package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by qlzh727 on 12/13/15.
 */
public class AndroidImageAdapter extends CursorAdapter {

    public AndroidImageAdapter(Activity currentActivity, Cursor cursor, int flags) {
        super(currentActivity, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item_view, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView)view;
        String imagePath = cursor.getString(MoviePosterMainFragment.COLUMN_IMAGE_URL);
        if (imagePath == null) {
            Picasso.with(context).load(R.drawable.image_place_holder).fit().into(imageView);
        } else {
            Picasso.with(context).load(imagePath).fit().into(imageView);
        }
    }
}
