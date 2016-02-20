package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.v("Main Activity","On Create");

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
