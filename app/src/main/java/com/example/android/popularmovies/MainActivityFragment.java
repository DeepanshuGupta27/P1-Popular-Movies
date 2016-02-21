package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private MoviesPosterAdapter moviesAdapter = null;
    private int gridViewPosition = GridView.INVALID_POSITION;
    private final static String SELECTED_KEY = "selected_key";
    private GridView gridview;
    private SharedPreferences sortCriteriaPreference;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these
    // must change.
    private static final int COL_TITLE = 0;
    private static final int COL_RELEASE_DATE = 1;
    private static final int COL_MOVIE_POSTER_URL = 2;
    private static final int COL_VOTE_AVG = 3;
    private static final int COL_PLOT_SYNOPSIS = 4;
    private static final int COL_MOVIE_ID = 5;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Adapter that will bind the data coming from the moviedb with movie poster grid view
        moviesAdapter = new MoviesPosterAdapter(getActivity(),new ArrayList<Movie>());

        //Sort Criteria Preference Manager
        sortCriteriaPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        //Saving movies data that will be restored on device rotation
        outState.putParcelableArrayList("movies", moviesAdapter.getMoviesData());

        //Saving gridview position that will be restored on device rotation
        if(gridViewPosition!=GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, gridViewPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        //Get gridview
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setDrawSelectorOnTop(false);

        //Set the adapter for this gridview
        gridview.setAdapter(moviesAdapter);

        //Open detail activity once poster is clicked
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                updateDetailFragment(position);
                gridViewPosition = position;
                //gridview.setItemChecked(position,true);
            }

        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //Check if there is any saveInstance, if there is an instance restore it else fill the movie adapter
        if(savedInstanceState==null || !savedInstanceState.containsKey("movies")) {
            //get the sort criteria
            String sort_criteria = sortCriteriaPreference.getString(getString(R.string.pref_sort_criteria_key), getString(R.string.pref_sort_critera_defaultvalue));

            //get the data in movieAdapter based on sort criteria
            showMoviePoster(sort_criteria);
            Global.sortCriteria = sort_criteria;
        }
        else{
            //Restore movie adapter
            moviesAdapter.moviesData = savedInstanceState.getParcelableArrayList("movies");

            if(savedInstanceState.containsKey(SELECTED_KEY)){
                gridViewPosition = savedInstanceState.getInt(SELECTED_KEY,0);

                //Scroll gridview to its actual position once rotation is done
                gridview.smoothScrollToPosition(gridViewPosition);
            }
        }


    }
    //Class for fetching movie data on worker thread
    private class FetchPopularMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchPopularMovies.class.getSimpleName();


        @Override
        protected void onPostExecute(ArrayList<Movie> movieDetails) {

            if (movieDetails != null) {
                //this will set moviesData ArrayList and will notify grid view for the data change
                moviesAdapter.setMoviesData(movieDetails);

                if(movieDetails.size()>0 && getActivity().findViewById(R.id.movie_detail_container)!=null)
                {
                    updateDetailFragment(0);
                }
            }

        }

        /**
         * Take the String representing the complete movie data in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Movie> getPopularMoviesThumbnailsJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULT = "results";
            final String TDMB_POSTERPATH = "poster_path";
            final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185";
            final String TMDB_PLOT_SYNOPSIS = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_TITLE = "title";
            final String TMDB_VOTE_AVG = "vote_average";
            final String TMDB_MOVIE_ID = "id";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULT);
            String poster_path,release_date,plot_synopsis,title,vote_avg;
            int movie_id;
            ArrayList<Movie> moviesData = new ArrayList<Movie>();


            //Create movie object for each element in JSON object
            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movie = movieArray.getJSONObject(i);
                poster_path = BASE_POSTER_URL + movie.getString(TDMB_POSTERPATH);
                release_date = movie.getString(TMDB_RELEASE_DATE);
                plot_synopsis = movie.getString(TMDB_PLOT_SYNOPSIS);
                title = movie.getString(TMDB_TITLE);
                vote_avg = String.valueOf(movie.getDouble(TMDB_VOTE_AVG))+"/10";
                movie_id = movie.getInt(TMDB_MOVIE_ID);
                moviesData.add(new Movie(title,release_date,poster_path,vote_avg,plot_synopsis,movie_id));

            }

            //returns the movie data
            return moviesData;

        }

        protected ArrayList<Movie> doInBackground(String... params) {

            //no zip code passed there is nothing to look up.
            if (params.length == 0) {
                return null;
            }
            //Adding networking code for handling api calls
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;
            String apikey_value = Global.api_key;

            final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String APIKEY_PARAM = "api_key";
            final String SORTBY_PARAM = "sort_by";

            ArrayList<Movie> moviesData = null;
            try {
                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORTBY_PARAM, params[0])
                        .appendQueryParameter(APIKEY_PARAM, apikey_value)
                        .build();

                URL url = new URL(buildUri.toString());
                Log.v(LOG_TAG, "Build URL : " + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                //Log.v(LOG_TAG, "Input stream created");
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJSONStr = buffer.toString();
                //Log.e(LOG_TAG, "Forecast JSON String " + movieJSONStr);
                moviesData = getPopularMoviesThumbnailsJson(movieJSONStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            //Log.v(LOG_TAG, "Result returned");
            return moviesData;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        //get sort criteria
        String sort_criteria = sortCriteriaPreference.getString(getString(R.string.pref_sort_criteria_key), getString(R.string.pref_sort_critera_defaultvalue));
        //Log.v("Global","Global "+Global.sortCriteria+" "+sort_criteria);

        //if sort criteria changed then update moviesAdapter and Detail Fragment
        if(!Global.sortCriteria.equals(sort_criteria)) {

            //set movies Adapter based on Sort Criteria
            showMoviePoster(sort_criteria);
            Global.sortCriteria = sort_criteria;
        }
    }

//    @Override
//    public void onStop()
//    {
//        super.onStop();
//        Log.v("Main Activity Fragement", "On stop");
//    }
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        Log.v("Main Activity Fragement", "On onPause");
//    }
//
//    @Override
//    public void onDestroy()
//    {
//        super.onDestroy();
//        Log.v("Main Activity Fragement", "On onDestroy");
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        Log.v("Main Activity Fragement", "On onResume");
//    }
//
//    @Override
//    public void onDestroyView()
//    {
//        super.onDestroyView();
//        Log.v("Main Activity Fragment", "On onDestroyView");
//    }



    //Based on share preference this code will start main activity with require data
    private void showMoviePoster(String sort_criteria)
    {
        String url = "";
        String poster_path,release_date,plot_synopsis,title,vote_avg;
        int movie_id;
        ArrayList<Movie> moviesData = new ArrayList<Movie>();

        if(sort_criteria.equals(getString(R.string.pref_sort_critera_defaultvalue)))
            new FetchPopularMovies().execute(sort_criteria);
        else if(sort_criteria.equals(getString(R.string.pref_sort_critera_highestrated)))
            new FetchPopularMovies().execute(sort_criteria);
        else if(sort_criteria.equals(getString(R.string.pref_sort_critera_favourite)))
        {
            // Test the basic content provider query
            Cursor movieCursor = getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );

            if(movieCursor!=null) {
                while (movieCursor.moveToNext()) {
                    title = movieCursor.getString(COL_TITLE);
                    release_date = movieCursor.getString(COL_RELEASE_DATE);
                    poster_path = movieCursor.getString(COL_MOVIE_POSTER_URL);
                    vote_avg = movieCursor.getString(COL_VOTE_AVG);
                    plot_synopsis = movieCursor.getString(COL_PLOT_SYNOPSIS);
                    movie_id = movieCursor.getInt(COL_MOVIE_ID);
                    moviesData.add(new Movie(title, release_date, poster_path, vote_avg, plot_synopsis, movie_id));
                }
                moviesAdapter.setMoviesData(moviesData);

                if(moviesData.size()>0 && getActivity().findViewById(R.id.movie_detail_container)!=null) {
                    //update detail fragment
                    updateDetailFragment(0);
                }
            }
        }

    }

    private void updateDetailFragment(int position)
    {
        Movie movie = (Movie) gridview.getItemAtPosition(position);
        if(movie!=null){
            ((Callback)getActivity()).onItemSelected(movie.title,
                    movie.movie_poster_url,
                    movie.plot_synopsis,
                    movie.releaseDate,
                    movie.vote_average,
                    movie.movie_id);
        }
    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String title,String poster_url,String plot_synopsis,String release_date,String vote_average,int movie_id);
    }
}
