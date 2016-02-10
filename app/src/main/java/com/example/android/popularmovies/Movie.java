package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by deepanshugupta on 09/02/16.
 */
public class Movie implements Parcelable{
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

    private Movie(Parcel in)
    {
        title = in.readString();
        releaseDate = in.readString();
        movie_poster_url = in.readString();
        vote_average = in.readString();
        plot_synopsis = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(movie_poster_url);
        dest.writeString(vote_average);
        dest.writeString(plot_synopsis);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
