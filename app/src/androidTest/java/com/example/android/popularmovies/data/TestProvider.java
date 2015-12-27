package com.example.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

/**
 * Created by qlzh727 on 12/25/15.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    public void setUp() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        assertEquals("Error: Records not deleted from Movie", 0, c.getCount());
    }

    // test the content provider is registered correctly.
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: MovieProvider registered with " + providerInfo.authority +
                            "but not " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // provider is not registered correctly
            assertTrue("Error: MovieProvider is not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        //content://com.example.android.popularmovies/movie
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        //vnd.android.cursor.dir/com.example.android.popularmovies/movie
        assertEquals("Error: returned type is incorrect, should be MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);
        long testRowId = 13456;
        Uri movieRowUri = MovieContract.MovieEntry.buildMovieUri(testRowId);
        String itemType = mContext.getContentResolver().getType(movieRowUri);
        //vnd.android.cursor.item/com.example.android.popularmovies/movie/13456
        assertEquals("Error: returned type is incorrect, should be MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, itemType);
    }

    public void testBasicMovieQuery() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValuesSetOne();
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
        assertTrue("Fail to insert to the movie table", movieRowId != -1);

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, testValues);
        // test NotificationUri been set correctly
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie query did not set NotificationUri",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }

        // test a paricular Movie Item
        testValues = TestUtilities.createMovieValuesSetTwo();
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
        assertTrue("Fail to insert to the movie table", movieRowId != -1);
        db.close();

        // append the movieId to MovieEntry.CONTENT_URI
        Uri itemUri = MovieContract.MovieEntry.buildMovieUri(movieRowId);
        // query the database to retrieve a single row of data whose _id == movieRowId
        movieCursor = mContext.getContentResolver().query(
                itemUri,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testSingleMovieItem", movieCursor, testValues);
    }

//    public void testMovieInsert() {
//        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
//        ContentValues testValues = TestUtilities.createMovieValuesSetOne();
//        Uri addedRow = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);
//        Cursor cursorAfterInsertion = mContext.getContentResolver().query(
//                addedRow,
//                null,
//                null,
//                null,
//                null
//        );
//        TestUtilities.validateCursor("testMovieInsert", cursorAfterInsertion, testValues);
//
//    }
}
