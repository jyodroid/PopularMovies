package com.jyo.android.popularmovies.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jyo.android.popularmovies.R;
import com.squareup.picasso.Callback;
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

    public MovieListAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.list_item_movies, movies);
        this.context = context;
        this.moviesResult = movies;
    }

    public List<Movie> getMoviesResult() {
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

        LayoutInflater inflater =
                (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder viewHolder;
        final Movie movie = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_movies, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Fill poster
        if (null != movie.getPosterBA()) {
            Bitmap posterBm =
                    BitmapFactory.decodeByteArray(
                            movie.getPosterBA(), 0, movie.getPosterBA().length);
            viewHolder.poster.setImageBitmap(posterBm);
        }else if (null != movie.getPosterURL() && "" != movie.getPosterURL()) {
            Picasso.with(context).load(movie.getPosterURL())
                    .into(viewHolder.poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(LOG_TAG, "Image loaded " + movie.getPosterURL());
                        }

                        @Override
                        public void onError() {
                            Log.d(LOG_TAG, "Image no loaded " + movie.getPosterURL());
                            Picasso.with(context).load(R.drawable.no_image)
                                    .into(viewHolder.poster);
                        }
                    });
        }else {
            Picasso.with(context).load(R.drawable.no_image).into(viewHolder.poster);
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.img_movie_poster)
        ImageView poster;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}