package com.jyo.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jyo.android.popularmovies.model.Trailer;
import com.jyo.android.popularmovies.model.TrailerListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JohnTangarife on 7/10/15.
 */
public class TrailerDBOperator {

    private static final String LOG_TAG = TrailerDBOperator.class.getSimpleName();
    private static ContentResolver resolver;

    public static void obtainTrailers(
            TrailerListAdapter trailerListAdapter,
            ProgressBar progressBar,
            String movieId,
            Context context){

        //Start ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        resolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(PopMoviesContract.TrailerEntry.CONTENT_URI, movieId);

        String[] projection = new String[]{
                PopMoviesContract.TrailerEntry.COLUMN_TRAILER_YOUTUBE,
                PopMoviesContract.TrailerEntry.COLUMN_TRAILER_ID,
                PopMoviesContract.TrailerEntry.COLUMN_TRAILER_SITE,
                PopMoviesContract.TrailerEntry.COLUMN_TRAILER_KEY,
                PopMoviesContract.TrailerEntry.COLUMN_TRAILER_NAME
        };

        Cursor cursor = resolver.query(
                uri,
                projection,
                null,
                null,
                null);

        //Obtain trailers from favorite movies
        try {
            int trailerIdIndx =
                    cursor.getColumnIndex(PopMoviesContract.TrailerEntry.COLUMN_TRAILER_ID);
            int trailerKeyIndx =
                    cursor.getColumnIndex(PopMoviesContract.TrailerEntry.COLUMN_TRAILER_KEY);
            int trailerSiteIndx =
                    cursor.getColumnIndex(PopMoviesContract.TrailerEntry.COLUMN_TRAILER_SITE);
            int trailerNameIndx =
                    cursor.getColumnIndex(PopMoviesContract.TrailerEntry.COLUMN_TRAILER_NAME);
            int trailerFromYouTubeIndx =
                    cursor.getColumnIndex(PopMoviesContract.TrailerEntry.COLUMN_TRAILER_YOUTUBE);

            List<Trailer> trailers = new ArrayList<>();

            while(cursor.moveToNext()){

                Trailer trailer = new Trailer();

                trailer.setId(cursor.getString(trailerIdIndx));
                trailer.setKey(cursor.getString(trailerKeyIndx));
                trailer.setSite(cursor.getString(trailerSiteIndx));
                trailer.setName(cursor.getString(trailerNameIndx));
                trailer.setFromYouTube(cursor.getInt(trailerFromYouTubeIndx)>0);

                trailers.add(trailer);
            }

            //clean adapter
            trailerListAdapter.clear();

            //Updating adapter
            if (trailers.size() == 0){

                CharSequence text =
                        "There are not trailers for this movie";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }else {

                trailerListAdapter.addAll(trailers);
            }
        }finally {
            if (null != cursor){
                cursor.close();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public static List<Long> addTrailersForFavorites(List<Trailer> trailers, String movieId, Context context){

        resolver = context.getContentResolver();
        List<Long> insertedUris = new ArrayList<>();


        for (Trailer trailer:trailers) {

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(
                    PopMoviesContract.TrailerEntry.COLUMN_MOVIE_ID,
                    movieId);
            trailerValues.put(
                    PopMoviesContract.TrailerEntry.COLUMN_TRAILER_ID,
                    trailer.getId());
            trailerValues.put(
                    PopMoviesContract.TrailerEntry.COLUMN_TRAILER_KEY,
                    trailer.getKey());
            trailerValues.put(
                    PopMoviesContract.TrailerEntry.COLUMN_TRAILER_NAME,
                    trailer.getName());
            trailerValues.put(
                    PopMoviesContract.TrailerEntry.COLUMN_TRAILER_SITE,
                    trailer.getSite());

            trailerValues.put(
                    PopMoviesContract.TrailerEntry.COLUMN_TRAILER_YOUTUBE,
                    trailer.isFromYouTube());

            Uri insertedUri =
                    resolver.insert(PopMoviesContract.TrailerEntry.CONTENT_URI, trailerValues);
            insertedUris.add(ContentUris.parseId(insertedUri));
        }

        return insertedUris;
    }

    public static long removeTrailersByMovie(String movieId, Context context){

        resolver = context.getContentResolver();

        return resolver.delete(
                PopMoviesContract.TrailerEntry.CONTENT_URI,
                PopMoviesContract.TrailerEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{movieId});
    }
}
