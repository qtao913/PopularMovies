package com.sunnietech.hotflicks.fetchRawJSON;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunnietech.hotflicks.BuildConfig;
import com.sunnietech.hotflicks.R;
import com.sunnietech.hotflicks.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qlzh727 on 1/17/16.
 */
public class FetchMovieAddtionalInfoTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieAddtionalInfoTask.class.getSimpleName();
    private final int MAX_GENRE_LEN = 3;
    private TextView runtimeView;
    private TextView genreView;
    private TextView revenueView;
    private TextView taglineView;
    private Button homepageButton;
    private String runtime;
    private String revenue;
    private String[] genre;
    private String tagline;
    private String homepage;
    private Activity activity;


    public FetchMovieAddtionalInfoTask(Activity activity, View rootView) {
        this.activity = activity;
        runtimeView = (TextView) rootView.findViewById(R.id.movie_runtime);
        genreView = (TextView) rootView.findViewById(R.id.movie_genre);
        revenueView = (TextView)rootView.findViewById(R.id.movie_revenue);
        taglineView = (TextView)rootView.findViewById(R.id.movie_tagline);
        homepageButton = (Button)rootView.findViewById(R.id.homepage_button);
    }

    private void getDataFromJson(String movieJsonStr) throws JSONException {
        final String MOVIE_GENRE = "genres";
        final String MOVIE_RUNTIME = "runtime";
        final String GENRE_NAME = "name";
        final String MOVIE_REVENUE = "revenue";
        final String MOVIE_TAGLINE = "tagline";
        final String MOVIE_HOMEPAGE = "homepage";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray genreArray = movieJson.getJSONArray(MOVIE_GENRE);
            runtime = movieJson.getString(MOVIE_RUNTIME) + " min";
            revenue = movieJson.getString(MOVIE_REVENUE);
            tagline = movieJson.getString(MOVIE_TAGLINE);
            homepage = movieJson.getString(MOVIE_HOMEPAGE);
            genre = new String[genreArray.length()];
            for (int i = 0; i < genreArray.length(); i++) {
                genre[i] = genreArray.getJSONObject(i).getString(GENRE_NAME);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String APIID_PARAM = "api_key";
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(APIID_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY)
                    .build();
        String rawJsonData = Utility.fetchRawJson(buildUri);
        try {
            getDataFromJson(rawJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        StringBuilder sb = new StringBuilder();
        int len = Math.min(MAX_GENRE_LEN, genre.length);
        for (int i = 0; i < len; i++) {
            sb.append(genre[i]);
            if (i < len - 1)
                sb.append(" | ");

        }
        genreView.setText(sb.toString());
        runtimeView.setText(runtime);
        revenueView.setText(Utility.numberConvert(revenue));
        if (tagline.equals(""))
            tagline = activity.getString(R.string.tagline_not_available);
        taglineView.setText(tagline);
        homepageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intent);
                }
            }
        });
    }
}
