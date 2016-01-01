package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract;

public class MoviePosterMainFragment extends Fragment {
    public AndroidImageAdapter imageAdapter;
    public MoviePosterMainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState == null || !savedInstanceState.containsKey("movie")) {
//            movieList = new ArrayList<>();
//        } else {
//            movieList = savedInstanceState.getParcelableArrayList("movie");
//        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.movie_refresh) {
            updateMovie();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    private void updateMovie(){
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortingPref = prefs.getString(getString(R.string.sorting_preference_key),
                getString(R.string.most_popular_value));
        fetchMovieTask.execute(sortingPref);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putParcelableArrayList("movie", movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        populateMovieListView(rootView);
        return rootView;
    }

    private void populateMovieListView(View rootView) {
        Cursor movieData = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        imageAdapter = new AndroidImageAdapter(getActivity(), movieData, 0);
        GridView movieGridView = (GridView) rootView.findViewById(R.id.grid_movie_view);
        movieGridView.setAdapter(imageAdapter);
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                Bundle detail = new Bundle();
//                detail.putParcelable("movie", movieList.get(position));
                intent.putExtras(detail);
                startActivity(intent);
            }
        });
    }
}
