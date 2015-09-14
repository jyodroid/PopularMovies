package com.jyo.android.popularmovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jyo.android.popularmovies.PopularMovies;
import com.jyo.android.popularmovies.R;
import com.jyo.android.popularmovies.commons.InternetUtils;
import com.jyo.android.popularmovies.connection.TrailersTask;
import com.jyo.android.popularmovies.model.Movie;
import com.jyo.android.popularmovies.model.Trailer;
import com.jyo.android.popularmovies.model.TrailerListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JohnTangarife on 12/09/15.
 */
public class MovieTrailerFragment extends Fragment {

    private static final String LOG_TAG = MovieTrailerFragment.class.getSimpleName();
    private TrailerListAdapter adapter;
    private ProgressBar mProgresBar;
    private String movieId;

    @Override
    public void onResume() {

        if (adapter.getTrailerResult().size() == 0 && movieId != null && !movieId.isEmpty()) {
            updateTrailersList(getActivity().getBaseContext(), movieId);
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Receive the movie object
        Intent intent = getActivity().getIntent();
        Movie movie = intent.getParcelableExtra(PopularMovies.MOVIE);

        //Set the movieId
        movieId = movie.getMovieID();

        //Create adapter
        adapter = new TrailerListAdapter(getActivity(), new ArrayList<Trailer>());

        //Prepare UI elements
        View rootView = inflater.inflate(R.layout.fragment_movie_trailer, container, false);
        ViewHolder holder = new ViewHolder(rootView);
        holder.originalTitle.setText(movie.getTitle());
        holder.trailersList.setAdapter(adapter);
        mProgresBar = holder.progressBar;

        //get trailers list
        updateTrailersList(getActivity().getBaseContext(), movieId);


        holder.trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trailer trailer = adapter.getItem(position);
                Intent videoIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse( "vnd.youtube" + trailer.getKey()));

                //Lets see if user have youtube app installed
                List<ResolveInfo> list =
                        getActivity()
                                .getPackageManager()
                                .queryIntentActivities(
                                        videoIntent,
                                        PackageManager.MATCH_DEFAULT_ONLY);

                //If video is from youTube and user have youtube app
                if (trailer.isFromYouTube() && list.size() != 0) {
                    //Youtube Intent
                    getActivity().startActivity(videoIntent);

                } else if (list.size() == 0 && trailer.isFromYouTube()) {
                    //Video is from youtube but no youtube app on device
                    //Browser Intent
                    final String youTubeBasePath = "http://www.youtube.com/watch";
                    final String V = "v";
                    Uri uri = Uri.parse(youTubeBasePath).buildUpon()
                            .appendQueryParameter(V, trailer.getKey())
                            .build();
                    videoIntent = new Intent(Intent.ACTION_VIEW, uri);
                    getActivity().startActivity(videoIntent);
                }else{
                    //Video from site not supported in this app for now
                    CharSequence text = "Video from " + trailer.getSite() + "Not supported yet";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(getActivity(), text, duration);
                    toast.show();
                }
            }
        });

        return rootView;
    }

    static class ViewHolder {
        @Bind(R.id.txt_trailer_original_title)
        TextView originalTitle;
        @Bind(R.id.list_view_trailers)
        ListView trailersList;
        @Bind(R.id.trailers_progress)
        ProgressBar progressBar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void updateTrailersList(Context context, String movieId) {

        //Check if we have internet access
        if (InternetUtils.isInternetAvailable(getActivity())) {
            TrailersTask task = new TrailersTask(adapter, mProgresBar, context);
            task.execute(movieId);

        } else {
            CharSequence text = "No internet connection available !!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

}
