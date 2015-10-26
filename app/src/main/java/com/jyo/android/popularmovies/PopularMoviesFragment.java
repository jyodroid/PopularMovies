package com.jyo.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jyo.android.popularmovies.commons.InternetUtils;
import com.jyo.android.popularmovies.connection.PopMoviesTask;
import com.jyo.android.popularmovies.data.FavoriteDBOperator;
import com.jyo.android.popularmovies.model.Movie;
import com.jyo.android.popularmovies.model.MovieListAdapter;

import java.io.ByteArrayOutputStream;
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
    private static final String MOVIES_LISTED_KEY = "movies_listed_key";
    private static int mToastDuration = Toast.LENGTH_SHORT;
    private SharedPreferences mSharedPreferences;
    private int mListedMovies;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    OnMovieSelectedListener onMovieSelectedListenerCallback;

    @Bind(R.id.main_progress)
    ProgressBar mProgressBar;
    @Bind(R.id.gridview_posters)
    GridView mGridView;


    public PopularMoviesFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //Ensures the activity implements callback interface
        try {
            onMovieSelectedListenerCallback = (OnMovieSelectedListener) activity;
        }catch (ClassCastException ex){
            throw new ClassCastException(activity.toString() +
                    "Must implement call back interface OnMovieSelectedListener");
        }
    }

    @Override
    public void onResume() {

        if (movieListAdapter.getMoviesResult().size() == 0 ||
                movieListAdapter.getMoviesResult().size() != mListedMovies) {
            updateMoviesList(getActivity().getBaseContext());
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        final Context context = getActivity().getBaseContext();
        sharedPreferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {

                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (movieListAdapter != null) {
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

                //Add movie poster
                ImageView poster = (ImageView) view.findViewById(R.id.img_movie_poster);
                BitmapDrawable bitmapDrawable =
                        ((BitmapDrawable) poster.getDrawable());
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                selectedMovie.setPosterBM(imageInByte);

                //Pass movie
                Bundle bundle = new Bundle();
                bundle.putParcelable(PopularMovies.MOVIE, selectedMovie);

                onMovieSelectedListenerCallback.onMovieSelected(selectedMovie);
            }
        });

        return rootView;
    }

    public void updateMoviesList(Context context) {

        //Obtain shared preferences
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        //Check if sort order is favorite
        if (preferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity)
        ).equals(getString(R.string.pref_sort_favorite))) {

            //Obtain movies from DB
            FavoriteDBOperator.obtainFavoriteMovies(movieListAdapter, mProgressBar, context);

        } else {
            //Check if we have internet access
            if (InternetUtils.isInternetAvailable(getActivity())) {
                PopMoviesTask task = new PopMoviesTask(movieListAdapter, mProgressBar, context);

                task.execute(preferences.getString(
                        getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_popularity)
                ));

            } else {
                CharSequence text = "No internet connection available !!";

                Toast toast = Toast.makeText(context, text, mToastDuration);
                toast.show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(
                MOVIE_KEY,
                (ArrayList<? extends Parcelable>) movieListAdapter.getMoviesResult());
        mListedMovies = movieListAdapter.getMoviesResult().size();
        outState.putInt(MOVIES_LISTED_KEY, mListedMovies);
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
        if (savedInstanceState != null && savedInstanceState.get(MOVIES_LISTED_KEY) != null) {
            mListedMovies = savedInstanceState.getInt(MOVIES_LISTED_KEY);
        }
    }

    public interface OnMovieSelectedListener {
        public void onMovieSelected(Movie movie);
    }
}