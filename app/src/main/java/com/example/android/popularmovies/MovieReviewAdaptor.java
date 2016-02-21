package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by deepanshugupta on 19/02/16.
 */
public class MovieReviewAdaptor extends ArrayAdapter<Review> {
    private Context mContext;

    // references to our movie review objects
    public ArrayList<Review> moviesReviewData;

    //Constructor
    public MovieReviewAdaptor(Context c,ArrayList<Review> moviesReviewData) {
        super(c,0,moviesReviewData);
        mContext = c;
        this.moviesReviewData = moviesReviewData;
    }

    //return number of moviesReview
    public int getCount() {
        return moviesReviewData.size();
    }

    //return movie review at particular index
    public Review getItem(int position) {
        return moviesReviewData.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    //Updates movie review data and notify grid view for update
    public void setMoviesReviewData(ArrayList<Review> moviesReviewData)
    {
        this.moviesReviewData = moviesReviewData;
        notifyDataSetChanged();
    }

    public ArrayList<Review> getMoviesReviewData()
    {
        return this.moviesReviewData;
    }

    //Create View for each trailer
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView review;
        TextView author;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_view,parent,false);
        }
        review = (TextView)convertView.findViewById(R.id.text_moviereview);
        review.setText(getItem(position).review);

        author = (TextView)convertView.findViewById(R.id.text_moviereviewauthor);
        author.setText(getItem(position).author);

        return convertView;
    }
}
