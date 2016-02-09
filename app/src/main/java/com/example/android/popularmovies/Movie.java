package com.example.android.popularmovies;

/**
 * Created by deepanshugupta on 09/02/16.
 */
public class Movie {
    String title;
    String releaseDate;
    String movie_poster_url;
    String vote_average;
    String plot_synopsis;

    public Movie(String title, String releaseDate, String movie_poster_url, String vote_average, String plot_synopsis)
    {
        this.movie_poster_url = movie_poster_url;
        this.title = title;
        this.releaseDate = releaseDate;
        this.vote_average = vote_average;
        this.plot_synopsis = plot_synopsis;
    }

}
