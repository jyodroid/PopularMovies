package com.jyo.android.popularmovies.moviedetails;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jyo.android.popularmovies.PopularMovies;
import com.jyo.android.popularmovies.R;
import com.jyo.android.popularmovies.commons.InternetUtils;
import com.jyo.android.popularmovies.connection.ReviewsTask;
import com.jyo.android.popularmovies.connection.TrailersTask;
import com.jyo.android.popularmovies.data.FavoriteDBOperator;
import com.jyo.android.popularmovies.data.ReviewDBOperator;
import com.jyo.android.popularmovies.data.TrailerDBOperator;
import com.jyo.android.popularmovies.model.Movie;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private static final String NOT_AVAILABLE = "Not available";
    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    private static int mToastDuration = Toast.LENGTH_SHORT;

    OnFavoriteDeselectedListener onFavoriteDeselectedListener;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Receive the movie object
//        Intent intent = getActivity().getIntent();
//        final Movie movie = intent.getParcelableExtra(PopularMovies.MOVIE);
        Bundle bundle = this.getArguments();
        final Movie movie = bundle.getParcelable(PopularMovies.MOVIE);

        //Prepare UI elements
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        //Sync view
        final ViewHolder viewHolder = new ViewHolder(rootView);

        final Context context = getActivity().getBaseContext();
        //Fill poster

        if (movie == null){
            return null;
        }

        if (null != movie.getPosterBA()) {
            Bitmap poster =
                    BitmapFactory.decodeByteArray(movie.getPosterBA(), 0, movie.getPosterBA().length);
            viewHolder.poster.setImageBitmap(poster);
        }

        //Fill movie data
        setVerifiedValue(movie.getTitle(), viewHolder.originalTitle);
        setVerifiedValue(movie.getReleaseDate(), viewHolder.releaseDate);
        setVerifiedValue(movie.getPlot(), viewHolder.plot);
        setVerifiedValue(String.valueOf(movie.getRating()), viewHolder.rate);

        //Checked for favorites
        //Check if movie is favorite and check the star
        if(isMovieFavorite(movie.getMovieID(), context)){
            //Star must be checked
            viewHolder.favoriteStar.setChecked(true);
        }

        //Add and Remove movie from favorites
        viewHolder.favoriteStar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    //Add to favorites if isn't yet
                    if(!isMovieFavorite(movie.getMovieID(), context)){

                        //add movies
                        FavoriteDBOperator.addMovieToFavorites(movie, context);

                        //add reviews
                        addReviews(movie.getMovieID(), context);

                        //add trailers
                        addTrailers(movie.getMovieID(), context);
                    }
                }else {
                    //Remove from favorites
                    if(isMovieFavorite(movie.getMovieID(), context)) {
                        //If movie exist delete it
                        FavoriteDBOperator.removeMovieFromFavorites(movie.getMovieID(), context);
                        //Delete reviews
                        ReviewDBOperator.removeReviewsByMovie(movie.getMovieID(), context);
                        //Delete trailers
                        TrailerDBOperator.removeTrailersByMovie(movie.getMovieID(), context);
                    }

                    //Refresh movie list
                    if (onFavoriteDeselectedListener != null){
                        onFavoriteDeselectedListener.favoriteDeselected(true);
                    }
                }
            }
        });

        return rootView;
    }
    static class ViewHolder {
        @Bind(R.id.img_movie_detail_poster)
        ImageView poster;
        @Bind(R.id.txt_det_original_title)
        TextView originalTitle;
        @Bind(R.id.txt_det_release)
        TextView releaseDate;
        @Bind(R.id.txt_det_plot)
        TextView plot;
        @Bind(R.id.txt_det_rating)
        TextView rate;

        @Bind(R.id.det_btn_favorite)
        CheckBox favoriteStar;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //Ensures the activity implements callback interface if two Pane mode
        if (!activity.getClass().equals(MovieDetailActivity.class)){

            try {
                onFavoriteDeselectedListener = (OnFavoriteDeselectedListener) activity;
            }catch (ClassCastException ex){
                throw new ClassCastException(activity.toString() +
                        "Must implement call back interface OnFavoriteDeselectedListener");
            }
        }
    }

    private void setVerifiedValue(String value, TextView textView){
        if (value != null && !value.toString().equals("null")){
            textView.setText(value);
        }else {
            textView.setText(NOT_AVAILABLE);
        }
    }

    private boolean isMovieFavorite(String movieId, Context context){
        boolean isThisMovieFavorite = false;
        Cursor movieCursor =
                FavoriteDBOperator.findMovieById(movieId, context);

        if(movieCursor.moveToFirst()){
            //check if movie is on database
            isThisMovieFavorite = true;
        }
        if(!movieCursor.isClosed()){
            movieCursor.close();
        }
        return isThisMovieFavorite;
    }

    private void addReviews(String movieId, Context context){
        //Check if we have internet access
        if (InternetUtils.isInternetAvailable(getActivity())) {
            ProgressBar progressBar =
                    new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
            ReviewsTask task = new ReviewsTask(progressBar, context);
            task.execute(movieId);

        } else {
            CharSequence text = "No internet connection available !!";

            Toast toast = Toast.makeText(context, text, mToastDuration);
            toast.show();
        }
    }

    private void addTrailers(String movieId, Context context){
        //Check if we have internet access
        if (InternetUtils.isInternetAvailable(getActivity())) {
            ProgressBar progressBar =
                    new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
            TrailersTask task = new TrailersTask(progressBar, context);
            task.execute(movieId);

        } else {
            CharSequence text = "No internet connection available !!";

            Toast toast = Toast.makeText(context, text, mToastDuration);
            toast.show();
        }
    }

    public interface OnFavoriteDeselectedListener {
        void favoriteDeselected(boolean isDeselected);
    }
}
