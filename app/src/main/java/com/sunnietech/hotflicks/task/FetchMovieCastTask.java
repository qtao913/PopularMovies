package com.sunnietech.hotflicks.task;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunnietech.hotflicks.BuildConfig;
import com.sunnietech.hotflicks.R;
import com.sunnietech.hotflicks.utility.DownloadData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchMovieCastTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieCastTask.class.getSimpleName();
    String[] castPortrait;
    String[] castName;
    View rootView;
    ViewGroup container;
    Activity activity;
    public FetchMovieCastTask(View rootView, ViewGroup container, Activity activity) {
        this.rootView = rootView;
        this.container = container;
        this.activity = activity;
    }

    private void getDataFromJson(String castJsonStr) throws JSONException {
        final String MOVIE_CAST = "cast";
        final String CAST_NAME = "name";
        final String CAST_PROFILE = "profile_path";
        final int MAX_CAST_NUM = 6;
        final String IMAGE_PATH_BASE = "http://image.tmdb.org/t/p/";
        final String DEFAULT_SIZE = "w185/";

        try {
            JSONObject castJson = new JSONObject(castJsonStr);
            JSONArray castArray = castJson.getJSONArray(MOVIE_CAST);
            int len = Math.min(MAX_CAST_NUM, castArray.length());
            castPortrait = new String[len];
            castName = new String[len];
            for (int i = 0; i < len; i++) {
                JSONObject cast = castArray.getJSONObject(i);
                String imagePath = cast.getString(CAST_PROFILE);
                imagePath = imagePath.equals("null") ?
                        null : IMAGE_PATH_BASE + DEFAULT_SIZE + imagePath;
                castPortrait[i] = imagePath;
                castName[i] = cast.getString(CAST_NAME);
//                result.put(imagePath, cast.getString(CAST_NAME));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String CAST = "casts";
        final String APIID_PARAM = "api_key";
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(params[0])
                .appendPath(CAST)
                .appendQueryParameter(APIID_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY)
                .build();
        String rawJsonData = DownloadData.fetchRawJson(buildUri);
        try {
            if (rawJsonData != null)
                getDataFromJson(rawJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPostExecute(Void aVoid) {
        if (isCancelled() || activity.isDestroyed() || rootView == null || castPortrait == null || castName == null)
            return;
        LinearLayout castView = (LinearLayout) rootView.findViewById(R.id.movie_casts);
        if (castView.getChildCount() == castPortrait.length)
            return;
        castView.removeAllViews();
        for (int i = 0; i < castName.length; i++) {
            View casts = LayoutInflater.from(activity).inflate(R.layout.movie_cast, container, false);
            ImageView castPortraitView = (ImageView) casts.findViewById(R.id.cast_portrait);
            if(!activity.isDestroyed())
                Glide.with(activity).load(castPortrait[i]).centerCrop().into(castPortraitView);
            TextView castNameView = (TextView) casts.findViewById(R.id.cast_name);
            castNameView.setText(castName[i]);
            castView.addView(casts);
        }
    }
}