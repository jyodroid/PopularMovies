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

import com.jyo.android.popularmovies.model.Review;
import com.jyo.android.popularmovies.model.ReviewListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JohnTangarife on 7/10/15.
 */
public class ReviewDBOperator {

    private static final String LOG_TAG = ReviewDBOperator.class.getSimpleName();
    private static ContentResolver resolver;

    public static void obtainReviews(
            ReviewListAdapter reviewsAdapter,
            ProgressBar progressBar,
            Context context,
            String movieId){

        //Start ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        resolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PopMoviesContract.ReviewEntry.CONTENT_URI, movieId);
        String[] projection = new String[]{
                PopMoviesContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
                PopMoviesContract.ReviewEntry.COLUMN_REVIEW_AUTHOR
        };

        Cursor cursor = resolver.query(
                uri,
                projection,
                null,
                null,
                null);

        //Obtain reviews from favorite movies
        try {
            int reviewAuthorIndx =
                    cursor.getColumnIndex(PopMoviesContract.ReviewEntry.COLUMN_REVIEW_AUTHOR);
            int reviewIndx =
                    cursor.getColumnIndex(PopMoviesContract.ReviewEntry.COLUMN_REVIEW_CONTENT);

            List<Review> reviews = new ArrayList<>();

            while(cursor.moveToNext()){

                Review review = new Review();

                review.setAuthor(cursor.getString(reviewAuthorIndx));
                review.setContent(cursor.getString(reviewIndx));

                reviews.add(review);
            }

            //clean adapter
            reviewsAdapter.clear();

            //Updating adapter
            if (reviews.size() == 0){

                CharSequence text =
                        "There are not reviews for this movie";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }else {
                reviewsAdapter.addAll(reviews);
            }
        }finally {
            if (null != cursor){
                cursor.close();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public static List<Long> addReviewsForFavorites(List<Review> reviews, String movieId, Context context){

        resolver = context.getContentResolver();
        List<Long> insertedUris = new ArrayList<>();
        for (Review review:reviews) {

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(
                    PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID,
                    movieId);
            reviewValues.put(
                    PopMoviesContract.ReviewEntry.COLUMN_REVIEW_ID,
                    review.getReviewId());
            reviewValues.put(
                    PopMoviesContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
                    review.getAuthor());
            reviewValues.put(
                    PopMoviesContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
                    review.getContent());

            Uri insertedUri =
                    resolver.insert(PopMoviesContract.ReviewEntry.CONTENT_URI, reviewValues);
            insertedUris.add(ContentUris.parseId(insertedUri));
        }

        return insertedUris;
    }

    public static long removeReviewsByMovie(String movieId, Context context){

        resolver = context.getContentResolver();

        return resolver.delete(
                PopMoviesContract.ReviewEntry.CONTENT_URI,
                PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{movieId});
    }
}
