package com.sunnietech.hotflicks.persistence;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.sunnietech.hotflicks.utils.PollingCheck;

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
        return movieValues;
    }

    /*
    Test the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
     CTS tests.

     This only tests that the onChange function is called; it does not test that the correct Uri is returned.
  */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;
        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }
        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }
        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
