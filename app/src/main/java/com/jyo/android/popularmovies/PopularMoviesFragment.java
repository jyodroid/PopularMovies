package com.jyo.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesFragment extends Fragment {

    private MovieListAdapter movieListAdapter;

    public PopularMoviesFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesList(getActivity().getBaseContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = getActivity().getBaseContext();

        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_posters);

        movieListAdapter = new MovieListAdapter(context, new ArrayList<Movie>());

        gridView.setAdapter(movieListAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie selectedMovie = movieListAdapter.getItem(position);
                Intent movieDetailIntent = new Intent(context, MovieDetailActivity.class);
                movieDetailIntent.putExtra("movie", selectedMovie);
                startActivity(movieDetailIntent);
            }
        });

        return rootView;
    }

    private void updateMoviesList(Context context){

        PopMoviesTask task = new PopMoviesTask(movieListAdapter, context);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        task.execute(preferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity)
        ));
    }
}