package com.jyo.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        viewHolder.originalTitle.setText(movie.getTitle());
        viewHolder.releaseDate.setText(movie.getReleaseDate());
        viewHolder.plot.setText(movie.getPlot());
        viewHolder.rate.setText(String.valueOf(movie.getRating()));

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

}
