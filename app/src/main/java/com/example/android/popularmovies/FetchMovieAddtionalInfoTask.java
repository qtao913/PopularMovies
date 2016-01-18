package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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
 * Created by qlzh727 on 1/17/16.
 */
public class FetchMovieAddtionalInfoTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchMovieAddtionalInfoTask.class.getSimpleName();

    private TextView runtimeView;
    private TextView genreView;

    public FetchMovieAddtionalInfoTask(TextView genreView, TextView runtimeView) {
        this.runtimeView = runtimeView;
        this.genreView = genreView;
    }


    private String[] getDataFromJson(String movieJsonStr) throws JSONException {
        final String MOVIE_GENRE = "genres";
        final String MOVIE_RUNTIME = "runtime";
        final String GENRE_NAME = "name";
        String[] result = new String[2];

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray genreArray = movieJson.getJSONArray(MOVIE_GENRE);
            result[0] = movieJson.getString(MOVIE_RUNTIME) + " min";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < genreArray.length(); i++) {
                JSONObject genreName = genreArray.getJSONObject(i);
                sb.append(genreName.getString(GENRE_NAME));
                if (i != genreArray.length() - 1)
                    sb.append(" | ");
            }
            result[1] = sb.toString();

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        String[] result = null;
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APIID_PARAM = "api_key";
            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(APIID_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY)
                    .build();
            URL url = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
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
            //Log.v(LOG_TAG, "check origianl Json: "+ movieJsonStr);
            result = getDataFromJson(movieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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
        return result;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        genreView.setText(strings[1]);
        runtimeView.setText(strings[0]);
    }
}
