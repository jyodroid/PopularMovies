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

import com.jyo.android.popularmovies.model.Movie;
import com.jyo.android.popularmovies.model.MovieListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JohnTangarife on 7/10/15.
 */
public class FavoriteDBOperator {

    private static final String LOG_TAG = FavoriteDBOperator.class.getSimpleName();
    private static int mToastDuration = Toast.LENGTH_SHORT;

    private static ContentResolver resolver;

    public static void obtainFavoriteMovies(
            MovieListAdapter moviesAdapter,
            ProgressBar progressBar,
            Context context){

        //Start ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        resolver = context.getContentResolver();
        Cursor cursor =
                resolver.query(PopMoviesContract.FavoriteEntry.CONTENT_URI, null, null, null, null);

        //Obtain favorite movies
        try {
            int movieIdIndx = cursor.getColumnIndex(PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID);
            int movieTitleIndx = cursor.getColumnIndex(PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_TITLE);
            int moviePosterIndx = cursor.getColumnIndex(PopMoviesContract.FavoriteEntry.COLUMN_POSTER);
            int movieRDIndx = cursor.getColumnIndex(PopMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE);
            int movieRatingIndx = cursor.getColumnIndex(PopMoviesContract.FavoriteEntry.COLUMN_RATING);
            int moviePlotIndx = cursor.getColumnIndex(PopMoviesContract.FavoriteEntry.COLUMN_PLOT);

            List<Movie> movies = new ArrayList<>();

            while(cursor.moveToNext()){

                Movie movie = new Movie();

                movie.setMovieID(cursor.getString(movieIdIndx));
                movie.setTitle(cursor.getString(movieTitleIndx));
                movie.setPosterBM(cursor.getBlob(moviePosterIndx));
                movie.setReleaseDate(cursor.getString(movieRDIndx));
                movie.setRating(cursor.getDouble(movieRatingIndx));
                movie.setPlot(cursor.getString(moviePlotIndx));

                movies.add(movie);
            }

            //clean adapter
            moviesAdapter.clear();

            //Updating adapter
            if (movies.size() == 0){

                CharSequence text =
                        "You don't have favorites yet";

                Toast toast = Toast.makeText(context, text, mToastDuration);
                toast.show();
            }else {
                moviesAdapter.addAll(movies);
            }
        }finally {
            if (null != cursor){
                cursor.close();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public static long addMovieToFavorites(Movie movie, Context context){

        resolver = context.getContentResolver();

        ContentValues movieValues = new ContentValues();

        movieValues.put(PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID, movie.getMovieID());
        movieValues.put(PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        movieValues.put(PopMoviesContract.FavoriteEntry.COLUMN_PLOT, movie.getPlot());
        movieValues.put(PopMoviesContract.FavoriteEntry.COLUMN_RATING, movie.getRating());
        movieValues.put(PopMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        movieValues.put(PopMoviesContract.FavoriteEntry.COLUMN_POSTER, movie.getPosterBA());

        Uri insertedUri =
                resolver.insert(PopMoviesContract.FavoriteEntry.CONTENT_URI, movieValues);

        return ContentUris.parseId(insertedUri);
    }

    public static Cursor findMovieById(String movieId, Context context){

        resolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(PopMoviesContract.FavoriteEntry.CONTENT_URI, movieId);

        Cursor cursor = resolver.query(
                uri,
                new String[]{PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID},
                null,
                null,
                null);

        return cursor;
    }

    public static long removeMovieFromFavorites(String movieId, Context context){

        resolver = context.getContentResolver();

        return resolver.delete(
                PopMoviesContract.FavoriteEntry.CONTENT_URI,
                PopMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{movieId});
    }

}
