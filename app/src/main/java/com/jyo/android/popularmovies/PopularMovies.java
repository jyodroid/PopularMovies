package com.jyo.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jyo.android.popularmovies.model.Movie;
import com.jyo.android.popularmovies.moviedetails.MovieDetailActivity;
import com.jyo.android.popularmovies.moviedetails.MovieDetailActivityFragment;
import com.jyo.android.popularmovies.moviedetails.MovieDetailTabsFragment;

public class PopularMovies extends AppCompatActivity
        implements PopularMoviesFragment.OnMovieSelectedListener,
        MovieDetailActivityFragment.OnFavoriteDeselectedListener{

    //Intent's data name
    public static final String MOVIE = "movie";
    public static final String PREFERENCES_KEY = "movie";
    private static final String DETAILFRAGMENT_TAG = "DFT";
    private boolean mTwoPane = false;
    private boolean mPreferencesChanged = false;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popular_movies);

        if (findViewById(R.id.detail_container) != null) {

            sharedPreferenceChangeListener =
                    new SharedPreferences.OnSharedPreferenceChangeListener() {

                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                            mPreferencesChanged = true;
                        }
                    };

            PreferenceManager.getDefaultSharedPreferences(this)
                    .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new Fragment(),
                                DETAILFRAGMENT_TAG)
                        .commit();
            }

            mTwoPane = true;
        }
    }

    @Override
    public void onResume() {

        if (mPreferencesChanged) {

            //If preferences change clean detail pane
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, new Fragment(),
                            DETAILFRAGMENT_TAG)
                    .commit();
            mPreferencesChanged = false;
        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(Movie movie) {

        //Add fragment

        //Pass movie
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE, movie);

        if(mTwoPane){

            MovieDetailTabsFragment tabsFragment = new MovieDetailTabsFragment();
            tabsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, tabsFragment,
                            DETAILFRAGMENT_TAG)
                    .commit();
        }else {

            Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);

            movieDetailIntent.putExtras(bundle);
            startActivity(movieDetailIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PREFERENCES_KEY,
                PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_popularity)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void favoriteDeselected(boolean isDeselected) {

        //Obtain shared preferences
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        if(mTwoPane && isDeselected &&
                preferences.getString(
                        getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_popularity)
                ).equals(getString(R.string.pref_sort_favorite))){
            PopularMoviesFragment moviesFragment =
                    (PopularMoviesFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.list_fragment);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, new Fragment(),
                            DETAILFRAGMENT_TAG)
                    .commit();

            moviesFragment.updateMoviesList(this);
        }
    }
}
