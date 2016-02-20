package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;
import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    private Movie movieObj;
    private MoviesTrailerAdapter trailerAdapter;
    private MovieReviewAdaptor reviewAdapter;
    private String youtube_url = null;
    private ListView trailerlistView,reviewlistView;
    private final static String baseURL = "http://api.themoviedb.org/3/movie/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);
        trailerAdapter = new MoviesTrailerAdapter(getContext(),new ArrayList<Trailer>());
        reviewAdapter = new MovieReviewAdaptor(getContext(),new ArrayList<Review>());

        trailerlistView = (ListView) rootview.findViewById(R.id.listview_movieTrailer);
        trailerlistView.setAdapter(trailerAdapter);

        reviewlistView = (ListView)rootview.findViewById(R.id.listview_movieReviews);
        reviewlistView.setAdapter(reviewAdapter);

        //get the intent sent from main activity
        Intent intent = getActivity().getIntent();

        String poster_path = "",release_date = "",plot_synopsis = "",title = "",vote_avg = "";
        int movie_id = 0;

        //pull out all the extra parameter sent with intent and populate required views.
        if (intent != null && intent.hasExtra("title")) {
            title = intent.getExtras().getString("title");
            ((TextView) rootview.findViewById(R.id.title)).setText(title);
        }

        if (intent != null && intent.hasExtra("poster_url")) {
            poster_path = intent.getExtras().getString("poster_url");
            ImageView view = (ImageView) rootview.findViewById(R.id.movie_poster);
            Picasso.with(getActivity()).load(poster_path).into(view);
        }

        if (intent != null && intent.hasExtra("plot_synopsis")) {
            plot_synopsis = intent.getExtras().getString("plot_synopsis");
            ((TextView) rootview.findViewById(R.id.plot_synopsis)).setText(plot_synopsis);
        }

        if (intent != null && intent.hasExtra("release_date")) {
            release_date = intent.getExtras().getString("release_date");
            ((TextView) rootview.findViewById(R.id.release_date)).setText(release_date);
        }

        if (intent != null && intent.hasExtra("vote_average")) {
            vote_avg  = intent.getExtras().getString("vote_average");
            ((TextView) rootview.findViewById(R.id.vote_average)).setText(vote_avg);
        }

        if (intent != null && intent.hasExtra("movie_id")) {
            movie_id  = intent.getExtras().getInt("movie_id");
        }
        movieObj = new Movie(title, release_date, poster_path, vote_avg, plot_synopsis, movie_id);

        Button button= (Button) rootview.findViewById(R.id.favouriteButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertFavouriteMovie();
            }
        });

        trailerlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String youtube_url = trailerAdapter.getItem(position).youtube_url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtube_url)));
            }
        });

        return rootview;
    }

    private class FetchMoviesTrailer extends AsyncTask<String, Void, ArrayList<Trailer>> {

        private final String LOG_TAG = FetchMoviesTrailer.class.getSimpleName();

        @Override
        protected void onPostExecute(ArrayList<Trailer> movieTrailers) {

            if (movieTrailers != null) {
                //this will set moviesData ArrayList and will notify grid view for the data change
                youtube_url = movieTrailers.get(0).youtube_url;
                setHasOptionsMenu(true);
                trailerAdapter.setMoviesTrailerData(movieTrailers);
                setListViewHeightBasedOnChildren(trailerlistView);
            }

        }

        /**
         * Take the String representing the complete movie data in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Trailer> getMoviesTrailerJson(String movieTrailerJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULT = "results";
            final String TDMB_YOUTUBE_KEY = "key";
            final String BASE_TRAILER_URL = "https://www.youtube.com/watch?v=";
            final String TMDB_TRAILER_WEBSITE = "site";

            JSONObject movieTrailerJson = new JSONObject(movieTrailerJsonStr);
            JSONArray movieTrailerArray = movieTrailerJson.getJSONArray(TMDB_RESULT);
            String display_text,youtube_url,site;
            int count = 1;

            ArrayList<Trailer> moviesTrailerData = new ArrayList<Trailer>();


            //Create movie object for each element in JSON object
            for (int i = 0; i < movieTrailerArray.length(); i++) {

                JSONObject movieTrailer = movieTrailerArray.getJSONObject(i);
                site = movieTrailer.getString(TMDB_TRAILER_WEBSITE);
                if(site.equals("YouTube")) {
                    youtube_url = BASE_TRAILER_URL + movieTrailer.getString(TDMB_YOUTUBE_KEY);
                    display_text = "Trailer " + String.valueOf(count);
                    count++;
                    moviesTrailerData.add(new Trailer(display_text, youtube_url));
                }
            }

            //returns the movie data
            return moviesTrailerData;

        }

        protected ArrayList<Trailer> doInBackground(String... params) {

            ArrayList<Trailer> moviesTrailerData = null;
            String movieTrailerJSONStr = null;
            try {
                movieTrailerJSONStr = Global.getJsonData(params);

                if(movieTrailerJSONStr!=null) {
                    moviesTrailerData = getMoviesTrailerJson(movieTrailerJSONStr);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            }

            //Log.v(LOG_TAG, "Result returned");
            return moviesTrailerData;
        }
    }

    private class FetchMovieReviews extends AsyncTask<String, Void, ArrayList<Review>> {

        private final String LOG_TAG = FetchMovieReviews.class.getSimpleName();

        @Override
        protected void onPostExecute(ArrayList<Review> movieReviews) {

            if (movieReviews != null) {
                reviewAdapter.setMoviesReviewData(movieReviews);
                //setListViewHeightBasedOnChildren(reviewlistView);
            }
        }

        /**
         * Take the String representing the complete movie data in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Review> getMoviesReviewJson(String movieReviewJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULT = "results";
            final String TMDB_REVIEW_CONTENT = "content";
            final String TMDB_AUTHOR = "author";

            JSONObject movieReviewJson = new JSONObject(movieReviewJsonStr);
            JSONArray movieReviewArray = movieReviewJson.getJSONArray(TMDB_RESULT);
            String review,author;

            ArrayList<Review> movieReviews = new ArrayList<Review>();

            //Create movie object for each element in JSON object
            for (int i = 0; i < movieReviewArray.length(); i++) {

                JSONObject movieReview = movieReviewArray.getJSONObject(i);
                review = movieReview.getString(TMDB_REVIEW_CONTENT);
                author = movieReview.getString(TMDB_AUTHOR) + " : ";
                movieReviews.add(new Review(author, review));
            }

            //returns the movie data
            return movieReviews;

        }

        protected ArrayList<Review> doInBackground(String... params) {

            //no zip code passed there is nothing to look up.
            String movieReviewJSONStr = null;
            ArrayList<Review> movieReviews = null;
            try {
                movieReviewJSONStr = Global.getJsonData(params);
                if(movieReviewJSONStr!=null) {
                    movieReviews = getMoviesReviewJson(movieReviewJSONStr);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            }
            //Log.v(LOG_TAG, "Result returned");
            return movieReviews;
        }
    }



    public void insertFavouriteMovie()
    {
        int duration = Toast.LENGTH_SHORT;
        String text;
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieObj.movie_id);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieObj.title);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieObj.releaseDate);
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, movieObj.movie_poster_url);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, movieObj.vote_average);
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, movieObj.plot_synopsis);
        Uri movieInsertUri = getContext().getContentResolver()
                .insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        if(movieInsertUri!=null) {
            text = "Movie Inserted Correctly";
        }
        else{
            text = "Movie didn't get inserted correctly";
        }

        Toast toast = Toast.makeText(getContext(), text, duration);
        toast.show();
    }

    public String createTrailerUri(int movieId)
    {
        return baseURL+movieId+"/videos?";
    }

    public String createReviewUri(int movieId)
    {
        return baseURL+movieId+"/reviews?";
    }

    @Override
    public void onStart()
    {
        super.onStart();
        String trailerBaseURL = createTrailerUri(movieObj.movie_id);
        String reviewBaseURL = createReviewUri(movieObj.movie_id);
        new FetchMoviesTrailer().execute(trailerBaseURL);
        new FetchMovieReviews().execute(reviewBaseURL);
    }

    private Intent createShareForecastIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, youtube_url);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem share = menu.findItem(R.id.action_share);
        ShareActionProvider mshareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);

        if(mshareActionProvider!=null)
            mshareActionProvider.setShareIntent(createShareForecastIntent());
        else
            Log.d("Detail Fragment","Cannot share");
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }



}
