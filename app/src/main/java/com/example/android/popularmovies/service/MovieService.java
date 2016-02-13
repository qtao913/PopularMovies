package com.example.android.popularmovies.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.Utility;
import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MovieService extends IntentService {
    private final String LOG_TAG = MovieService.class.getSimpleName();
    public static final String MOIVE_QUERY_CRITERIA = "mqc";
    private final String FIRST_PAGE = "1";
    public MovieService() {
        super("PopularMovies");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String queryCriteria = intent.getStringExtra(MOIVE_QUERY_CRITERIA);
        final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
        final String SORT_PARAM = "sort_by";
        final String APIID_PARAM = "api_key";
        final String PAGE_PARAM = "page";

        deleteDatabaseContent();
        String page = FIRST_PAGE;
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_PARAM, queryCriteria)
                .appendQueryParameter(APIID_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();
        String rawJsonData = Utility.fetchRawJson(buildUri);
        try {
            getMovieDataFromJson(rawJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getMovieDataFromJson(String movieJsonStr) throws JSONException {
        // Movie General Information
        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_SYNOPSIS = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE = "release_date";
        final String IMAGE_PATH = "poster_path";
        final String IMAGE_PATH_BASE = "http://image.tmdb.org/t/p/";
        final String DEFAULT_SIZE = "w185/";
        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);
            ContentValues[] moviesForDatabase = new ContentValues[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);
                int id = movieObject.getInt(MOVIE_ID);
                String title = movieObject.getString(MOVIE_TITLE);
                String imagePath = movieObject.getString(IMAGE_PATH);
                imagePath = imagePath.equals("null") ? null : IMAGE_PATH_BASE + DEFAULT_SIZE + imagePath;
                String synopsis = movieObject.getString(MOVIE_SYNOPSIS);
                double rating = movieObject.getDouble(MOVIE_RATING);
                String releaseDate = movieObject.getString(MOVIE_RELEASE);
                // build up the content value for database insertion
                ContentValues movieItem = new ContentValues();
                movieItem.put(MovieContract.MovieEntry.COLUMN_MID, id);
                movieItem.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieItem.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);
                movieItem.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
                movieItem.put(MovieContract.MovieEntry.COLUMN_RELEASE, releaseDate);
                movieItem.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, imagePath);
                movieItem.put(MovieContract.MovieEntry.COLUMN_RANK, i);
                moviesForDatabase[i] = movieItem;
            }
            int rowInserted = 0;
            if (moviesForDatabase.length > 0) {
                rowInserted = getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, moviesForDatabase);
                //Log.v(LOG_TAG, "Bulk insert: "+rowInserted);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void deleteDatabaseContent() {
        getContentResolver().delete(
            MovieContract.MovieEntry.CONTENT_URI,
            null,
            null
        );
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, MovieService.class);
            sendIntent.putExtra(MovieService.MOIVE_QUERY_CRITERIA,
                    intent.getStringExtra(MovieService.MOIVE_QUERY_CRITERIA));
            context.startService(intent);
        }
    }
}
