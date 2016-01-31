package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
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
import com.example.android.popularmovies.fetchRawJSON.FetchMovieCastTask;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieGalleryTask;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieTrailerTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
public class MovieDetailInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DETAIL_LOADER = 0;
    private Toolbar detailViewToolBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Uri currentUri;

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


    public void fetchAdditionalMovieData(int movieIdForQuery) {
        FetchMovieAddtionalInfoTask task = new FetchMovieAddtionalInfoTask(getView());
        task.execute(Integer.toString(movieIdForQuery));
    }

    public void fetchMovieGallery(int movieIdForQuery) {
        FetchMovieGalleryTask galleryTask = new FetchMovieGalleryTask(
                getActivity(), (ViewPager) getView().findViewById(R.id.pager));
        galleryTask.execute(Integer.toString(movieIdForQuery));
    }

    public void fetchMovieCastTask(int movieIdForQuery) {
        FetchMovieCastTask castTask = new FetchMovieCastTask(
                getView(), (ViewGroup) getView().getParent(), getActivity());
        castTask.execute(Integer.toString(movieIdForQuery));
    }

    public void fetchMovieTrailerTask(int movieIdForQuery) {
        FetchMovieTrailerTask trailerTask = new FetchMovieTrailerTask(
                getView(), (ViewGroup) getView().getParent(), getActivity());
        trailerTask.execute(Integer.toString(movieIdForQuery));
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
            final int mid = data.getInt(
                    data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MID));
            // query the runtime and genre
            fetchAdditionalMovieData(mid) ;

            //query the image gallary
            fetchMovieGallery(mid);

            //query the cast portrait
            fetchMovieCastTask(mid);

            //query movie trailer
            fetchMovieTrailerTask(mid);

            final TextView titleView = (TextView)getView().findViewById(R.id.movie_title);
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

            TextView ratingView = (TextView) getView().findViewById(R.id.movie_rating);
            String rating = Double.toString(data.getDouble(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
            String fullMark = getString(R.string.rating_criteria);
            ratingView.setText(String.format(fullMark, rating));

            TextView releaseView = (TextView)getView().findViewById(R.id.movie_release);
            releaseView.setText(data.getString(
                    data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE)));

            final TextView reviewButton = (TextView)getView().findViewById(R.id.review_view);
            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MovieReviewActivity.class);
                    intent.putExtra(getString(R.string.movie_id), mid);
                    startActivity(intent);
                }
            });

            final ImageView posterView = (ImageView) getView().findViewById(R.id.movie_poster);
            ImageView imageToolBar = (ImageView)getView().findViewById(R.id.image_toolbar);
            int index = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL);
            if (data.getString(index) == null) {
                Picasso.with(getActivity()).load(R.drawable.image_place_holder).resize(480,640)
                        .into(posterView);
            } else {
                Picasso.with(getActivity()).load(data.getString(index)).into(posterView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) posterView.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                Palette.Swatch mutedSwatch = p.getMutedSwatch();
                                if (mutedSwatch != null) {
                                    final int backgroundColor = mutedSwatch.getRgb();
                                    final int textColor = mutedSwatch.getTitleTextColor();
                                    titleView.setBackgroundColor(backgroundColor);
                                    titleView.setTextColor(textColor);
                                    android.support.design.widget.CollapsingToolbarLayout collapsingToolbar
                                            = (android.support.design.widget.CollapsingToolbarLayout)getView().findViewById(R.id.collapsingToolbarLayout);
                                    collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
                                    collapsingToolbarLayout.setContentScrimColor(backgroundColor);

                                    reviewButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), MovieReviewActivity.class);
                                            intent.putExtra(getString(R.string.movie_id), mid);
                                            intent.putExtra(getString(R.string.background_color), backgroundColor);
                                            intent.putExtra(getString(R.string.text_color), textColor);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onError() {
                    }
                });
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}