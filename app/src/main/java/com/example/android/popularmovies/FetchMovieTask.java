package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qlzh727 on 12/18/15.
 */
public class FetchMovieTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_SYNOPSIS = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE = "release_date";
        final String IMAGE_PATH = "poster_path";
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);
        String[] movieStrs = new String[movieArray.length()];
        for (int i = 0; i < movieStrs.length; i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);
            String temp = "id is: " + movieObject.getInt(MOVIE_ID)
                    + "\n original title is: " + movieObject.getString(MOVIE_TITLE)
                    + "\n synopsis: " + movieObject.getString(MOVIE_SYNOPSIS)
                    + "\n rating is: " + movieObject.getDouble(MOVIE_RATING)
                    + "\n release on: " + movieObject.getString(MOVIE_RELEASE)
                    + "\n";
            Log.v(LOG_TAG, "Each Movie Object: \n" + temp);
            movieStrs[i] = temp;
        }
        return movieStrs;
    }
    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        String apikey = "";
        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String APIID_PARAM = "api_key";
            Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0])
                    .appendQueryParameter(APIID_PARAM, apikey)
                    .build();
            URL url = new URL(buildUri.toString());
            //Log.v(LOG_TAG, "Build URI " + buildUri.toString());
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
    protected void onPostExecute(String[] result) {
        if(result != null) {

            // adapter.add()
        }
    }
}
