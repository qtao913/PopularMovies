package com.example.android.popularmovies.fetchRawJSON;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qlzh727 on 1/18/16.
 */
public class FetchMovieGalleryTask extends AsyncTask<String, Void, String[]> {

    private String[] getDataFromJson(String movieJsonStr) throws JSONException {
        final String LOG_TAG = FetchMovieGalleryTask.class.getSimpleName();
        final String IMAGE_PATH_BASE = "http://image.tmdb.org/t/p/";
        final String DEFAULT_SIZE = "w185/";
        final String OBJECT_NAME = "backdrops";
        final String IMAGE_PATH = "file_path";
        String[] result = null;
        final int MAX_IMAGE_LENGTH = 5;

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray infoArray = movieJson.getJSONArray(OBJECT_NAME);
            int len = Math.min(MAX_IMAGE_LENGTH, infoArray.length());
            result = new String[len];
            for (int i = 0; i < len; i++) {
                String imagePath = infoArray.getJSONObject(i).getString(IMAGE_PATH);
                imagePath = imagePath.equals("null") ? null : IMAGE_PATH_BASE + DEFAULT_SIZE + imagePath;
                result[i] = imagePath;
            }
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
        final String IMAGE = "images";
        String[] result = null;
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(params[0])
                .appendPath(IMAGE)
                .appendQueryParameter(APIID_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY)
                .build();
        String rawJsonData = Utility.fetchRowJson(buildUri);
        try {
            return getDataFromJson(rawJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String[] strings) {

    }
}

