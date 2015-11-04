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
public class ReviewListAdapter extends ArrayAdapter<Review> {
    private static final String LOG_TAG = ReviewListAdapter.class.getSimpleName();

    private List<Review> reviewsResult;
    private Context context;

    public ReviewListAdapter(Context context, List<Review> reviews){
        super(context, R.layout.list_item_reviews, reviews);
        this.context = context;
        this.reviewsResult = reviews;
    }

    public List<Review> getReviewsResult(){
        return this.reviewsResult;
    }

    @Override
    public int getCount() {
        return reviewsResult.size();
    }

    @Override
    public Review getItem(int position) {
        return reviewsResult.get(position);
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
        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_item_reviews, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Review review = getItem(position);

        viewHolder.reviewContent.setText(
                String.format(context.getString(R.string.review_content),
                        review.getContent())
        );
        viewHolder.reviewAuthor.setText(
                String.format(context.getString(R.string.review_author),
                        review.getAuthor())
        );
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.txt_review_content)
        TextView reviewContent;
        @Bind(R.id.txt_review_author)
        TextView reviewAuthor;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
