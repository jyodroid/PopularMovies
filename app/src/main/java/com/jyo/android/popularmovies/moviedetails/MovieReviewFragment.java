package com.jyo.android.popularmovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jyo.android.popularmovies.PopularMovies;
import com.jyo.android.popularmovies.R;
import com.jyo.android.popularmovies.commons.InternetUtils;
import com.jyo.android.popularmovies.connection.ReviewsTask;
import com.jyo.android.popularmovies.model.Movie;
import com.jyo.android.popularmovies.model.Review;
import com.jyo.android.popularmovies.model.ReviewListAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JohnTangarife on 12/09/15.
 */
public class MovieReviewFragment extends Fragment {

    private ReviewListAdapter adapter;
    private ProgressBar mProgresBar;
    private String movieId;

    @Override
    public void onResume() {

        if (adapter.getReviewsResult().size() == 0 && movieId != null && !movieId.isEmpty()) {
            updateReviewsList(getActivity().getBaseContext(), movieId);
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Receive the movie object
        Intent intent = getActivity().getIntent();
        Movie movie = intent.getParcelableExtra(PopularMovies.MOVIE);
        movieId = movie.getMovieID();

        adapter = new ReviewListAdapter(getActivity(), new ArrayList<Review>());

        //Prepare UI elements
        View rootView = inflater.inflate(R.layout.fragment_movie_review, container, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        viewHolder.originalTitle.setText(movie.getTitle());
        mProgresBar = viewHolder.progressBar;
        viewHolder.reviewsList.setAdapter(adapter);

        //get reviews list
        updateReviewsList(getActivity().getBaseContext(), movieId);

        return rootView;
    }

    static class ViewHolder {
        @Bind(R.id.txt_review_original_title)
        TextView originalTitle;
        @Bind(R.id.list_view_reviews)
        ListView reviewsList;
        @Bind(R.id.review_progress)
        ProgressBar progressBar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void updateReviewsList(Context context, String movieId) {

        //Check if we have internet access
        if (InternetUtils.isInternetAvailable(getActivity())) {
            ReviewsTask task = new ReviewsTask(adapter, mProgresBar, context);
            task.execute(movieId);

        } else {
            CharSequence text = "No internet connection available !!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}
