package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by deepanshugupta on 19/02/16.
 */
public class Global {
    public final static String api_key = "";
    public static String sortCriteria =  "";

    public final static String getJsonData(String... params)
    {
        final String LOG_TAG = Global.class.getSimpleName();
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
        String apikey_value = api_key;

        final String BASE_URL = params[0];
        final String APIKEY_PARAM = "api_key";

        try {
            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
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
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error IO Exception ", e);
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
        return movieJSONStr;
    }
}
