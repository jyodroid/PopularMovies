package com.jyo.android.popularmovies.moviedetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyo.android.popularmovies.PopularMovies;
import com.jyo.android.popularmovies.R;
import com.jyo.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private static final String NOT_AVAILABLE = "Not available";
    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Receive the movie object
        Intent intent = getActivity().getIntent();
        Movie movie = intent.getParcelableExtra(PopularMovies.MOVIE);

        //Prepare UI elements
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        //Sync view
        ViewHolder viewHolder = new ViewHolder(rootView);

        //Fill poster
        if (movie.getPosterURL() != null && movie.getPosterURL() != "") {
            Picasso.with(getActivity()
                    .getBaseContext())
                    .load(movie.getPosterURL())
                    .into(viewHolder.poster);
        }

        //Fill movie data
        setVerifiedValue(movie.getTitle(), viewHolder.originalTitle);
        setVerifiedValue(movie.getReleaseDate(), viewHolder.releaseDate);
        setVerifiedValue(movie.getPlot(), viewHolder.plot);
        setVerifiedValue(String.valueOf(movie.getRating()), viewHolder.rate);

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

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void setVerifiedValue(String value, TextView textView){
        if (value != null && !value.toString().equals("null")){
            textView.setText(value);
        }else {
            textView.setText(NOT_AVAILABLE);
        }
    }
}
