package com.example.android.popularmovies;

import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieAddtionalInfoTask;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieGalleryTask;
import com.squareup.picasso.Picasso;

public class MovieDetailInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
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
    private Uri currentUri;
    public static String[] results;

    public static MovieDetailInfoFragment create (Uri uri) {
        MovieDetailInfoFragment fragment = new MovieDetailInfoFragment();
        fragment.currentUri = uri;
        return fragment;
    }
    public MovieDetailInfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        detailViewToolBar = (Toolbar) rootView.findViewById(R.id.movie_detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(detailViewToolBar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("Movie Detail");
        return rootView;
    }

    public void fetchAdditionalMovieData(int movieIdForQuery, TextView genreView, TextView runtimeView) {
        FetchMovieAddtionalInfoTask task = new FetchMovieAddtionalInfoTask(genreView, runtimeView);
        task.execute(Integer.toString(movieIdForQuery));
    }

    public void fetchMovieGallery(int movieIdForQuery) {
        FetchMovieGalleryTask galleryTask = new FetchMovieGalleryTask(
                getActivity(), (ViewPager) getView().findViewById(R.id.pager));
        galleryTask.execute(Integer.toString(movieIdForQuery));
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),currentUri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int mid = data.getInt(
                    data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MID));
            // query the runtime and genre
            TextView genreView = (TextView)getView().findViewById(R.id.movie_genre);
            TextView runtimeView = (TextView)getView().findViewById(R.id.movie_runtime);
            fetchAdditionalMovieData(mid, genreView, runtimeView);

            //query the image gallary
            fetchMovieGallery(mid);

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