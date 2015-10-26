package com.jyo.android.popularmovies.connection;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jyo.android.popularmovies.R;
import com.jyo.android.popularmovies.data.ReviewDBOperator;
import com.jyo.android.popularmovies.model.Review;
import com.jyo.android.popularmovies.model.ReviewListAdapter;

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
public class ReviewsTask extends AsyncTask<String, Integer, List<Review>> {

    //Error codes when retrieve data from API
    public static final int NO_API_KEY_SET = 0;
    public static final int NO_DATA_RETRIEVED = 1;
    public static final int EMPTY_STRING = 2;
    public static final int IO_EXCEPTION = 3;

    private static final String API_BASE_PATH = "http://api.themoviedb.org/3/movie/";
    private static final String LOG_TAG = ReviewsTask.class.getSimpleName();
    private static final String REVIEWS = "reviews";

    private static int mToastDuration = Toast.LENGTH_SHORT;

    private ReviewListAdapter reviewsAdapter;
    private Context context;
    private String apiKey;
    private ProgressBar mProgressBar;
    private boolean onlyList = false;
    private int mDataErrorCode = -1;
    private String mMovieId;

    public ReviewsTask(ReviewListAdapter reviewsAdapter, ProgressBar progressBar, Context context) {

        this.reviewsAdapter = reviewsAdapter;
        this.mProgressBar = progressBar;
        this.context = context;
        //Insert here your API KEY from themoviedb.org
        this.apiKey = context.getString(R.string.api_key);
    }

    public ReviewsTask(ProgressBar progressBar, Context context) {

        this.mProgressBar = progressBar;
        this.context = context;
        //Insert here your API KEY from themoviedb.org
        this.apiKey = context.getString(R.string.api_key);
        this.onlyList = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<Review> doInBackground(String... params) {

        //Preparing connection
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //JSON response as a string.
        String reviewsJsonStr = null;

        try {

            //Param names
            final String API_KEY = "api_key";

            //Param values
            mMovieId = params[0];


            //Verify Api key is set
            if (apiKey.isEmpty() || apiKey.equals("")) {
                mDataErrorCode = NO_API_KEY_SET;
                return null;
            }

            // Construct the URL
            Uri builtUri = Uri.parse(API_BASE_PATH).buildUpon()
                    .appendPath(mMovieId)
                    .appendPath(REVIEWS)
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
            reviewsJsonStr = buffer.toString();

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
            return getReviewsFromJson(reviewsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Review> result) {

        //Obtaining reviews
        if (result == null) {

            //For debug propose
            switch (mDataErrorCode) {
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
                    "We got a problem retrieving reviews info";
            Toast toast = Toast.makeText(context, text, mToastDuration);
            toast.show();
        } else {
            if (0 == result.size()) {
                if (context != null) {
                    CharSequence text = "No Reviews found";

                    Toast toast = Toast.makeText(context, text, mToastDuration);
                    toast.show();
                }
            } else {
                if (onlyList){
                    //Add review list to database
                    ReviewDBOperator.addReviewsForFavorites(result, mMovieId, context);
                }else {
                    //Clean adapter
                    reviewsAdapter.clear();
                    reviewsAdapter.addAll(result);
                }
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }

    private List<Review> getReviewsFromJson(String reviewsJsonStr) throws JSONException, MalformedURLException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String REVIEW_ID = "id";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);

        //List to store reviews
        List<Review> reviews = new ArrayList<>();

        //Iterate result list
        for (int i = 0; i < reviewsArray.length(); i++) {

            //Object from array;
            JSONObject reviewsResponse = reviewsArray.getJSONObject(i);

            //Object to store the review info
            Review review = new Review();


            //Assign values to object
            review.setAuthor(reviewsResponse.getString(AUTHOR));
            review.setContent(reviewsResponse.getString(CONTENT));
            review.setReviewId(reviewsResponse.getString(REVIEW_ID));

            reviews.add(review);
        }

        return reviews;
    }
}
