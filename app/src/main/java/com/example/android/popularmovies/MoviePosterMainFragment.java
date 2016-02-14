package com.example.android.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.fetchRawJSON.FetchMovieTask;

import java.util.concurrent.locks.ReentrantLock;

public class MoviePosterMainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public AndroidImageAdapter imageAdapter;
    private static final int MOVIE_LOADER = 0;
    private Toolbar toolbar;
    private static final String[] POSTER_PROJECTION = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_IMAGE_URL
    };
    public static final int COLUMN_IMAGE_URL = 1;
//    public static final String SORT_ORDER = MovieContract.MovieEntry._ID + " ASC";
    public static final String SORT_ORDER = MovieContract.MovieEntry.COLUMN_RANK + " ASC";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final boolean IS_REFRESH = true;
    private final boolean IS_NOT_REFRESH = false;
    private CoordinatorLayout mCoordinatorLayout;
    private final ReentrantLock lock = new ReentrantLock();
    private int currentItemLoadingCount = 0;

    public MoviePosterMainFragment() {
    }

    public void onMovieSortingChanged() {
        updateMovie(IS_REFRESH, 0);
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
            updateMovie(IS_REFRESH, 0);
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovie(boolean isRefresh, int itemCount) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity(), mSwipeRefreshLayout, isRefresh, itemCount);
        String sortingPref = Utility.getPreferredMovieSorting(getActivity());
        fetchMovieTask.execute(sortingPref);
        if (isRefresh) {
            currentItemLoadingCount = 0;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_container);
        //mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LAYOUT_DIRECTION_RTL);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //should erase all the data in the database first before update?
                updateMovie(IS_REFRESH, 0);
            }
        });
        populateMovieListView(rootView);
        return rootView;
    }

    private void populateMovieListView(final View rootView) {
        Cursor movieEntries = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (!movieEntries.moveToFirst())
            updateMovie(IS_REFRESH, 0);
        imageAdapter = new AndroidImageAdapter(getActivity(), null, 0);
        GridView movieGridView = (GridView) rootView.findViewById(R.id.grid_movie_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            movieGridView.setNestedScrollingEnabled(true);
        }
        movieGridView.setAdapter(imageAdapter);
        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
//                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
//                cursor.moveToPosition(-1);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("current pos", position);
                startActivity(intent);
            }
        });
        movieGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int total = firstVisibleItem + visibleItemCount;
                if (total == totalItemCount && total != 0 && currentItemLoadingCount != totalItemCount) {
//                if (firstVisibleItem == totalItemCount && total != 0 && currentItemLoadingCount != totalItemCount) {
                    currentItemLoadingCount = totalItemCount;
                    updateMovie(IS_NOT_REFRESH, totalItemCount);
                    Snackbar.make(mCoordinatorLayout, getString(R.string.loading_data), Snackbar.LENGTH_SHORT).show();
                }
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
