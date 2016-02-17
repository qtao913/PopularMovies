package com.sunnietech.hotflicks.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by qlzh727 on 12/25/15.
 */
public class TestUriMatcher extends AndroidTestCase {
    // content://com.example.android.popularmovies/movie
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        assertEquals("Error: Movie URI was matched incorrectly",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
    }
}
