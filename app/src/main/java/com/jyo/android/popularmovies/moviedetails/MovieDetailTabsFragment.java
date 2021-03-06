package com.jyo.android.popularmovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyo.android.popularmovies.PopularMovies;
import com.jyo.android.popularmovies.R;
import com.jyo.android.popularmovies.model.Movie;

public class MovieDetailTabsFragment extends Fragment {

    FragmentTabHost tabHost;
    Movie mMovie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail_tabs, container, false);

        //Receive the movie object
        Intent intent = getActivity().getIntent();
        mMovie = intent.getParcelableExtra(PopularMovies.MOVIE);

        if (mMovie == null) {
            Bundle bundle = this.getArguments();
            mMovie = bundle.getParcelable(PopularMovies.MOVIE);
        }

        Context context = getActivity().getBaseContext();
        //TabHost
        tabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        tabHost.setup(context, getChildFragmentManager(), android.R.id.tabcontent);

        //add tabs Fragments
        Bundle bundle = new Bundle();
        bundle.putParcelable(PopularMovies.MOVIE, mMovie);

        //Bring Trailers

        //Bring reviews


        tabHost.addTab(tabHost.newTabSpec("movie_details")
                        .setIndicator(getTabIndicator(context, getString(R.string.tab_movie_details))),
                MovieDetailActivityFragment.class, bundle);
        tabHost.addTab(tabHost.newTabSpec("movie_trailers")
                        .setIndicator(getTabIndicator(context, getString(R.string.tab_movie_trailers))),
                MovieTrailerFragment.class, bundle);
        tabHost.addTab(tabHost.newTabSpec("movie_reviews")
                        .setIndicator(getTabIndicator(context, getString(R.string.tab_movie_reviews))),
                MovieReviewFragment.class, bundle);

        return rootView;
    }

    private View getTabIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tabTitle = (TextView) view.findViewById(R.id.tab_title);
        tabTitle.setText(title);
        return view;
    }
}
