package com.example.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
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
        deleteAllRecordsFromProvider();
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        Cursor c = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records in Movie table are not deleted completely", 0, c.getCount());
        c.close();
    }
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
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

    // at this stage, contentResolver().insert() is not coded,
    // therefore, directly insert test values to the db by calling the db.insert();
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

    public void testMovieInsert() {
        ContentValues testValues = TestUtilities.createMovieValuesSetOne();
        // Register a content observer for insert.
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri addedRow = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(ContentUris.parseId(addedRow) != -1);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        Cursor cursorAfterInsertion = mContext.getContentResolver().query(
                addedRow,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testMovieInsert", cursorAfterInsertion, testValues);
    }

    public void testMovieDeletion() {
        ContentValues setOne = TestUtilities.createMovieValuesSetOne();
        ContentValues setTwo = TestUtilities.createMovieValuesSetTwo();
        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, setOne);
        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, setTwo);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        // test deleting rows where isFavourite == false
        // setOne has isFav == false; setTwo has isFav == true
        String selection = MovieContract.MovieEntry.COLUMN_FAVORITE + " = ? ";
        String[] selectionArgs = {"0"};
        int rowsDeleted = mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                selection,
                selectionArgs);
        tco.waitForNotificationOrFail();
        assertEquals("Error: Incorrect number of rows are deleted.", 1, rowsDeleted);
        //query the database, should get a record of setTwo
        Cursor c = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testMovieDelete", c, setTwo);
        // delete all the record
        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, setOne);
        c = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error wrong insertion", 2, c.getCount());
        rowsDeleted = mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        tco.waitForNotificationOrFail();
        assertEquals("Error: records from table are not deleted completely", 0, c.getCount()-rowsDeleted);
        mContext.getContentResolver().unregisterContentObserver(tco);
        c.close();
    }

    public void testUpdateMovie() {
        ContentValues testValues = TestUtilities.createMovieValuesSetOne();
        Uri itemUri = mContext.getContentResolver().insert(
                MovieContract.MovieEntry.CONTENT_URI,testValues
        );
        long itemRowId = ContentUris.parseId(itemUri);
        assertTrue(itemRowId != -1);
        String rowId = MovieContract.getMovieRowRecord(itemUri);

        Cursor beforeUpdate = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        beforeUpdate.registerContentObserver(tco);

        ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(MovieContract.MovieEntry._ID, itemRowId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);

        int countUpdate = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                updatedValues,
                MovieContract.MovieEntry._ID + " = ? ",
                new String[] {rowId}
        );
        assertEquals(countUpdate, 1);
        // check whether observer is notified
        tco.waitForNotificationOrFail();
        beforeUpdate.unregisterContentObserver(tco);
        beforeUpdate.close();

        Cursor afterUpdate = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry._ID + " = ? ",
                new String[] {rowId},
                null
        );
        TestUtilities.validateCursor("Error: Update wrong information", afterUpdate, updatedValues);
        afterUpdate.close();
    }

    public void testBulkInsert() {
        ContentValues[] bulkValues = new ContentValues[2];
        bulkValues[0] = TestUtilities.createMovieValuesSetOne();
        bulkValues[1] = TestUtilities.createMovieValuesSetTwo();
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        int count = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, bulkValues);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        assertEquals(count, bulkValues.length);

        Cursor c = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals(c.getCount(), bulkValues.length);
        c.moveToFirst();
        for (int i = 0; i < bulkValues.length; i++, c.moveToNext()) {
            TestUtilities.validateCurrentRecord("Error: bulk insert is incorrect at entry: " + i,
                    c, bulkValues[i]);
        }
        c.close();
    }
}
