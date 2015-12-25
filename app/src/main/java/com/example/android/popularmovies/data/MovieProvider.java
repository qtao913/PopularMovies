package com.example.android.popularmovies.data;

import android.content.UriMatcher;

/**
 * Created by qlzh727 on 12/25/15.
 */
public class MovieProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int MOVIE = 100;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        return matcher;
    }
}
