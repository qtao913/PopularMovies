package com.example.android.popularmovies.fetchRawJSON;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utility;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FetchMovieTrailerTask extends AsyncTask<String, Void, Void>
        implements YouTubeThumbnailView.OnInitializedListener {
    private final String LOG_TAG = FetchMovieTrailerTask.class.getSimpleName();
    private ArrayList<String> result = new ArrayList<>();
    private View rootView;
    private ViewGroup container;
    private Activity activity;
    private YouTubeThumbnailView[] trailerThumbnailViews;

    public FetchMovieTrailerTask(View rootView, ViewGroup container, Activity activity) {
        this.rootView = rootView;
        this.container = container;
        this.activity = activity;
    }

    private void getDataFromJson(String trailerJsonStr) throws JSONException {
        final String SITE = "site";
        final String PATH = "key";
        final String TRAILER_LIST = "results";
        final String YOUTUBE_SITE = "YouTube";
        final int MAX_TRAILER_NUM = 4;

        try {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_LIST);
            int counter = 0;
            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailerObj = trailerArray.getJSONObject(i);
                String trailerSite = trailerObj.getString(SITE);
                if (trailerSite.equals(YOUTUBE_SITE)) {
                    result.add(trailerObj.getString(PATH));
                    counter++;
                }
                if (counter == MAX_TRAILER_NUM) break;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String VIDEO = "videos";
        final String APIID_PARAM = "api_key";
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(params[0])
                .appendPath(VIDEO)
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
        LinearLayout mTrailers = (LinearLayout) rootView.findViewById(R.id.movie_trailers);
        if (mTrailers.getChildCount() == result.size())
            return;
        mTrailers.removeAllViews();
        trailerThumbnailViews = new YouTubeThumbnailView[result.size()];
        for (int i = 0; i < result.size(); i++) {
            View trailer = LayoutInflater.from(activity)
                    .inflate(R.layout.youtube_thumbnail_view, container, false);
            YouTubeThumbnailView thumbnailView = (YouTubeThumbnailView)
                    trailer.findViewById(R.id.youtube_thumbnail_item);
            thumbnailView.initialize(BuildConfig.YOUTUBE_ANDROID_API_KEY, this);
            mTrailers.addView(trailer);
            trailerThumbnailViews[i] = thumbnailView;
        }
    }


    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView,
                                        final YouTubeThumbnailLoader youTubeThumbnailLoader) {
        Log.v(LOG_TAG, "Youtube init success");
        for (int i = 0; i < result.size(); i++) {
            if (this.trailerThumbnailViews[i] == youTubeThumbnailView) {
                final String viewPath = result.get(i);
                youTubeThumbnailLoader.setVideo(viewPath);
                youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent playYoutube = new Intent(getActivity(), YouTubePlayerActivity.class);
//                        playYoutube.putExtra("youtube path", viewPath);
//                        startActivity(playYoutube);
                        Intent intent = YouTubeStandalonePlayer.createVideoIntent
                                (activity, BuildConfig.YOUTUBE_ANDROID_API_KEY, viewPath, 0, true, false);
                        activity.startActivity(intent);
                    }
                });
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(
                        new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                            @Override
                            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                                Log.v(LOG_TAG, "Youtube loader released");
                                youTubeThumbnailLoader.release();
                                RelativeLayout parentView = (RelativeLayout)youTubeThumbnailView.getParent();
                                ImageView buttonView = (ImageView)parentView.findViewById(R.id.play_button);
                                buttonView.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView,
                                                         YouTubeThumbnailLoader.ErrorReason errorReason) {
                                Log.v(LOG_TAG, "Youtube loader released");
                                youTubeThumbnailLoader.release();
                            }
                        });
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView,
                                        YouTubeInitializationResult youTubeInitializationResult) {
        Log.v(LOG_TAG, "Youtube init failure");
    }
}