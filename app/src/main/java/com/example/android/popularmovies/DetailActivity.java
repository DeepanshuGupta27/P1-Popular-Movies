package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState==null)
        {

            Intent intent = getIntent();

            String poster_url = "",release_date = "",plot_synopsis = "",title = "",vote_average = "";
            int movie_id = 0;

            //pull out all the extra parameter sent with intent and populate required views.
            if (intent != null && intent.hasExtra("title")) {
                title = intent.getExtras().getString("title");
            }

            if (intent != null && intent.hasExtra("poster_url")) {
                poster_url = intent.getExtras().getString("poster_url");
            }

            if (intent != null && intent.hasExtra("plot_synopsis")) {
                plot_synopsis = intent.getExtras().getString("plot_synopsis");
            }

            if (intent != null && intent.hasExtra("release_date")) {
                release_date = intent.getExtras().getString("release_date");
            }

            if (intent != null && intent.hasExtra("vote_average")) {
                vote_average  = intent.getExtras().getString("vote_average");
            }

            if (intent != null && intent.hasExtra("movie_id")) {
                movie_id  = intent.getExtras().getInt("movie_id");
            }

            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("poster_url", poster_url);
            args.putString("plot_synopsis", plot_synopsis);
            args.putString("release_date", release_date);
            args.putString("vote_average", vote_average);
            args.putInt("movie_id", movie_id);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container,fragment)
                    .commit();

        }

    }

//
//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        Log.v("Detail Activity", "On Start");
//    }
//
//    @Override
//    public void onStop()
//    {
//        super.onStop();
//        Log.v("Detail Activity", "On stop");
//    }
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        Log.v("Detail Activity", "On onPause");
//    }
//
//    @Override
//    public void onDestroy()
//    {
//        super.onDestroy();
//        Log.v("Detail Activity", "On onDestroy");
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        Log.v("Detail Activity", "On onResume");
//    }
//
//    @Override
//    public void onRestart()
//    {
//        super.onRestart();
//        Log.v("Detail Activity", "On onRestart");
//    }


}
