package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movie_detail_container)!=null){
            mTwoPane = true;
        }
        else{
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Start the setting class for getting share preference from user.
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String title, String poster_url, String plot_synopsis, String release_date, String vote_average, int movie_id) {

        //if its a 2pane layout then create detail fragment in main activity else start detail activity using intent
        if(mTwoPane==true){

            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("poster_url", poster_url);
            args.putString("plot_synopsis", plot_synopsis);
            args.putString("release_date", release_date);
            args.putString("vote_average", vote_average);
            args.putInt("movie_id", movie_id);


            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            //(Add)replace detail fragment if it (does not)exist
            if(getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG)==null)
            {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container,fragment,DETAILFRAGMENT_TAG)
                        .commit();
            }
            else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else{

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("poster_url", poster_url);
            intent.putExtra("plot_synopsis", plot_synopsis);
            intent.putExtra("release_date", release_date);
            intent.putExtra("vote_average", vote_average);
            intent.putExtra("movie_id",movie_id);
            startActivity(intent);
        }

    }
//
//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        Log.v("Main Activity", "On Start");
//    }
//
//    @Override
//    public void onStop()
//    {
//        super.onStop();
//        Log.v("Main Activity", "On stop");
//    }
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        Log.v("Main Activity", "On onPause");
//    }
//
//    @Override
//    public void onDestroy()
//    {
//        super.onDestroy();
//        Log.v("Main Activity", "On onDestroy");
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        Log.v("Main Activity", "On onResume");
//    }
//
//    @Override
//    public void onRestart()
//    {
//        super.onRestart();
//        Log.v("Main Activity", "On onRestart");
//    }


}
