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
            String cursorValue = valueCursor.getString(idx);
            assertEquals("Value '" + valueCursor.getString(idx) +
                    "' did not match the expected value '" +
                    entryValue + "'. " + error, entryValue, cursorValue);
        }
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }
    // create default movie values for test
    static ContentValues createMovieValuesSetOne() {
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
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
        return movieValues;
    }

    static ContentValues createMovieValuesSetTwo() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MID,112161);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "6 Month Rule");
        movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS,
                "A womanizer teaches his clueless friend the rules about being single and avoiding emotional attachment.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 10);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, "2012-06-01");
        movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL,
                "http://image.tmdb.org/t/p/w185//jcVhomttAiaeabeK30luUQeAlzd.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        return movieValues;
    }
}
