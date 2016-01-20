package com.example.android.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieTask;

public class MoviePosterMainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public AndroidImageAdapter imageAdapter;
    private static final int MOVIE_LOADER = 0;
    private Toolbar toolbar;
    private static final String[] POSTER_PROJECTION = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_IMAGE_URL
    };
    public static final int COLUMN_IMAGE_URL = 1;
    public static final String SORT_ORDER = MovieContract.MovieEntry._ID + " ASC";

    public MoviePosterMainFragment() {
    }

    public void onMovieSortingChanged() {
        updateMovie();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void updateMovie(){
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity());
        String sortingPref = Utility.getPreferredMovieSorting(getActivity());
        fetchMovieTask.execute(sortingPref);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        populateMovieListView(rootView);
        return rootView;
    }

    private void populateMovieListView(View rootView) {
        imageAdapter = new AndroidImageAdapter(getActivity(), null, 0);
        GridView movieGridView = (GridView) rootView.findViewById(R.id.grid_movie_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            movieGridView.setNestedScrollingEnabled(true);
        }
        movieGridView.setAdapter(imageAdapter);
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                cursor.moveToPosition(-1);
                intent.putExtra("current pos", position);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                POSTER_PROJECTION,
                null,
                null,
                SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        imageAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        imageAdapter.swapCursor(null);
    }
}
