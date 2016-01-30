package com.example.android.popularmovies;

import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieAddtionalInfoTask;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieCastTask;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieGalleryTask;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieTrailerTask;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;

public class MovieDetailInfoFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, YouTubeThumbnailView.OnInitializedListener {
    private static final int DETAIL_LOADER = 0;
    private static final String TEST_VIDEO_ID = "o7VVHhK9zf0";
    private Toolbar detailViewToolBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Uri currentUri;
    private LinearLayout mTrailers;
    private YouTubeThumbnailView[] trailerThumbnailViews;

    private String[] ids = new String[] {
            "xf8wVezS3JY",
            "BOVriTeIypQ",
            "7GqClqvlObY"
    };

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

        //test horizontal scroll view
        //setMovieTrailerView(rootView, container);
        return rootView;
    }

    private void setMovieTrailerView(View rootView, ViewGroup container) {
        mTrailers = (LinearLayout) rootView.findViewById(R.id.movie_trailers);

        trailerThumbnailViews = new YouTubeThumbnailView[ids.length];
        for (int i = 0; i < ids.length; i++) {
            View trailer = LayoutInflater.from(getActivity())
                    .inflate(R.layout.youtube_thumbnail_view, container, false);
            YouTubeThumbnailView testView = (YouTubeThumbnailView) trailer.findViewById(R.id.youtube_thumbnail_item);
            testView.initialize(BuildConfig.YOUTUBE_ANDROID_API_KEY, this);
            mTrailers.addView(trailer);
            trailerThumbnailViews[i] = testView;
        }
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
            TextView genreView = (TextView)getView().findViewById(R.id.movie_genre);
            TextView runtimeView = (TextView)getView().findViewById(R.id.movie_runtime);
            fetchAdditionalMovieData(mid, genreView, runtimeView);

            //query the image gallary
            fetchMovieGallery(mid);

            //query the cast portrait
            fetchMovieCastTask(mid);

            //query movie trailer
            fetchMovieTrailerTask(mid);

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

            TextView reviewButton = (TextView)getView().findViewById(R.id.review_view);
            reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MovieReviewActivity.class);
                    intent.putExtra("movie id", mid);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView,
                                        final YouTubeThumbnailLoader youTubeThumbnailLoader) {
        Log.v("", "Youtube init success");
        for (int i = 0; i < ids.length; i++) {
            if (this.trailerThumbnailViews[i] == youTubeThumbnailView) {
                final String viewPath = ids[i];
                youTubeThumbnailLoader.setVideo(viewPath);
                youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent playYoutube = new Intent(getActivity(), YouTubePlayerActivity.class);
//                        playYoutube.putExtra("youtube path", viewPath);
//                        startActivity(playYoutube);
                        Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),BuildConfig.YOUTUBE_ANDROID_API_KEY, viewPath, 0, true, false);
                        startActivity(intent);
                    }
                });
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        youTubeThumbnailLoader.release();
                        RelativeLayout parentView = (RelativeLayout)youTubeThumbnailView.getParent();
                        ImageView buttonView = (ImageView)parentView.findViewById(R.id.play_button);
                        buttonView.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        youTubeThumbnailLoader.release();
                    }
                });
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView,
                                        YouTubeInitializationResult youTubeInitializationResult) {
        Log.v("", "Youtube init failure");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}