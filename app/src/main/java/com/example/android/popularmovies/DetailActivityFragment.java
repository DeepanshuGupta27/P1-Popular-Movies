package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);

        //get the intent sent from main activity
        Intent intent = getActivity().getIntent();

        //pull out all the extra parameter sent with intent and populate required views.
        if(intent!=null && intent.hasExtra("title")){
            ((TextView)rootview.findViewById(R.id.title)).setText(intent.getExtras().getString("title"));
        }

        if(intent!=null && intent.hasExtra("poster_url")){
            ImageView view = (ImageView)rootview.findViewById(R.id.movie_poster);
            Picasso.with(getActivity()).load(intent.getExtras().getString("poster_url")).into(view);
        }

        if(intent!=null && intent.hasExtra("plot_synopsis")){
            ((TextView)rootview.findViewById(R.id.plot_synopsis)).setText(intent.getExtras().getString("plot_synopsis"));
        }

        if(intent!=null && intent.hasExtra("release_date")){
            ((TextView)rootview.findViewById(R.id.release_date)).setText(intent.getExtras().getString("release_date"));
        }

        if(intent!=null && intent.hasExtra("vote_average")){
            ((TextView)rootview.findViewById(R.id.vote_average)).setText(intent.getExtras().getString("vote_average"));
        }
        return rootview;
    }
}
