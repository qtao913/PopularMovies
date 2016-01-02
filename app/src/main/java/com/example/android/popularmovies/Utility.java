package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by qlzh727 on 1/1/16.
 */
public class Utility {
    public static String getPreferredMovieSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sorting_preference_key),
                context.getString(R.string.most_popular_value));
    }
}
