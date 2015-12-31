package com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by qlzh727 on 12/28/15.
 */
public class TestFetchMovieTask extends AndroidTestCase {
    static final int ADD_MID = 10894;
    static final String ADD_TITLE = "Minions";
    static final String ADD_SYNOPSIS = "Very interesting cartoon";
    static final double ADD_RATING = 8.9;
    static final String ADD_REALEASE = "2015-10-18";
    static final String ADD_IMAGE_URL =
            "http://image.tmdb.org/t/p/w185//fYzpM9GmpBlIC893fNjoWCwE24H.jpg";

    @TargetApi(11)
    public void testAddMovie() {
        getContext().getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                MovieEntry.COLUMN_MID + " = ? ",
                new String[] {Integer.toString(ADD_MID)}
        );
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext(), null);
        long rowId = fetchMovieTask.addMovie(
                ADD_MID,
                ADD_TITLE,
                ADD_SYNOPSIS,
                ADD_RATING,
                ADD_REALEASE,
                ADD_IMAGE_URL
        );
        assertFalse("Error: add Location returned an invalid ID on insert", rowId == -1);
        Cursor movieCursor = getContext().getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry.COLUMN_MID + " = ? ",
                new String[] {Integer.toString(ADD_MID)},
                null
        );
        if (movieCursor.moveToFirst()) {
            assertEquals("Error: wrong row Id", movieCursor.getLong(0), rowId);
            assertEquals("Error: wrong MID", movieCursor.getInt(1), ADD_MID);
            assertEquals("Error: wrong Title", movieCursor.getString(2), ADD_TITLE);
            assertEquals("Error: wrong Synopsis", movieCursor.getString(3), ADD_SYNOPSIS);
            assertEquals("Error: wrong rating", movieCursor.getDouble(4), ADD_RATING);
            assertEquals("Error: wrong release date", movieCursor.getString(5), ADD_REALEASE);
            assertEquals("Error: wrong image URL", movieCursor.getString(6), ADD_IMAGE_URL);
        } else {
            fail("Empty cursor returned by querying with the ADD_MID");
        }
        assertFalse("Error: there is only one record from the query", movieCursor.moveToNext());

        // add the movie again
        long newRowId = fetchMovieTask.addMovie(
                ADD_MID,
                ADD_TITLE,
                ADD_SYNOPSIS,
                ADD_RATING,
                ADD_REALEASE,
                ADD_IMAGE_URL
        );
        assertEquals("Error: inserting a movie record again should return the same ID", rowId, newRowId);
    }
}
