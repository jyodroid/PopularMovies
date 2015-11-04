package com.jyo.android.popularmovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.jyo.android.popularmovies.data.TrailerDBOperator;
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
public class MovieTrailerFragment extends Fragment implements TrailersTask.OnTrailersListUpdated{

    private static final String LOG_TAG = MovieTrailerFragment.class.getSimpleName();
    private static int mToastDuration = Toast.LENGTH_SHORT;
    private static final String YOUTUBEBASEPATH = "http://www.youtube.com/watch";
    private static final String V = "v";
    private final TrailersTask.OnTrailersListUpdated onTrailersListUpdated = this;

    private TrailerListAdapter trailersAdapter;
    private ProgressBar mProgressBar;
    private String movieId;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_movie_detail, menu);

        MenuItem item = menu.findItem(R.id.share_trailer);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setSharedIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Receive the movie object
        Bundle bundle = this.getArguments();
        final Movie movie = bundle.getParcelable(PopularMovies.MOVIE);

        //Set the movieId
        movieId = movie.getMovieID();

        //Create adapter
        trailersAdapter = new TrailerListAdapter(getActivity(), new ArrayList<Trailer>());

        //Prepare UI elements
        View rootView = inflater.inflate(R.layout.fragment_movie_trailer, container, false);
        ViewHolder holder = new ViewHolder(rootView);
        holder.originalTitle.setText(movie.getTitle());
        holder.trailersList.setAdapter(trailersAdapter);

        mProgressBar = holder.progressBar;

        //get trailers list
        updateTrailersList(getActivity().getBaseContext(), movieId);

        holder.trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Trailer trailer = trailersAdapter.getItem(position);
                Intent videoIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("vnd.youtube" + trailer.getKey()));

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
                    Uri uri = Uri.parse(YOUTUBEBASEPATH).buildUpon()
                            .appendQueryParameter(V, trailer.getKey())
                            .build();
                    videoIntent = new Intent(Intent.ACTION_VIEW, uri);
                    getActivity().startActivity(videoIntent);
                } else {
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

        //Obtain shared preferences
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (preferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity)
        ).equals(getString(R.string.pref_sort_favorite))) {

            //Obtain reviews from DB
            TrailerDBOperator.obtainTrailers(
                    trailersAdapter, mProgressBar, movieId, getActivity().getBaseContext());
            setSharedIntent();

        } else {
            //Check if we have internet access
            if (InternetUtils.isInternetAvailable(getActivity())) {
                TrailersTask task = new TrailersTask(
                        trailersAdapter, mProgressBar, getActivity().getBaseContext(),
                        onTrailersListUpdated);
                task.execute(movieId);

            } else {
                CharSequence text = "No internet connection available !!";

                Toast toast = Toast.makeText(context, text, mToastDuration);
                toast.show();
            }
        }
    }

    private void setSharedIntent(){
        //set share intent
        if (trailersAdapter.getTrailerResult().size() > 0){
            Uri uri = Uri.parse(YOUTUBEBASEPATH).buildUpon()
                    .appendQueryParameter(V, trailersAdapter.getTrailerResult().get(0).getKey())
                    .build();

            Intent shareTrailerIntent =
                    ShareCompat.IntentBuilder
                            .from(getActivity())
                            .setType("text/plain")
                            .setText(uri.toString()).getIntent();
            shareTrailerIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

            if (mShareActionProvider != null){
                mShareActionProvider.setShareIntent(shareTrailerIntent);
                mShareActionProvider.refreshVisibility();
            }
        }else {
            if (mShareActionProvider != null){
                mShareActionProvider.setShareIntent(new Intent());
                mShareActionProvider.refreshVisibility();
            }
        }
    }

    @Override
    public void trailersListUpdated(int trailers) {
        if (trailers > 0){
            setSharedIntent();
        }
    }

}
