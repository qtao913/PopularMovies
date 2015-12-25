package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by qlzh727 on 12/24/15.
 */
public class TestUtilities extends AndroidTestCase {
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String entryValue = entry.getValue().toString();
            assertEquals("Value '" + valueCursor.getString(idx) +
                    "' did not match the expected value '" +
                    entryValue + "'. " + error, entryValue, valueCursor.getString(idx));
        }
    }
    // create default movie values for test
    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MID, 140607);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Star Wars: The Force Awakens");
        movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS,
                "Thirty years after defeating the Galactic Empire, Han Solo and his allies face a new " +
                        "threat from the evil Kylo Ren and his army of Stormtroopers.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 8.11);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, "2015-12-18");
        movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL,
                "http://image.tmdb.org/t/p/w185//fYzpM9GmpBlIC893fNjoWCwE24H.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, false);
        return movieValues;
    }
}
