package com.jyo.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Receive the movie object
        Intent intent = getActivity().getIntent();
        Movie movie = (Movie) intent.getSerializableExtra("movie");

        //Prepare UI elements
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ImageView poster = (ImageView) rootView.findViewById(R.id.img_movie_detail_poster);
        TextView originalTitle = (TextView) rootView.findViewById(R.id.txt_det_original_title);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.txt_det_release);
        TextView plot = (TextView) rootView.findViewById(R.id.txt_det_plot);
        TextView rate = (TextView) rootView.findViewById(R.id.txt_det_rating);

        //Fill poster
        if(movie.getPosterURL() != null && movie.getPosterURL() != ""){
            Picasso.with(getActivity().getBaseContext()).load(movie.getPosterURL()).into(poster);
        }

        //Fill movie data
        originalTitle.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        plot.setText(movie.getPlot());
        rate.setText(String.valueOf(movie.getRating()));

        return rootView;
    }
}
