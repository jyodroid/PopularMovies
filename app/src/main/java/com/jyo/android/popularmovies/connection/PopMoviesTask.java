package com.jyo.android.popularmovies.connection;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jyo.android.popularmovies.R;
import com.jyo.android.popularmovies.model.Movie;
import com.jyo.android.popularmovies.model.MovieListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JohnTangarife on 5/08/15.
 * UDACITY project
 */
public class PopMoviesTask extends AsyncTask<String, Integer, List<Movie>>{

    //Error codes when retrieve data from API
    public static final int NO_API_KEY_SET = 0;
    public static final int NO_DATA_RETRIEVED = 1;
    public static final int EMPTY_STRING = 2;
    public static final int IO_EXCEPTION = 3;


    private static final String API_BASE_PATH = "http://api.themoviedb.org/3/discover/movie";
    private static final String LOG_TAG = PopMoviesTask.class.getSimpleName();
    private MovieListAdapter moviesAdapter;
    private Context context;
    private String apiKey;
    private ProgressBar mProgressBar;
    private int mDataErrorCode = -1;

    public PopMoviesTask(MovieListAdapter moviesAdapter, ProgressBar progressBar, Context context) {

        this.moviesAdapter = moviesAdapter;
        this.mProgressBar = progressBar;
        this.context = context;
        //Insert here your API KEY from themoviedb.org
        this.apiKey = context.getString(R.string.api_key);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        //Preparing connection
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //JSON response as a string.
        String moviesJsonStr = null;

        try {

            //Param names
            final String SORT_BY_PARAMETER = "sort_by";
            final String API_KEY = "api_key";

            //Param values
            String sortTypeParam = params[0]+"."+"desc";

            if (apiKey.isEmpty() || apiKey.equals("")){
                mDataErrorCode = NO_API_KEY_SET;
                return null;
            }
            // Construct the URL
            Uri builtUri = Uri.parse(API_BASE_PATH).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAMETER, sortTypeParam)
                    .appendQueryParameter(API_KEY, apiKey)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                mDataErrorCode = NO_DATA_RETRIEVED;
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                mDataErrorCode = EMPTY_STRING;
                return null;
            }
            moviesJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            mDataErrorCode = IO_EXCEPTION;
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        //Parsing the movie JSON and return list of movies
        try {
            return getMoviesFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> result) {
//        clean adapter
        moviesAdapter.clear();

        //Obtaining movies
        if (result == null){

            //For debug propose
            switch (mDataErrorCode){
                case NO_API_KEY_SET:
                    Log.e(LOG_TAG, "No Api key set for themoviedb.org API");
                    break;
                case NO_DATA_RETRIEVED:
                    Log.e(LOG_TAG, "No data retrieved from query on themoviedb.org API");
                    break;
                case EMPTY_STRING:
                    Log.e(LOG_TAG, "Empty string retrieved from query on themoviedb.org API");
                    break;
                case IO_EXCEPTION:
                    Log.e(LOG_TAG, "IO Exception reading JSON String from themoviedb.org API");
                    break;
            }

            CharSequence text =
                    "We got a problem retrieving movies info";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }else {
            if(0 == result.size()){
                if(context != null){
                    CharSequence text = "No Movies found";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }else{
                moviesAdapter.addAll(result);
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private List<Movie> getMoviesFromJson(String moviesJsonStr) throws JSONException, MalformedURLException {

        //Constants for build the poster path
        final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p";
        final String POSTER_SIZE = "w185";

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String ID = "id";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        //List to store movies
        List<Movie> movies = new ArrayList<>();

        //Iterate result list
        for(int i = 0; i < moviesArray.length(); i++) {

            //Object from array;
            JSONObject movieResponse = moviesArray.getJSONObject(i);

            //Object to store the movie info
            Movie movie = new Movie();

            //Assign values to object
            movie.setMovieID(movieResponse.getString(ID));
            movie.setTitle(movieResponse.getString(ORIGINAL_TITLE));
            movie.setPlot(movieResponse.getString(OVERVIEW));
            movie.setRating(movieResponse.getDouble(VOTE_AVERAGE));
            movie.setReleaseDate(movieResponse.getString(RELEASE_DATE));

            //Construct the full url for the poster
            String posterUrl = movieResponse.getString(POSTER_PATH).substring(1);

            Uri builtUri = Uri.parse(POSTER_BASE_PATH).buildUpon()
                    .appendPath(POSTER_SIZE)
                    .appendPath(posterUrl)
                    .build();

            //Assign to movie the Poster URL
            movie.setPosterURL(builtUri.toString());
            //Add movie to the List
            movies.add(movie);
        }

        return movies;
    }
}
