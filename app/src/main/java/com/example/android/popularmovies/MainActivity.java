package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private AndroidImageAdapter imageAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Movie[] movies = {
                    new Movie(R.drawable.test1),
                    new Movie(R.drawable.test2),
                    new Movie(R.drawable.test3),
                    new Movie(R.drawable.test4)
            };

            List<Movie> movieList = new ArrayList<>(Arrays.asList(movies));
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            populateMovieListView(rootView, movieList);
            //ImageView test = (ImageView)rootView.findViewById(R.id.test_piccaso);
            //Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").into(test);
            return rootView;
        }

        private void populateMovieListView(View rootView, List<Movie> movies) {
            imageAdapter = new AndroidImageAdapter(getActivity(), movies);
            ListView movieListView = (ListView) rootView.findViewById(R.id.movie_list);
            movieListView.setAdapter(imageAdapter);
        }
    }
}
