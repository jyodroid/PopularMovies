package com.jyo.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JohnTangarife on 5/08/15.
 */
public class MovieListAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private List<Movie> moviesResult;
    private Context context;

    @Bind(R.id.img_movie_poster) ImageView poster;

    public MovieListAdapter(Context context, List<Movie> movies){
        super(context, R.layout.list_item_movies, movies);
        this.context = context;
        this.moviesResult = movies;
    }

    public List<Movie> getMoviesResult(){
        return this.moviesResult;
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

        ButterKnife.bind(this, item);
        Movie movie = getItem(position);

        if(movie.getPosterURL() != null && movie.getPosterURL() != ""){
            Picasso.with(context).load(movie.getPosterURL()).into(poster);
        }

        return item;
    }

}
