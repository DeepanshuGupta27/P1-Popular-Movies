package com.example.android.popularmovies;

import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;
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

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
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

        if(savedInstanceState==null || !savedInstanceState.containsKey("movies")) {
            showMoviePoster();
        }
        else{
            moviesAdapter.moviesData = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", moviesAdapter.getMoviesData());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        //movie poster grid view
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(moviesAdapter);

        //Open detail activity once poster is clicked
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("title", movie.title);
                intent.putExtra("poster_url", movie.movie_poster_url);
                intent.putExtra("plot_synopsis", movie.plot_synopsis);
                intent.putExtra("release_date", movie.releaseDate);
                intent.putExtra("vote_average", movie.vote_average);
                intent.putExtra("movie_id",movie.movie_id);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //Class for fetching movie data on worker thread
    private class FetchPopularMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchPopularMovies.class.getSimpleName();


        @Override
        protected void onPostExecute(ArrayList<Movie> movieDetails) {

            if (movieDetails != null) {
                //this will set moviesData ArrayList and will notify grid view for the data change
                moviesAdapter.setMoviesData(movieDetails);
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
                //Log.v(LOG_TAG, "Build URL : " + url);

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
        showMoviePoster();
    }

    //Based on share preference this code will start main activity with require data
    private void showMoviePoster()
    {
        String url = "";
        String sort_criteria = "";
        String poster_path,release_date,plot_synopsis,title,vote_avg;
        int movie_id;
        ArrayList<Movie> moviesData = new ArrayList<Movie>();

        SharedPreferences sortCriteriaPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sort_criteria = sortCriteriaPreference.getString(getString(R.string.pref_sort_criteria_key),getString(R.string.pref_sort_critera_defaultvalue));
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
            }
        }
    }
}
