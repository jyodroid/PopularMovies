package com.jyo.android.popularmovies.moviedetails;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jyo.android.popularmovies.R;

public class MovieDetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new MovieDetailTabsFragment())
                    .commit();
        }
    }
}
