package com.example.android.popularmovies.fetchRawJSON;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String APIID_PARAM = "api_key";
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(APIID_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY)
                    .build();
        String rowJsonData = Utility.fetchRowJson(buildUri);
        try {
            return getDataFromJson(rowJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        genreView.setText(strings[1]);
        runtimeView.setText(strings[0]);
    }
}
