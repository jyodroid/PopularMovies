package com.jyo.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jyo.android.popularmovies.commons.InternetUtils;
import com.jyo.android.popularmovies.connection.PopMoviesTask;
import com.jyo.android.popularmovies.model.Movie;
import com.jyo.android.popularmovies.model.MovieListAdapter;
import com.jyo.android.popularmovies.moviedetails.MovieDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesFragment extends Fragment {

    private MovieListAdapter movieListAdapter;
    private static final String MOVIE_KEY = "movie_key";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    @Bind(R.id.main_progress)
    ProgressBar mProgressBar;
    @Bind(R.id.gridview_posters)
    GridView mGridView;


    public PopularMoviesFragment() {
    }

    @Override
    public void onResume() {
        if (movieListAdapter.getMoviesResult().size() == 0) {
            updateMoviesList(getActivity().getBaseContext());
        }

        if(mSharedPreferences != null &&
                mSharedPreferences.equals(PreferenceManager
                .getDefaultSharedPreferences(getActivity().getBaseContext()))){
            updateMoviesList(getActivity().getBaseContext());
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = getActivity().getBaseContext();
        sharedPreferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {

                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (movieListAdapter != null){
                            updateMoviesList(context);
                        }
                        mSharedPreferences = sharedPreferences;
                    }
                };

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        ButterKnife.bind(this, rootView);

        movieListAdapter = new MovieListAdapter(context, new ArrayList<Movie>());
        mGridView.setAdapter(movieListAdapter);

        if (savedInstanceState == null || savedInstanceState.get(MOVIE_KEY) == null) {
            updateMoviesList(context);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie selectedMovie = movieListAdapter.getItem(position);
                Intent movieDetailIntent = new Intent(context, MovieDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PopularMovies.MOVIE, selectedMovie);
                movieDetailIntent.putExtras(bundle);
                startActivity(movieDetailIntent);
            }
        });

        return rootView;
    }

    private void updateMoviesList(Context context) {

        //Check if we have internet access
        if (InternetUtils.isInternetAvailable(getActivity())) {
            PopMoviesTask task = new PopMoviesTask(movieListAdapter, mProgressBar, context);

            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());

            task.execute(preferences.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_popularity)
            ));

        } else {
            CharSequence text = "No internet connection available !!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(
                MOVIE_KEY,
                (ArrayList<? extends Parcelable>) movieListAdapter.getMoviesResult());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.get(MOVIE_KEY) != null) {
            movieListAdapter.addAll((List<Movie>) savedInstanceState.get(MOVIE_KEY));
        }
    }
}