package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by qlzh727 on 12/21/15.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MID = "mid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE = "release_date";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_RANK = "rank";

        public static Uri buildMovieUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "review";
        public static final String COLUMN_MID = "mid";
        public static final String COLUMN_review = "review_detail";
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_MID = "mid";
        public static final String COLUMN_URL = "trailer_url";
    }

    public static String getMovieRowRecord(Uri uri) {
        return uri.getLastPathSegment();
    }

}
