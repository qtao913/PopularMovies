package com.sunnietech.hotflicks.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.sunnietech.hotflicks.BuildConfig;
import com.sunnietech.hotflicks.persistence.MovieContract.MovieEntry;
import com.sunnietech.hotflicks.utility.DownloadData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qlzh727 on 12/18/15.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isRefresh;
    private int currentItemCount;

    public FetchMovieTask(Context context, SwipeRefreshLayout swipeRefreshLayout, boolean isRefresh, int itemCount) {
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;
        this.isRefresh = isRefresh;
        currentItemCount = itemCount;
    }

    private void deleteDatabaseContent() {
        if (isRefresh) {
            // delete movies before adding new bulk data
            mContext.getContentResolver().delete(
                    MovieEntry.CONTENT_URI,
                    null,
                    null
            );
        }
    }

    // handle insertion of a new Movie record in the Movie database
    // return the row ID of the added movie item
    public long addMovie(int movieId, String title, String synopsis,
                  double rating, String releaseDate, String imageUrl) {
        long rowId;
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                new String[] {MovieEntry._ID},
                MovieEntry.COLUMN_MID + " = ? ",
                new String[] {Integer.toString(movieId)},
                null
        );
        if (movieCursor.moveToFirst()) {
            int rowIdIndex = movieCursor.getColumnIndex(MovieEntry._ID);
            rowId = movieCursor.getLong(rowIdIndex);
        } else {
            // record is not stored in the database yet
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_MID, movieId);
            movieValues.put(MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieEntry.COLUMN_SYNOPSIS, synopsis);
            movieValues.put(MovieEntry.COLUMN_RATING, rating);
            movieValues.put(MovieEntry.COLUMN_RELEASE, releaseDate);
            movieValues.put(MovieEntry.COLUMN_IMAGE_URL, imageUrl);
            movieValues.put(MovieEntry.COLUMN_RANK, currentItemCount);

            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieEntry.CONTENT_URI,
                    movieValues
            );
            rowId = ContentUris.parseId(insertedUri);
        }
        return rowId;
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
                movieItem.put(MovieEntry.COLUMN_MID, id);
                movieItem.put(MovieEntry.COLUMN_TITLE, title);
                movieItem.put(MovieEntry.COLUMN_SYNOPSIS, synopsis);
                movieItem.put(MovieEntry.COLUMN_RATING, rating);
                movieItem.put(MovieEntry.COLUMN_RELEASE, releaseDate);
                movieItem.put(MovieEntry.COLUMN_IMAGE_URL, imagePath);
                movieItem.put(MovieEntry.COLUMN_RANK, currentItemCount + i);
                moviesForDatabase[i] = movieItem;
            }
            int rowInserted = 0;
            if (moviesForDatabase.length > 0) {
                rowInserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, moviesForDatabase);
                //Log.v(LOG_TAG, "Bulk insert: "+rowInserted);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
    @Override
    protected Void doInBackground(String... params) {
        final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
        final String SORT_PARAM = "sort_by";
        final String APIID_PARAM = "api_key";
        final String PAGE_PARAM = "page";
        final int COUNT_PER_PAGE = 20;
        if (isRefresh)
            deleteDatabaseContent();
        String page = Integer.toString(currentItemCount / COUNT_PER_PAGE + 1);
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0])
                    .appendQueryParameter(APIID_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY)
                    .appendQueryParameter(PAGE_PARAM, page)
                    .build();
        String rawJsonData = DownloadData.fetchRawJson(buildUri);
        try {
            getMovieDataFromJson(rawJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //This notifies the SwipeRefreshLayout widget that work is done and to stop displaying the loader animation.
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
