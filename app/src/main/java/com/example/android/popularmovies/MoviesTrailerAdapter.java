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
public class MoviesTrailerAdapter extends ArrayAdapter<Trailer> {
    private Context mContext;

    // references to our movie objects
    public ArrayList<Trailer> moviesTrailerData;

    //Constructor
    public MoviesTrailerAdapter(Context c,ArrayList<Trailer> moviesTrailerData) {
        super(c,0,moviesTrailerData);
        mContext = c;
        this.moviesTrailerData = moviesTrailerData;
    }

    //return number of moviesTrailer
    public int getCount() {
        return moviesTrailerData.size();
    }

    //return movie at particular index
    public Trailer getItem(int position) {
        return moviesTrailerData.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    //Updates movie data and notify grid view for update
    public void setMoviesTrailerData(ArrayList<Trailer> moviesTrailerData)
    {
        this.moviesTrailerData = moviesTrailerData;
        notifyDataSetChanged();
    }

    public ArrayList<Trailer> getMoviesTrailerData()
    {
        return this.moviesTrailerData;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textview;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_view,parent,false);
        }
        textview = (TextView)convertView.findViewById(R.id.trailer_displayText);
        textview.setText(getItem(position).display_text);

        return convertView;
    }
}
