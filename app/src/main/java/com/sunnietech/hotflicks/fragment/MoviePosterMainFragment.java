package com.sunnietech.hotflicks.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sunnietech.hotflicks.R;
import com.sunnietech.hotflicks.activity.SettingsActivity;
import com.sunnietech.hotflicks.adapter.AndroidImageAdapter;
import com.sunnietech.hotflicks.adapter.MoviePosterRecylerViewAdapter;
import com.sunnietech.hotflicks.persistence.MovieContract;
import com.sunnietech.hotflicks.task.FetchMovieTask;
import com.sunnietech.hotflicks.utility.SharedPreferenceUtil;

public class MoviePosterMainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int COLUMN_IMAGE_URL = 1;
    public static final String SORT_ORDER = MovieContract.MovieEntry.COLUMN_RANK + " ASC";
    private static final int MOVIE_LOADER = 0;
    private static final String[] POSTER_PROJECTION = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_IMAGE_URL
    };
    private static final boolean IS_REFRESH = true;
    private static final boolean IS_NOT_REFRESH = false;
    public AndroidImageAdapter imageAdapter;
    private Toolbar toolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private int currentItemLoadingCount = 0;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    MoviePosterRecylerViewAdapter posterRecylerViewAdapter;

    public MoviePosterMainFragment() {
    }

    public void onMovieSortingChanged() {
        updateMovie(IS_REFRESH, 0);
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.movie_main_fragment, menu);
//    }

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
        String sortingPref = SharedPreferenceUtil.getPreferredMovieSorting(getActivity());
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
//        imageAdapter = new AndroidImageAdapter(getActivity(), null, 0);
//        GridView movieGridView = (GridView) rootView.findViewById(R.id.grid_movie_view);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            movieGridView.setNestedScrollingEnabled(true);
//        }
//        movieGridView.setAdapter(imageAdapter);
//        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
//                intent.putExtra("current pos", position);
//                startActivity(intent);
//            }
//        });
//        movieGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int total = firstVisibleItem + visibleItemCount;
//                if (total == totalItemCount && total != 0 && currentItemLoadingCount != totalItemCount) {
//                    currentItemLoadingCount = totalItemCount;
//                    updateMovie(IS_NOT_REFRESH, totalItemCount);
//                    Snackbar.make(mCoordinatorLayout, getString(R.string.loading_data), Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        });

        recyclerView = (RecyclerView)rootView.findViewById(R.id.poster_recycler_view);
        layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) { //check for scroll down
                    int totalItemCount = layoutManager.getItemCount();
                    int visibleItemCount = layoutManager.getChildCount();
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    int total = firstVisibleItem + visibleItemCount;
                    if (total == totalItemCount && total != 0 && currentItemLoadingCount != totalItemCount) {
                        currentItemLoadingCount = totalItemCount;
                        updateMovie(IS_NOT_REFRESH, totalItemCount);
                        Snackbar.make(mCoordinatorLayout, getString(R.string.loading_data), Snackbar.LENGTH_SHORT).show();
                    }
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
//        imageAdapter.swapCursor(data);
        if(posterRecylerViewAdapter == null) {
            posterRecylerViewAdapter = new MoviePosterRecylerViewAdapter(data, getActivity());
            recyclerView.setAdapter(posterRecylerViewAdapter);
        } else {
            posterRecylerViewAdapter.setCursor(data);
            posterRecylerViewAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        imageAdapter.swapCursor(null);
        MoviePosterRecylerViewAdapter posterRecylerViewAdapter = new MoviePosterRecylerViewAdapter(null, getActivity());
        recyclerView.setAdapter(posterRecylerViewAdapter);
    }
}
