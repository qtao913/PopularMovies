package com.example.android.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

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
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
        private static final int DETAIL_LOADER = 0;
        private Toolbar detailViewToolBar;
        private CollapsingToolbarLayout collapsingToolbarLayout;
        int[] mResources = {
                R.drawable.first,
                R.drawable.second,
                R.drawable.third
        };
        private CustomPagerAdapter mCustomPagerAdapter;
        private ViewPager mViewPager;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
            detailViewToolBar = (Toolbar) rootView.findViewById(R.id.movie_detail_toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(detailViewToolBar);

            //loading image in the tool bar
            mCustomPagerAdapter = new CustomPagerAdapter(getActivity(), mResources);
            mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
            mViewPager.setAdapter(mCustomPagerAdapter);

            collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsingToolbarLayout);
            collapsingToolbarLayout.setTitle("Movie Detail");
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Intent intent = getActivity().getIntent();
            if (intent == null)
                return null;
            return new CursorLoader(getActivity(),intent.getData(),null,null,null,null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.moveToFirst()) {
                //Log.v("Count database", Integer.toString(data.getCount()));
                TextView titleView = (TextView)getView().findViewById(R.id.movie_title);
                String title = data.getString(
                        data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                String releaseDate = data.getString(
                        data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE));
                String year = releaseDate.split("-")[0];

                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(
                        title + " (" + year + ") ");
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                stringBuilder.setSpan(new StyleSpan(Typeface.NORMAL), 0, year.length(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                titleView.setText(stringBuilder);

                // Hardcode genre and runtime at this moment
                TextView genreView = (TextView)getView().findViewById(R.id.movie_genre);
                genreView.setText("Test: Action | Comedy");

                TextView runtimeView = (TextView)getView().findViewById(R.id.movie_runtime);
                runtimeView.setText("Test: 120min");

                TextView synopsisView = (TextView) getView().findViewById(R.id.movie_synopsis);
                synopsisView.setText(data.getString(
                        data.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS)));
                
                ImageView posterView = (ImageView) getView().findViewById(R.id.movie_poster);
                ImageView imageToolBar = (ImageView)getView().findViewById(R.id.image_toolbar);
                int index = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL);
                if (data.getString(index) == null) {
                    Picasso.with(getActivity()).load(R.drawable.image_place_holder).resize(480,640)
                    .into(posterView);
//                    Picasso.with(getActivity()).load(R.drawable.image_place_holder)
//                            .into(imageToolBar);
                } else {
                    Picasso.with(getActivity()).load(data.getString(index)).into(posterView);
//                    Picasso.with(getActivity()).load(data.getString(index)).into(imageToolBar);
                }

                TextView ratingView = (TextView) getView().findViewById(R.id.movie_rating);
                ratingView.setText(Double.toString(data.getDouble(
                        data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING))));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
