package com.jyo.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jyo.android.popularmovies.data.PopMoviesContract.FavoriteEntry;
import com.jyo.android.popularmovies.data.PopMoviesContract.TrailerEntry;
import com.jyo.android.popularmovies.data.PopMoviesContract.ReviewEntry;


/**
 * Manages a local database for weather data.
 */
public class PopMoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 7;

    static final String DATABASE_NAME = "popular_movies.db";

    public PopMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold favorite movies.
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry.COLUMN_MOVIE_ID + " TEXT PRIMARY KEY," +
                FavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER + " BLOB, " +
                FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                FavoriteEntry.COLUMN_RATING + " REAL, " +
                FavoriteEntry.COLUMN_PLOT + " TEXT " +
                " );";

        //Create table to hold trailers
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry.COLUMN_TRAILER_ID + " TEXT," +
                TrailerEntry.COLUMN_MOVIE_ID + " TEXT," +
                TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_YOUTUBE + " BOOLEAN NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_SITE + " TEXT NOT NULL," +
                "PRIMARY KEY("+TrailerEntry.COLUMN_TRAILER_ID+","+TrailerEntry.COLUMN_MOVIE_ID+"));";

        //Create table to hold reviews
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT," +
                ReviewEntry.COLUMN_MOVIE_ID + " TEXT," +
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT," +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT," +
                "PRIMARY KEY("+ReviewEntry.COLUMN_REVIEW_ID+","+ReviewEntry.COLUMN_MOVIE_ID+"));";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
