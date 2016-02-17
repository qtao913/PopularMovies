package com.sunnietech.hotflicks.fetchRawJSON;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sunnietech.hotflicks.BuildConfig;
import com.sunnietech.hotflicks.MovieReviewAdapter;
import com.sunnietech.hotflicks.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

public class FetchMovieReviewTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieReviewTask.class.getSimpleName();
    String[] authorName;
    String[] content;
    private Activity activity;
    private RecyclerView recyclerView;
    public FetchMovieReviewTask(Activity activity, RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
    }


    private void getDataFromJson(String reviewJsonStr) throws JSONException {
        final String REVIEW_LIST = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final int MAX_REVIEW_NUM = 8;

        try {
            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_LIST);
            int len = Math.min(MAX_REVIEW_NUM, reviewArray.length());
            authorName = new String[len];
            content = new String[len];
            for (int i = 0; i < len; i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                authorName[i] = review.getString(AUTHOR);
                content[i] = review.getString(CONTENT);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String REVIEW = "reviews";
        final String APIID_PARAM = "api_key";
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(params[0])
                .appendPath(REVIEW)
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
        if (authorName.length == 0) {
            authorName = new String[1];
            content = new String [1];
            authorName[0] = "Oops~";
            content[0] = "No available review for this movie at the moment";
        }
        MovieReviewAdapter mAdapter = new MovieReviewAdapter(authorName, content, activity);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        alphaAdapter.setDuration(1000);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
    }
}
