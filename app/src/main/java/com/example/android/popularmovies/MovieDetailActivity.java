package com.example.android.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;


public class MovieDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
            ImageView movieDetailView = (ImageView) rootView.findViewById(R.id.movie_detail);
            String movieDetail = getActivity().getIntent().getDataString();
            Uri buildUri = Uri.parse(movieDetail);
            Cursor c = getActivity().getContentResolver().query(
                    buildUri,
                    null,
                    null,
                    null,
                    null
            );
            if (c.moveToFirst()) {
                Log.v("Count database", Integer.toString(c.getCount()));
                int index = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL);
                if (c.getString(index) == null) {
                    Picasso.with(getActivity()).load(R.drawable.image_place_holder).resize(185, 252).into(movieDetailView);
                } else {
                    Picasso.with(getActivity()).load(c.getString(index)).into(movieDetailView);
                }
            }

            return rootView;
        }
    }
}
