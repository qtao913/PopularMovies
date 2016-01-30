package com.example.android.popularmovies.fetchRawJSON;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.MovieReviewAdapter;
import com.example.android.popularmovies.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

public class FetchMovieReviewTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieReviewTask.class.getSimpleName();
    String[] authorName;
    String[] content;
    public  String[] contentData = new String[] {
            "This is a dummy content. So RecyclerView is the appropriate view to use when you have multiple " +
                    "items of the same type and it’s very likely that your user’s device cannot present all of those items at once. " +
                    "Possible examples are contacts, customers, audio files and so on. ",
            "Continued. Recycle (view): A view previously used to display data for a specific adapter position may be placed in a cache for later reuse " +
                    "to display the same type of data again later. This can drastically improve performance " +
                    "by skipping initial layout inflation or construction",
            "RecyclerView introduces an additional level of abstraction between the RecyclerView.Adapter and RecyclerView.LayoutManager to be able to detect data set changes in batches during a layout calculation. " +
                    "This saves LayoutManager from tracking adapter changes to calculate animations. It also helps with performance because all view bindings happen at the same time and " +
                    "unnecessary bindings are avoided.",
            "We recommend that this project use Picasso, a powerful library that will handle image loading and caching on your behalf. If you prefer, you’re welcome to use an alternate library such as Glide.\n" +
                    "We’ve included this to reduce unnecessary extra work and help you focus on applying your app development skills.\n",
            "This is a dummy content. So RecyclerView is the appropriate view to use when you have multiple " +
                    "items of the same type and it’s very likely that your user’s device cannot present all of those items at once. " +
                    "Possible examples are contacts, customers, audio files and so on. "
    };
    public  String[] authorData = new String[] {
            "Kitty 913",
            "Uncle smelly",
            "abcd2016",
            "Jay Chow",
            "hateGames"
    };
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
        MovieReviewAdapter mAdapter = new MovieReviewAdapter(authorName, content, activity);
//        MovieReviewAdapter mAdapter = new MovieReviewAdapter(authorData, contentData, activity);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        alphaAdapter.setDuration(1000);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
    }
}
