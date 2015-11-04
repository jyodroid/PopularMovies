package com.jyo.android.popularmovies.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jyo.android.popularmovies.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JohnTangarife on 5/08/15.
 */
public class TrailerListAdapter extends ArrayAdapter<Trailer> {
    private static final String LOG_TAG = TrailerListAdapter.class.getSimpleName();

    private List<Trailer> trailersResult;
    private Context context;

    public TrailerListAdapter(Context context, List<Trailer> trailers){
        super(context, R.layout.list_item_trailer, trailers);
        this.context = context;
        this.trailersResult = trailers;
    }

    public List<Trailer> getTrailerResult(){
        return this.trailersResult;
    }

    @Override
    public int getCount() {
        return trailersResult.size();
    }

    @Override
    public Trailer getItem(int position) {
        return trailersResult.get(position);
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
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_trailer, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Trailer trailer = getItem(position);
        viewHolder.trailerTitle.setText(trailer.getName());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.txt_movie_trailer_name)
        TextView trailerTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
