package com.jyo.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by JohnTangarife on 5/08/15.
 */
public class MovieListAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private List<Movie> moviesResult;
    private Context context;

    public MovieListAdapter(Context context, List<Movie> movies){
        super(context, R.layout.list_item_movies, movies);
        this.context = context;
        this.moviesResult = movies;
    }

    @Override
    public int getCount() {
        return moviesResult.size();
    }

    @Override
    public Movie getItem(int position) {
        return moviesResult.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.list_item_movies, null);
        Movie movie = getItem(position);

        ImageView imageView = (ImageView) item.findViewById(R.id.img_movie_poster);

        if(movie.getPosterURL() != null && movie.getPosterURL() != ""){
            Picasso.with(context).load(movie.getPosterURL()).into(imageView);
        }

        return item;
    }

}
