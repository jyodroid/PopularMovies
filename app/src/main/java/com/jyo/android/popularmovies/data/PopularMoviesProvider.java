/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jyo.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PopularMoviesProvider extends ContentProvider {

    private  PopMoviesDbHelper mPopMoviesHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sFavoritesQueryBuilder;

    private static final int FAVORITES = 100;
    private static final int FIND_FAVORITE_BY_ID = 101;
    private static final int TRAILERS = 200;
    private static final int TRAILERS_FOR_MOVIE = 201;
    private static final int REVIEWS = 300;
    private static final int REVIEWS_FOR_MOVIE = 301;

    private static final String movieTableName = PopMoviesContract.FavoriteEntry.TABLE_NAME;
    private static final String trailerTableName = PopMoviesContract.TrailerEntry.TABLE_NAME;
    private static final String reviewTableName = PopMoviesContract.ReviewEntry.TABLE_NAME;

    private static final String sMovieSelection =
             movieTableName + "." +
                    PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?";

    private static final String sTrailersSelection =
            trailerTableName + "." +
                    PopMoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?";

    private static final String sReviewsSelection =
            reviewTableName + "." +
                    PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?";

    static{
        sFavoritesQueryBuilder = new SQLiteQueryBuilder();

//        sFavoritesQueryBuilder.setTables(
//                movieTableName + " INNER JOIN ( "
//                        + reviewTableName + " INNER JOIN " + trailerTableName +
//                        " ON " + reviewTableName + "." +
//                        PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID +
//                        " = " + trailerTableName + "." +
//                        PopMoviesContract.TrailerEntry.COLUMN_MOVIE_ID +
//                        " ) review_trailer ON " + movieTableName +
//                        "." + PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID +
//                        " = review_trailer" +
//                        "." + PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID
//                 movieTableName + " INNER JOIN " + reviewTableName +
//                        " ON " + movieTableName +
//                        "." + PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID +
//                        " = " + reviewTableName +
//                        "." + PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID +
//                        " INNER JOIN " + trailerTableName +
//                        " ON " + reviewTableName + "." +
//                        PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID +
//                        " = " + trailerTableName + "." +
//                        PopMoviesContract.TrailerEntry.COLUMN_MOVIE_ID
//        );

        sFavoritesQueryBuilder.setDistinct(true);
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                return PopMoviesContract.FavoriteEntry.CONTENT_TYPE;
            case FIND_FAVORITE_BY_ID:
                return PopMoviesContract.FavoriteEntry.CONTENT_TYPE;
            case TRAILERS:
                return PopMoviesContract.TrailerEntry.CONTENT_TYPE;
            case TRAILERS_FOR_MOVIE:
                return PopMoviesContract.TrailerEntry.CONTENT_TYPE;
            case REVIEWS:
                return PopMoviesContract.ReviewEntry.CONTENT_TYPE;
            case REVIEWS_FOR_MOVIE:
                return PopMoviesContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopMoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, PopMoviesContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, PopMoviesContract.PATH_FAVORITES+"/*", FIND_FAVORITE_BY_ID);

        matcher.addURI(authority, PopMoviesContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, PopMoviesContract.PATH_TRAILERS + "/*", TRAILERS_FOR_MOVIE);

        matcher.addURI(authority, PopMoviesContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, PopMoviesContract.PATH_REVIEWS + "/*", REVIEWS_FOR_MOVIE);

        return matcher;
    }

    private Cursor getFavoriteById(Uri uri, String[] projection, String sortOrder){

        sFavoritesQueryBuilder.setTables(movieTableName);

        String movieId = PopMoviesContract.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};

        return sFavoritesQueryBuilder.query(mPopMoviesHelper.getReadableDatabase(),
                projection,
                sMovieSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailersForMovie(Uri uri, String[] projection, String sortOrder) {

        sFavoritesQueryBuilder.setTables(trailerTableName);

        String movieId = PopMoviesContract.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};

        return sFavoritesQueryBuilder.query(mPopMoviesHelper.getReadableDatabase(),
                projection,
                sTrailersSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewsForMovie(Uri uri, String[] projection, String sortOrder) {

        sFavoritesQueryBuilder.setTables(reviewTableName);

        String movieId = PopMoviesContract.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};

        return sFavoritesQueryBuilder.query(mPopMoviesHelper.getReadableDatabase(),
                projection,
                sReviewsSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mPopMoviesHelper = new PopMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
            {
                retCursor = mPopMoviesHelper.getReadableDatabase().query(
                        PopMoviesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FIND_FAVORITE_BY_ID: {
                retCursor = getFavoriteById(uri, projection, sortOrder);
                break;
            }
            case TRAILERS_FOR_MOVIE: {
                retCursor = getTrailersForMovie(uri, projection, sortOrder);
                break;
            }
            case REVIEWS_FOR_MOVIE: {
                retCursor = getReviewsForMovie(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mPopMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES: {
                long _id = db.insert(PopMoviesContract.FavoriteEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PopMoviesContract.FavoriteEntry.buildFavoriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILERS: {
                long _id = db.insert(PopMoviesContract.TrailerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PopMoviesContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(PopMoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PopMoviesContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mPopMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case FAVORITES:
                rowsDeleted = db.delete(
                        PopMoviesContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(
                        PopMoviesContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        PopMoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mPopMoviesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FAVORITES:
                rowsUpdated = db.update(PopMoviesContract.FavoriteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILERS:
                rowsUpdated = db.update(PopMoviesContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(PopMoviesContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsUpdated;
    }
}