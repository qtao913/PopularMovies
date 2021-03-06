package com.sunnietech.hotflicks.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sunnietech.hotflicks.persistence.MovieContract.MovieEntry;

/**
 * Created by qlzh727 on 12/21/15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 3;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT, " +
                MovieEntry.COLUMN_RATING + " REAL, " +
                MovieEntry.COLUMN_RELEASE + " TEXT, " +
                MovieEntry.COLUMN_IMAGE_URL + " TEXT, " +
                MovieEntry.COLUMN_RANK + " INTEGER NOT NULL, " +
                "UNIQUE (" + MovieEntry.COLUMN_MID + ") ON CONFLICT REPLACE, " +
                "UNIQUE (" + MovieEntry.COLUMN_RANK + ") ON CONFLICT REPLACE" +
                " );";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
