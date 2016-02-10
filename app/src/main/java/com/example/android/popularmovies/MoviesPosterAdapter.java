package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by deepanshugupta on 06/02/16.
 */
public class MoviesPosterAdapter extends ArrayAdapter<Movie> {
    private Context mContext;

    // references to our movie objects
    private ArrayList<Movie> moviesData;

    //Constructor
    public MoviesPosterAdapter(Context c,ArrayList<Movie> moviesData) {
        super(c,0,moviesData);
        mContext = c;
        this.moviesData = moviesData;
    }

    //return number of movies
    public int getCount() {
        return moviesData.size();
    }

    //return movie at particular index
    public Movie getItem(int position) {
        return moviesData.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    //Updates movie data and notify grid view for update
    public void setMoviesData(ArrayList<Movie> moviesData)
    {
        this.moviesData = moviesData;
        notifyDataSetChanged();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            View rootview = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie,parent,false);
            imageView = (ImageView)rootview.findViewById(R.id.grid_view_movie_poster);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(1,1,1,1);

        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load(moviesData.get(position).movie_poster_url).into(imageView);
        //Log.v("Adapter","IN movie adapter");
        return imageView;
    }
}
