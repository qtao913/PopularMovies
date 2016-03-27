package com.sunnietech.hotflicks.task;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;

import com.sunnietech.hotflicks.BuildConfig;
import com.sunnietech.hotflicks.adapter.CustomPagerAdapter;
import com.sunnietech.hotflicks.utility.DownloadData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qlzh727 on 1/18/16.
 */
public class FetchMovieGalleryTask extends AsyncTask<String, Void, String[]> {
    Activity mActivity;
    ViewPager mViewPager;
    public FetchMovieGalleryTask (Activity activity, ViewPager viewPager) {
        mActivity = activity;
        mViewPager = viewPager;
    }
    private String[] getDataFromJson(String movieJsonStr) throws JSONException {
        final String LOG_TAG = FetchMovieGalleryTask.class.getSimpleName();
        final String IMAGE_PATH_BASE = "http://image.tmdb.org/t/p/";
//        final String DEFAULT_SIZE = "w500/";
        final String DEFAULT_SIZE = "w780/";
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
        String rawJsonData = DownloadData.fetchRawJson(buildUri);
        try {
            if (rawJsonData != null)
                return getDataFromJson(rawJsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPostExecute(String[] strings) {
        //loading image in the tool bar
        if(isCancelled() || mActivity.isDestroyed() || mViewPager == null || strings == null)
            return;
        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(mActivity, strings);
        mViewPager.setAdapter(mCustomPagerAdapter);
        periodicalSwipe(strings.length);
    }

    private void periodicalSwipe(final int totalPage) {
        final Handler handler = new Handler();
        final Runnable swipe = new Runnable(){
            @Override
            public void run() {
                int currentPage = mViewPager.getCurrentItem();
                if (currentPage == totalPage - 1) {
                    currentPage = 0;
                } else {
                    currentPage ++;
                }
                mViewPager.setCurrentItem(currentPage, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(swipe);
            }
        }, 6000, 6000);
    }
}

