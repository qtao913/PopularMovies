package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by qlzh727 on 12/21/15.
 */
public class MovieContract {
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MID = "mid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE = "release_date";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_FAVORITE = "favorite";
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
}
