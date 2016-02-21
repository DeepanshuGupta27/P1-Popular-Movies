package com.example.android.popularmovies;

/**
 * Created by deepanshugupta on 18/02/16.
 */
public class Trailer {
    String display_text; //Display text for the trailers
    String youtube_url; //URL of youtube trailer


    public Trailer(String display_text, String youtube_url)
    {
        this.display_text = display_text;
        this.youtube_url = youtube_url;
    }
}
