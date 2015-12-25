package com.example.android.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by qlzh727 on 12/21/15.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);
        assertTrue("Error: database is not created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
        assertTrue("Error: does not contain movie entry", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);
        assertTrue("Error: unable to query the database for table information", c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RATING);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_IMAGE_URL);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());
        assertTrue("Error: database does not contain all the required entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }

//    public void testMovieTable() {
//        insertMovie();
//    }

//    public long insertMovie() {
//        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues testValues = TestUtilities.createMovieValues();
//        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
//        assertTrue(movieRowId != -1);
//
//        // pull out the information by querying database and receiving a cursor back
//        Cursor cursor = db.query(
//                MovieContract.MovieEntry.TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//
//        assertTrue("Error: no record returned from movie query", cursor.moveToFirst());
//        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed", cursor, testValues);
//        assertFalse("Error: more than one record returned from this query", cursor.moveToNext());
//        cursor.close();
//        db.close();
//        return movieRowId;
//    }
}
