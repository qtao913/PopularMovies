package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    private final String MOVIEPOSTERFRAGMENT_TAG = "moviePosterFragment";
    private String movieSortingPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        movieSortingPreference = Utility.getPreferredMovieSorting(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviePosterMainFragment(), MOVIEPOSTERFRAGMENT_TAG)
                    .commit();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortingPreference = Utility.getPreferredMovieSorting(this);
        if (sortingPreference != null && !sortingPreference.equals(movieSortingPreference)) {
            MoviePosterMainFragment moviePosterFragment =
                    (MoviePosterMainFragment) getSupportFragmentManager()
                            .findFragmentByTag(MOVIEPOSTERFRAGMENT_TAG);
            if (moviePosterFragment != null) {
                moviePosterFragment.onMovieSortingChanged();
            }
            movieSortingPreference = sortingPreference;
        }
    }
}
