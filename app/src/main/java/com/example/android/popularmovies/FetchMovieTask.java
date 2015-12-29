package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by qlzh727 on 12/18/15.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    public static final int IS_FAVORITE = 1;
    public static final int IS_NOT_FAVORITE = 0;

    private AndroidImageAdapter imageAdapter;
    private final Context mContext;

    public FetchMovieTask(Context context, AndroidImageAdapter imageAdapter) {
        mContext = context;
        this.imageAdapter = imageAdapter;
    }

    // handle insertion of a new Movie record in the Movie database
    // return the row ID of the added movie item
    long addMovie(int movieId, String title, String synopsis,
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
            movieValues.put(MovieEntry.COLUMN_FAVORITE, IS_NOT_FAVORITE);

            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieEntry.CONTENT_URI,
                    movieValues
            );
            rowId = ContentUris.parseId(insertedUri);
        }
        return rowId;
    }

    private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
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
                movieItem.put(MovieEntry.COLUMN_FAVORITE, IS_NOT_FAVORITE);
                moviesForDatabase[i] = movieItem;
            }
            if (moviesForDatabase.length > 0) {
                mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, moviesForDatabase);
            }
            Cursor cursor = mContext.getContentResolver().query(
                    MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            ArrayList<Movie> movieList = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MID));
                    String title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                    String imagePath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_IMAGE_URL));
                    String synopsis = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_SYNOPSIS));
                    double rating = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_RATING));
                    String releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE));
                    movieList.add(new Movie(id, title, imagePath, synopsis, rating, releaseDate));
                } while (cursor.moveToNext());
            }
            return movieList.toArray(new Movie[cursor.getCount()]);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String APIID_PARAM = "api_key";
            Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0])
                    .appendQueryParameter(APIID_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY)
                    .build();
            URL url = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return  null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            movieJsonStr = buffer.toString();
            //Log.v(LOG_TAG, "check origianl Json: "+movieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error Closing Stream", e);
                }
            }
        }
        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        if(result != null) {
            imageAdapter.clear();
            for (Movie elem : result) {
                imageAdapter.add(elem);
            }
        }
    }
}
