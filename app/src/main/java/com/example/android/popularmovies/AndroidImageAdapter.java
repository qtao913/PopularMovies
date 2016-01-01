package com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by qlzh727 on 12/13/15.
 */
public class AndroidImageAdapter extends CursorAdapter {
//    private final Activity currentActivity;
//
//    static class ViewHolder {
//        public ImageView movieItemView;
//    }

//    public AndroidImageAdapter(Activity currentActivity, List<Movie> movies) {
//        super(currentActivity, 0, movies);
//        this.currentActivity = currentActivity;
//    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AndroidImageAdapter(Activity currentActivity, Cursor cursor, int flags) {
        super(currentActivity, cursor, flags);
    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View itemView = convertView;
//        if (itemView == null) {
//            LayoutInflater inflater = currentActivity.getLayoutInflater();
//            itemView = inflater.inflate(R.layout.image_item_view, null);
//            // configure view holder
//            ViewHolder viewHolder = new ViewHolder();
//            viewHolder.movieItemView = (ImageView)itemView.findViewById(R.id.movie_poster_item);
//            itemView.setTag(viewHolder);
//        }
//
//        // fill data
//        ViewHolder currentHolder = (ViewHolder)itemView.getTag();
//        Movie currentMovie = getItem(position);
//        Picasso.with(currentActivity).setDebugging(true);
//        if (currentMovie.imagePath == null) {
//            Picasso.with(currentActivity).load(R.drawable.image_place_holder)
//                    .fit().into(currentHolder.movieItemView);
//        } else {
//            Picasso.with(currentActivity).load(currentMovie.imagePath)
//                    .fit().into(currentHolder.movieItemView);
//        }
//        return itemView;
//    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item_view, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView)view;
        int columnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL);
        String imagePath = cursor.getString(columnIndex);
        if (imagePath == null) {
            Picasso.with(context).load(R.drawable.image_place_holder).fit().into(imageView);
        } else {
            Picasso.with(context).load(imagePath).fit().into(imageView);
        }
    }
}
